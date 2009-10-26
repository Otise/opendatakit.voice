package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.SortedSet;
import java.util.Vector;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.SelectMultiData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.core.util.OrderedHashtable;
import org.odk.voice.constants.StringConstants;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public class SelectMultiWidget extends QuestionWidget {
  
  private static final String ANSWER_SEPARATOR = "@@@";

  public SelectMultiWidget(PromptElement p) {
    super(p);
  }
  

      
  public void getPromptVxml(Writer out) throws IOException{
    List<VxmlSection> sections = new ArrayList<VxmlSection>();
    String confirmPrompts = "";
    String concatScript = "";
      
    VxmlSection pre = new VxmlSection("<block>" + 
        createPrompt(prompt.getQuestionText(), StringConstants.selectInstructions).getPromptString() +
        "</block>");
    sections.add(pre);
    
    if (prompt.getSelectItems() != null) {
      OrderedHashtable h = prompt.getSelectItems();
      Enumeration items = h.keys();
      while (items.hasMoreElements()) {
        String itemLabel = (String) items.nextElement();
        String itemValue = (String) h.get(itemLabel);
        VxmlField f = new VxmlField(itemValue,
            createPrompt(itemLabel),
            VxmlUtils.createGrammar(new String[]{"1","2"} ,
                new String[]{"out." + itemValue + " = \"true\";", "out." + itemValue + " = \"false\";"}),
            "");
        sections.add(f);
        confirmPrompts += "<prompt cond=\"" + itemValue + "=='true'\">" + VxmlUtils.getAudio(itemLabel) + "</prompt>";
        concatScript += "if (" + itemValue + " == 'true') answer = answer + '" + itemValue + "' + '" + ANSWER_SEPARATOR + "';\n";
      }
    }
    addPromptString(StringConstants.answerConfirmationKeypad);
    VxmlSection repeat = new VxmlSection("<block>" + VxmlUtils.getAudio(StringConstants.answerConfirmationKeypad) + 
        confirmPrompts + "</block>");
    String concat = "<script>var answer = '';\n" + concatScript + "</script>\n";
    sections.add(repeat);
    VxmlField actionField = new VxmlField("action",createPrompt(StringConstants.answerConfirmationOptions), 
        actionGrammar, concat + actionFilled(false));
    sections.add(actionField);
    VxmlForm mainForm = new VxmlForm("main", sections.toArray(new VxmlSection[]{}));
      
    VxmlDocument d = new VxmlDocument(questionCountForm, mainForm);
    d.write(out);
  }

  @Override
  public IAnswerData getAnswer(String stringData, MultiPartFormData binaryData)
      throws IllegalArgumentException {
    Vector<Selection> ve = new Vector<Selection>();
    String[] split = stringData.split(ANSWER_SEPARATOR);

    for (int i = 0; i < split.length; i++) {
      ve.add(new Selection(split[i]));
    }
    if (ve.size() == 0) {
        return null;
    } else {
      return new SelectMultiData(ve);
    }
  }

}
