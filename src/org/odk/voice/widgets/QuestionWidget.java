package org.odk.voice.widgets;

import java.io.IOException;
import java.io.StringWriter;

import org.javarosa.core.model.data.IAnswerData;
import org.odk.voice.constants.QuestionAttributes;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public abstract class QuestionWidget extends WidgetBase{
  
  PromptElement prompt;
  int questionNum, totalNum;
  VxmlForm questionCountForm = null;
  
  public QuestionWidget(PromptElement p) {
    super();
    this.prompt = p;
    try {
      if (p.getConstraintText() != null)
        addPromptString(p.getConstraintText());
    } catch (NullPointerException e){
      //unfortunately, if there is no constraint, it throws nullpointer
    }
    if (p.isRequired())
      addPromptString(getString(ResourceKeys.ANSWER_REQUIRED_BUT_EMPTY));
  }
  
  /**
   * 
   * @param questionNum The question number of the current question.
   * @param totalNum The total number of questions in the form, or -1 if 
   * there is not a fixed number of questions (e.g. if there are branches).
   */
  public void setQuestionCount(int questionNum, int totalNum){
    this.questionNum = questionNum;
    this.totalNum = totalNum;
    this.questionCountForm = new VxmlForm("questionCount");

    String skipQuestionCount = prompt.getAttribute(QuestionAttributes.SKIP_QUESTION_COUNT);
      this.questionCountForm.setContents("<block>" + 
          (skipQuestionCount==null || !skipQuestionCount.equals("true") ?
              createPrompt(this.totalNum > 0 ?
              String.format(getString(ResourceKeys.QUESTION_X_OF_Y) ,questionNum, totalNum) :
              String.format(getString(ResourceKeys.QUESTION_X) ,questionNum)):"")+
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
