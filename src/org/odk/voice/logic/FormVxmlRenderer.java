package org.odk.voice.logic;

import java.io.InputStream;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.odk.voice.constants.FileConstants;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.constants.VoiceError;
import org.odk.voice.session.VoiceSession;
import org.odk.voice.session.VoiceSessionManager;
import org.odk.voice.storage.FormLoader;
import org.odk.voice.widgets.WidgetFactory;
import org.odk.voice.xform.FormHandler;
import org.odk.voice.xform.PromptElement;

public class FormVxmlRenderer {

  private static org.apache.log4j.Logger log = Logger
  .getLogger(FormVxmlRenderer.class);
  
  private Writer out;
  private VoiceSessionManager vsm;
  private VoiceSession vs;

  public FormVxmlRenderer(Writer out){
    this.out = out;
    this.vsm = VoiceSessionManager.getManager();
  }
  
  public void renderDialogue(
      String sessionid, 
      String callerid, 
      String action, 
      String stringData, 
      InputStream binaryData) {
    
    if (action == null) {
      vs = newSession(sessionid, callerid);
    } else {
      vs = vsm.get(sessionid);
    }
    
    if (vs == null) {
        renderError(VoiceError.SESSION_LOST, null);
        return;
    }
    
    VoiceAction va = Enum.valueOf(VoiceAction.class, action);
    if (va == null) {
      log.error("action string was not a valid VoiceAction");
      renderError(VoiceError.INTERNAL_ERROR, null);
      return;
    }
    switch(va){
    case SELECT_FORM:
      selectForm(stringData);
      break;
    case RESUME_FORM:
      continueForm();
    case SAVE_ANSWER:
      saveAnswer(stringData, binaryData);
      nextPrompt();
      break;
    case NEXT_PROMPT:
      nextPrompt();
      break;
    case PREV_PROMPT:
      prevPrompt();
      break;
    }
  }
  
  private void saveAnswer(String stringData, InputStream binaryData) {
    
  }
  
  private void nextPrompt(){
    createView(vs.getFormHandler().nextPrompt());
  }
  
  private void prevPrompt(){
    createView(vs.getFormHandler().prevPrompt()); 
  }
  
  private void selectForm(String answerString) {
    String formPath = FileConstants.FORMS_PATH + "/" + answerString;
    FormHandler fh = FormLoader.getFormHandler(formPath, null);
    vs.setFormHandler(fh);
    createView(fh.currentPrompt());
  }
  
  private void continueForm() {
    
  }
  
  private void createView(PromptElement prompt) {
    switch (prompt.getType()) {
    case PromptElement.TYPE_START:
      
      break;
    case PromptElement.TYPE_END:
      break;
    case PromptElement.TYPE_QUESTION:
      WidgetFactory.createWidgetFromPrompt(prompt, null);
    }
  }

  public void renderError(VoiceError error, String details){
    log.error("Voice error rendered. Type: " + error.name() + "; Details: " + details);
  }
  
  private VoiceSession newSession(String sessionid, String callerid){
    VoiceSession vs = new VoiceSession();
    vs.setCallerid(callerid);
    vsm.put(sessionid, vs);
    return vs;
  }
}
//  /**
//   * Creates a view given the View type and a prompt
//   * 
//   * @param prompt
//   * @return newly created View
//   */
//  private View createView(PromptElement prompt) {
//      setTitle(getString(R.string.app_name) + " > " + mFormHandler.getFormTitle());
//      FileDbAdapter fda = null;
//      Cursor c = null;
//
//      switch (prompt.getType()) {
//          case PromptElement.TYPE_START:
//              View startView = View.inflate(this, R.layout.form_entry_start, null);
//              setTitle(getString(R.string.app_name) + " > " + mFormHandler.getFormTitle());
//
//              fda = new FileDbAdapter(FormEntryActivity.this);
//              fda.open();
//              c = fda.fetchFilesByPath(mInstancePath, null);
//              if (c != null && c.getCount() > 0) {
//                  ((TextView) startView.findViewById(R.id.description)).setText(getString(
//                          R.string.review_data_description, c.getString(c
//                                  .getColumnIndex(FileDbAdapter.KEY_DISPLAY))));
//              } else {
//                  ((TextView) startView.findViewById(R.id.description)).setText(getString(
//                          R.string.enter_data_description, mFormHandler.getFormTitle()));
//              }
//
//              // clean up cursor
//              if (c != null) {
//                  c.close();
//              }
//
//              fda.close();
//              return startView;
//          case PromptElement.TYPE_END:
//              View endView = View.inflate(this, R.layout.form_entry_end, null);
//              fda = new FileDbAdapter(FormEntryActivity.this);
//              fda.open();
//              c = fda.fetchFilesByPath(mInstancePath, null);
//              if (c != null && c.getCount() > 0) {
//                  ((TextView) endView.findViewById(R.id.description)).setText(getString(
//                          R.string.save_data_description, c.getString(c
//                                  .getColumnIndex(FileDbAdapter.KEY_DISPLAY))));
//              } else {
//                  ((TextView) endView.findViewById(R.id.description)).setText(getString(
//                          R.string.save_data_description, mFormHandler.getFormTitle()));
//              }
//              // Create 'save complete' button.
//              ((Button) endView.findViewById(R.id.complete_exit_button))
//                      .setOnClickListener(new OnClickListener() {
//                          public void onClick(View v) {
//                              // Form is markd as 'done' here.
//                              if (saveDataToDisk(true)) finish();
//                          }
//                      });
//              // Create 'save for later' button
//              ((Button) endView.findViewById(R.id.save_exit_button))
//                      .setOnClickListener(new OnClickListener() {
//                          public void onClick(View v) {
//                              // Form is markd as 'saved' here.
//                              if (saveDataToDisk(false)) finish();
//                          }
//                      });
//
//              // clean up cursor
//              if (c != null) {
//                  c.close();
//              }
//
//              fda.close();
//              return endView;
//          case PromptElement.TYPE_QUESTION:
//          default:
//              QuestionView qv = new QuestionView(this, prompt, mInstancePath);
//              qv.buildView(prompt);
//              return qv;
//      }
//  }
//}
