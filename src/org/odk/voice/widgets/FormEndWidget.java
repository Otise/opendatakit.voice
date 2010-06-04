package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.odk.voice.constants.VoiceAction;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlUtils;

public class FormEndWidget extends WidgetBase {
 
  String formTitle;
  
  public FormEndWidget(String formTitle) {
    this.formTitle = formTitle;
  }
  
  @Override
  public void getPromptVxml(Writer out) throws IOException {
//    VxmlForm endForm = new VxmlForm("end", 
//        createPrompt(StringConstants.formEndPrompt(formTitle)),
//            "", "");
   
    VxmlField field = createField(("main"),
                      createPrompt(String.format(getString(ResourceKeys.FORM_END), formTitle)),
                      VxmlUtils.createGrammar(new String[]{"*"}, new String[]{""}),
                      "Sorry, not yet implemented.<reprompt/>");
    
    field.setNoinput(null, VxmlUtils.createVar("action", VoiceAction.HANGUP.name(), true) + 
                      createPrompt(getString(ResourceKeys.GOODBYE)) + 
                       VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action"));
    
    VxmlForm endForm = new VxmlForm("main",field);
    new VxmlDocument(sessionid, endForm).write(out);
  }
}
