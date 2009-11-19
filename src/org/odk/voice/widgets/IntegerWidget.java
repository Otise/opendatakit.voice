package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.IntegerData;
import org.odk.voice.constants.StringConstants;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.xform.PromptElement;

public class IntegerWidget extends QuestionWidget {
  
  public IntegerWidget(PromptElement p) {
    super(p);
  }
  
  public void getPromptVxml(Writer out) throws IOException{

    final String intGrammar = "<grammar src=\"builtin:dtmf/number\"/>";
    
    final String sayasDigitsScript = "<script><![CDATA[" + 
          "function sayasDigits(number){" + 
            "var digitNumber = number.charAt(0);" + 
            "for(var i = 1; i < number.length; i++)" + 
            "{digitNumber += ' ' + number.charAt(i);}" +
            "return digitNumber;" + 
          "}]]></script>";
      
      VxmlSection digitsSection = new VxmlSection(sayasDigitsScript);
      VxmlField answerField = new VxmlField("answer", 
          createPrompt(prompt.getQuestionText(), StringConstants.intInstructions),
          intGrammar,
          createPrompt(
              new String[]{StringConstants.answerConfirmationKeypad, "<value expr=\"sayasDigits(answer)\"/>"},
              new String[]{StringConstants.answerConfirmationKeypad, null})
              .getPromptString()
      );
      
      VxmlForm mainForm = new VxmlForm("main", digitsSection, answerField, getActionField(false));
      
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
