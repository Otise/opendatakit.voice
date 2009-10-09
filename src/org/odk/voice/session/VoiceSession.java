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

  public void setCallerid(String callerid) {
    this.callerid = callerid;
  }

  public String getCallerid() {
    return callerid;
  }


  public Date getDate() {
    return date;
  }  
}
