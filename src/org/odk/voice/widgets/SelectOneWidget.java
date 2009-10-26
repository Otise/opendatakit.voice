package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.javarosa.core.model.data.IAnswerData;
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

public class SelectOneWidget extends QuestionWidget {
  
  public SelectOneWidget(PromptElement p) {
    super(p);
  }
  
//  @Override
//  public String[] getPromptStrings() {
//    List<String> ps = new ArrayList<String>();
//    ps.add(StringConstants.questionXOfY(questionNum, totalNum));
//    ps.add(StringConstants.select1Instructions);
//    if (prompt.getSelectItems()!=null) {
//      OrderedHashtable h = prompt.getSelectItems();
//      Enumeration items = h.keys();
//      int i = 1;
//      while (items.hasMoreElements()) {
//          ps.add(StringConstants.select1Press(i));
//          ps.add((String) items.nextElement());
//          i++;
//      }
//    }
//    ps.add(StringConstants.answerConfirmationKeypad);
//    ps.add(StringConstants.answerConfirmationOptions);
//    ps.add(StringConstants.thankYou);
//    return ps.toArray(new String[]{});
//  }
  
  public void getPromptVxml(Writer out) throws IOException{
    List<String> promptSegments = new ArrayList<String>();
    List<String> grammarKeys = new ArrayList<String>();
    List<String> grammarTags = new ArrayList<String>();
    promptSegments.add(prompt.getQuestionText());
    promptSegments.add(StringConstants.select1Instructions);
    
    StringBuilder confPrompt = new StringBuilder();
    
    //addConfAudio(StringConstants.answerConfirmationKeypad, confPrompt, confPromptStrings);
    addPromptString(StringConstants.answerConfirmationKeypad);
    if (prompt.getSelectItems() != null) {
      OrderedHashtable h = prompt.getSelectItems();
      Enumeration items = h.keys();
      String itemLabel = null;
      String itemValue = null;
      
      int i = 1;
      confPrompt.append(VxmlUtils.getAudio(StringConstants.answerConfirmationKeypad));
      while (items.hasMoreElements()) {
          itemLabel = (String) items.nextElement();
          itemValue = (String) h.get(itemLabel);
          promptSegments.add(StringConstants.select1Press(i));
          promptSegments.add(itemLabel);
          grammarKeys.add(Integer.toString(i));
          grammarTags.add("out.answer=\"" + itemValue + "\";");
          
          confPrompt.append("<" + (i==1?"if":"elseif") + " cond=\"answer=='" + itemValue + "'\"" + (i==1?"":"/") + ">\n");
          confPrompt.append(VxmlUtils.getAudio(itemLabel));
          addPromptString(itemLabel);
          
          i++;
      }
      //addConfAudio(StringConstants.answerConfirmationOptions, confPrompt, confPromptStrings);
      confPrompt.append("</if>");
      VxmlField answerField = new VxmlField("answer", 
          createPrompt(promptSegments.toArray(new String[]{})), 
          VxmlUtils.createGrammar(grammarKeys.toArray(new String[]{}), grammarTags.toArray(new String[]{})),
          createBasicPrompt(confPrompt.toString()).getPromptString());
      
//      
//      VxmlField actionField = new VxmlField("action", 
//          createPrompt(StringConstants.answerConfirmationOptions),
//          VxmlUtils.actionGrammar,
//          VxmlUtils.actionFilled(this));
      
      VxmlForm mainForm = new VxmlForm("main", answerField, getActionField(false));
      
      VxmlDocument d = new VxmlDocument(questionCountForm, mainForm);
      d.write(out);
    }
  }
    
  @Override
  public IAnswerData getAnswer(String stringData, MultiPartFormData binaryData)
      throws IllegalArgumentException {
    if (stringData == null)
      return null;
    return new SelectOneData(new Selection(stringData));
  }

}
