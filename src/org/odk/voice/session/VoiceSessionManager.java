package org.odk.voice.session;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.odk.voice.logic.FormVxmlRenderer;

/**
 * A singleton manager object for holding all active sessions.
 * @author Adam Lerer (adam.lerer@gmail.com)
 *
 */
public class VoiceSessionManager {
  private static VoiceSessionManager m;
  private static org.apache.log4j.Logger log = Logger
  .getLogger(VoiceSessionManager.class);
  
  public static VoiceSessionManager getManager(){
    if (m == null){
      m = new VoiceSessionManager();
    }
    return m;
  }
  
  //////////////////////////////////////////////////
  
  private Map<String, VoiceSession> vs;
  private Map<String, Date> fresh;
  
  private VoiceSessionManager(){
    log.info("VoiceSessionManager instantiated");
    this.vs = new HashMap<String, VoiceSession>();
    this.fresh = new HashMap<String, Date>();
  }
  
  public void put(String sessionid, VoiceSession s){
    vs.put(sessionid, s);
    fresh.put(sessionid, new Date());
    log.info("Session put. Sessionid: " + sessionid + ". Size: " + vs.size());
  }
  
  public VoiceSession get(String sessionid){
    fresh.put(sessionid, new Date());
    log.info("Session get. Sessionid: " + sessionid + ". Size: " + vs.size());
    return vs.get(sessionid);
  }
  
  public void purge(Date stale){
    for (Entry<String, Date> e : fresh.entrySet()) {
      if (e.getValue().before(stale)){
        vs.remove(e.getKey());
        fresh.remove(e.getKey());
      }
    }
  }
}
