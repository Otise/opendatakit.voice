package org.odk.voice.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.odk.voice.audio.AudioSample;
import org.odk.voice.constants.GlobalConstants;
import org.odk.voice.db.DbAdapter;
import org.odk.voice.storage.MultiPartFormData;


public class PromptHandlerServlet extends HttpServlet {
  
  public static String ADDR = "recordPrompt";
	private static final long serialVersionUID = 1L;
	
  private static org.apache.log4j.Logger log = Logger
  .getLogger(PromptHandlerServlet.class);
  
	/**
   * Used by record.jsp
   * If an admin voice session is occuring that is recording prompts, returns the current prompt 
   * that is being recorded. 
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	  DbAdapter dba = null;
	  try {
  	  dba = new DbAdapter();
  	  String currentPrompt = dba.getMiscValue(GlobalConstants.CURRENT_RECORD_PROMPT_KEY);
  	  resp.getWriter().write(currentPrompt == null ? "" : currentPrompt);
	  } catch (SQLException e) {
	    e.printStackTrace();
	    log.error(e);
	  } finally {
	    if (dba != null) dba.close();
	  }
	}


	/**
	 * Controls prompt handling actions in record.jsp, such as deleting or uploading audio files.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	  DbAdapter dba = null;
	  try {
      dba = new DbAdapter();
      String delete = req.getParameter("delete");
      if (delete != null) {
        int deleteHash = Integer.parseInt(delete);
        dba.deleteAudioPrompt(deleteHash);
      }
      else if (req.getParameter("deleteall") != null) {
	      List<String> prompts = dba.getAudioPrompts();
	      log.info("Size: " + prompts.size());
	      for (String prompt : dba.getAudioPrompts()) {
	        dba.deleteAudioPrompt(prompt);
	      }
	      dba.setMiscValue(GlobalConstants.CURRENT_RECORD_PROMPT_KEY, null);
      } else if (ServletFileUpload.isMultipartContent(req)){
        try {
          // process form
          MultiPartFormData uploadedFormItems = new MultiPartFormData(req);
          String hashString = new String(
              uploadedFormItems.getFormDataByFieldName("upload").getData());
          log.info("Upload=" + hashString);
          int hash = Integer.parseInt(hashString);
          if (!uploadedFormItems.getFormDataByFieldName("data").getFilename().endsWith(".wav")) {
            resp.getWriter().write("Uploaded file must be of type wav.");
            return;
          }
          byte[] data = uploadedFormItems.getFormDataByFieldName("data").getData();
          log.info("Data length: " + data.length);
          AudioSample as = new AudioSample(data);
          //as.clipAudio(0, PROMPT_END_CLIP);
          data = as.getAudio();
          dba.putAudioPrompt(hash, data);
        } catch(Exception e) {
          log.error("Exception", e);
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
          e.printStackTrace();
        }
      }
    } catch (SQLException e) {
      log.error(e);
    }
    resp.sendRedirect("record.jsp");
	}

}
