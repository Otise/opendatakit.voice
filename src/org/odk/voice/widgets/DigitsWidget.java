package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.odk.voice.constants.QuestionAttributes;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public class DigitsWidget extends QuestionWidget {
  
  public DigitsWidget(PromptElement p) {
    super(p);
  }
  
  public void getPromptVxml(Writer out) throws IOException{

    final String intGrammar = "<grammar src=\"builtin:dtmf/number\"/>";
    
    String digitsReader = "<foreach item=\"digit\" array=\"digitsArray(answer)\">";
    for (int i = 0; i <= 9; i++){
      addPromptString(String.valueOf(i));
      digitsReader += "<prompt cond=\"digit =='" + i + "'\">" + VxmlUtils.getAudio(String.valueOf(i)) + "</prompt>";
    }
    digitsReader += "</foreach>";
    
    final String sayasDigitsScript = "<script><![CDATA[" + 
          "function digitsArray(number){" + 
            "var array=new Array();" +
            "for(var i = 0; i < number.length; i++)" + 
            "{array[i] = number.charAt(i);}" +
            "return array;" + 
          "}]]></script>";
      
    
      VxmlSection digitsSection = new VxmlSection(sayasDigitsScript);
      VxmlField answerField = createField("answer", 
          createPrompt(prompt.getQuestionText(), getString(ResourceKeys.INT_INSTRUCTIONS),
              ( prompt.getAttribute(QuestionAttributes.REPEAT_QUESTION_OPTION, true) ? 
                  getString(ResourceKeys.PRESS_STAR_TO_REPEAT) : "") 
                  ),
          intGrammar,
          createPrompt(getString(ResourceKeys.ANSWER_CONFIRMATION_KEYPAD)) +
          digitsReader
      );
      
      VxmlForm mainForm = new VxmlForm("main", digitsSection, answerField, getActionField(
          !prompt.getAttribute(QuestionAttributes.SKIP_CONFIRMATION, true), false));
      
      VxmlDocument d = new VxmlDocument(sessionid, questionCountForm, mainForm);
      d.write(out);
  }
    
  @Override
  public IAnswerData getAnswer(String stringData, MultiPartFormData binaryData)
      throws IllegalArgumentException {
    if (stringData == null)
      return null;
    return new StringData(stringData);
  }

}
