package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.SelectMultiData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.core.util.OrderedHashtable;
import org.odk.voice.local.ResourceKeys;
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
        createPrompt(prompt.getQuestionText(), getString(ResourceKeys.SELECT_INSTRUCTIONS)) +
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
                new String[]{"'true'", "'false'"}),
            "");
        sections.add(f);
        confirmPrompts += "<prompt cond=\"" + itemValue + "=='true'\">" + VxmlUtils.getAudio(itemLabel) + "</prompt>";
        concatScript += "if (" + itemValue + " == 'true') answer = answer + '" + itemValue + "' + '" + ANSWER_SEPARATOR + "';\n";
      }
    }
    addPromptString(getString(ResourceKeys.ANSWER_CONFIRMATION_KEYPAD));
    addPromptString(getString(ResourceKeys.SELECT_NONE));
    String concat = "<script>function concat(){var answer = '';\n" + concatScript + " return answer;}</script>\n";
    //sections.add(concat);
    confirmPrompts += "<prompt cond=\"concat()==''\">" + VxmlUtils.getAudio(getString(ResourceKeys.SELECT_NONE)) + "</prompt>";
    //VxmlSection repeat = new VxmlSection("<block>" + VxmlUtils.getAudio(StringConstants.answerConfirmationKeypad) + 
    //    confirmPrompts + "</block>");
    //sections.add(repeat);
    
    VxmlSection confirmSection = new VxmlSection(concat + "<block>" + 
        createPrompt(getString(ResourceKeys.ANSWER_CONFIRMATION_KEYPAD))
        + confirmPrompts + "</block>");
    sections.add(confirmSection);
    VxmlField actionField = new VxmlField("action",createPrompt(getString(ResourceKeys.ANSWER_CONFIRMATION_OPTIONS)), 
        actionGrammar, VxmlUtils.createVar("answer", "concat()", false) + actionFilled(false));
    sections.add(actionField);
    VxmlForm mainForm = new VxmlForm("main", sections.toArray(new VxmlSection[]{}));
      
    VxmlDocument d = new VxmlDocument(sessionid, questionCountForm, mainForm);
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
