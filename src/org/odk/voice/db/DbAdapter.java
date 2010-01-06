package org.odk.voice.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;


public class DbAdapter {
 
  private static org.apache.log4j.Logger log = Logger
  .getLogger(DbAdapter.class);
  
  public static final String DB_CLASS = "com.mysql.jdbc.Driver";
  public static final String DB_URL = "jdbc:mysql://localhost:3306";
  public static final String DB_NAME = "odkvoice";
  public static final String DB_USER = "root";
  public static final String DB_PASS = "odk-voice";
  private static boolean initialized = false;
  
  Connection con = null;
  
  public DbAdapter() throws SQLException {
    try {
      Class.forName(DB_CLASS);
    } catch (ClassNotFoundException e) {
      throw new SQLException("Class not found: " + DB_CLASS);
    }
    if (!initialized)
      createDb();
    String url = DB_URL + "/" + DB_NAME;
    con = DriverManager.getConnection(url, DB_USER, DB_PASS);
    if (!initialized)
      initDb();
    initialized = true;
  }

  /**
   * Creates a new XForm instance.
   * preconditions: callerid.length < 50
   * @param callerid
   * @return the id of the new instance.
   * @throws SQLException
   */
  public int createInstance(String callerid) throws SQLException {
    String q = "INSERT INTO instance (callerid) VALUES (?);";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setString(1, callerid);
    stmt.executeUpdate();
    
    // a hack to get the id of the inserted (auto_incremented) row
    stmt = con.prepareStatement("SELECT MAX(id) FROM instance");
    ResultSet rs = stmt.executeQuery();
    rs.next();
    return rs.getInt("MAX(id)");
  }
  
  /**
   * 
   * @param callerid The callerid of a voice session.
   * @return An array of instance ids of any uncompleted instances (surveys)
   * from that callerid. 
   * @throws SQLException
   */
  public int[] getUncompletedInstances(String callerid) throws SQLException {
    String q = "SELECT id FROM instance WHERE callerid=?;";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setString(1, callerid);
    ResultSet rs = stmt.executeQuery();
    
    List<Integer> l = new ArrayList<Integer>();
    
    while (rs.next()) {
      l.add(rs.getInt("id"));
    }
    
    int[] res = new int[l.size()];
    for (int i = 0; i < l.size(); i ++) res[i] = l.get(i);
      
    return res;
  }
  
  /**
   * 
   * @param instanceId The instanceId for the instance.
   * @param xml The XML representation of the (partially or fully) completed 
   * XForm data model for this instance.
   * @throws SQLException
   */
  public void setInstanceXml(int instanceId, byte[] xml) throws SQLException {
    String q = "UPDATE instance SET xml=? WHERE id=?;";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setBytes(1, xml);
    stmt.setInt(2, instanceId);
    stmt.executeUpdate();
  }
  
  /**
   * Marks an instance as completed or uncompleted in the database.
   * 
   * @param instanceId
   * @param completed True if this instance is completed (i.e. the survey was completed).
   * False otherwise.
   * @throws SQLException
   */
  public void markInstanceCompleted(int instanceId, boolean completed) throws SQLException {
    String q = "UPDATE instance SET completed=? WHERE instance=?;";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setBoolean(1, completed);
    stmt.setInt(2, instanceId);
    stmt.executeUpdate();
  }
  
  /**
   * Get the XML representation of the data model of an XForm instance.
   * @param instanceId
   * @return A byte array representation of the XForm data model, or null if (a) the instance 
   * does not exist, or (b) the instance has no associated xml.
   * @throws SQLException
   */
  public byte[] getInstanceXml(int instanceId) throws SQLException {
    String q = "SELECT xml FROM instance WHERE id=?;";
    
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setInt(1, instanceId);
    ResultSet rs = stmt.executeQuery();
    
    if (rs.next()) {
      return rs.getBytes("xml");
    } else {
      return null;
    }
  }
  
  /**
   * Stores a binary blob (e.g. audio file, image) associated with a given instance.
   * 
   * @param instanceId
   * @param binaryName 
   * @param binary The binary file.
   * @return The id of the binary, which can be used to identify it when it is requrested from the db.
   * @throws SQLException
   */
  public boolean addBinaryToInstance(int instanceId, String binaryName, String mimeType, byte[] binary) throws SQLException {
    String q = "INSERT INTO instance_binary (instanceid, name, mimeType, data) " +
      "VALUES (?,?,?,?)";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setInt(1, instanceId);
    stmt.setString(2, binaryName);
    stmt.setString(3, mimeType);
    stmt.setObject(4, binary);
    stmt.executeUpdate();
    
    return true;
  }
  
