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

public class OutboundCallScheduler implements ServletContextListener{

  private static org.apache.log4j.Logger log = Logger
  .getLogger(OutboundCallScheduler.class);
  
  public static final int CONNECTION_TIMEOUT = 30000;
  public static final String SERVER_URL = "http://api.voxeo.net/SessionControl/VoiceXML.start";

  private static final long TIMER_TASK_RATE = 60000;

    public OutboundCallScheduler() {}

    public void contextDestroyed(ServletContextEvent event)
    {

//      //Output a simple message to the server's console
//      System.out.println("The Simple Web App. Has Been Removed");
//      this.context = null;

    }


    //This method is invoked when the Web Application
    //is ready to service requests

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
            String token = dba.getMiscValue(GlobalConstants.OUTBOUND_TOKEN_KEY);
            List<ScheduledCall> calls = dba.getScheduledCalls(Status.PENDING);
            if (calls.size() > 0) {
              String number = calls.get(0).phoneNumber;
              int id = calls.get(0).id;
              boolean success = sendOutboundCallRequest(token, number, id);
              log.info("Outbound call request: number=" + number + "; id=" + id + "; success=" + success);
              dba.setOutboundCallStatus(id, success ? Status.IN_PROGRESS : Status.CALL_FAILED);
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
  
  private boolean sendOutboundCallRequest(String token, String number, int id) {
 // configure connection
    HttpParams params = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
    HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);
    HttpClientParams.setRedirecting(params, false);

    // setup client
    DefaultHttpClient httpclient = new DefaultHttpClient(params);
    String url = SERVER_URL + "?numbertodial=" + number + "&tokenid=" + token + 
    "&outboundId=" + id;
    HttpGet httpget = new HttpGet(url);

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
