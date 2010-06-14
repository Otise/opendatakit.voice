package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.odk.voice.constants.GlobalConstants;
import org.odk.voice.constants.FormAttribute;
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

/**
 * This widget renders string XForms controls. This widget is coupled to {@link FormVxmlRenderer} 
 * in between getPromptVxml and getConfirmationVxml, the FormVxmlRenderer does a bunch of work using 
 * the digits2string package.
 * 
 * @author alerer
 *
 */
public class StringWidget extends QuestionWidget {
  
  private static org.apache.log4j.Logger log = Logger
  .getLogger(StringWidget.class);
  
  public StringWidget(PromptElement p) {
    super(p);
  }
  
  public void getPromptVxml(Writer out) throws IOException{

    final String interdigitTimeout = "<property name=\"interdigettimeout\" value=\"" + 
            GlobalConstants.INTERDIGIT_TIMEOUT + "\"/>"; 
    final String actionVar = VxmlUtils.createVar("action", VoiceAction.GET_STRING_MATCHES.name(), true);
    
    VxmlSection propSection = new VxmlSection(interdigitTimeout + actionVar);
    final String digitGrammar = "<grammar src=\"builtin:dtmf/digits\"/>";
    VxmlField answerField = createField("answer", 
        createPrompt(prompt.getQuestionText(), 
            getString(ResourceKeys.STRING_INSTRUCTIONS),
            ( prompt.getAttribute(FormAttribute.REPEAT_QUESTION_OPTION, true) ? 
                getString(ResourceKeys.PRESS_STAR_TO_REPEAT) : "")
                ),
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
      VxmlField f = createField("action",
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
    
    VxmlField noMoreMatches = createField("action",
        createPrompt(getString(ResourceKeys.STRING_CONFIRM_NO_MORE_MATCHES)),
        VxmlUtils.createGrammar(new String[]{"1","2"} ,
            new String[]{"'NEXT_PROMPT'", "'CURRENT_PROMPT'"}),
        actionFilled(false));
    VxmlForm nmmForm = new VxmlForm("match" + stringMatches.length, noMoreMatches);
    forms.add(nmmForm);
    
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
