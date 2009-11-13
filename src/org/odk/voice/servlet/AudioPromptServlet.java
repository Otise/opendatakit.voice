package org.odk.voice.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.odk.voice.constants.FileConstants;
import org.odk.voice.storage.FileUtils;

/**
 * Servlet for serving recorded audio prompts.
 */

public class AudioPromptServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
  private static org.apache.log4j.Logger log = Logger
  .getLogger(AudioPromptServlet.class);
  
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	  String pathInfo = req.getPathInfo();
	  log.info("AudioPromptServlet called. pathInfo: " + pathInfo);
	  String path = FileConstants.PROMPT_AUDIO_PATH + File.separator + pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
	   log.info("Path: " + path);
	  File f = new File(path);
	  if (!f.exists()) {
	    resp.sendError(404);
	    return;
	  }
	  log.info("Found audio");
	  resp.setContentType("audio/x-wav");
	  resp.getOutputStream().write(FileUtils.getFileAsBytes(f));
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}

}
