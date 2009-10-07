package org.odk.voice.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.odk.voice.logic.FormVxmlRenderer;
import org.odk.voice.session.VoiceSessionManager;

/**
 * Servlet implementation class FormVxmlServlet
 */
public class FormVxmlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  public static final String ADDR = "formVxml";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FormVxmlServlet() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	  String callerid = req.getParameter("session.callerid");
	  String sessionid = req.getParameter("session.sessionid");
	  String action = req.getParameter("action");
	  String answer = req.getParameter("answer");
	  InputStream binaryData = null;
	  
	  FormVxmlRenderer fvr = new FormVxmlRenderer(resp.getWriter());
	  fvr.renderDialogue(sessionid, callerid, action, answer, binaryData);
	  
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
