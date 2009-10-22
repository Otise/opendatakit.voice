package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.odk.voice.constants.StringConstants;
import org.odk.voice.constants.VoiceAction;
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
    String grammar = VxmlUtils.createGrammar(new String[]{"1","9"}, 
        new String[]{"out.action=\"" + VoiceAction.NEXT_PROMPT + "\";",
                     "out.action=\"" + VoiceAction.ADMIN + "\";"});
    String filled = 
      VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action") + "\n";
    VxmlForm startForm = new VxmlForm("action", 
        createPrompt(StringConstants.formStartPrompt(formTitle)),
            grammar, filled);
    new VxmlDocument(startForm).write(out);
  }

}
