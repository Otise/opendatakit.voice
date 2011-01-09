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
      // except that one of the prompts is different.
      boolean confirm = !prompt.getAttribute(FormAttribute.SKIP_CONFIRMATION, true);
      VxmlSection actionField;
      if (confirm) {
        actionField = createField("action", 
        createPrompt(getString(ResourceKeys.INFO_CONFIRMATION)),
        actionGrammar,
        actionFilled(false));
      } else {
        actionField = new VxmlSection(
            "<block>" + 
            VxmlUtils.createVar("action", VoiceAction.SAVE_ANSWER.name(), true) + 
            VxmlUtils.createSubmit(FormVxmlServlet.ADDR, new String[]{"action", "answer"}) + 
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
