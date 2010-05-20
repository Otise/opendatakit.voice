package org.odk.voice.schedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.catalina.util.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.odk.voice.constants.GlobalConstants;
import org.odk.voice.db.DbAdapter;
import org.odk.voice.schedule.ScheduledCall.Status;

/**
 * An {@link OutboundCallScheduler} automatically runs a daemon timer thread 
 * in the application background that regularly queries for pending scheduled 
 * outbound calls and executes them (one at a time).
 * 
 * @author alerer
 *
 */
public class OutboundCallScheduler implements ServletContextListener{

  private static org.apache.log4j.Logger log = Logger
  .getLogger(OutboundCallScheduler.class);
  
  public static final int CONNECTION_TIMEOUT = 30000;

  private static final long TIMER_TASK_RATE = 15000;

    public OutboundCallScheduler() {}

    public void contextDestroyed(ServletContextEvent event)
    {

//      //Output a simple message to the server's console
//      System.out.println("The Simple Web App. Has Been Removed");
//      this.context = null;

    }


    //This method is invoked when the Web Application
    //is ready to service requests

    public static final int MAX_SIMUL_CONNECTIONS = 1;
    
    public void contextInitialized(ServletContextEvent event)
    {
//      this.context = event.getServletContext();

      Timer timer = new Timer("CallScheduler", true);
      
      TimerTask task = new TimerTask(){
        @Override
        public void run() {
          
          DbAdapter dba = null;
          try {
            dba = new DbAdapter();
            String url = dba.getMiscValue(GlobalConstants.OUTBOUND_URL_KEY);
            String tokenid = dba.getMiscValue(GlobalConstants.OUTBOUND_TOKEN_KEY);
            String callerid = dba.getMiscValue(GlobalConstants.OUTBOUND_CALLERID_KEY);
            List<ScheduledCall> pendingCalls = dba.getScheduledCalls(Status.PENDING);
            List<ScheduledCall> inprogressCalls = dba.getScheduledCalls(Status.IN_PROGRESS);
            if (pendingCalls.size() > 0 && inprogressCalls.size() < MAX_SIMUL_CONNECTIONS) {
              boolean success;
              Date now = new Date();
              for (ScheduledCall pc : pendingCalls) {
                if (pc.nextTime == null) {
                  success = sendOutboundCallRequest(url, tokenid, callerid, pc.phoneNumber, pc.id);
                  log.info("Outbound call request: number=" + pc.phoneNumber + "; id=" + pc.id + "; success=" + success);
                  dba.setOutboundCallStatus(pc.id, success ? Status.IN_PROGRESS : Status.CALL_FAILED);
                  break;
                } else if (pc.nextTime.before(now)) {
                  success = pc.timeTo.after(now) ? 
                      sendOutboundCallRequest(url, tokenid, callerid, pc.phoneNumber, pc.id) : false;
                  Date newNextTime = new Date(now.getTime() + pc.intervalMs);
                  dba.setOutboundCallNextTime(pc, (newNextTime.after(pc.timeTo)|| success) ? null : newNextTime); 
                  dba.setOutboundCallStatus(pc.id, success ? Status.IN_PROGRESS : 
                    newNextTime.after(pc.timeTo) ? Status.CALL_FAILED : Status.PENDING);
                  
                  break;
                } 
              } 
            }
          } catch (SQLException e) {
            log.error(e);
          } finally {
            if (dba != null) {
              dba.close();
            }
          }
        }
      };
      
      timer.scheduleAtFixedRate(task, new Date(), TIMER_TASK_RATE);
      //Output a simple message to the server's console
      
      log.info("OutboundCallScheduler Timer started.");

    }
  
  private boolean sendOutboundCallRequest(String baseUrl, 
      String tokenid,
      String callerid,
      String numbertodial, 
      int id) {
 // configure connection
    HttpParams params = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
    HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);
    HttpClientParams.setRedirecting(params, false);

    // setup client
    DefaultHttpClient httpclient = new DefaultHttpClient(params);
    URLEncoder ee = new URLEncoder();
    String url = baseUrl + "?numbertodial=" + ee.encode(numbertodial) + "&tokenid=" + tokenid + 
    "&callerid=" + ee.encode(callerid) + "&outboundId=" + id;
    log.info("url=" + url);
    HttpGet httpget = null;
    try {
      httpget = new HttpGet(url);
    } catch (IllegalArgumentException e) {
      log.error(e);
      return false;
    }

    // prepare response and return uploaded
    HttpResponse response = null;
    String responseBody = null;
    try {
        response = httpclient.execute(httpget);
        responseBody = new BufferedReader(
            new InputStreamReader(response.getEntity().getContent())).readLine();
    } catch (ClientProtocolException e) {
        log.error(e);
        return false;
    } catch (IOException e) {
        log.error(e);
        return false;
    } catch (IllegalStateException e) {
        log.error(e);
        return false;
    }
    if (responseBody != null && responseBody.equals("success")) {
      log.info("Response: " + responseBody);
      return true;
    } else {
      log.warn("Response: " + responseBody);
      return false;
    }
    
    
    
  }
}
