package org.odk.voice.widgets;

import java.io.IOException;
import java.io.StringWriter;

import org.javarosa.core.model.data.IAnswerData;
import org.odk.voice.constants.StringConstants;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public abstract class QuestionWidget extends WidgetBase{
  
  PromptElement prompt;
  int questionNum, totalNum;
  VxmlForm questionCountForm = null;
  
  public QuestionWidget(PromptElement p) {
    this.prompt = p;
  }
  
  public void setQuestionCount(int questionNum, int totalNum){
    this.questionNum = questionNum;
    this.totalNum = totalNum;
    this.questionCountForm= new VxmlForm("questionCount");
    this.questionCountForm.setContents("<block>" + 
        createPrompt(StringConstants.questionXOfY(questionNum, totalNum)) +
        VxmlUtils.createLocalGoto("main") +
        "</block>");
        
  }
  
  public String getPromptVxml() {
    StringWriter sw = new StringWriter(); //we need to change getPromptVxml interface to use a writer natively
    try{
      getPromptVxml(sw);
    } catch (IOException e) {
      return "IOException";
    }
    return sw.toString();
  }
  
  /**
   * Process the data returned from the vxml client to produce an IAnswerData object.
   * 
   * @param stringData The contents of the stringData parameter from the vxml client, or null if the parameter is not sent.
   * @param binaryData The contents of the request body from the vxml client, or null if there is no request body.
   * @return An IAnswerData object.
   * @throws IllegalArgumentException If the returned data is invalid.
   */
  public abstract IAnswerData getAnswer(String stringData, MultiPartFormData binaryData) throws IllegalArgumentException;
  
}
