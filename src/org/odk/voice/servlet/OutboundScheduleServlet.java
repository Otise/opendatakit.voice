package org.odk.voice.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.odk.voice.constants.GlobalConstants;
import org.odk.voice.db.DbAdapter;
import org.odk.voice.schedule.ScheduledCall.Status;

/**
 * Servlet implementation class OutboundScheduleServlet
 */
public class OutboundScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = Logger
  .getLogger(OutboundScheduleServlet.class);
	private static final long H2MS = 1000 * 60 * 60;
	
	public static final String ADDR = "admin/outboundSchedule";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OutboundScheduleServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  String outboundUrl = request.getParameter("outboundUrl");
	  String outboundTokenid = request.getParameter("outboundTokenid");
	  String outboundCallerid = request.getParameter("outboundCallerid");
	  String scheduleTimes = request.getParameter("scheduleTimes");
	  String phoneNumbers = request.getParameter("phoneNumbers");
	  String fromS = request.getParameter("timeFrom"); 
	  String toS = request.getParameter("timeTo");
	  String intervalS = request.getParameter("timeInterval");
	  String now = request.getParameter("now");
	  String retry = request.getParameter("retry");
	  String delete = request.getParameter("delete");
    DbAdapter dba = null;
    
    try {
      dba = new DbAdapter();
      // --------------- misc settings ------------
      if (outboundUrl != null) {
        dba.setMiscValue(GlobalConstants.OUTBOUND_URL_KEY, outboundUrl);
      }
      if (outboundTokenid != null) {
        dba.setMiscValue(GlobalConstants.OUTBOUND_TOKEN_KEY, outboundTokenid);
      }
      if (outboundCallerid != null) {
        dba.setMiscValue(GlobalConstants.OUTBOUND_CALLERID_KEY, outboundCallerid);
      }
      // ------------------------------------------

      if (retry != null) {
        int retryInt = Integer.parseInt(retry);
        dba.setOutboundCallStatus(retryInt, Status.PENDING);
      }
  		if (delete != null) {
  		  int deleteInt = Integer.parseInt(delete);
  		  dba.deleteOutboundCall(deleteInt);
  		}
  		if (phoneNumbers != null) {
  	    String[] phoneNumberArray = phoneNumbers.split("\n");
  	    Date from=null, to=null; long intervalMs = -1;
  	    if (!"true".equals(now)) {
    	    try {
    	      double fromD = Double.valueOf(fromS);
    	      from = new Date(Math.round(new Date().getTime() + fromD * H2MS));
    	    } catch (NumberFormatException e) { response.sendError(401, "Invalid schedule."); }
          try {
            double toD = Double.valueOf(toS);
            to = new Date(Math.round(new Date().getTime() + toD * H2MS));
          } catch (NumberFormatException e) { response.sendError(401, "Invalid schedule.");}
          try {
            double intervalH = Double.valueOf(intervalS);
            intervalMs = Math.round(intervalH * H2MS);
          } catch (NumberFormatException e) { response.sendError(401, "Invalid schedule."); }
  	    }
        
    		for (String phoneNumber : phoneNumberArray) {
    		  phoneNumber = phoneNumber.replace("\r", "");
    		  dba.addOutboundCall(phoneNumber, from, to, intervalMs);
    		}
  		}
  		response.sendRedirect("callcontrol.jsp");
		} catch (SQLException e) {
  		  log.error(e);
		} finally {
		  if (dba != null) {
		    dba.close();
		  }
	  dba = null;
		}
		
	}

}
