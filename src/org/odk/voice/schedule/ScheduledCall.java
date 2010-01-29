package org.odk.voice.schedule;

import java.util.Date;

public class ScheduledCall {
  
  public int id;
  public String phoneNumber;
  public Status status;
  public enum Status {
    PENDING("#FFFFFF"), 
    COMPLETE("#EEFFEE"), 
    NOT_COMPLETED("#FFEEEE"), 
    NO_RESPONSE("#FFEEEE"), 
    IN_PROGRESS("#FFFFEE"),
    CALL_FAILED("#FFEEEE");
    
    public String color;
    private Status(String color) {
      this.color = color;
    }
  }
  public Date nextAttempt;
  public ScheduledCall (int id, String phoneNumber, Status status) {
    this.id = id; this.phoneNumber = phoneNumber; this.status = status;
  }
}
