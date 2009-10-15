package org.odk.voice.logic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.javarosa.core.model.FormDef;
import org.odk.voice.constants.FileConstants;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.constants.VoiceError;
import org.odk.voice.constants.XFormConstants;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.session.VoiceSession;
import org.odk.voice.session.VoiceSessionManager;
import org.odk.voice.storage.FileUtils;
import org.odk.voice.storage.FormLoader;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.widgets.FormEndWidget;
import org.odk.voice.widgets.FormStartWidget;
import org.odk.voice.widgets.QuestionWidget;
import org.odk.voice.widgets.RecordPromptWidget;
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
  
  Writer out;
  VoiceSessionManager vsm;
  VoiceSession vs;
  FormHandler fh;
  boolean evaluateConstraints = true;
  final String sessionid, callerid, action, answer;
  final InputStream binaryData;

  public FormVxmlRenderer(String sessionid, String callerid, String action, String answer, InputStream binaryData, Writer out){
    this.out = out;
    this.sessionid = sessionid;
    this.callerid = callerid;
    this.action = action;
    this.answer = answer;
    this.binaryData = binaryData;
  }
  
  public void renderDialogue() {
    
    log.info("Rendering dialogue: sessionid=" + sessionid + ", callerid=" + callerid + ", action=" + action + ", answer=" + answer);

    this.vsm = VoiceSessionManager.getManager();
    
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
    
    if (action.equals(VoiceAction.ADMIN.name())) {
      boolean isAdmin = Boolean.parseBoolean(answer);
      vs.setAdmin(isAdmin);
    }
    
    if (vs.isAdmin()) {
      //renderAdmin();
    } else {
      renderUser();
    }
    
  }
  
  private VoiceAction getAction(String actionString) {
    try {
      return Enum.valueOf(VoiceAction.class, action);
    } catch (IllegalArgumentException e) {

      return null;
    }
  }
  
  public void renderAdmin() {
    VoiceAction va = getAction(action);
    String prompt;
    switch(va){
    case ADMIN:
    case MAIN_MENU:
      //adminMainMenu();
      break;
    case SAVE_ANSWER:
      savePrompt();
      // purposely fall through
    case NEXT_PROMPT:
      prompt = nextRecordPrompt(false);
      setCurrentRecordPrompt(prompt);
      renderRecordPromptDialogue(prompt);
      break;
    default:
      log.error("Invalid adminvaction type: " + va.name());
      renderError(VoiceError.INTERNAL_ERROR, "Unexpected admin action type");
    }
  }
  
  private void savePrompt(){
    //FileUti
  }
  
  private String nextRecordPrompt(boolean rerecord){
    if(vs.getRecordPromptIndex() < 0) { // if recordPromptIndex uninitialized
      vs.setRecordPrompts(WidgetFactory.createWidgetFromPrompt(fh.currentPrompt(), null).getPromptStrings());
      vs.setRecordPromptIndex(0);
    } else {
      vs.setRecordPromptIndex(vs.getRecordPromptIndex() + 1);
    }
    while (vs.getRecordPrompts() == null || 
           vs.getRecordPromptIndex() >= vs.getRecordPrompts().length //||
           //(!rerecord && audioExists(vs.getRecordPrompts()[vs.getRecordPromptIndex()])
           )
    {
      if (fh.isEnd())
        return null;
      vs.setRecordPrompts(WidgetFactory.createWidgetFromPrompt(fh.nextQuestionPrompt(), null).getPromptStrings());
      vs.setRecordPromptIndex(0);
    }
    return vs.getRecordPrompts()[vs.getRecordPromptIndex()];
  }
 
  private void setCurrentRecordPrompt(String prompt) {
    try {
      FileUtils.writeFile(prompt.getBytes(), FileConstants.CURRENT_RECORD_PROMPT_PATH);
    } catch (IOException e) {
      log.error("IOException writing to the currentRecordPrompt file", e);
    }
  }
  
  private void renderRecordPromptDialogue(String prompt) {
    try {
      (new RecordPromptWidget(prompt)).getPromptVxml(out);
    } catch (IOException e) { 
      log.error("",e); renderError(VoiceError.INTERNAL_ERROR,"");
    }
  }

  
  public void renderUser() {
    VoiceAction va = getAction(action);
    if (va == null){
      log.error("action string " + action + "was not a valid VoiceAction");
      renderError(VoiceError.INTERNAL_ERROR, null);
    }
    switch(va){
    case ADMIN:
      beginSession();
      break;
    case SELECT_FORM:
      selectForm(answer);
      break;
    case RESUME_FORM:
      continueForm();
      break;
    case SAVE_ANSWER:
      saveAnswer(answer, binaryData);
      renderPrompt(fh.nextPrompt());
      break;
    case NEXT_PROMPT:
      renderPrompt(fh.nextPrompt());
      break;
    case PREV_PROMPT:
      renderPrompt(fh.prevPrompt());
    case HANGUP:
      exportData();
      break;
    default:
      log.error("Invalid action type: " + va.name());
      renderError(VoiceError.INTERNAL_ERROR, "Unexpected action type");
    }
  }
  
  private void beginSession() {
    // this should eventually allow the user to pick a form, (or pass-through if only one form)
    // and should go in a widget
    
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
  
  private void selectForm(String formName) {
    String formPath = FileConstants.FORMS_PATH + File.separator + formName;
    fh = FormLoader.getFormHandler(formPath, null);
    vs.setFormHandler(fh);
    
    preloadForm(fh.getForm(), true);
    
    renderPrompt(fh.currentPrompt());
  }
  
  private void preloadForm(FormDef fd, boolean newInstance) {
    // see PropertyPreloadHandler javadoc for info on what's going on here
    PropertyPreloadHandler pph = new PropertyPreloadHandler();
    pph.setProperty(PropertyPreloadHandler.PHONE_NUMBER_PROPERTY, callerid);
    pph.setProperty(PropertyPreloadHandler.SESSION_ID_PROPERTY, sessionid);
    fd.getPreloader().addPreloadHandler(pph);
    fd.initialize(true);
  }
  
  private void continueForm() {

  }
  
  private void renderPrompt(PromptElement prompt) {
    try {
      switch (prompt.getType()) {
      case PromptElement.TYPE_START:
        (new FormStartWidget(fh.getFormTitle())).getPromptVxml(out);
      case PromptElement.TYPE_END:
        (new FormEndWidget(fh.getFormTitle())).getPromptVxml(out);
      case PromptElement.TYPE_QUESTION:
        QuestionWidget w = WidgetFactory.createWidgetFromPrompt(prompt, null);
        w.setQuestionCount(fh.getQuestionNumber(), fh.getQuestionCount());
        w.getPromptVxml(out);
      }
    } catch (IOException e) {
      log.error("IOException in createView", e);
      renderError(VoiceError.INTERNAL_ERROR, null);
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
