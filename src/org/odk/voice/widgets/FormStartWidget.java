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

public class FormStartWidget extends WidgetBase {
 
  private static org.apache.log4j.Logger log = Logger
  .getLogger(FormStartWidget.class);
  
  public static final String ADMIN_CODE = "7";
  public static String recordCallLabel = "label";  //set in FormVxmlRenderer with phone number, etc.
  
  FormHandler fh;
  String formTitle;
  boolean hasLanguages;
  
  public FormStartWidget(FormHandler fh) {
    this.fh = fh;
    this.formTitle = fh.getFormTitle();
    this.hasLanguages = fh.getLanguages()!=null;
  }
  
  @Override
  public void getPromptVxml(Writer out) throws IOException {
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
      String[] ciPrompts = customIntroPrompts.split(FormAttribute.CUSTOM_INTRO_PROMPTS_DELIM);
      for(String ciPrompt : ciPrompts)
        startPrompts.add(ciPrompt);
    }
    VxmlPrompt prompt = createPrompt(startPrompts.toArray(new String[startPrompts.size()]));
    VxmlField startField = createField("action", prompt, grammar, filled);
    
    if (fh.getFormAttribute(FormAttribute.SKIP_CONFIRMATION, true)){
      startField.setNoinput(
          VxmlUtils.createVar("action", VoiceAction.NEXT_PROMPT.name(), true) + filled);
      startField.setContents("<property name=\"timeout\" value=\"0s\"/>");
    }
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

}
