package org.odk.voice.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.xform.util.XFormUtils;
import org.odk.voice.constants.FileConstants;
import org.odk.voice.db.DbAdapter;
import org.odk.voice.xform.FormHandler;


public class FormLoader {

  private static org.apache.log4j.Logger log = Logger
  .getLogger(FormLoader.class);
  
  public static FormHandler getFormHandler(byte[] formXml, byte[] formBin) {
    FormHandler fh = null;
    FormDef fd = null;
    InputStream is = new ByteArrayInputStream(formXml);
    fd = XFormUtils.getFormFromInputStream(is);
    fd.setEvaluationContext(new EvaluationContext());
    
  // create formhandler from formdef
  return new FormHandler(fd);
  }
  
  /**
   * Loads an XForm from the database, parses them
   * with javarosa, populates them with a saved data instance if necessary, and returns 
   * a FormHandler.
   * 
   * @param formName the name of the form in the database.
   * @param instanceId the ID of the instance in the database, or null for no instance.
   *
   */
	public static FormHandler getFormHandler(String formName, Integer instanceId) {
	    DbAdapter dba = null;
	    FormHandler fh = null;
	    try {
	      dba = new DbAdapter();
	      byte[] formXml = dba.getFormXml(formName);
	      if (formXml == null) return null;
	      byte[] formBin = dba.getFormBinary(formName);
	      
	      fh = getFormHandler(formXml, formBin);
	      ByteArrayOutputStream out = new ByteArrayOutputStream();
	      
	      // write form binary
	      // we're not using this at the moment, but it could speed up form loading
	      try {
	        fh.getForm().writeExternal(new DataOutputStream(out));
	        dba.setFormBinary(formName,out.toByteArray());
	      } catch (IOException e) {
	        log.error(e);
	      }
	      
	      // import existing data into formdef
	      if (instanceId != null) {
	          byte[] xml = dba.getInstanceXml(instanceId);
	          if (xml != null) {
	            fh.importData(xml);
	          }
	      }
	    } catch (SQLException e) {
	      log.error(e);
	      return null;
	    } finally {
	      dba.close();
	    }
	
	    return fh;
	}

}
