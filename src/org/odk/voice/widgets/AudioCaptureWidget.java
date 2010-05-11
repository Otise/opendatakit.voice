package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.odk.voice.constants.FormAttribute;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.db.DbAdapter;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.storage.MultiPartFormItem;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlPrompt;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public class AudioCaptureWidget extends QuestionWidget {
 
  public static final String AUDIO_EXTENSION = "audio/wav";
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
    
/*    VxmlPrompt prePrompt = createPrompt(prompt.getQuestionText(), getString(ResourceKeys.AUDIO_INSTRUCTIONS));
     String preGrammar = VxmlUtils.createGrammar(new String[]{"1", "3"}, 
        new String[]{"RECORD", VoiceAction.NEXT_PROMPT.name()});
    String preFilled = 
      "<if cond=\"action=='RECORD'\">" + 
      VxmlUtils.createLocalGoto("main2") +
      "<else/>" +
      VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action") + 
      "</if>\n";
    VxmlForm preForm = new VxmlForm("main", createField("action", prePrompt, preGrammar, preFilled));
*/   
    String maxtime = prompt.getAttribute(FormAttribute.AUDIO_MAX_TIME);
    maxtime = (maxtime == null) ? "120s" : maxtime;
    
    log.info("Before skipInstructions");
    String skipInstructions = prompt.getAttribute(FormAttribute.AUDIO_SKIP_INSTRUCTIONS);
    log.info("skipInstructions=" + skipInstructions);
    VxmlSection recordSection = new VxmlSection(
      "<property name=\"com.voxeo.prophecy.CaptureOnSpeech\" value=\"false\"/>" + 
      "<record name=\"answer\" beep=\"true\" dtmfterm=\"true\" maxtime=\"" + maxtime +"\" finalsilence=\"4s\" type=\"audio/x-wav\">\n" +
      createPrompt(prompt.getQuestionText(),
                      ( prompt.getAttribute(FormAttribute.AUDIO_SKIP_INSTRUCTIONS, true) ? 
                            "" :  getString(ResourceKeys.AUDIO_INSTRUCTIONS)), 
                      ( prompt.getAttribute(FormAttribute.REPEAT_QUESTION_OPTION, true) ? 
                          getString(ResourceKeys.PRESS_STAR_TO_REPEAT) : "")
                          ) +
                      
      "<filled>\n" + 
      "<if cond=\"answer$.termchar == '*'\">" + VxmlUtils.createLocalGoto("main") + 
      // uncomment to repeat their answer
      /*"<else/>\n" + 
      createPrompt(new String[]{getString(ResourceKeys.ANSWER_CONFIRMATION_VOICE), "<value expr=\"answer\"/>"},
        new String[]{getString(ResourceKeys.ANSWER_CONFIRMATION_VOICE), null}) +  */
     // notice that the recorded audio for the answer is null, because we want it to play the answer
      "</if></filled>\n" + 
      "<noinput><reprompt/></noinput>" + 
      "<noinput count=\"3\">" + createPrompt(getString(ResourceKeys.NO_INPUT_3)) + 
      VxmlUtils.createVar("action", VoiceAction.NO_RESPONSE.name(), true) +
      VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action") + "</noinput><nomatch><prompt>NOMATCH</prompt></nomatch>" + 
      "</record>");
    
//    VxmlForm exitForm = new VxmlForm("exit", new VxmlSection("<block>" + 
//        createPrompt(getString(ResourceKeys.NO_INPUT_3)) + "</block>"));
    
    VxmlPrompt p2 = createPrompt(
        getString(ResourceKeys.ANSWER_CONFIRMATION_OPTIONS));
    
    VxmlSection actionSection = prompt.getAttribute(FormAttribute.SKIP_CONFIRMATION, true) ?
        new VxmlSection("<block>" + 
            VxmlUtils.createVar("action",VoiceAction.SAVE_ANSWER.name(), true) +
            actionFilled(true) + "</block>") :
        createField("action", p2, actionGrammar, actionFilled(true));
    
    VxmlForm mainForm = new VxmlForm("main", recordSection, actionSection);
    
    new VxmlDocument(sessionid, questionCountForm, /*preForm,*/ mainForm).write(out);
  }

  @Override
  public IAnswerData getAnswer(String stringData, MultiPartFormData binaryData)
      throws IllegalArgumentException {
    MultiPartFormItem item = binaryData.getFormDataByFieldName("answer");
    if (item == null)
      return null;
    byte[] data = item.getData();
    String binaryName = prompt.getInstanceNode().getName() + ".wav";
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
