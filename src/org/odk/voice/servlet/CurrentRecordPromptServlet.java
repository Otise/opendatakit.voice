package org.odk.voice.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.odk.voice.db.DbAdapter;

/**
 * If an admin voice session is occuring that is recording prompts, returns the current prompt 
 * that is being recorded. This is useful when the admin wants to see the prompt they're recording 
 * on the screen.
 */

public class CurrentRecordPromptServlet extends HttpServlet {
  
  public static String ADDR = "currentRecordPrompt";
	private static final long serialVersionUID = 1L;
	
  private static org.apache.log4j.Logger log = Logger
  .getLogger(CurrentRecordPromptServlet.class);
  
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//	  File f = new File(FileConstants.CURRENT_RECORD_PROMPT_PATH);
//	  if (!f.exists()) {
//	    return;
//	  }
//	  InputStream is = new FileInputStream(f);
//	  int i;
//	  while ((i = is.read()) != -1)
//	    resp.getOutputStream().write((byte) i);
	  DbAdapter dba = null;
	  try {
  	  dba = new DbAdapter();
  	  String currentPrompt = dba.getCurrentRecordPrompt();
  	  resp.getWriter().write(currentPrompt == null ? "" : currentPrompt);
	  } catch (SQLException e) {
	    e.printStackTrace();
	    log.error(e);
	  } finally {
	    if (dba != null) dba.close();
	  }
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	  if (req.getParameter("deleteall").equals("true")) {
	    // delete all prompt audio
	  }
	}

}
