package org.odk.voice.session;

import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;
import org.odk.voice.local.OdkLocales;
import org.odk.voice.widgets.FormEndWidget;
import org.odk.voice.widgets.FormStartWidget;
import org.odk.voice.widgets.QuestionWidget;
import org.odk.voice.widgets.VxmlWidget;
import org.odk.voice.widgets.WidgetBase;
import org.odk.voice.widgets.WidgetFactory;
import org.odk.voice.xform.FormHandler;
import org.odk.voice.xform.PromptElement;

/**
 * A session object containing information for a single phone session.
 * @author alerer
 *
 */
public class VoiceSession {
  
  private static org.apache.log4j.Logger log = Logger
  .getLogger(VoiceSession.class);
  
  private static Random rand = new Random((new Date()).getTime());
  private FormHandler fh;
  private boolean admin;
  private String callerid; 
  private String sessionid;
  private int instanceid;
  private Date date;
  
  private String[] recordPrompts; //if an admin is recording prompts, this variable stores the prompts for the current question
  private int recordPromptIndex = -1; //the index in recordPrompts that is currently being recorded
  private int recordLanguageIndex = -1;
  
  public VoiceSession(){
    this.date = new Date();
    sessionid = "session" + String.valueOf(rand.nextLong()); // default sessionid in case one is not provided
  }
  
  public void setFormHandler(FormHandler fh){
    this.fh = fh;
  }
  
  public FormHandler getFormHandler() {
    return fh;
  }

  public boolean isAdmin() {
    return admin;
  }
  
  public void setAdmin(boolean isAdmin) {
    this.admin = isAdmin;
  }

  public void setCallerid(String callerid) {
    this.callerid = callerid;
  }

  public String getCallerid() {
    return callerid;
  }


  public Date getDate() {
    return date;
  }
  
  private VxmlWidget getWidgetFromPrompt(PromptElement prompt) {
    WidgetBase w = null;
    switch (prompt.getType()) {
    case PromptElement.TYPE_START:
      w = new FormStartWidget(fh.getFormTitle());
      break;
    case PromptElement.TYPE_END:
      w = new FormEndWidget(fh.getFormTitle());
      break;
    case PromptElement.TYPE_QUESTION:
      QuestionWidget w2 = WidgetFactory.createWidgetFromPrompt("", prompt, 0);
      w2.setQuestionCount(fh.getQuestionNumber(), fh.getQuestionCount() - 1); //TODO(alerer): why is getQuestionCount wrong?
      w = w2;
      break;
    default:
      log.error("Prompt type was not expected: " + prompt.getType());
      return null;
    }
    w.setLocale(OdkLocales.getLocale(fh.getCurrentLanguage()));
    return w;
  }
  
  public String getCurrentRecordPrompt(){
    if (recordPromptIndex < 0) {
      return null;
    }
    return recordPrompts[recordPromptIndex];
  }
  
  public String getNextRecordPrompt(){
    if (recordLanguageIndex == -1) {
      if (fh.getLanguages() != null && fh.getLanguages().length > 0){
        recordLanguageIndex = 0;;
        fh.setLanguage(fh.getLanguages()[0]);
      }
    }  
    if(recordPromptIndex < 0) { // if recordPromptIndex uninitialized
        while (!fh.isBeginning()){
          fh.prevPrompt();
        }
        recordPrompts = getWidgetFromPrompt(fh.currentPrompt()).getPromptStrings();
        recordPromptIndex = 0;
    } else {
      recordPromptIndex++;
    }
    while (recordPrompts == null || 
           recordPromptIndex >= recordPrompts.length
           )
    {          
      if (fh.isEnd()) {
        String[] langs = fh.getLanguages();
        
        if (langs == null || recordLanguageIndex == langs.length - 1){
          log.info("Tried to get record prompt, but at the end.");
          return null;
        } else {
          recordLanguageIndex++;
          fh.setLanguage(langs[recordLanguageIndex]);
          recordPromptIndex = -1;
          return getNextRecordPrompt();
        }
      } else {
        recordPrompts = getWidgetFromPrompt(fh.nextPrompt()).getPromptStrings();
        recordPromptIndex = 0;
      }
    }
    log.info("Next record prompt. Index: " + recordPromptIndex + ". Value: " + 
        recordPrompts[recordPromptIndex]);
    return getCurrentRecordPrompt();
  }
  
//  public void setRecordPrompts(String[] recordPrompts) {
//    this.recordPrompts = recordPrompts;
//  }
//
//  public String[] getRecordPrompts() {
//    return recordPrompts;
//  }
//
//  public void setRecordPromptIndex(int recordPromptIndex) {
//    this.recordPromptIndex = recordPromptIndex;
//  }
//
//  public int getRecordPromptIndex() {
//    return recordPromptIndex;
//  }

  public void setSessionid(String sessionid) {
    this.sessionid = sessionid;
    
  }
  
  public String getSessionid(){
    return sessionid;
  }

  public void setRecordLanguageIndex(int recordLanguageIndex) {
    this.recordLanguageIndex = recordLanguageIndex;
  }

  public int getRecordLanguageIndex() {
    return recordLanguageIndex;
  }

  public void setInstanceid(int instanceid) {
    this.instanceid = instanceid;
  }

  public int getInstanceid() {
    return instanceid;
  }

}
