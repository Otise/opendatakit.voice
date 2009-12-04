package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.odk.voice.constants.VoiceAction;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlUtils;

public class FormStartWidget extends WidgetBase {
 
  String formTitle;
  
  public FormStartWidget(String formTitle) {
    this.formTitle = formTitle;
  }
  
  @Override
  public void getPromptVxml(Writer out) throws IOException {
    String grammar = VxmlUtils.createGrammar(new String[]{"1","9","7"}, 
        new String[]{VoiceAction.NEXT_PROMPT.name(),
                     VoiceAction.SET_LANGUAGE.name(),
                     VoiceAction.ADMIN.name()});
    String filled = 
      VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action") + "\n";
    VxmlForm startForm = new VxmlForm("action", 
        createPrompt(String.format(getString(ResourceKeys.THANK_YOU),formTitle)),
            grammar, filled);
    new VxmlDocument(sessionid, startForm).write(out);
  }

}
