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
 
  private String prompt;
  
  public RecordPromptWidget(String promptToRecord) {
    this.prompt = promptToRecord;
  }
  
  @Override
  public void getPromptVxml(Writer out) throws IOException {

    VxmlPrompt prePrompt = createPrompt(StringConstants.recordPromptInstructions);
     String preGrammar = VxmlUtils.createGrammar(new String[]{"1", "3"}, 
        new String[]{"RECORD", VoiceAction.NEXT_PROMPT.name()});
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
    if (prompt == null) {
      new VxmlDocument(sessionid).write(out);
    } else {
      new VxmlDocument(sessionid, preForm, mainForm).write(out);
    }
  }
}