  /**
   * A data structure for an instance binary.
   * @author alerer
   *
   */
  public static class InstanceBinary{
    public String name;
    public byte[] binary;
    public String mimeType;
    InstanceBinary (String name, String mimeType, byte[] binary){
      this.name = name;
      this.mimeType = mimeType;
      this.binary = binary;
    } 
  }
  
  /**
   * 
   * @param instanceId
   * @return A List of all instance binaries associated with the given instanceId.
   * Each InstanceBinary contains an id (returned from {@link addBinaryToInstance},
   * and a byte array of data.
   * @throws SQLException
   */
  public List<InstanceBinary> getBinariesForInstance(int instanceId) throws SQLException {
    String q = "SELECT name, mimeType, data FROM instance_binary WHERE instanceid=?;";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setInt(1, instanceId);
    ResultSet rs = stmt.executeQuery();
    
    List<InstanceBinary> l = new ArrayList<InstanceBinary>();
    
    while (rs.next()) {
      String name = rs.getString("name");
      String mimeType = rs.getString("mimeType");
      byte[] data = rs.getBytes("data");
      l.add(new InstanceBinary(name, mimeType, data));
    }
    return l;
  }
  
  /**
   * Add an XForm to the database. If a form with this name already exists, 
   * it is overwritten.
   * @param name Form name.
   * @param xml The XML representation of the XForm.
   * @throws SQLException
   */
  public void addForm(String name, byte[] xml) throws SQLException {
    String q = "REPLACE INTO form (name, xml) VALUES (?,?);";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setString(1, name);
    stmt.setObject(2, xml);
    stmt.executeUpdate();
  }
  
  /**
   * @param name Name of the XForm.
   * @return the XForm from the database with this name. If no form with this name exists,
   * returns null.
   * @throws SQLException
   */
  public byte[] getFormXml(String name) throws SQLException {
    String q = "SELECT xml FROM form WHERE name=?;";
    
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setString(1, name);
    ResultSet rs = stmt.executeQuery();
    
    if (rs.next()) {
      return rs.getBytes("xml");
    } else {
      return null;
    }
  }
  
  /**
   * Sets the binary associated with a form for faster loading.
   * @param formname
   * @param formdef
   * @throws SQLException
   */
  public void setFormBinary(String formname, byte[] formdef) throws SQLException {
    String q = "UPDATE form SET formdef=? WHERE name=?;";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setString(2, formname);
    stmt.setObject(1, formdef);
    stmt.executeUpdate();
  }
  
  /**
   * Gets the binary associated with a form, used for faster loading.
   * @param formname
   * @return
   * @throws SQLException
   */
  public byte[] getFormBinary(String formname) throws SQLException {
    String q = "SELECT formdef FROM form WHERE name=?;";
    
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setString(1, formname);
    ResultSet rs = stmt.executeQuery();
    
    if (rs.next()) {
      return rs.getBytes("formdef");
    } else {
      return null;
    }
  }
  
  
  public byte[] getAudioPrompt(String prompt) {
    return getAudioPrompt(getPromptHash(prompt));
  }
  
  public byte[] getAudioPrompt(int prompthash) {
    log.info("getAudioPrompt: " + prompthash);
    try {
      String q = "SELECT data FROM audio_prompt WHERE prompthash=?;";
      
      PreparedStatement stmt = con.prepareStatement(q);
      stmt.setInt(1, prompthash);
      ResultSet rs = stmt.executeQuery();
      
      if (rs.next()) {
        //log.info("get audio prompt success: " + prompthash);
        return rs.getBytes("data");
  //      Blob dataBlob = rs.getBlob("data");
  //      return dataBlob.getBytes(1L, (int) dataBlob.length());
      } else {
        //log.info("get audio prompt failure: " + prompthash);
        return null;
      }
    } catch (SQLException e) {
      log.error(e);
      return null;
    }
  }
  
  public List<String> getAudioPrompts(){
    try {
      String q = "SELECT prompt FROM audio_prompt;";
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(q);
      List<String> res = new ArrayList<String>();
      while (rs.next()) {
        res.add(rs.getString("prompt"));
      }
      return res;
    } catch (SQLException e) {
      log.error(e);
      return null;
    }
  }
  
  public boolean deleteAudioPrompt(String prompt) {
    log.info("Deleting audio prompt: " + prompt);
    return deleteAudioPrompt(getPromptHash(prompt));
  }
  
