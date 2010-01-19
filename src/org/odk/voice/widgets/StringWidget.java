package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.odk.voice.constants.GlobalConstants;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public class StringWidget extends QuestionWidget {
  
  private static org.apache.log4j.Logger log = Logger
  .getLogger(SelectOneWidget.class);
  
  public StringWidget(PromptElement p) {
    super(p);
  }
  
  
  public void getPromptVxml(Writer out) throws IOException{

    
    final String interdigitTimeout = "<property name=\"interdigettimeout\" value=\"" + 
            GlobalConstants.INTERDIGIT_TIMEOUT + "\"/>"; 
    final String actionVar = VxmlUtils.createVar("action", VoiceAction.GET_STRING_MATCHES.name(), true);
    
    VxmlSection propSection = new VxmlSection(interdigitTimeout + actionVar);
    final String digitGrammar = "<grammar src=\"builtin:dtmf/digits\"/>";
    VxmlField answerField = new VxmlField("answer", 
        createPrompt(prompt.getQuestionText(), 
            getString(ResourceKeys.STRING_INSTRUCTIONS)),
            digitGrammar,
            VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "answer", "action")
      );
      
//      String sms = StringMatcherServlet.ADDR;
//      String getMatches = 
//      		"<block><data name=\"" + StringMatcherServlet.ADDR + 
//      		"\" namelist=\"sessionid answerDigits\" method=\"get\"/>" +
//      		"<assign name=\"document.matches\" expr=\"" + StringMatcherServlet.ADDR + 
//      		"\".documentElement\"/>";
      
      VxmlForm mainForm = new VxmlForm("main", propSection, answerField);
      
      VxmlDocument d = new VxmlDocument(sessionid, questionCountForm, mainForm);
      
      // for the confirmation section //
      addPromptString(getString(ResourceKeys.STRING_CONFIRM_INSTRUCTIONS));
      addPromptString(getString(ResourceKeys.STRING_CONFIRM_ITEM));
      addPromptString(getString(ResourceKeys.STRING_CONFIRM_NO_MORE_MATCHES));
      //TODO(alerer): allow opportunity to record corpus?
      //////////////////////////////////
      d.write(out);
  }
  
  /**
   * This is the VXML document that should be rendered after the user has 
   * answered a string question and the string matcher has returned a list 
   * of possible matches for the user to choose from.
   * 
   * @param out
   * @param stringMatches
   * @throws IOException
   */
  public void getConfirmationVxml(Writer out, String[] stringMatches) throws IOException {
    List<VxmlForm> forms = new ArrayList<VxmlForm>();
    
    for (int i = 0; i < stringMatches.length; i++) {
      String match = stringMatches[i];
      VxmlSection s = new VxmlSection(VxmlUtils.createVar("answer", match, true));
      VxmlField f = new VxmlField("action",
          createPrompt(
              getString(ResourceKeys.STRING_CONFIRM_ITEM),
              match,
              getString(ResourceKeys.STRING_CONFIRM_INSTRUCTIONS)),
          VxmlUtils.createGrammar(new String[]{"1","2","3","4"} ,
              new String[]{"'SAVE_ANSWER'", "'NEXT_MATCH'","'CURRENT_PROMPT'","'NEXT_PROMPT'"}),
          "<if cond=\"action=='NEXT_MATCH'\">" + VxmlUtils.createLocalGoto("match" + (i+1)) + 
          "<else/> " + actionFilled(false) + "</if>");
      VxmlForm form = new VxmlForm("match" + i, s, f);
      forms.add(form);
    }
    
    VxmlForm noMoreMatches = new VxmlForm("match" + stringMatches.length,
        new VxmlSection("<block>" + 
        VxmlUtils.createVar("action", VoiceAction.NEXT_PROMPT.name(), true) +
        createPrompt(getString(ResourceKeys.STRING_CONFIRM_NO_MORE_MATCHES)) +
        VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action") + 
        "</block>"));
    forms.add(noMoreMatches);
    
    VxmlDocument d = new VxmlDocument(sessionid, forms.toArray(new VxmlForm[]{}));
    d.write(out);
  }
    
  @Override
  public IAnswerData getAnswer(String stringData, MultiPartFormData binaryData)
      throws IllegalArgumentException {
    if (stringData == null || stringData.equals(""))
      return null;
    return new StringData(stringData);
  }

}
