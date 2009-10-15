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
  public String[] getPromptStrings() {
    return new String[]{StringConstants.formStartPrompt(formTitle)};
  }
  
  @Override
  public void getPromptVxml(Writer out) throws IOException {
    String grammar = VxmlUtils.createGrammar(new String[]{"1"}, 
        new String[]{"out.action=\"" + VoiceAction.NEXT_PROMPT + "\";"});
    String filled = 
      "<if expr=\"action='" + VoiceAction.NEXT_PROMPT + "'>" + 
      VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action");
    VxmlForm startForm = new VxmlForm("start", 
        createPrompt(StringConstants.formStartPrompt(formTitle)),
            grammar, filled);
    new VxmlDocument(startForm).write(out);
  }

}
