package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.IntegerData;
import org.odk.voice.constants.FormAttribute;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public class IntegerWidget extends QuestionWidget {
  
  public IntegerWidget(PromptElement p) {
    super(p);
  }
  
  public void getPromptVxml(Writer out) throws IOException{

    final String intGrammar = "<grammar src=\"builtin:dtmf/number\"/>";
    
      VxmlField answerField = createField("answer", 
          createPrompt(prompt.getQuestionText(), getString(ResourceKeys.INT_INSTRUCTIONS),
              ( prompt.getAttribute(FormAttribute.REPEAT_QUESTION_OPTION, true) ? 
                  getString(ResourceKeys.PRESS_STAR_TO_REPEAT) : "")
                  ),
          intGrammar,
          createPrompt(new String[]{getString(ResourceKeys.ANSWER_CONFIRMATION_KEYPAD), "<value expr=\"answer\"/>"}, 
              new String[]{getString(ResourceKeys.ANSWER_CONFIRMATION_KEYPAD), null}).toString()
      );
      
      VxmlForm mainForm = new VxmlForm("main", answerField, getActionField(
          !prompt.getAttribute(FormAttribute.SKIP_CONFIRMATION, true), false));
      
      VxmlDocument d = new VxmlDocument(sessionid, questionCountForm, mainForm);
      d.write(out);
  }
    
  @Override
  public IAnswerData getAnswer(String stringData, MultiPartFormData binaryData)
      throws IllegalArgumentException {
    if (stringData == null)
      return null;
    try {
      return new IntegerData(Integer.parseInt(stringData));
    } catch (Exception NumberFormatException) {
      throw new IllegalArgumentException(stringData + " is not a number.");
    }
  }

}
