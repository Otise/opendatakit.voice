<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ page import="org.odk.voice.db.DbAdapter" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ODK Voice Prompt Recorder</title>
<script>
function refresh()
{
  var xmlhttp=new XMLHttpRequest();
  xmlhttp.onreadystatechange=function()
  {
    if(xmlhttp.readyState==4)
    {
      if (xmlhttp.responseText) {
          var promptDiv = document.getElementById("promptDiv");
          var prompt = xmlhttp.responseText;
          promptDiv.innerHTML = prompt;
      }
      setTimeout('refresh()',1000);
    }
  }
  xmlhttp.open("GET","recordPrompt",true);
  xmlhttp.send(null);
}</script>
</head>
<body onload="refresh()">
  <h1>ODK Voice Prompt Recorder</h1>
  
  
  <div id='promptDiv' style="width:600px; padding:20px; border-style:solid; border-color:red"></div>
  
  
  <div id="instructions" style="margin-top:50px; width:600px">
  
  <b>Instructions: </b><p/>Call the ODK Voice server, select a survey (if prompted),
  and when it says "Welcome to the ...", press 7. You will be asked to record prompts 
  over the phone. The prompt you are being asked to record will appear in the red box in 
  real time.</b></div>
  
  <div id="promptlist" style="margin-top:50px">
  <b>Recorded prompts:</b><p/>
  <table border>
  <tr>
  <th>Prompt</th><th>Audio</th><th>Delete</th><th>Upload wav file</th>
  <%  DbAdapter dba = null;
      List<String> prompts = null;
      try {
        dba = new DbAdapter();
        prompts = dba.getAudioPrompts();for (String prompt : prompts) { %>
    <tr>
    <td style="width:500px"><%= StringEscapeUtils.escapeHtml(prompt) %></td>
    <td><a href="../audio/<%= dba.getPromptHash(prompt) %>.wav">Listen</a></td>
    <td><form action="recordPrompt" method="post">
    <input type="hidden" name="delete" value="<%= dba.getPromptHash(prompt) %>"/>
    <input type="submit" value="Delete"/>
    </form></td>
    <td><form action="recordPrompt" enctype="multipart/form-data" method="post">
    <input type="hidden" name="upload" value="<%= dba.getPromptHash(prompt) %>"/>
    <input type="file" name="data"/>
    <input type="submit" value="Upload"/>
    </form></td>
  </form>
    </tr>
  <% } 
   } finally {
   if (dba != null) {
     dba.close();
   }
   dba = null;
 }
 %>
  </table>
  </div>
    <div id='deleteDiv' style="margin-top:25px">
    <form method="post" action="recordPrompt">
      <input type="hidden" name="deleteall" value="true"/>
      <input type="submit" value="Delete all prompts"/>
    </form>
  </div>
</body>
</html>