<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ page import="org.odk.voice.db.DbAdapter" %>
<%@ page import="org.odk.voice.db.DbAdapter.FormMetadata" %>
<%@ page import="java.util.List" %>
 
<%
   DbAdapter dba = null;
 List<FormMetadata> formNames = null;
 try {
   dba = new DbAdapter();
   formNames = dba.getForms();
 } finally {
   if (dba != null) {
     dba.close();
   }
   dba = null;
 }
 %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ODK Voice XForm Upload</title>
</head>
<body>
  <h1>ODK Voice XForm Upload</h1>
  <form action="formUpload" enctype="multipart/form-data" method="post">
    <!-- Form name: <input type="text" name="formname" /><br/> -->
    XForm file to be uploaded: <input type="file" name="form"/><br/>
    <input type="submit" value="Upload" />
  </form>
  <div style="margin-top: 40px">
  <b>Currently uploaded forms:</b> <br/>
  <table>
  <% for (FormMetadata md: formNames) { %>
  <tr>
  <td><%= md.getName() %> (<%= md.getTitle() %>)</td>
  <td><form action="formUpload" method="post">
    <input type="hidden" name="delete" value="<%= md.getName() %>" />
    <input type="submit" value="Delete"/>
  </form></td></tr>
  <% } %>
  </table>
  </div>
</body>
</html>