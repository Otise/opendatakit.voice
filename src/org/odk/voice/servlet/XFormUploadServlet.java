package org.odk.voice.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.odk.voice.constants.FileConstants;
import org.odk.voice.storage.FileUtils;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.storage.MultiPartFormItem;

/**
 * Servlet for uploading XForms
 */
public class XFormUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  public static final String ADDR = "formUpload";
  
  private static org.apache.log4j.Logger log = Logger
  .getLogger(XFormUploadServlet.class);
  
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
    
    // verify request is multipart
    if(!ServletFileUpload.isMultipartContent(req)) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No multipart content");
      return;
    }
    try {
      // process form
      MultiPartFormData uploadedFormItems = new MultiPartFormData(req);
  
  //      MultiPartFormItem formNameData = uploadedFormItems.getFormDataByFieldName("formname");
      MultiPartFormItem formXmlData = uploadedFormItems.getFormDataByFieldName("form");
      
      String fileName = saveForm(formXmlData);
      resp.getWriter().write("Form " + fileName + " uploaded successfully.");
    } catch(Exception e) {
      log.error("Exception", e);
      e.printStackTrace();
    }
  }
    
  private String saveForm(MultiPartFormItem item) throws FileUploadException, IOException {
    String filename = "form.xml";
    String path = FileConstants.FORMS_PATH + File.separator + filename;
    byte[] data =  item.getData();
    FileUtils.writeFile(data, path, true);
    return filename;
//    String filename = item.getFilename();
//    filename = filename.substring(Math.max(filename.lastIndexOf("/"), filename.lastIndexOf("\\")) + 1);
//    byte[] data =  item.getData();
//    String path = FileConstants.FORMS_PATH + File.separator + filename;
//    String path2 = FileUtils.writeFile(data, path, false);
//    if (path2 == null) 
//      throw new FileUploadException();
//    return path2.substring(path2.lastIndexOf(File.separator) + 1);
  }
}
