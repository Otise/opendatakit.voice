package org.odk.voice.widgets;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.db.DbAdapter;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.storage.FileUtils;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.storage.MultiPartFormItem;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlPrompt;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public class AudioCaptureWidget extends QuestionWidget {
 
  public static final String AUDIO_EXTENSION = "wav";
  private static org.apache.log4j.Logger log = Logger
  .getLogger(AudioCaptureWidget.class);
  
  PromptElement prompt;
  int instanceId;
  
  public AudioCaptureWidget(PromptElement prompt, int instanceId) {
    super(prompt);
    this.prompt = prompt;
    this.instanceId = instanceId;
  }
  
  @Override
  public void getPromptVxml(Writer out) throws IOException {
    
    VxmlPrompt prePrompt = createPrompt(prompt.getQuestionText(), getString(ResourceKeys.AUDIO_INSTRUCTIONS));
     String preGrammar = VxmlUtils.createGrammar(new String[]{"1", "3"}, 
        new String[]{"RECORD", VoiceAction.NEXT_PROMPT.name()});
    String preFilled = 
      "<if cond=\"action=='RECORD'\">" + 
      VxmlUtils.createLocalGoto("main2") +
      "<else/>" +
      VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action") + 
      "</if>\n";
    VxmlForm preForm = new VxmlForm("main", new VxmlField("action", prePrompt, preGrammar, preFilled));
    
    VxmlSection recordSection = new VxmlSection(
      "<record name=\"answer\" beep=\"true\" dtmfterm=\"true\" type=\"audio/x-wav\">\n" +
      "<filled>\n" + 
      createPrompt(new String[]{getString(ResourceKeys.ANSWER_CONFIRMATION_VOICE), "<value expr=\"answer\"/>"},
        new String[]{getString(ResourceKeys.ANSWER_CONFIRMATION_VOICE), null}) + 
     // notice that the recorded audio for the answer is null, because we want it to play the answer
      "</filled>\n" + 
      "</record>\n");
    
    VxmlPrompt p2 = createPrompt(getString(ResourceKeys.ANSWER_CONFIRMATION_OPTIONS));
    
    
    VxmlField actionField = new VxmlField("action", p2, actionGrammar, actionFilled(true));
    
    VxmlForm mainForm = new VxmlForm("main2", recordSection, actionField);
    
    new VxmlDocument(sessionid, questionCountForm, preForm, mainForm).write(out);
  }

  @Override
  public IAnswerData getAnswer(String stringData, MultiPartFormData binaryData)
      throws IllegalArgumentException {
    MultiPartFormItem item = binaryData.getFormDataByFieldName("answer");
    if (item == null)
      return null;
    byte[] data = item.getData();
    String binaryName = prompt.getInstanceNode().getName();
    String mimeType = AUDIO_EXTENSION;
//    String path = FileUtils.getInstancePath(instanceId) + File.separator + filename;
//    log.info("Path for saving audio data: " + path);
//    try {
//      FileUtils.writeFile(data, path, true);
//    } catch (IOException e){
//      log.error("IOException writing audio data to " + path + ".", e);
//    }
    DbAdapter dba = null;
    try {
      dba = new DbAdapter();
      dba.addBinaryToInstance(instanceId, binaryName, mimeType, data);
    } catch (SQLException e) {
      
    } finally {
      dba.close();
    }
    return new StringData(binaryName);
  }
  
}
