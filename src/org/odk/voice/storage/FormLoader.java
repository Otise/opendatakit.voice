package org.odk.voice.storage;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.xform.util.XFormUtils;
import org.odk.voice.constants.FileConstants;
import org.odk.voice.logic.PropertyPreloadHandler;
import org.odk.voice.xform.FormHandler;


public class FormLoader {

  private static org.apache.log4j.Logger log = Logger
  .getLogger(FormLoader.class);
  
  /**
   * Loads an XForm from the filesystem (or cached binary representations), parses them
   * with javarosa, populates them with a saved data instance if necessary, and returns 
   * a FormHandler.
   * 
   * @param formPath A path to the XForm on the filesystem.
   *
   */
	public static FormHandler getFormHandler(String formPath, String instancePath) {
	    FormHandler fh = null;
	    FormDef fd = null;
	    FileInputStream fis = null;
	    
	    File formXml = new File(formPath);
	    log.info("Form path: " + formPath);
	    if (!formXml.exists()){
	      log.error("Form " + formPath + " does not exist.");
	      return null;
	    }
	    File formBin =
	            new File(FileConstants.CACHE_PATH + File.separator + FileUtils.getMd5Hash(formXml) + ".formdef");
	
	    if (formBin.exists()) {
	      log.info("Form binary exists");
	        // if we have binary, deserialize binary
//	        fd = deserializeFormDef(formBin);
	    } else {
	         // no binary, read from xml
	        try {
	          log.info("Form binary does not exist");
	            fis = new FileInputStream(formXml);
	            fd = XFormUtils.getFormFromInputStream(fis);
	            fd.setEvaluationContext(new EvaluationContext());
	            serializeFormDef(fd, formPath);
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } finally {
	          try { 
	            fis.close();
	          } catch (IOException e) { }
	        }
	    }
	
	    if (fd == null) {
	      log.error("FormDef was null from " + formPath);
	      return null;
	    }
	
	    // create formhandler from formdef
	    fh = new FormHandler(fd);
	
	    // import existing data into formdef
	    if (instancePath != null) {
	        fh.importData(instancePath);
	    }
	
	    // clean up vars
	    fis = null;
	    fd = null;
	    formBin = null;
	    formXml = null;
	    formPath = null;
	    instancePath = null;
	
	    return fh;
	}
	
//    /**
//     * Read serialized {@link FormDef} from file and recreate as object.
//     * 
//     * @param formDef serialized FormDef file
//     * @return {@link FormDef} object
//     */
//    public static FormDef deserializeFormDef(File formDef) {
//
//        // TODO: any way to remove reliance on jrsp?
//
//        // need a list of classes that formdef uses
//        JavaRosaServiceProvider.instance().registerPrototypes(GlobalConstants.SERIALISABLE_CLASSES);
//        FileInputStream fis = null;
//        FormDef fd = null;
//        try {
//            // create new form def
//            fd = new FormDef();
//            fis = new FileInputStream(formDef);
//            DataInputStream dis = new DataInputStream(fis);
//
//            // read serialized formdef into new formdef
//            fd.readExternal(dis, ExtUtil.defaultPrototypes());
//            dis.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (DeserializationException e) {
//            e.printStackTrace();
//        }
//
//
//        return fd;
//    }
    
    /**
     * Write the FormDef to the file system as a binary blob.
     * 
     * @param filepath path to the form file
     */
    public static void serializeFormDef(FormDef fd, String filepath) {

        // if cache folder is missing, create it.
        if (FileUtils.createFolder(FileConstants.CACHE_PATH)) {

            // calculate unique md5 identifier
            String hash = FileUtils.getMd5Hash(new File(filepath));
            File formDef = new File(FileConstants.CACHE_PATH + hash + ".formdef");

            // formdef does not exist, create one.
            if (!formDef.exists()) {
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(formDef);
                    DataOutputStream dos = new DataOutputStream(fos);
                    fd.writeExternal(dos);
                    dos.flush();
                    dos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
