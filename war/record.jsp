<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
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
  xmlhttp.open("GET","currentRecordPrompt",true);
  xmlhttp.send(null);
}</script>
</head>
<body onload="refresh()">
  <h1>ODK Voice Prompt Recorder</h1>
  <div id='promptDiv'></div>
  <div id='listDiv'>
    <form method="post" action="currentRecordPrompt">
      <input type="hidden" name="deleteall" value="true"/>
      <input type="submit" value="Delete all prompts"/>
    </form>
  </div>
</body>
</html>