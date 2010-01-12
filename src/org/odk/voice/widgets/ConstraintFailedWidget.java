package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.constants.XFormConstants;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.PromptElement;

public class ConstraintFailedWidget extends WidgetBase {
 
  private static org.apache.log4j.Logger log = Logger
  .getLogger(ConstraintFailedWidget.class);
  
  private PromptElement prompt;
  private int saveStatus;
  
  public ConstraintFailedWidget(PromptElement p, int saveStatus) {
    this.prompt = p;
    this.saveStatus = saveStatus;
  }
  
  @Override
  public void getPromptVxml(Writer out) throws IOException {

    String constraintPrompt = null;
    switch(saveStatus){
    case XFormConstants.ANSWER_CONSTRAINT_VIOLATED:
      constraintPrompt = prompt.getConstraintText();
      break;
    case XFormConstants.ANSWER_INVALID:
      constraintPrompt = getString(ResourceKeys.ANSWER_INVALID);
    case XFormConstants.ANSWER_REQUIRED_BUT_EMPTY:
      constraintPrompt = getString(ResourceKeys.ANSWER_REQUIRED_BUT_EMPTY);
    default:
      log.error("Invalid saveStatus: " + saveStatus);
    }
    log.info("ConstraintPrompt: " + constraintPrompt);
    VxmlSection section = new VxmlSection("<block>" + 
        VxmlUtils.createVar("action",  VoiceAction.CURRENT_PROMPT.name(), true) +
    		createPrompt(constraintPrompt) + 
    		VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action")
    		+ "</block>");
    VxmlForm mainForm = new VxmlForm("main", section);

    new VxmlDocument(sessionid, mainForm).write(out);
  }
}
