package org.odk.voice.session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.odk.voice.db.DbAdapter;
import org.odk.voice.local.OdkLocales;
import org.odk.voice.widgets.ChangeLanguageWidget;
import org.odk.voice.widgets.FormEndWidget;
import org.odk.voice.widgets.FormResumeWidget;
import org.odk.voice.widgets.FormStartWidget;
import org.odk.voice.widgets.QuestionWidget;
import org.odk.voice.widgets.RecordPromptWidget;
import org.odk.voice.widgets.SelectFormWidget;
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
  
  static Random rand = new Random((new Date()).getTime());
  FormHandler fh;
  boolean admin;
  String callerid; 
  String sessionid;
  int instanceid;
  Date date;
  int attempt = 0;
  
  List<String> recordPrompts = null; //if an admin is recording prompts, this variable stores the prompts for the current question
  int recordPromptIndex = -1; //the index in recordPrompts that is currently being recorded
  // ---------------------------------
  
  int outboundId = -1;
  
  public VoiceSession(int attempt){
    this.attempt = attempt;
    this.date = new Date();
    sessionid = "session" + String.valueOf(rand.nextLong());
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

  public void setSessionid(String sessionid) {
    this.sessionid = sessionid;
    
  }
  
  public String getSessionid(){
    return sessionid;
  }
  
  //----- prompt recording ------

  public void setRecordPromptIndex(int recordPromptIndex) {
    this.recordPromptIndex = recordPromptIndex;
  }
  
  public int getRecordPromptIndex() {
    return recordPromptIndex;
  }
  
  public void setRecordPrompts(List<String> recordPrompts) {
    this.recordPrompts = recordPrompts;
  }
  
  public List<String> getRecordPrompts() {
    return recordPrompts;
  }

  // ---------------------------
  
  public void setInstanceid(int instanceid) {
    this.instanceid = instanceid;
  }

  public int getInstanceid() {
    return instanceid;
  }

  public void setOutboundId(int outboundId) {
    this.outboundId = outboundId;
  }

  public int getOutboundId() {
    return outboundId;
  }
  
  public void incrementAttempt(){
    attempt++;
  }
  public int getAttempt(){
    return attempt;
  }
  

}
