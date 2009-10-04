package org.odk.voice.widgets;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.util.OrderedHashtable;
import org.odk.voice.constants.StringConstants;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlPrompt;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public class SelectOneWidget implements IQuestionWidget {
  
  private final PromptElement prompt;
  
  public SelectOneWidget(PromptElement prompt) {
    this.prompt = prompt;
  }
  
  @Override
  public String[] getPromptStrings() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getPromptVxml() {
    StringWriter sw = new StringWriter(); //we need to change getPromptVxml interface to use a writer natively
    getPromptVxml(sw);
    return sw.toString();
  }
  
  public void getPromptVxml(Writer out) {
    List<String> promptSegments;
    Map<String, String> grammarMap = new HashMap<String,String>();
    promptSegments.add(prompt.getQuestionText()));
    promptSegments.add(StringConstants.select1Instructions);
    
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
          grammarMap.put(Integer.toString(i), "out.itemLabel=\"" + itemLabel + "\"; out.itemValue=\"" + itemValue + "\";");
          i++;
      }
      
      VxmlField answer = new VxmlField("answer", promptSegments, VxmlUtils.createGrammar(grammarMap));
      answer.setGrammar(VxmlUtils.createGrammar(grammarMap);
      VxmlPrompt confirmationPrompt = new VxmlPrompt(new String[]{
          StringConstants.answerConfirmationKeypad,
          "<value expr=\"answerLabel\"/>",
          StringConstants.answerConfirmationOptions}));
      VxmlField confirm = new VxmlField("confirm", confirmationPrompt);
      confirm.
      VxmlDocument d = new VxmlDocument();
  }

  private String createAnswerGrammar() {
    Map<String, String> grammarMap = new HashMap<String,String>();
    
  }
    
  @Override
  public IAnswerData getAnswer(String stringData, InputStream binaryData)
      throws IllegalArgumentException {
    // TODO Auto-generated method stub
    return null;
  }

}
