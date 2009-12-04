package org.odk.voice.logic;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;
import org.javarosa.core.model.FormDef;
import org.odk.voice.audio.AudioSample;
import org.odk.voice.constants.FileConstants;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.constants.VoiceError;
import org.odk.voice.constants.XFormConstants;
import org.odk.voice.db.DbAdapter;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.session.VoiceSession;
import org.odk.voice.session.VoiceSessionManager;
import org.odk.voice.storage.FileUtils;
import org.odk.voice.storage.FormLoader;
import org.odk.voice.storage.InstanceUploader;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.storage.MultiPartFormItem;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.widgets.ChangeLanguageWidget;
import org.odk.voice.widgets.ConstraintFailedWidget;
import org.odk.voice.widgets.FormEndWidget;
import org.odk.voice.widgets.FormStartWidget;
import org.odk.voice.widgets.QuestionWidget;
import org.odk.voice.widgets.RecordPromptWidget;
import org.odk.voice.widgets.VxmlWidget;
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
  
  // The amount to clip at the end of prompts (in seconds) to remove the beep from the DTMF termination of the prompt recording
  private static final float PROMPT_END_CLIP = 0.2F;

  private static org.apache.log4j.Logger log = Logger
  .getLogger(FormVxmlRenderer.class);
  
  Writer out;
  VoiceSessionManager vsm;
  VoiceSession vs;
  FormHandler fh;
  boolean evaluateConstraints = true;
  String sessionid, callerid, action, answer;
  final MultiPartFormData binaryData;
  DbAdapter dba;

  public FormVxmlRenderer(String sessionid, String callerid, String action, String answer, MultiPartFormData binaryData, Writer out) {
    this.out = out;
    this.sessionid = sessionid;
    this.callerid = callerid;
    this.action = action;
    this.answer = answer;
    this.binaryData = binaryData;
    try {
      this.dba = new DbAdapter();
    } catch (SQLException e) {
      log.error("SQLException creating DbAdapter!!!", e);
    }
  }
  
  /**
   *  frees system resources (i.e. database)
   */
  public void close() {
    dba.close();
  }
  
  public void renderDialogue() {
    
    log.info("Rendering dialogue: sessionid=" + sessionid + ", callerid=" + callerid + ", action=" + action + ", answer=" + answer);

    this.vsm = VoiceSessionManager.getManager();
    
    if (action == null || action.equals("")) {
      vs = newSession(sessionid, callerid);
      if (vs != null) {
        sessionid = vs.getSessionid();
        beginSession();
        return;
      }
    } else {
      vs = vsm.get(sessionid);
    }
    
    if (vs == null) {
      log.error("session lost");
        renderError(VoiceError.SESSION_LOST, null);
        return;
    } else {
      sessionid = vs.getSessionid();
      callerid = vs.getCallerid();
    }
    
    fh = vs.getFormHandler();
    
    if (action.equals(VoiceAction.ADMIN.name())) {
      log.info("Session entered admin mode");
      vs.setAdmin(true);
    }
    
    if (vs.isAdmin()) {
      renderAdmin();
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
    log.info("Rendering admin mode. Action=" + action);
    switch(va){
    case ADMIN:
    case MAIN_MENU:
      adminMainMenu();
      break;
    case SAVE_ANSWER:
      savePrompt();
      // purposely fall through
    case NEXT_PROMPT:
      prompt = nextRecordPrompt(false);
      writeCurrentRecordPrompt(prompt);
      renderRecordPromptDialogue(prompt);
      break;
    case CURRENT_PROMPT:
      prompt = currentRecordPrompt();
      writeCurrentRecordPrompt(prompt);
      renderRecordPromptDialogue(prompt);
      break;
    case HANGUP:
      writeCurrentRecordPrompt(null);
      break;
    default:
      log.error("Invalid admin action type: " + va.name());
      renderError(VoiceError.INTERNAL_ERROR, "Unexpected admin action type");
    }
  }
  
  private void adminMainMenu() {
 // this should eventually allow the user to pick a form, (or pass-through if only one form)
    // and should go in a widget
    
    VxmlDocument d = new VxmlDocument(sessionid);
    d.setContents("<form id=\"begin\"><block>" + VxmlUtils.createRemoteGoto(FormVxmlServlet.ADDR + "?action=NEXT_PROMPT") + "</block></form>\n");
    try {
      d.write(out);
    } catch (IOException e) {
      log.error("IOException in beginSession", e);
      e.printStackTrace();
    }
  }
  
  private void savePrompt(){
    if (binaryData == null) {
      log.error("Tried to save prompt, but binaryData is null");
      return;
    }
    MultiPartFormItem item = binaryData.getFormDataByFieldName("answer");
    if (item == null) {
      log.error("Tried to save prompt, but 'answer' item does not exist.");
      return;
    }
    String prompt = currentRecordPrompt();
    byte[] data = item.getData();
    try { 
      AudioSample as = new AudioSample(data);
      as.clipAudio(0, PROMPT_END_CLIP);
      data = as.getAudio();
      dba.putAudioPrompt(prompt, data);
    
//    String path = FileConstants.PROMPT_AUDIO_PATH + File.separator + VxmlUtils.getWav(prompt);
//    try {
//      FileUtils.writeFile(item.getData(), path, true);
//      AudioSample as = new AudioSample(path);
//      as.clipAudio(0, PROMPT_END_CLIP);
    } catch (SQLException e) {
      log.error("Tried to save prompt, got SQLException: " + prompt);
    } catch (UnsupportedAudioFileException e) {
      log.error("Unsupported Audio File: " + prompt, e);
    } catch (IOException e) {
      log.error("IOException: " + prompt, e);
    }
  }
  
  private String currentRecordPrompt(){
    if (vs.getRecordPromptIndex() < 0) {
      return null;
    }
    return vs.getRecordPrompts()[vs.getRecordPromptIndex()];
  }
  
  private String nextRecordPrompt(boolean rerecord){
    if (vs == null) {
      log.warn("VoiceSession null trying to get record prompt");
      return null;
    }
    if (vs.getRecordLanguageIndex() == -1){
      if (fh.getLanguages() != null && fh.getLanguages().length > 0){
        vs.setRecordLanguageIndex(0);
        fh.setLanguage(fh.getLanguages()[0]);
      }
    }
    if(vs.getRecordPromptIndex() < 0) { // if recordPromptIndex uninitialized
      vs.setRecordPrompts(getWidgetFromPrompt(fh.currentPrompt()).getPromptStrings());
      vs.setRecordPromptIndex(0);
    } else {
      vs.setRecordPromptIndex(vs.getRecordPromptIndex() + 1);
    }
    while (vs.getRecordPrompts() == null || 
           vs.getRecordPromptIndex() >= vs.getRecordPrompts().length ||
           vs.getRecordPrompts()[vs.getRecordPromptIndex()].equals("") ||
           (!rerecord && dba.getAudioPrompt(vs.getRecordPrompts()[vs.getRecordPromptIndex()])!=null))
               // TODO(alerer): create audioPromptExists
//               FileUtils.fileExists(
//               FileConstants.PROMPT_AUDIO_PATH + File.separator + 
//               VxmlUtils.getWav(vs.getRecordPrompts()[vs.getRecordPromptIndex()])))
//           )
    {
      if (vs.getRecordPrompts() != null && vs.getRecordPromptIndex() < vs.getRecordPrompts().length) {
        vs.setRecordPromptIndex(vs.getRecordPromptIndex() + 1);
        //log.debug("Incrementing again, now prompt is " + vs.getRecordPrompts()[vs.getRecordPromptIndex()]);
      } else {
        
        if (fh.isEnd()) {
          String[] langs = fh.getLanguages();
          
          if (langs == null || vs.getRecordLanguageIndex() == langs.length){
            log.info("Tried to get record prompt, but at the end, so offering non-survey prompts.");
            return nextExtraPrompt();
          }
          vs.setRecordLanguageIndex(vs.getRecordLanguageIndex() + 1);
          fh.setLanguage(langs[vs.getRecordLanguageIndex()]);
          vs.setRecordPromptIndex(-1);
          return nextRecordPrompt(rerecord);
        }
        vs.setRecordPrompts(getWidgetFromPrompt(fh.nextQuestionPrompt()).getPromptStrings());
        vs.setRecordPromptIndex(0);
        //log.debug("Incrementing question, now prompt is " + vs.getRecordPrompts()[vs.getRecordPromptIndex()]);
      }
    }
    log.info("Next record prompt. Index: " + vs.getRecordPromptIndex() + ". Value: " + 
        vs.getRecordPrompts()[vs.getRecordPromptIndex()]);
    return vs.getRecordPrompts()[vs.getRecordPromptIndex()];
  }
  
  /**
   * Iterates through 'extra' prompts, i.e. prompts for things that aren't part of the 
   * form controls, e.g. language selection, audio prompt recording, etc.
   * @return the next prompt, or null if no prompts remain.
   */
  public String nextExtraPrompt(){
    // not yet implemented
    return null;
  }
 
  private void writeCurrentRecordPrompt(String prompt) {
    log.info("Writing record prompt: " + prompt);
    try {
      dba.setCurrentRecordPrompt(prompt);
    } catch (SQLException e) {
      log.error(e);
    }
//    if (prompt == null) return;
//    try {
//      FileUtils.writeFile(prompt.getBytes(), FileConstants.CURRENT_RECORD_PROMPT_PATH, true);
//    } catch (IOException e) {
//      log.error("IOException writing to the currentRecordPrompt file", e);
//    }
  }
  
  private void renderRecordPromptDialogue(String prompt) {
    try {
      RecordPromptWidget w = new RecordPromptWidget(prompt);
      w.setSessionid(sessionid);
      w.getPromptVxml(out);
    } catch (IOException e) { 
      log.error("",e); renderError(VoiceError.INTERNAL_ERROR,"");
    }
  }

  
  public void renderUser() {
    VoiceAction va = getAction(action);
    if (va == null){
      log.error("action string " + action + " was not a valid VoiceAction");
      renderError(VoiceError.INTERNAL_ERROR, action + " was not a valid Voice Action");
      return;
    }
    switch(va){
    case SELECT_FORM:
      selectForm(answer);
      break;
    case RESUME_FORM:
      continueForm();
      break;
    case LANGUAGE_MENU:
      changeLanguageMenu();
      break;
    case SET_LANGUAGE:
      fh.setLanguage(answer);
      renderPrompt(fh.currentPrompt());
      break;
    case SAVE_ANSWER:
      int saveStatus = saveAnswer(answer, binaryData);
      if (saveStatus == XFormConstants.ANSWER_OK || saveStatus == XFormConstants.ANSWER_NOT_SAVED) {
        renderPrompt(fh.nextPrompt());
      } else {
        renderConstraintFailed(fh.currentPrompt(), saveStatus);
      }
      break;
    case CURRENT_PROMPT:
      renderPrompt(fh.currentPrompt());
      break;
    case NEXT_PROMPT:
      renderPrompt(fh.nextPrompt());
      break;
    case PREV_PROMPT:
      renderPrompt(fh.prevPrompt());
      break;
    case HANGUP:
      try {
        new VxmlDocument(null).write(out);
      } catch (IOException e) {
        log.error(e);
      }
      exportData(fh.isEnd());
      vsm.remove(callerid);
      break;
    default:
      log.error("Invalid action type: " + va.name());
      renderError(VoiceError.INTERNAL_ERROR, "Unexpected action type");
    }
  }
  
  private void changeLanguageMenu() {
    log.info("Change language menu");
    try {
      ChangeLanguageWidget w = new ChangeLanguageWidget(fh);
      w.getPromptVxml(out);
    } catch (IOException e) {
      log.error(e);
    }
  }
  private void beginSession() {
    // this should eventually allow the user to pick a form, (or pass-through if only one form)
    // and should go in a widget
    log.info("Sessionid = " + sessionid);
    VxmlDocument d = new VxmlDocument(sessionid);
    d.setContents(
        "<var name=\"action\" expr=\"'" + VoiceAction.SELECT_FORM + "'\"/>" +
    		"<var name=\"answer\" expr=\"'form.xml'\"/>" +
    		"<form id=\"begin\">" +    
    		"<block>" + VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action", "answer") + "</block></form>\n");
    try {
      d.write(out);
    } catch (IOException e) {
      log.error("IOException in beginSession", e);
      e.printStackTrace();
    }
  }
  
  private int saveAnswer(String answer, MultiPartFormData binaryData) {
    log.info("Answer: " + answer);
    PromptElement pe = fh.currentPrompt();
    int saveStatus = XFormConstants.ANSWER_NOT_SAVED;
    // If the question is readonly there's nothing to save.
    if (!pe.isReadonly()) {
      try {
        saveStatus =
                fh.saveAnswer(pe, WidgetFactory.createWidgetFromPrompt(sessionid, pe, vs.getInstanceid()).getAnswer(answer, binaryData),
                        evaluateConstraints);
      } catch (IllegalArgumentException e) {
        log.error("Illegal argument exception saving answer", e);
        saveStatus = XFormConstants.ANSWER_INVALID;
      }
      if (saveStatus == XFormConstants.ANSWER_OK) {
        log.info("Answer saved successfully.");
      } else {
          log.warn("Save answer failed. Error code: " + saveStatus);      
      }
    }
    return saveStatus;
  }
  
  /**
   * Creates and displays a dialog displaying the violated constraint.
   */
  private void renderConstraintFailed(PromptElement p, int saveStatus) {
    log.warn("Constraint failed: Savestatus=" + saveStatus + ". ConstraintText: " + p.getConstraintText());
    try {
      (new ConstraintFailedWidget(p, saveStatus)).getPromptVxml(out);
    } catch (IOException e) {
      log.error(e);
      this.renderError(VoiceError.INTERNAL_ERROR, "IOException");
    }
  }
  
  private void selectForm(String formName) {
    //String formPath = FileConstants.FORMS_PATH + File.separator + formName;
    fh = FormLoader.getFormHandler(formName, null);
    if (fh == null) {
      log.error("Could not find form " + formName);
      renderError(VoiceError.FORM_NOT_FOUND, null);
      return;
    }
    int instanceId = -1;
    try {
      instanceId = dba.createInstance(callerid);
    } catch (SQLException e) {
      log.error("SQLException creating new instance. Callerid=" + callerid, e);
    }
    vs.setFormHandler(fh);
    vs.setInstanceid(instanceId);
    
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
  
  private VxmlWidget getWidgetFromPrompt(PromptElement prompt) {
    switch (prompt.getType()) {
    case PromptElement.TYPE_START:
      FormStartWidget fsw = new FormStartWidget(fh.getFormTitle());
      fsw.setSessionid(sessionid);
      return fsw;
    case PromptElement.TYPE_END:
      FormEndWidget few = new FormEndWidget(fh.getFormTitle());
      few.setSessionid(sessionid);
      return few;
    case PromptElement.TYPE_QUESTION:
      QuestionWidget w = WidgetFactory.createWidgetFromPrompt(sessionid, prompt, vs.getInstanceid());
      w.setQuestionCount(fh.getQuestionNumber(), fh.getQuestionCount() - 1); //TODO(alerer): why is getQuestionCount wrong?
      return w;
    default:
      log.error("Prompt type was not expected: " + prompt.getType());
      renderError(VoiceError.INTERNAL_ERROR, "Unexpected prompt type.");
      return null;
    }
  }
    
  private void renderPrompt(PromptElement prompt) {
    VxmlWidget w = getWidgetFromPrompt(prompt);
    if (w == null)
      return;
    try{
      w.getPromptVxml(out);
    } catch (IOException e) {
    log.error("IOException in createView", e);
    renderError(VoiceError.INTERNAL_ERROR, null);
    }
  }

  public void renderError(VoiceError error, String details){
    log.error("Voice error rendered. Type: " + error.name() + "; Details: " + details);
    String contents = "<block><prompt>Sorry, an error occured of type " + error.name() + ". Details are: " + details + "</prompt></block>";
    VxmlForm f = new VxmlForm("errorForm");
    f.setContents(contents);
    try {
      new VxmlDocument(null, f).write(out);
    } catch (IOException e) {log.error("",e);}
  }
  
  private VoiceSession newSession(String sessionid, String callerid){
    
    VoiceSession vs = new VoiceSession();
    if (callerid != null)
      vs.setCallerid(callerid);
    if (sessionid != null)
      vs.setSessionid(sessionid);
    vsm.put(vs.getCallerid(), vs.getSessionid(), vs);
    return vs;
  }
  
  private void exportData(boolean complete) {
    /*String path = (complete ? FileConstants.COMPLETE_INSTANCES_PATH : FileConstants.INCOMPLETE_INSTANCES_PATH) + 
        File.separator + ((vs.getCallerid()==null)?"unknown":vs.getCallerid()) +
        File.separator + vs.getDate().getTime(); */
    // this will all get replaced with database stuff soon so don't worry about how well it works
    // in complex situations
    String path = getExportPath();
    FileUtils.createFolder(path);
    log.info("Export data path: " + path);
    byte[] xml = fh.getInstanceXml();
    String filename = path + File.separator +
               path.substring(path.lastIndexOf(File.separator) + 1) + ".xml";
    log.info("XML path: " + filename);
    try {
      FileUtils.writeFile(xml, filename, true);
    } catch (IOException e) {
      log.error("IOException writing instance XML to backup file.");
    }
    try {
      dba.setInstanceXml(vs.getInstanceid(), xml);
    } catch (SQLException e) {
      log.error("SQLException setting instance XML.");
    }
    
    // probably markCompleted true iff they actually finished the survey
    // but this is dependent on an instances DB...are we making one?
    if (complete) {
      InstanceUploader iu = new InstanceUploader();
      iu.setServerUrl(FileConstants.UPLOAD_URL);
      if (iu.uploadInstance(vs.getInstanceid()) == InstanceUploader.STATUS_OK) {
        log.info("Instance uploaded to server at " + FileConstants.UPLOAD_URL + " successfully.");
      } else {
        log.warn("Instance upload to server at " + FileConstants.UPLOAD_URL + " failed.");
      }
      
    }
  }
  
  private String getExportPath() {
    return FileConstants.INSTANCES_PATH + File.separator + (vs.getCallerid()==null ? "unknown" : vs.getCallerid()) + 
    File.separator + vs.getDate().getTime();
  }
}
