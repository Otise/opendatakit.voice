package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.odk.voice.constants.FormAttribute;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.FormHandler;

public class FormEndWidget extends WidgetBase {
 
  FormHandler fh;
  String formTitle;
  
  public FormEndWidget(FormHandler fh) {
    this.fh = fh;
    this.formTitle = fh.getFormTitle();
  }
  
  @Override
  public void getPromptVxml(Writer out) throws IOException {
//    VxmlForm endForm = new VxmlForm("end", 
//        createPrompt(StringConstants.formEndPrompt(formTitle)),
//            "", "");
   
    String customEndPrompts = fh.getFormAttribute(FormAttribute.CUSTOM_END_PROMPTS);
    List<String> endPrompts = new ArrayList<String>();
    if (customEndPrompts == null) {
      endPrompts.add(String.format(getString(ResourceKeys.FORM_END), formTitle));
    } else {
      String[] cePrompts = customEndPrompts.split(FormAttribute.CUSTOM_PROMPTS_DELIM);
      for(String cePrompt : cePrompts)
        endPrompts.add(cePrompt);
    }
    
    VxmlField field = createField(("main"),
                      createPrompt(endPrompts.toArray(new String[endPrompts.size()])),
                      VxmlUtils.createGrammar(new String[]{"*"}, new String[]{""}),
                      "Sorry, not yet implemented.<reprompt/>");
    
    field.setNoinput(null, VxmlUtils.createVar("action", VoiceAction.HANGUP.name(), true) + 
                      createPrompt(getString(ResourceKeys.GOODBYE)) + 
                       VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action"));
    
    VxmlForm endForm = new VxmlForm("main",field);
    new VxmlDocument(sessionid, endForm).write(out);
  }
}
