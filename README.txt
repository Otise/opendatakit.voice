Setup:

* Export as WAR file.
* Install Apache Tomcat 6.0.x
* Install MySQL with root password of 'odk-voice' (this is not safe, should be changed):
    * sudo apt-get install mysql-server-5.0
* Upload ODK Voice WAR file to Tomcat
* Setup a VoiceXML server and point it at {tomcat server address}/odk-voice/formVxmlServlet.
* Upload forms at {}/odk-voice/upload.jsp
* Record prompts by calling in, pressing 7 in the initial menu, then going to {}/odk-voice/record.jsp, 
    and recording whatever prompt is displayed there.
* View {}/odk-voice/logs for logs.