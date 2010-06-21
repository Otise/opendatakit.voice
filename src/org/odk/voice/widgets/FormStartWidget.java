package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.odk.voice.constants.FormAttribute;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlPrompt;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.FormHandler;

/**
 * Widget for the 'title dialogue' of a survey.
 * @author alerer
 *
 */
public class FormStartWidget extends WidgetBase {
 
  private static org.apache.log4j.Logger log = Logger
  .getLogger(FormStartWidget.class);
  
  public static final String ADMIN_CODE = "7";
  public String recordCallLabel = "label";  //set in FormVxmlRenderer with phone number, etc.
  
  FormHandler fh;
  String formTitle;
  boolean hasLanguages;
  int attempt = 1;
  
  public FormStartWidget(FormHandler fh) {
    this.fh = fh;
    this.formTitle = fh.getFormTitle();
    this.hasLanguages = fh.getLanguages()!=null && fh.getLanguages().length > 1;
  }
  
  @Override
  public void getPromptVxml(Writer out) throws IOException {
    // forceQuiet stuff ----------
    // if forceQuiet is on, all the prompts after some prompt in the instructions will have 
    // bargein on with voice (not dtmf). If the user barges in, it will count as nomatch, 
    // since the grammar is empty. Thus the 'be quiet' prompt will play and the instructions 
    // will start over. On the third 'be quiet', the survey will end, and if an outbound call, 
    // try to call back. This feature was no successful on evaluation, so it is not recommended 
    // tht it be used.
    String forceQuietS = fh.getFormAttribute(FormAttribute.FORCE_QUIET);
    boolean forceQuiet = (forceQuietS != null);
    int forceQuietStart = 0;
    if (forceQuiet) {
      try {
        forceQuietStart = Integer.parseInt(forceQuietS); 
        } catch (NumberFormatException e) { 
          forceQuiet = false;
      }
    } 
    // -------------------------
    
    boolean skipConfirmation = fh.getFormAttribute(FormAttribute.SKIP_CONFIRMATION, true);
    
    String grammar = hasLanguages ? 
        VxmlUtils.createGrammar(new String[]{"1","9",ADMIN_CODE}, 
        new String[]{VoiceAction.NEXT_PROMPT.name(),
                     VoiceAction.LANGUAGE_MENU.name(),
                     VoiceAction.ADMIN.name()}) :
        VxmlUtils.createGrammar(new String[]{"1",ADMIN_CODE}, 
        new String[]{VoiceAction.NEXT_PROMPT.name(),
                     VoiceAction.ADMIN.name()});
        
    String filled = 
      VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action") + "\n";
    
    List<String> startPrompts = new ArrayList<String>();
    String customIntroPrompts = fh.getFormAttribute(FormAttribute.CUSTOM_INTRO_PROMPTS);
    if (customIntroPrompts == null) {
      startPrompts.add(String.format(getString(ResourceKeys.FORM_START),formTitle));
      startPrompts.add(getString(ResourceKeys.PRESS_STAR_TO_REPEAT_INITIAL));
      startPrompts.add(getString(ResourceKeys.FORM_START_PRESS_1_TO_BEGIN));
      if (hasLanguages) startPrompts.add(getString(ResourceKeys.FORM_START_LANGUAGES));
    } else {
      String[] ciPrompts = customIntroPrompts.split(FormAttribute.CUSTOM_PROMPTS_DELIM);
      for(String ciPrompt : ciPrompts)
        startPrompts.add(ciPrompt);
    }
    
    VxmlPrompt[] prompts = new VxmlPrompt[startPrompts.size()];
    for (int i=0; i < startPrompts.size(); i++) {
      prompts[i] = createPrompt(!forceQuiet || i+1 >= forceQuietStart, 
          startPrompts.get(i));
    }
    VxmlPrompt prompt = createCompositePrompt(prompts);
    VxmlField startField = createField("action", prompt, grammar, filled);
    
    
    String properties = "";
    if (skipConfirmation){
      startField.setNoinput(null, 
          VxmlUtils.createVar("action", VoiceAction.NEXT_PROMPT.name(), true) + filled);
      properties += "<property name=\"timeout\" value=\"0s\"/>";
    }   
    if (forceQuiet && attempt == 1){
      startField.setNomatch(null, createPrompt(false, getString(ResourceKeys.FORCE_QUIET_WARNING)) + "<reprompt/>");
      startField.setNomatch(3,    VxmlUtils.createVar("action", VoiceAction.TOO_LOUD.name(), true) + 
          createPrompt(false, getString(ResourceKeys.FORCE_QUIET_HANGUP)).toString() +
           VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action"));
      properties += "<property name=\"inputmodes\" value=\"dtmf voice\"/>";
    }
    startField.setContents(properties);

    //<property name="timeout" value="10s"/> 
    // we use this instead of startField if we're skipping confirmation
//    VxmlSection skipConfSection = new VxmlSection("<block>" + 
//        prompt.toString() + 
//        VxmlUtils.createVar("action", VoiceAction.NEXT_PROMPT.name(), true) +
//        filled + "</block>");
    
    
    VxmlSection recordCallSection = new VxmlSection("<block><voxeo:recordcall value=\"100\" info=\"" + recordCallLabel + "\" /></block>");
    VxmlForm startForm = new VxmlForm("action", recordCallSection, startField);
    VxmlDocument doc = new VxmlDocument(sessionid, startForm);
    doc.write(out);
  }

  public void setAttempt(int attempt) {
    this.attempt = attempt;
  }

}
