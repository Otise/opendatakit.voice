package org.odk.voice.widgets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.odk.voice.constants.StringConstants;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlPrompt;
import org.odk.voice.vxml.VxmlPromptCreator;
import org.odk.voice.vxml.VxmlUtils;

public abstract class WidgetBase implements VxmlWidget, VxmlPromptCreator{

  private List<String> promptStrings = new ArrayList<String>();
  
  @Override
  public String[] getPromptStrings(){
    try {
      getPromptVxml(new OutputStreamWriter(new OutputStream(){
        public void write ( int b ) { }
      }));
    } catch (IOException e) {}
    return promptStrings.toArray(new String[]{});
  }
  
  public String getWmvPath(String audio) {
    if (promptStrings != null)
      promptStrings.add(audio);
    return audio.hashCode() + ".wmv";
  }
  
  public void addPromptString(String... promptString) {
    for (String p: promptStrings) {
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
  public VxmlPrompt createBasicPrompt(String vxml){
    final String vxml2 = vxml;
    return new VxmlPrompt(){
      public String getPromptString(){
        return vxml2;
      }
    };
  }
  
  public VxmlPrompt createPrompt(String[] text, String[] audio) {
    int length = text==null? audio.length : text.length;
    String vxml = "      <prompt>\n";
    for (int i = 0; i < length; i++){
      vxml = vxml + VxmlUtils.indent(VxmlUtils.getAudio(text==null?null:text[i], audio==null?null:audio[i]), 4);
    }
    vxml = vxml + "      </prompt>\n";
    for (String s: audio)
      addPromptString(s);
    return createBasicPrompt(vxml);
  }
  
  public VxmlPrompt createPrompt(String... textAndAudio) {
    return createPrompt(textAndAudio, textAndAudio);
  }
  
  VxmlField getActionField(boolean binary) {
    return new VxmlField("action", 
      createPrompt(StringConstants.answerConfirmationOptions),
      actionGrammar,
      actionFilled(binary));
  }
  
  String actionFilled (boolean binary) {
    String submit = binary? VxmlUtils.createMultipartSubmit(FormVxmlServlet.ADDR, new String[]{"action", "answer"}) :
      VxmlUtils.createSubmit(FormVxmlServlet.ADDR, new String[]{"action", "answer"});
    return
    "<if cond=\"action=='REPEAT'\">" + 
    "<clear namelist=\"action answer\"/>" +
    VxmlUtils.createLocalGoto("main") + "<else/>" + 
    createPrompt(StringConstants.thankYou).getPromptString() + 
    (binary ? createPrompt(StringConstants.pleaseHold).getPromptString() : "") +
    submit + 
    "</if>\n";
  }
  
  String actionGrammar = VxmlUtils.createGrammar(new String[]{"1","2"}, 
      new String[]{"out.action=\"" + VoiceAction.SAVE_ANSWER + "\";", 
                   "out.action=\"REPEAT\";"});

}
