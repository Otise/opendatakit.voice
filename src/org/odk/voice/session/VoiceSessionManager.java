package org.odk.voice.session;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A singleton object
 * @author Adam Lerer (adam.lerer@gmail.com)
 *
 */
public class VoiceSessionManager {
  private static VoiceSessionManager m;
  
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
    this.vs = new HashMap<String, VoiceSession>();
    this.fresh = new HashMap<String, Date>();
  }
  
  public void put(String sessionid, VoiceSession s){
    vs.put(sessionid, s);
    fresh.put(sessionid, new Date());
  }
  
  public VoiceSession get(String sessionid){
    fresh.put(sessionid, new Date());
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
