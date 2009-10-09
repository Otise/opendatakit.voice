package org.odk.voice.logic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.odk.voice.constants.FileConstants;
import org.odk.voice.constants.StringConstants;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.constants.VoiceError;
import org.odk.voice.constants.XFormConstants;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.session.VoiceSession;
import org.odk.voice.session.VoiceSessionManager;
import org.odk.voice.storage.FormLoader;
import org.odk.voice.utils.FileUtils;
import org.odk.voice.vxml.VxmlArrayPrompt;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.widgets.IQuestionWidget;
import org.odk.voice.widgets.WidgetFactory;
import org.odk.voice.xform.FormHandler;
import org.odk.voice.xform.PromptElement;

/**
 * Top-level class for rendering the VoiceXML UI for a form.
 * Note: This class may have to be broken up into several classes (one for rendering a particular form, one for admins, and one for 
 * meta-form actions like choosing a form.
 * @author alerer
 *
 */
public class FormVxmlRenderer {

  private static org.apache.log4j.Logger log = Logger
  .getLogger(FormVxmlRenderer.class);
  
  private Writer out;
  private VoiceSessionManager vsm;
  private VoiceSession vs;
  private FormHandler fh;
  private boolean evaluateConstraints = true;

  public FormVxmlRenderer(Writer out){
    this.out = out;
    this.vsm = VoiceSessionManager.getManager();
  }
  
  public void renderDialogue(
      String sessionid, 
      String callerid, 
      String action, 
      String answer, 
      InputStream binaryData) {
    
    log.info("Rendering dialogue: sessionid=" + sessionid + ", callerid=" + callerid + ", action=" + action + ", answer=" + answer);
    if (action == null || action.equals("")) {
      vs = newSession(sessionid, callerid);
      beginSession();
      return;
    } else {
      vs = vsm.get(sessionid);
    }
    
    if (vs == null) {
      log.error("session lost");
        renderError(VoiceError.SESSION_LOST, null);
        return;
    }
    fh = vs.getFormHandler();
    
    VoiceAction va;
    try {
      va = Enum.valueOf(VoiceAction.class, action);
    } catch (IllegalArgumentException e) {
      log.error("action string was not a valid VoiceAction");
      renderError(VoiceError.INTERNAL_ERROR, null);
      return;
    }
    switch(va){
    case SELECT_FORM:
      selectForm(answer);
      break;
    case RESUME_FORM:
      continueForm();
    case SAVE_ANSWER:
      saveAnswer(answer, binaryData);
      nextPrompt();
      break;
    case NEXT_PROMPT:
      nextPrompt();
      break;
    case PREV_PROMPT:
      prevPrompt();
      break;
    case HANGUP:
      exportData();
      break;
    }
  }
  
  private void beginSession() {
    VxmlDocument d = new VxmlDocument();
    d.setContents("<block>" + VxmlUtils.createGoto(FormVxmlServlet.ADDR + "?action=SELECT_FORM&answer=form.xml") + "</block>\n");
    try {
      d.write(out);
    } catch (IOException e) {
      log.error("IOException in beginSession", e);
      e.printStackTrace();
    }
  }
  
  private boolean saveAnswer(String answer, InputStream binaryData) {
    log.info("Answer: " + answer);
    PromptElement pe = fh.currentPrompt();

    // If the question is readonly there's nothing to save.
    if (!pe.isReadonly()) {

        int saveStatus =
                fh.saveAnswer(pe, WidgetFactory.createWidgetFromPrompt(pe, null).getAnswer(answer, binaryData),
                        evaluateConstraints);
        if (evaluateConstraints && saveStatus != XFormConstants.ANSWER_OK) {
            renderConstraintFailed(pe, saveStatus);
            return false;
        }
    }
    return true;
  }
  
  /**
   * Creates and displays a dialog displaying the violated constraint.
   */
  private void renderConstraintFailed(PromptElement p, int saveStatus) {
    log.warn("Constraint failed: " + p.getConstraintText());
//      String constraintText = null;
//      switch (saveStatus) {
//          case XFormConstants.ANSWER_CONSTRAINT_VIOLATED:
//              if (p.getConstraintText() != null) {
//                  constraintText = p.getConstraintText();
//              } else {
//                  constraintText = getString(R.string.invalid_answer_error);
//              }
//              break;
//          case XFormConstants.ANSWER_REQUIRED_BUT_EMPTY:
//              constraintText = getString(R.string.required_answer_error);
//              break;
//      }
//
//      showCustomToast(constraintText);
//      mBeenSwiped = false;
  }
  
  private void nextPrompt(){
    createView(fh.nextPrompt());
  }
  
  private void prevPrompt(){
    createView(fh.prevPrompt()); 
  }
  
  private void selectForm(String answerString) {
    String formPath = FileConstants.FORMS_PATH + File.separator + answerString;
    fh = FormLoader.getFormHandler(formPath, null);
    vs.setFormHandler(fh);
    createView(fh.currentPrompt());
  }
  
  private void continueForm() {
    
  }
  
  private void createView(PromptElement prompt) {
    try {
      switch (prompt.getType()) {
      case PromptElement.TYPE_START:
        String grammar = VxmlUtils.createGrammar(new String[]{"1"}, 
            new String[]{"out.action=\"" + VoiceAction.NEXT_PROMPT + "\";"});
        String filled = 
          "<if expr=\"action='" + VoiceAction.NEXT_PROMPT + "'>" + 
          VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action");
        VxmlForm startForm = new VxmlForm("start", 
            new VxmlArrayPrompt(StringConstants.formStartPrompt(fh.getFormTitle())),
                grammar, filled);
        new VxmlDocument(startForm).write(out);
        break;
      case PromptElement.TYPE_END:
        VxmlForm endForm = new VxmlForm("start", 
            new VxmlArrayPrompt(StringConstants.formEndPrompt(fh.getFormTitle())),
                "", "");
        new VxmlDocument(endForm).write(out);
        break;
      case PromptElement.TYPE_QUESTION:
        IQuestionWidget w = WidgetFactory.createWidgetFromPrompt(prompt, null);
        w.getPromptVxml(out);
      }
    } catch (IOException e) {
      log.error("IOException in createView", e);
    }
  }

  public void renderError(VoiceError error, String details){
    log.error("Voice error rendered. Type: " + error.name() + "; Details: " + details);

  }
  
  private VoiceSession newSession(String sessionid, String callerid){
    if (sessionid == null)
      renderError(VoiceError.INTERNAL_ERROR, "No sessionid");
    VoiceSession vs = new VoiceSession();
    vs.setCallerid(callerid);
    vsm.put(sessionid, vs);
    return vs;
  }
  
  private void exportData() {
    String path = FileConstants.INSTANCES_PATH + 
        File.separator + ((vs.getCallerid()==null)?"unknown":vs.getCallerid()) +
        File.separator + vs.getDate().getTime();
    FileUtils.createFolder(path);
    log.info("Export data path: " + path);
    fh.exportData(path, fh.isEnd());
    // probably markCompleted true iff they actually finished the survey
    // but this is dependent on an instances DB...are we making one?
  }
}
