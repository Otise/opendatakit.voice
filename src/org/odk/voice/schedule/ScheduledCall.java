package org.odk.voice.schedule;

import java.util.Date;

public class ScheduledCall {
  
  public int id;
  public String phoneNumber;
  public Status status;
  public Date timeAdded, timeFrom, timeTo, nextTime;
  public long intervalMs;
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
  public ScheduledCall (int id, String phoneNumber, Status status, Date timeAdded, Date timeFrom, Date timeTo, Date nextTime, long intervalMs) {
    this.id = id; this.phoneNumber = phoneNumber; this.status = status;
    this.timeAdded = timeAdded; this.timeFrom = timeFrom; this.timeTo = timeTo; this.nextTime = nextTime; this.intervalMs = intervalMs;
  }
}
