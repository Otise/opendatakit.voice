package org.odk.voice.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.odk.voice.constants.FileConstants;
import org.odk.voice.db.DbAdapter;
import org.odk.voice.storage.FileUtils;
import org.odk.voice.storage.FormLoader;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.storage.MultiPartFormItem;
import org.odk.voice.xform.FormHandler;

/**
 * Servlet for uploading XForms
 */
public class XFormUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  public static final String ADDR = "admin/formUpload";
  
  private static org.apache.log4j.Logger log = Logger
  .getLogger(XFormUploadServlet.class);
  
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String viewFormname = req.getParameter("view");
    
    DbAdapter dba = null;
    try {
      // You can uncomment these lines to enable a (dangerous) backdoor to reset the db
      //String resetDb = req.getParameter("resetDb");
      // if (resetDb != null) {
      //   dba = new DbAdapter();
      //   dba.resetDb();
      // }
      if (viewFormname != null) {
        dba = new DbAdapter();
        byte[] xml = dba.getFormXml(viewFormname);
        if (xml != null) {
          resp.setContentType("text/xml");
          resp.setHeader("Content-disposition", "attachment; filename=" + viewFormname);
          resp.getWriter().write(new String(xml));
          return;
        } else {
          log.error("Invalid form name.");
          return;
        }
      }
    } catch (SQLException e) {
      log.error("SQLException");
    } finally {
      if (dba != null) {
        dba.close();
      }
      dba = null;
    }
  }
  
  /**
   * Handler for HTTP Post request that takes an xform, parses, and saves a 
   * parsed version in the datastore 
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    String deleteFormname = req.getParameter("delete");
    // delete form
    if (deleteFormname != null) {
      DbAdapter dba = null;
      try {
        dba = new DbAdapter();
        boolean success = dba.deleteForm(deleteFormname);
        if (success) {
          resp.sendRedirect("upload.jsp");
          return;
        } else {
          log.error("Invalid form name.");
          return;
        }
      } catch (SQLException e) {
        log.error("SQLException");
      } finally {
        if (dba != null) {
          dba.close();
        }
        dba = null;
      }
    }
    
    // upload form
    
    // verify request is multipart
    if(!ServletFileUpload.isMultipartContent(req)) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No multipart content");
      return;
    }
    
    try {
      // process form
      MultiPartFormData uploadedFormItems = new MultiPartFormData(req);
  
  //      MultiPartFormItem formNameData = uploadedFormItems.getFormDataByFieldName("formname");
      MultiPartFormItem item = uploadedFormItems.getFormDataByFieldName("form");
      
      String filename = item.getFilename();
      String formTitle = null;
      String path = FileConstants.FORMS_PATH + File.separator + filename;
      byte[] data =  item.getData();
      
      try {
        FormHandler fh = FormLoader.getFormHandler(data, null);
        formTitle = fh.getFormTitle();
      } catch (Exception e) {
        log.error(e);
        e.printStackTrace(resp.getWriter());
        return;
      }
      
      DbAdapter dba = null;
      try {
        dba = new DbAdapter();
        dba.addForm(filename, formTitle, data);
      } finally {
        if (dba != null) {
          dba.close();
        }
        dba = null;
      }
   // right now, we write to database AND to file
      FileUtils.writeFile(data, path, true);
      //String fileName = saveForm(formXmlData);
      //resp.getWriter().write("Form " + fileName + " uploaded successfully.");
      resp.sendRedirect("upload.jsp");
    } catch(Exception e) {
      log.error("Exception", e);
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
      e.printStackTrace();
    }
  }
    
}
