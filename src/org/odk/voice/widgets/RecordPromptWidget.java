package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.odk.voice.constants.StringConstants;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlPrompt;
import org.odk.voice.vxml.VxmlPromptCreator;
import org.odk.voice.vxml.VxmlUtils;

public class RecordPromptWidget extends WidgetBase {
 
  String prompt;
  
  public RecordPromptWidget(String promptToRecord) {
    this.prompt = promptToRecord;
  }
  
  @Override
  public void getPromptVxml(Writer out) throws IOException {
    
    VxmlPrompt prePrompt = createPrompt(StringConstants.recordPromptInstructions);
     String preGrammar = VxmlUtils.createGrammar(new String[]{"1"}, 
        new String[]{"out.action=\"RECORD\";"});
    String preFilled = "<if expr=\"action='RECORD'\">" + 
      VxmlUtils.createGoto("answer") +
      "</if>\n";
    VxmlForm preForm = new VxmlForm("pre", prePrompt, preGrammar, preFilled);
    
    VxmlForm answerForm = new VxmlForm("answer");
    String contents = 
      "<record name=\"answer\" beep=\"true\" dtmfterm=\"true\" type=\"audio/x-wav\">\n" +
      "<filled>\n" + 
      "  " + VxmlUtils.createGoto("#confirm") +
      "</filled>\n" + 
      "</record\n";
    answerForm.setContents(contents);
    
    VxmlPrompt p2 = createPrompt(new String[]{
        StringConstants.answerConfirmationVoice, 
        "<var expr=\"answer\"/>",
        StringConstants.answerConfirmationOptions},
        new String[]{
        StringConstants.answerConfirmationVoice, 
        null, // notice that the recorded audio for the answer is null, because we want it to play the answer
        StringConstants.answerConfirmationOptions});
    
    
    VxmlForm confirmForm = new VxmlForm("confirm", p2, VxmlUtils.confirmGrammar, VxmlUtils.confirmFilled(this));
    
    new VxmlDocument(answerForm, confirmForm).write(out);
  }
}


//<?xml version="1.0" encoding="UTF-8"?>
//
//<vxml version = "2.1">
//
//<meta name="author" content="Matthew Henry"/>
//<meta name="copyright" content="2005 voxeo corporation"/>
//<meta name="maintainer" content="YOUR_EMAIL@HERE.COM"/>
//
//<form id="F1">
//
//  <record name="R_1" beep="true" dtmfterm="true">
//    <prompt>
//      here you will hear a beep indicating
//      that you should start your recording.
//    </prompt>
//
//    <prompt>
//      after you are finished, you may press any DTMF key to indicate that you are done recording.
//    </prompt>
//
//    <filled>
//      <log expr="R_1$.duration"/>
//      <log expr="R_1$.termchar"/>
//      <log expr="R_1$.size"/>
//
//      <prompt> your recording was <value expr="R_1"/> </prompt>
//    </filled>
//
//  </record>
//
//</form>
//
//</vxml>


//<?xml version="1.0" encoding="UTF-8"?>
//
//<vxml version = "2.1">
//
//<meta name="author" content="Matthew Henry"/>
//<meta name="copyright" content="2005 voxeo corporation"/>
//<meta name="maintainer" content="YOUR_EMAIL@HERE.COM"/>
//
//<form id="F1">
//
//  <record name="R_1" maxtime="20s" finalsilence="5s">
//    <prompt>
//      the maximum length of his message is 20 seconds.
//      In addition, the longest a caller can remain silent is
//      no more than 5 seconds, else the recording will be submitted.
//    </prompt>
//
//    <filled>
//      <log expr="R_1$.duration"/>
//      <log expr="R_1$.termchar"/>
//      <log expr="R_1$.size"/>
//
//      <prompt> your recording was <value expr="R_1"/> </prompt>
//  </filled>
//
//  </record>
//
//</form>
//
//</vxml>