package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.odk.voice.constants.StringConstants;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlPrompt;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.vxml.VxmlUtils;

public class RecordPromptWidget extends WidgetBase {
 
  String prompt;
  
  public RecordPromptWidget(String promptToRecord) {
    this.prompt = promptToRecord;
  }
  
  @Override
  public void getPromptVxml(Writer out) throws IOException {
    
    VxmlPrompt prePrompt = createPrompt(StringConstants.recordPromptInstructions);
     String preGrammar = VxmlUtils.createGrammar(new String[]{"1", "3"}, 
        new String[]{"out.action=\"RECORD\";", "out.action=\"" + VoiceAction.NEXT_PROMPT + "\";"});
    String preFilled = 
      "<if cond=\"action=='RECORD'\">" + 
      VxmlUtils.createLocalGoto("main") +
      "<else/>" +
      VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action") + 
      "</if>\n";
    VxmlForm preForm = new VxmlForm("action", prePrompt, preGrammar, preFilled);
    
    VxmlSection recordSection = new VxmlSection(
      "<record name=\"answer\" beep=\"true\" dtmfterm=\"true\" type=\"audio/x-wav\">\n" +
      "<filled>\n" + 
      "</filled>\n" + 
      "</record>\n");
    
    VxmlPrompt p2 = createPrompt(new String[]{
        StringConstants.answerConfirmationVoice, 
        "<value expr=\"answer\"/>",
        StringConstants.answerConfirmationOptions},
        new String[]{
        StringConstants.answerConfirmationVoice, 
        null, // notice that the recorded audio for the answer is null, because we want it to play the answer
        StringConstants.answerConfirmationOptions});
    
    
    VxmlField actionField = new VxmlField("action", p2, actionGrammar, actionFilled(true));
    
    VxmlForm mainForm = new VxmlForm("main", recordSection, actionField);
    
    new VxmlDocument(preForm, mainForm).write(out);
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