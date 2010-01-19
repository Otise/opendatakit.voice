package org.odk.voice.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.odk.voice.constants.FileConstants;

/**
 * Servlet implementation class LogServlet
 */
public class LogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	  String sBytes = request.getParameter("bytes");
//	  int bytes = 10000;
//	  try {
//	    bytes = Integer.valueOf(sBytes);
//	  } catch (NumberFormatException e) {}
    
	  InputStream is = null;
    
    try {
      File f = new File(FileConstants.LOG_FILE);
      is = new FileInputStream(f);
      // Get the size of the file
      long length = f.length();
      
      // Output the file.
      int offset = 0;
      while (offset < length) {
          response.getWriter().write(is.read());
          offset++;
      }
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
        // Close the input stream
        try {
            if (is != null)
              is.close();
        } catch (IOException e) {}
    }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
