package org.odk.voice.widgets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.odk.voice.constants.GlobalConstants;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlPrompt;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.vxml.VxmlUtils;

/**
 * <p>{@link WidgetBase} is the base class for VxmlWidgets. It contains a number 
 * of methods that simplify the process of creating VxmlWidgets properly.</p> 
 * 
 * <p>WidgetBase handles the {@link #getPromptStrings} method of {@link VxmlWidget}; 
 * however, correct functionality requires that the subclass 'tells' WidgetBase 
 * about all prompt strings in the {@link #getPromptVxml} method by either (a) declaring 
 * the prompt using {@link #createPrompt}, or (b) calling {@link #addPromptString} with 
 * the prompt as an argument.</p>
 * 
 * <p>The {@link #getActionField} method provides an easy shortcut for creating the 
 * 'confirmation' dialog that says 'Press 1 if that is correct, or 2 to try again.', 
 * and submits the result to the {@link FormVxmlServlet}.</p>
 * 
 * <p> <b>Note:</b> For proper functionality of a WidgetBase, {@link #setSessionId} and 
 * {@link #setLocale} should be called on the WidgetBase before {@ #getPromptVxml} is 
 * called.</p>
 * 
 * @author alerer
 *
 */
public abstract class WidgetBase implements VxmlWidget{

  String sessionid;
  
  ResourceBundle resources;
  
  private List<String> promptStrings = new ArrayList<String>();
  
  public WidgetBase(){
    setLocale(GlobalConstants.DEFAULT_LOCALE);
  }
  
  public void setLocale(Locale l) {
    if (l != null) {
      resources = 
        ResourceBundle.getBundle(org.odk.voice.local.Resources.class.getCanonicalName(), l);
    }
  }
  
  public VxmlField createField(String name, VxmlPrompt prompt, String grammar, String filled){
    VxmlField res = new VxmlField(name, prompt, grammar, filled);
    res.setNomatch(null, createPrompt(getString(ResourceKeys.NO_MATCH)) + "<reprompt/>");
    res.setNoinput(null, createPrompt(getString(ResourceKeys.NO_INPUT)) + "<reprompt/>");
    res.setNoinput(3, createPrompt(getString(ResourceKeys.NO_INPUT_3)) + 
        VxmlUtils.createVar("action", VoiceAction.NO_RESPONSE.name(), true) +
        VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action"));
    return res;
  }
  
  public String getString(String key){
    return resources.getString(key);
  }
  
  public void setSessionid(String sessionid){
    this.sessionid = sessionid;
  }
  
  @Override
  public List<String> getPromptStrings(){
    
    try {
      getPromptVxml(new OutputStreamWriter(new OutputStream(){
        public void write ( int b ) { }
      }));
    } catch (IOException e) {}
    return promptStrings;
  }
  
  String getWmvPath(String audio) {
    if (promptStrings != null)
      promptStrings.add(audio);
    return audio.hashCode() + ".wmv";
  }
  
  void addPromptString(String... ps) {
    for (String p: ps) {
    if (p != null && !p.equals(""))
      promptStrings.add(p);
    }
  }
  
  /**
   * Note: This does not add the prompt strings to be recorded. You must do that 
   * manually using the {@link addPromptString} method.
   * @param vxml
   * @return
   */
  VxmlPrompt createBasicPrompt(String vxml){
    final String vxml2 = vxml;
    return new VxmlPrompt(){
      @Override
      public String toString(){
        return vxml2;
      }
    };
  }
  
  VxmlPrompt createPrompt(String[] text, String[] audio, boolean bargein) {
    int length = text==null? audio.length : text.length;
    String vxml = "<prompt" + (bargein?"":" bargein=\"false\"") + ">";
    for (int i = 0; i < length; i++){
      vxml = vxml + VxmlUtils.getAudio(text==null?null:text[i], audio==null?null:audio[i]);
    }
    vxml = vxml + "</prompt>";
    for (String s: audio)
      addPromptString(s);
    return createBasicPrompt(vxml);
  }
  
  VxmlPrompt createPrompt(String[] text, String[] audio) {
    return createPrompt(text, audio, true);
  }
  
  VxmlPrompt createPrompt(String... textAndAudio) {
    return createPrompt(textAndAudio, textAndAudio);
  }
//  VxmlPrompt createPrompt(boolean bargein, String... textAndAudio) {
//    return createPrompt(textAndAudio, textAndAudio, bargein);
//  }
  VxmlPrompt createPrompt(boolean ttsOnly, String... text) {
    return createPrompt(text, null);
  }
  
  
  VxmlPrompt createCompositePrompt(VxmlPrompt... prompts){
    StringBuilder vxml = new StringBuilder("");
    for (VxmlPrompt p : prompts) {
      vxml.append(p);
    }
    return createBasicPrompt(vxml.toString());
  }
  
  VxmlSection getActionField(boolean confirm, boolean binary) {
    if (confirm) {
      return createField("action", 
      createPrompt(getString(ResourceKeys.ANSWER_CONFIRMATION_OPTIONS)),
      actionGrammar,
      actionFilled(binary));
    } else {
      return new VxmlSection(
          "<block>" + 
          VxmlUtils.createVar("action", VoiceAction.SAVE_ANSWER.name(), true) + 
          actionFilled(binary) + 
          "</block>");
    }
  }
  
  String actionFilled (boolean binary) {
    String submit = binary? VxmlUtils.createMultipartSubmit(FormVxmlServlet.ADDR, new String[]{"action", "answer"}) :
      VxmlUtils.createSubmit(FormVxmlServlet.ADDR, new String[]{"action", "answer"});
    return
    "<if cond=\"action=='REPEAT'\">" + 
    "<clear namelist=\"action answer\"/>" +
    VxmlUtils.createLocalGoto("main") + "<else/>" + 
    createPrompt(getString(ResourceKeys.THANK_YOU))  +
    submit + 
    "</if>\n";
  }
  
  String actionGrammar = VxmlUtils.createGrammar(new String[]{"1","2"}, 
      new String[]{VoiceAction.SAVE_ANSWER.name(), 
                   "REPEAT"});

}
