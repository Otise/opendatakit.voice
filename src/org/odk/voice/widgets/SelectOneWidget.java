package org.odk.voice.widgets;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.util.OrderedHashtable;
import org.odk.voice.constants.StringConstants;
import org.odk.voice.vxml.VxmlArrayPrompt;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlPrompt;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public class SelectOneWidget extends WidgetBase {
  
  private final PromptElement prompt;
  
  public SelectOneWidget(PromptElement prompt) {
    this.prompt = prompt;
  }
  
  @Override
  public String[] getPromptStrings() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public void getPromptVxml(Writer out) throws IOException{
    List<String> promptSegments = new ArrayList<String>();
    List<String> grammarKeys = new ArrayList<String>();
    List<String> grammarTags = new ArrayList<String>();
    promptSegments.add(prompt.getQuestionText());
    promptSegments.add(StringConstants.select1Instructions);
    
    StringBuilder confPrompt = new StringBuilder("<prompt>\n");
    confPrompt.append(VxmlUtils.getAudio(StringConstants.answerConfirmationKeypad));
    
    if (prompt.getSelectItems() != null) {
      OrderedHashtable h = prompt.getSelectItems();
      Enumeration items = h.keys();
      String itemLabel = null;
      String itemValue = null;
      
      int i = 1;

      while (items.hasMoreElements()) {
          itemLabel = (String) items.nextElement();
          itemValue = (String) h.get(itemLabel);
          promptSegments.add(StringConstants.select1Press(i));
          grammarKeys.add(Integer.toString(i));
          grammarTags.add("out.label=\"" + itemLabel + "\"; out.answer=\"" + itemValue + "\";");
          
          confPrompt.append("<" + (i==1?"if":"elseif") + " expr=\"answer=='" + itemValue + "'\"" + (i==1?"":"/") + ">\n");
          confPrompt.append(VxmlUtils.getAudio(itemLabel));
          
          i++;
      }
      confPrompt.append("</if>\n");
      confPrompt.append(VxmlUtils.getAudio(StringConstants.answerConfirmationOptions));
      confPrompt.append("</prompt>");
      
      VxmlForm answerForm = new VxmlForm("answer", 
          new VxmlArrayPrompt(promptSegments.toArray(new String[]{})), 
          VxmlUtils.createGrammar(grammarKeys.toArray(new String[]{}), grammarTags.toArray(new String[]{})),
          VxmlUtils.createGoto("#confirm")
          );
      
      VxmlForm confirmForm = new VxmlForm("confirm", 
          new VxmlPrompt(confPrompt.toString()), 
          VxmlUtils.confirmGrammar,
          VxmlUtils.confirmFilled);
      
      VxmlDocument d = new VxmlDocument(answerForm, confirmForm);
      d.write(out);
    }
  }
    
  @Override
  public IAnswerData getAnswer(String stringData, InputStream binaryData)
      throws IllegalArgumentException {
    // TODO Auto-generated method stub
    return null;
  }

}
