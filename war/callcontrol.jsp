<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ page import="org.odk.voice.db.DbAdapter" %>
<%@ page import="java.util.List" %>
<%@ page import="org.odk.voice.schedule.ScheduledCall" %>
<%@ page import="org.odk.voice.constants.GlobalConstants" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ODK Voice Outbound Call Control</title>
</head>
<body>
  <h1>ODK Voice Outbound Call Control</h1>
  
  <div id="outboundFormDiv">
    <form action="outboundSchedule" method="post">
        <b>Enter the phone numbers, one per line:</b><br/>
        <textarea cols="50" rows="10" name="phoneNumbers"></textarea>
        <!--  <select name="frequency">
		  <option value="once">Once</option>
		  <option value="daily">Daily</option>
		  <option value="weekly">Weekly</option>
		  <option value="monthly">monthly</option>
		</select><p/> 
		<b>Note: </b>Calls will be scheduled starting immediately. If you select daily, 
		calls will be scheduled at this time every day.<p/> -->
		<br/><input type="submit" value="Schedule"/>
    </form>
  </div>
  
  <div id="callQueueDiv" style="margin-top:50px">
  <b>Call Queue</b><br/>
  <table border>
  <tr>
  <th>Phone Number</th><th>Status</th><th>Retry</th><th>Delete</th>
  <%  DbAdapter dba = null;
      List<ScheduledCall> calls = null;
      try {
        dba = new DbAdapter();
        calls = dba.getScheduledCalls(null);
        for (ScheduledCall call : calls) { %>
    <tr style="background-color:<%= call.status.color %>">
    <td style="padding-left:20px;padding-right:20px"><%= StringEscapeUtils.escapeHtml(call.phoneNumber) %></td>
    <td><%= StringEscapeUtils.escapeHtml(call.status.name()) %></td>
    <td><form action="outboundSchedule" method="post">
    <input type="hidden" name="retry" value="<%= call.id %>"/>
    <input type="submit" <%= call.status.equals(ScheduledCall.Status.PENDING) || call.status.equals(ScheduledCall.Status.IN_PROGRESS) ? "disabled=\"true\"" : "" %> value="Retry"/>
    </form></td>
    <td><form action="outboundSchedule" method="post">
    <input type="hidden" name="delete" value="<%= call.id %>"/>
    <input type="submit" value="Delete"/>
    </form></td>
    </tr>
  <% }  %>
  </table>
  </div>
  
  <!-- 
    <div id='statusDiv' style="width:600px; padding:20px; border-style:solid; border-color:red"></div>
  -->
  
  <div id='urlDiv' style="margin-top:100px"><form action="outboundSchedule" method="post">
  <b>Outbound dialing token: </b><br/>
  <% String url = dba.getMiscValue(GlobalConstants.OUTBOUND_TOKEN_KEY); url = (url==null) ? "" : url; %>
  <input style="width:600px" type="text" name="outboundUrl" value="<%= StringEscapeUtils.escapeHtml(url) %>"/>
  <br/><input type="submit" value="Update"/>
  </form></div>
  
  <%
     } finally {
   if (dba != null) {
     dba.close();
   }
   dba = null;
 }
 %>
</body>
</html>