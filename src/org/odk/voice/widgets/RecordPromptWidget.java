package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.odk.voice.constants.VoiceAction;
import org.odk.voice.local.ResourceKeys;
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

    VxmlPrompt prePrompt = createPrompt(getString(ResourceKeys.RECORD_PROMPT_INSTRUCTIONS));
     String preGrammar = VxmlUtils.createGrammar(new String[]{"1", "3"}, 
        new String[]{"RECORD", VoiceAction.NEXT_PROMPT.name()});
    String preFilled = 
      "<if cond=\"action=='RECORD'\">" + 
      VxmlUtils.createLocalGoto("main") +
      "<else/>" +
      VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action") + 
      "</if>\n";
    VxmlField preField = createField("action", prePrompt, preGrammar, preFilled);
    VxmlForm preForm = new VxmlForm("action", preField);
    
    VxmlSection recordSection = new VxmlSection(
      "<record name=\"answer\" beep=\"true\" dtmfterm=\"true\" maxtime=\"120s\" type=\"audio/x-wav\">\n" +
      "<filled>\n" + 
      "</filled>\n" + 
      "</record>\n");
    
    VxmlPrompt p2 = createPrompt(new String[]{
        getString(ResourceKeys.ANSWER_CONFIRMATION_VOICE), 
        "<value expr=\"answer\"/>",
        getString(ResourceKeys.ANSWER_CONFIRMATION_OPTIONS)},
        new String[]{
        getString(ResourceKeys.ANSWER_CONFIRMATION_VOICE), 
        null, // notice that the recorded audio for the answer is null, because we want it to play the answer
        getString(ResourceKeys.ANSWER_CONFIRMATION_OPTIONS)});
    
    
    VxmlField actionField = createField("action", p2, actionGrammar, actionFilled(true));
    
    VxmlForm mainForm = new VxmlForm("main", recordSection, actionField);
    if (prompt == null) {
      new VxmlDocument(sessionid).write(out);
    } else {
      new VxmlDocument(sessionid, preForm, mainForm).write(out);
    }
  }
}
