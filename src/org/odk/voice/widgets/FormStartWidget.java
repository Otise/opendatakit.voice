package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.odk.voice.constants.VoiceAction;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.vxml.VxmlUtils;

public class FormStartWidget extends WidgetBase {
 
  public static final String ADMIN_CODE = "7531";
  public String recordCallLabel = "label";
  String formTitle;
  boolean hasLanguages;
  
  public FormStartWidget(String formTitle, boolean hasLanguages) {
    this.formTitle = formTitle;
    this.hasLanguages = hasLanguages;
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
    VxmlField startField = createField("action", 
            createPrompt(
                String.format(getString(ResourceKeys.FORM_START),formTitle),
                getString(ResourceKeys.PRESS_STAR_TO_REPEAT_INITIAL),
                getString(ResourceKeys.FORM_START_PRESS_1_TO_BEGIN),
                hasLanguages? getString(ResourceKeys.FORM_START_LANGUAGES) : ""),
            grammar, filled);
    startField.setContents("<noinput count=\"3\">" + 
        createPrompt(getString(ResourceKeys.GOODBYE)) + 
        VxmlUtils.createVar("action", VoiceAction.NO_RESPONSE.name(), true) +
        VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action") + "</noinput>");
    VxmlSection recordCallSection = new VxmlSection("<block><voxeo:recordcall value=\"100\" info=\"" + recordCallLabel + "\" /></block>");
    VxmlForm startForm = new VxmlForm("action", recordCallSection,startField);
    VxmlDocument doc = new VxmlDocument(sessionid, startForm);
    doc.write(out);
  }

}
