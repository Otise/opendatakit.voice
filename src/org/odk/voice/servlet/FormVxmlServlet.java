package org.odk.voice.servlet;

import java.io.IOException;
import java.io.Writer;

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
	  log.info("Entered FormVxmlServlet");
	  String callerid=null, sessionid=null, action=null, answer=null, outboundIdString=null;
	  MultiPartFormData binaryData;
	  if (ServletFileUpload.isMultipartContent(req)) {
	    try {
          binaryData = new MultiPartFormData(req);
          sessionid = getMultipartParam("sessionid", binaryData);
          action = getMultipartParam("action", binaryData);
      } catch (FileUploadException e) {
        log.error("Multipart data in request produced FileUploadException", e);
        return;
      }
	  } else {
	    outboundIdString = req.getParameter("outboundId");
	    // if this is an outbound call, we actually want to see who was CALLED
	    String callerIdKey = outboundIdString == null ? "callerid":"calledid";
	    callerid = req.getParameter("session." + callerIdKey);
	    if (callerid == null) {
	      callerid = req.getParameter(callerIdKey);
	    }
	    if (callerid!=null)
	      callerid = callerid.replace("tel:", "");
	    sessionid = req.getParameter("sessionid");
	    
	    action = req.getParameter("action");
	    answer = req.getParameter("answer");
	    
	    binaryData = null;
	  }
	  
	  FormVxmlRenderer fvr = new FormVxmlRenderer(sessionid, callerid, action, answer, binaryData, resp.getWriter());

    if (outboundIdString != null) {
      fvr.setOutboundId(Integer.parseInt(outboundIdString));
    }
	  fvr.renderDialogue();
	  fvr.close();
	  fvr = null;
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
