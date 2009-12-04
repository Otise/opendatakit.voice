package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.SelectOneData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.core.util.OrderedHashtable;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public class SelectOneWidget extends QuestionWidget {
  
  private static org.apache.log4j.Logger log = Logger
  .getLogger(SelectOneWidget.class);
  
  public SelectOneWidget(PromptElement p) {
    super(p);
  }
  
  public void getPromptVxml(Writer out) throws IOException{
    List<String> promptSegments = new ArrayList<String>();
    List<String> grammarKeys = new ArrayList<String>();
    List<String> grammarTags = new ArrayList<String>();
    promptSegments.add(prompt.getQuestionText());
    promptSegments.add(getString(ResourceKeys.SELECT_1_INSTRUCTIONS));
    
    StringBuilder confPrompt = new StringBuilder();
    

    if (prompt.getSelectItems() != null) {
      OrderedHashtable h = prompt.getSelectItems();
      Enumeration items = h.keys();
      String itemLabel = null;
      String itemValue = null;
      
      addPromptString(getString(ResourceKeys.ANSWER_CONFIRMATION_KEYPAD));
      confPrompt.append(VxmlUtils.getAudio(getString(ResourceKeys.ANSWER_CONFIRMATION_KEYPAD)));
      int i = 1;
      while (items.hasMoreElements()) {
          if (i > 9) {
            log.warn("ODK Voice cannot handle more than 9 elements in a select1 control.");
            break;
          }
          itemLabel = (String) items.nextElement();
          itemValue = (String) h.get(itemLabel);
          promptSegments.add(String.format(getString(ResourceKeys.SELECT_1_PRESS),i));
          promptSegments.add(itemLabel);
          grammarKeys.add(Integer.toString(i));
          grammarTags.add(itemValue);
          
          confPrompt.append("<" + (i==1?"if":"elseif") + " cond=\"answer=='" + itemValue + "'\"" + (i==1?"":"/") + ">\n");
          confPrompt.append(VxmlUtils.getAudio(itemLabel));
          // addPromptString(itemLabel);
          
          i++;
      }
      //addConfAudio(StringConstants.answerConfirmationOptions, confPrompt, confPromptStrings);
      confPrompt.append("</if>");
      VxmlField answerField = new VxmlField("answer", 
          createPrompt(promptSegments.toArray(new String[]{})), 
          VxmlUtils.createGrammar(grammarKeys.toArray(new String[]{}), grammarTags.toArray(new String[]{})),
          createBasicPrompt(confPrompt.toString()).toString());
      
//      
//      VxmlField actionField = new VxmlField("action", 
//          createPrompt(StringConstants.answerConfirmationOptions),
//          VxmlUtils.actionGrammar,
//          VxmlUtils.actionFilled(this));
      
      VxmlForm mainForm = new VxmlForm("main", answerField, getActionField(false));
      
      VxmlDocument d = new VxmlDocument(sessionid, questionCountForm, mainForm);
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
