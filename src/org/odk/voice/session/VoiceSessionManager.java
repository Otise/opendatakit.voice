package org.odk.voice.session;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * A singleton manager object for holding all active sessions.
 * @author Adam Lerer (adam.lerer@gmail.com)
 *
 */
public class VoiceSessionManager {
  public static double PURGE_PROB = 0.01; // sessions are purged every 100 calls
  public static long STALE_MS = 1000 * 60 * 60 * 24; // sessions become stale after 24 hours
  
  private static VoiceSessionManager m;
  private static org.apache.log4j.Logger log = Logger
  .getLogger(VoiceSessionManager.class);
  
  public static VoiceSessionManager getManager(){
    if (m == null){
      m = new VoiceSessionManager();
    }
    return m;
  }
  private Random r;
  
  //////////////////////////////////////////////////
  
  private Map<String, VoiceSession> vs;
  private Map<String, Date> fresh;
  
  private VoiceSessionManager(){
    r = new Random(System.currentTimeMillis());
    log.info("VoiceSessionManager instantiated");
    this.vs = new HashMap<String, VoiceSession>();
    this.fresh = new HashMap<String, Date>();
  }
  
  public void put(String callerid, String sessionid, VoiceSession s){
    if (r.nextDouble() < PURGE_PROB) purge(new Date(new Date().getTime() - STALE_MS));
    if (callerid != null) {
      vs.put(callerid, s);
      fresh.put(callerid, new Date());
    }
    if (sessionid != null) {
      vs.put(sessionid, s);
      fresh.put(sessionid, new Date());
    }
    log.info("Session put. Callerid: " + callerid + ". Size: " + vs.size());
  }
  
  public VoiceSession get(String calleridOrSessionid){
    fresh.put(calleridOrSessionid, new Date());
    log.info("Session get. Size: " + vs.size());
    return vs.get(calleridOrSessionid);
  }
  
  public VoiceSession remove(String calleridOrSessionid){
    VoiceSession v = vs.remove(calleridOrSessionid);
    vs.remove(v.getCallerid());
    vs.remove(v.getSessionid());
    return v;
  }

  
  public void purge(Date stale){
    for (Entry<String, Date> e : fresh.entrySet()) {
      if (e.getValue().before(stale)){
        remove(e.getKey());
        fresh.remove(e.getKey());
      }
    }
  }
}