  public boolean deleteAudioPrompt(int prompthash) {
    try {
      String q = "DELETE FROM audio_prompt WHERE prompthash=?";
      PreparedStatement stmt = con.prepareStatement(q);
      stmt.setInt(1, prompthash);
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      log.error(e);
      return false;
    }
  }
  
  /**
   * Note: getPromptHash(null) == getPromptHash("");
   * @param prompt The audio prompt.
   * @return The hash of the audio prompt used by the database.
   */
  public int getPromptHash(String prompt) {
    if (prompt == null) return 0;
    return prompt.hashCode();
  }
    
  public void putAudioPrompt(String prompt, byte[] data) throws SQLException {
    log.info("putAudioPrompt: " + prompt);
    String q = "REPLACE INTO audio_prompt (prompthash, prompt, " +
    "data) VALUES (?,?,?);";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setInt(1, getPromptHash(prompt));
    stmt.setString(2, prompt);
    stmt.setObject(3, data);
    stmt.executeUpdate();
  }
  
  private static final String CURRENT_RECORD_PROMPT_KEY = "currentrecordprompt";
  
  public void setCurrentRecordPrompt(String prompt) throws SQLException {
    String q = "REPLACE INTO misc (k, v) VALUES (?,?);";
    PreparedStatement stmt = con.prepareStatement(q);
    log.info("set record prompt: " + prompt);
    stmt.setString(1, CURRENT_RECORD_PROMPT_KEY);
    stmt.setString(2, prompt);
    stmt.executeUpdate();
  }
  public String getCurrentRecordPrompt() throws SQLException {
    String q = "SELECT v FROM misc WHERE k=?;";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setString(1, CURRENT_RECORD_PROMPT_KEY);
    ResultSet rs = stmt.executeQuery();
    
    if (rs.next()) {
      return rs.getString("v");
    } else {
      return null;
    }
  }
  
  //--------------------- INTERNAL METHODS --------------------------
  
  private String escape(String s) {
    return StringEscapeUtils.escapeSql(s);
  }
  
  /**
   * Creates the database if it hasn't already been created.
   * @throws SQLException
   */
  private void createDb() throws SQLException {
    Connection con =
      DriverManager.getConnection(
                  DB_URL, DB_USER, DB_PASS);

    Statement stmt = con.createStatement();
    stmt.executeUpdate(
        "CREATE DATABASE IF NOT EXISTS " + DB_NAME + ";");
    con.close();
  }
  
  public void close() {
    try {
      con.close();
      con = null;
    } catch (SQLException e) {
      // not much we can do here
      log.error(e);
    }
  }
  
  /**
   * Initializes the tables in the database.
   * @throws SQLException
   */
  protected void initDb() throws SQLException {
    Statement stmt = con.createStatement();
    stmt.execute(
        "CREATE TABLE IF NOT EXISTS instance (" + 
             "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," + 
             "callerid VARCHAR(50)," +
             "completed BOOLEAN DEFAULT FALSE," +
             "xml MEDIUMTEXT );"
      );
    stmt.execute(
        "CREATE TABLE IF NOT EXISTS instance_binary (" + 
            "instanceid INT," + 
            "name VARCHAR(200)," +
            "mimeType VARCHAR(20)," +
            "data MEDIUMBLOB," + 
            "PRIMARY KEY (instanceid, name)," +
            "FOREIGN KEY (instanceid) REFERENCES instance(id) );"
      );
    stmt.execute(
        "CREATE TABLE IF NOT EXISTS form ( " + 
            "name VARCHAR(100) NOT NULL PRIMARY KEY," +
            "xml MEDIUMTEXT," +
            "formdef MEDIUMBLOB );"
      );
    stmt.execute(
        "CREATE TABLE IF NOT EXISTS audio_prompt (" + 
            "prompthash INT NOT NULL PRIMARY KEY," +
            "prompt VARCHAR(10000)," + 
            "data MEDIUMBLOB );"
        );
    stmt.execute(
        "CREATE TABLE IF NOT EXISTS misc (" + 
            "k VARCHAR(100) NOT NULL PRIMARY KEY," + 
            "v VARCHAR(10000) );"
        );
  }
  
  /**
   * Resets the database, deleting all data.
   * @throws SQLException
   */
  protected void resetDb() throws SQLException {
    Statement stmt = con.createStatement();
    //stmt.execute("DROP DATABASE " + DB_NAME);
    stmt.execute("DROP TABLE instance_binary");
    stmt.execute("DROP TABLE instance;");
    stmt.execute("DROP TABLE form;");
    stmt.execute("DROP TABLE audio_prompt;");
    stmt.execute("DROP TABLE misc;");
    initDb();
  }

}