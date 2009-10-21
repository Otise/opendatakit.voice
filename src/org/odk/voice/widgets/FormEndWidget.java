package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.odk.voice.constants.StringConstants;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlForm;

public class FormEndWidget extends WidgetBase {
 
  String formTitle;
  
  public FormEndWidget(String formTitle) {
    this.formTitle = formTitle;
  }
  
//  @Override
//  public String[] getPromptStrings() {
//    return new String[]{StringConstants.formEndPrompt(formTitle)};
//  }
  
  @Override
  public void getPromptVxml(Writer out) throws IOException {
    VxmlForm endForm = new VxmlForm("end", 
        createPrompt(StringConstants.formEndPrompt(formTitle)),
            "", "");
    new VxmlDocument(endForm).write(out);
  }
}
