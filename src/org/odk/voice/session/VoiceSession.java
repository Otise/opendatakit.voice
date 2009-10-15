package org.odk.voice.session;

import java.util.Date;

import org.odk.voice.xform.FormHandler;

/**
 * A session object containing information for a single phone session.
 * @author alerer
 *
 */
public class VoiceSession {
  private FormHandler fh;
  private boolean admin;
  private String callerid;
  private Date date;
  
  private String[] recordPrompts; //if an admin is recording prompts, this variable stores the prompts for the current question
  private int recordPromptIndex = -1; //the index in recordPrompts that is currently being recorded
  
  
  public VoiceSession(){
    this.date = new Date();
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

  public void setRecordPrompts(String[] recordPrompts) {
    this.recordPrompts = recordPrompts;
  }

  public String[] getRecordPrompts() {
    return recordPrompts;
  }

  public void setRecordPromptIndex(int recordPromptIndex) {
    this.recordPromptIndex = recordPromptIndex;
  }

  public int getRecordPromptIndex() {
    return recordPromptIndex;
  }  
}
