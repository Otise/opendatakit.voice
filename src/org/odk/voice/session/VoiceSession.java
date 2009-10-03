package org.odk.voice.session;

import org.odk.voice.xform.FormHandler;

public class VoiceSession {
  private FormHandler fh;
  private boolean admin;
  private String callerid;
  
  public VoiceSession(){
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

  public void setCallerid(String callerid) {
    this.callerid = callerid;
  }

  public String getCallerid() {
    return callerid;
  }  
}
