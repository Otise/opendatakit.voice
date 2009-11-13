package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.IntegerData;
import org.javarosa.core.model.data.SelectOneData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.core.util.OrderedHashtable;
import org.odk.voice.constants.StringConstants;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public class InfoWidget extends QuestionWidget {
  
  public InfoWidget(PromptElement p) {
    super(p);
  }
  
  public void getPromptVxml(Writer out) throws IOException{

    String intGrammar = "<grammar src=\"builtin:dtmf/number\"/>";
    //String intGrammar = 
      
      VxmlField answerField = new VxmlField("answer", 
          createPrompt(prompt.getQuestionText(), StringConstants.intInstructions),
          intGrammar,
          createPrompt(
              new String[]{StringConstants.answerConfirmationKeypad, "<value expr=\"answer\"/>"},
              new String[]{StringConstants.answerConfirmationKeypad, null})
              .getPromptString()
      );
      
      VxmlForm mainForm = new VxmlForm("main", answerField, getActionField(false));
      
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
