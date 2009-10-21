package org.odk.voice.servlet;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.odk.voice.logic.FormVxmlRenderer;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.storage.MultiPartFormItem;

/**
 * Servlet for rendering VoiceXML dialogues
 */
public class FormVxmlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  public static final String ADDR = "formVxml";

  private static org.apache.log4j.Logger log = Logger
  .getLogger(FormVxmlServlet.class);
  

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	  
	  String callerid=null, sessionid=null, action=null, answer=null;
	  MultiPartFormData binaryData;
	  if (ServletFileUpload.isMultipartContent(req)) {
	    try {
          binaryData = new MultiPartFormData(req);
          callerid = getMultipartParam("session.callerid", binaryData);
          sessionid = getMultipartParam("session.sessionid", binaryData);
          action = getMultipartParam("action", binaryData);
        //TODO(alerer): receiving and storing the entire request stream before continuing is very inefficient.
        // We should probably 
      } catch (FileUploadException e) {
        log.error("Multipart data in request produced FileUploadException", e);
        return;
      }
	  } else {
	    callerid = req.getParameter("session.callerid");
	    sessionid = req.getParameter("session.sessionid");
	    action = req.getParameter("action");
	    answer = req.getParameter("answer");
	    binaryData = null;
	  }
	  
	  FormVxmlRenderer fvr = new FormVxmlRenderer(sessionid, callerid, action, answer, binaryData, resp.getWriter());
	  fvr.renderDialogue();
	  
	}
  
  // this is fine because these should be very short strings
  private String getMultipartParam(String param, MultiPartFormData data){
    MultiPartFormItem item = data.getFormDataByFieldName(param);
    if (item == null) return null;
    return new String(item.getData());
  }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
