package org.odk.voice.schedule;

import java.text.DateFormat;
import java.util.Date;

public class ScheduledCall {
  public static final float H2MS = 1000 * 60 * 60;
  
  public int id;
  public String phoneNumber;
  public Status status;
  public Date timeAdded, timeFrom, timeTo, nextTime;
  public int numAttempts;
  public long intervalMs;
  public enum Status {
    PENDING("#FFFFFF"), 
    COMPLETE("#EEFFEE"), 
    NOT_COMPLETED("#FFEEEE"), 
    NO_RESPONSE("#FFEEEE"), 
    IN_PROGRESS("#FFFFEE"),
    CALL_FAILED("#FFEEEE"); 
    //TOO_LOUD("FFFFFF"); 
    
    public String color;
    private Status(String color) {
      this.color = color;
    }
  }
  public ScheduledCall (int id, String phoneNumber, Status status, Date timeAdded, Date timeFrom, Date timeTo, Date nextTime, long intervalMs, int numAttempts) {
    this.id = id; this.phoneNumber = phoneNumber; this.status = status;
    this.timeAdded = timeAdded; this.timeFrom = timeFrom; this.timeTo = timeTo; this.nextTime = nextTime; this.intervalMs = intervalMs; this.numAttempts = numAttempts;
  }
  
  public String getDeliveryInfo(){
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    if (timeFrom == null || timeTo == null) {
      return "";
    }
    if (Status.PENDING.equals(status)) {
      return String.format("Delivery scheduled between %s and %s every %.2f hours.<br/> %d attempts so far; next attempt at %s.", 
          df.format(timeFrom), df.format(timeTo), ((float)intervalMs)/H2MS, numAttempts, df.format(nextTime));
    } else {
      return String.format("%s after %d attempts between %s and %s",
          status, numAttempts, df.format(timeFrom), df.format(timeTo));
    }
   
  }
}
