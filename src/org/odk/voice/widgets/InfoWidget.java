package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.javarosa.core.model.data.IAnswerData;
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
 * Widget for read-only elements.
 * @author alerer
 *
 */
public class InfoWidget extends QuestionWidget {
  
  PromptElement p;
  
  public InfoWidget(PromptElement p) {
    super(p);
  }
  
  public void getPromptVxml(Writer out) throws IOException{
      
      VxmlSection infoSection = new VxmlSection("<block>" + 
      		 createCompositePrompt(
      				 createPrompt(prompt.getQuestionText()), 
               createPrompt(true, prompt.getAnswerText())) +
          "</block>");
      
      // almost a replica of WidgetBase.getActionField,
      // except that some things are different :).
      boolean skip_conf = prompt.getAttribute(FormAttribute.SKIP_CONFIRMATION, true);
      String submit = VxmlUtils.createVar("action", VoiceAction.NEXT_PROMPT.name(), true) +
      						    VxmlUtils.createSubmit(FormVxmlServlet.ADDR, new String[]{"action"});
      
      VxmlSection actionField;
      if (!skip_conf) {
      	String myActionFilled = "<if cond=\"action=='REPEAT'\">" + 
        "<clear namelist=\"action\"/>" +
        VxmlUtils.createLocalGoto("main") + "<else/>" + 
        createPrompt(getString(ResourceKeys.THANK_YOU))  +
        submit + 
        "</if>\n";
      	
        actionField = createField("action", 
        createPrompt(getString(ResourceKeys.INFO_CONFIRMATION)),
        actionGrammar,
        myActionFilled);
      } else {
        actionField = new VxmlSection(
            "<block>" + 
            submit + 
            "</block>");
      }

      VxmlForm mainForm = new VxmlForm("main", infoSection, actionField);
      
      VxmlDocument d = new VxmlDocument(sessionid, mainForm);
      d.write(out);
  }
    
  @Override
  public IAnswerData getAnswer(String stringData, MultiPartFormData binaryData)
      throws IllegalArgumentException {
    return null;
  }

}
