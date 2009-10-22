package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.javarosa.core.model.data.DateData;
import org.javarosa.core.model.data.IAnswerData;
import org.odk.voice.constants.StringConstants;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.xform.PromptElement;

public class DateWidget extends QuestionWidget {
  
  public final static String DATE_SEPARATOR = "/";
  
  public DateWidget(PromptElement p) {
    super(p);
  }
  
  public void getPromptVxml(Writer out) throws IOException{

    final String yearGrammar = "<grammar src=\"builtin:dtmf/digits?length=4\"/>";
    final String monthGrammar = "<grammar src=\"builtin:dtmf/digits?minlength=1;maxlength=2\"/>";
    final String dayGrammar = "<grammar src=\"builtin:dtmf/digits?minlength=1;maxlength=2\"/>";
    
    VxmlField yearField = new VxmlField("year", 
          createPrompt(prompt.getQuestionText(), StringConstants.dateInstructionsYear),
          yearGrammar,
          createPrompt(
              new String[]{StringConstants.answerConfirmationKeypad, "<value expr=\"year\"/>"},
              new String[]{StringConstants.answerConfirmationKeypad, null})
              .getPromptString()
      );
      
      VxmlField monthField = new VxmlField("month", 
          createPrompt(StringConstants.dateInstructionsMonth),
          monthGrammar,
          createPrompt(
              new String[]{StringConstants.answerConfirmationKeypad, "<value expr=\"month\"/>"},
              new String[]{StringConstants.answerConfirmationKeypad, null})
              .getPromptString()
      );
      
      VxmlField dateField = new VxmlField("date", 
          createPrompt(StringConstants.dateInstructionsDay),
          dayGrammar,
          createPrompt(
              new String[]{StringConstants.answerConfirmationKeypad, "<value expr=\"date\"/>"},
              new String[]{StringConstants.answerConfirmationKeypad, null})
              .getPromptString()
      );
      
      String concatAnswer = //"<var name=\"answer\" expr=\"year.toString() + '/' + month.toString() + '/' date.toString()\"/>";
        "<script>var answer = year + '/' + month + '/' + date</script>;";

        
      VxmlField actionField = new VxmlField("action", 
          createPrompt(StringConstants.answerConfirmationOptions), 
          actionGrammar, 
          concatAnswer + actionFilled(false));
      
      VxmlForm mainForm = new VxmlForm("main", yearField, monthField, dateField, actionField);
      
      VxmlDocument d = new VxmlDocument(questionCountForm, mainForm);
      d.write(out);
  }
    
  @Override
  public IAnswerData getAnswer(String stringData, MultiPartFormData binaryData)
      throws IllegalArgumentException {
    if (stringData == null)
      return null;
    String[] split = stringData.split(DATE_SEPARATOR);
    if (split.length != 3){
      throw new IllegalArgumentException(stringData + " was not of the form x/y/z");
    }
    try {
      int year = Integer.parseInt(split[0]);
      int month = Integer.parseInt(split[1]);
      int date = Integer.parseInt(split[2]);
      Date d =
        new Date(year - 1900, month - 1, date);
      return new DateData(d);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("One of the parts of " + stringData + " was not a number.");
    }
    
  }

}
