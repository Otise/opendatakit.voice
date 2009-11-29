package org.odk.voice.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;


public class DbAdapter {
 
  public static final String DB_URL = "jdbc:mysql://localhost:3306";
  public static final String DB_NAME = "odkvoice";
  public static final String DB_USER = "root";
  public static final String DB_PASS = "odk-voice";
  
  Connection con = null;
  
  public DbAdapter() throws SQLException, ClassNotFoundException {
    Class.forName("com.mysql.jdbc.Driver");
    createDb();
    String url = DB_URL + "/" + DB_NAME;
    con = DriverManager.getConnection(url, DB_USER, DB_PASS);
    initDb();
  }
  
//      try {
//        Statement stmt;
//        ResultSet rs;
//
//        //Register the JDBC driver for MySQL.
//        Class.forName("com.mysql.jdbc.Driver");
//
//        //Define URL of database server for
//        // database named JunkDB on the localhost
//        // with the default port number 3306.
//        
//
//        //Get a connection to the database for a
//        // user named auser with the password
//        // drowssap, which is password spelled
//        // backwards.
//        Connection con =
//                       DriverManager.getConnection(
//                          url,"auser", "drowssap");
//
//        //Display URL and connection information
//        System.out.println("URL: " + url);
//        System.out.println("Connection: " + con);
//
//        //Get a Statement object
//        stmt = con.createStatement();
//
//        //As a precaution, delete myTable if it
//        // already exists as residue from a
//        // previous run.  Otherwise, if the table
//        // already exists and an attempt is made
//        // to create it, an exception will be
//        // thrown.
//        try{
//          stmt.executeUpdate("DROP TABLE myTable");
//        }catch(Exception e){
//          System.out.print(e);
//          System.out.println(
//                    "No existing table to delete");
//        }//end catch
//
//        //Create a table in the database named
//        // myTable.
//        stmt.executeUpdate(
//              "CREATE TABLE myTable(test_id int," +
//                    "test_val char(15) not null)");
//
//        //Insert some values into the table
//        stmt.executeUpdate(
//                  "INSERT INTO myTable(test_id, " +
//                      "test_val) VALUES(1,'One')");
//        stmt.executeUpdate(
//                  "INSERT INTO myTable(test_id, " +
//                      "test_val) VALUES(2,'Two')");
//        stmt.executeUpdate(
//                  "INSERT INTO myTable(test_id, " +
//                    "test_val) VALUES(3,'Three')");
//        stmt.executeUpdate(
//                  "INSERT INTO myTable(test_id, " +
//                     "test_val) VALUES(4,'Four')");
//        stmt.executeUpdate(
//                  "INSERT INTO myTable(test_id, " +
//                     "test_val) VALUES(5,'Five')");
//
//        //Get another statement object initialized
//        // as shown.
//        stmt = con.createStatement(
//                 ResultSet.TYPE_SCROLL_INSENSITIVE,
//                       ResultSet.CONCUR_READ_ONLY);
//
//        //Query the database, storing the result
//        // in an object of type ResultSet
//        rs = stmt.executeQuery("SELECT * " +
//                  "from myTable ORDER BY test_id");
//
//        //Use the methods of class ResultSet in a
//        // loop to display all of the data in the
//        // database.
//        System.out.println("Display all results:");
//        while(rs.next()){
//          int theInt= rs.getInt("test_id");
//          String str = rs.getString("test_val");
//          System.out.println("\ttest_id= " + theInt
//                               + "\tstr = " + str);
//        }//end while loop
//
//        //Display the data in a specific row using
//        // the rs.absolute method.
//        System.out.println(
//                          "Display row number 2:");
//        if( rs.absolute(2) ){
//          int theInt= rs.getInt("test_id");
//          String str = rs.getString("test_val");
//          System.out.println("\ttest_id= " + theInt
//                               + "\tstr = " + str);
//        }//end if
//
//        //Delete the table and close the connection
//        // to the database
//        stmt.executeUpdate("DROP TABLE myTable");
//        con.close();
//      }catch( Exception e ) {
//        e.printStackTrace();
//      }//end catch
//    }//end main
//  }//end class Jdbc10
  

  /**
   * preconditions: callerid.length < 50
   */
  public int createInstance(String callerid) throws SQLException {
    String q = "INSERT INTO instance (callerid) VALUES (?);";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setString(1, callerid);
    stmt.executeUpdate();
    stmt = con.prepareStatement("SELECT callerid, id, MAX(id) FROM instance");
    stmt.executeQuery();
    
    // a hack to get the id of the inserted (auto_incremented) row
    stmt = con.prepareStatement("SELECT MAX(id) FROM instance");
    ResultSet rs = stmt.executeQuery();
    rs.next();
    return rs.getInt("MAX(id)");
  }
  
  public int[] getUncompletedInstances(String callerid) throws SQLException {
    String q = "SELECT id FROM instance WHERE callerid=?;";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setString(1, callerid);
    ResultSet rs;
    rs = stmt.executeQuery();
    
    List<Integer> l = new ArrayList<Integer>();
    
    while (rs.next()) {
      l.add(rs.getInt("id"));
    }
    
    int[] res = new int[l.size()];
    for (int i = 0; i < l.size(); i ++) res[i] = l.get(i);
      
    return res;
  }
  
  public void setInstanceXml(int instanceId, InputStream xml) throws SQLException {
    String q = "UPDATE instance SET xml=? WHERE id=?;";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setBinaryStream(1, xml);
    stmt.setInt(2, instanceId);
    stmt.executeUpdate();
  }
  
  public void markInstanceCompleted(int instanceId, boolean completed) throws SQLException {
    String q = "UPDATE instance SET completed=? WHERE instance=?;";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setBoolean(1, completed);
    stmt.setInt(2, instanceId);
    stmt.executeUpdate();
  }
  
  public byte[] getInstanceXml(int instanceId) throws SQLException {
    String q = "SELECT xml FROM instance WHERE id=?;";
    
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setInt(1, instanceId);
    ResultSet rs;
  
    rs = stmt.executeQuery();
    
    if (rs.next()) {
      return rs.getBytes("xml");
//      Blob dataBlob = rs.getBlob("xml");
//      return dataBlob.getBytes(1L, (int) dataBlob.length());
    } else {
      return null;
    }
  }
  
  public int addBinaryToInstance(int instanceId, byte[] binary) throws SQLException {
    String q = "INSERT INTO instance_binary (instanceid, data) " +
      "VALUES (?,?)";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setInt(1, instanceId);
    stmt.setObject(2, binary);
    stmt.executeUpdate();
    
    // a hack to get the id of the inserted (auto_incremented) row
    stmt = con.prepareStatement("SELECT MAX(id) FROM instance_binary");
    ResultSet rs = stmt.executeQuery();
    rs.next();
    return rs.getInt("MAX(id)");
  }
  
  static class InstanceBinary{
    public int id;
    public byte[] binary;
    InstanceBinary (int id, byte[] binary){
      this.id = id;
      this.binary = binary;
    } 
  }
  
  public List<InstanceBinary> getBinariesForInstance(int instanceId) throws SQLException {
    String q = "SELECT id, data FROM instance_binary WHERE instanceid=?;";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setInt(1, instanceId);
    ResultSet rs;
    rs = stmt.executeQuery();
    
    List<InstanceBinary> l = new ArrayList<InstanceBinary>();
    
    while (rs.next()) {
      int id = rs.getInt("id");
      byte[] data = rs.getBytes("data");
      l.add(new InstanceBinary(id,data));
    }
    return l;
  }
  
  public int addForm(String name, byte[] xml) throws SQLException {
    String q = "REPLACE INTO form (name, xml) VALUES (?,?);";
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setString(1, name);
    stmt.setObject(2, xml);
    return stmt.executeUpdate();
  }
  
  public byte[] getForm(String name) throws SQLException {
    String q = "SELECT xml FROM form WHERE name=?;";
    
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setString(1, name);
    ResultSet rs;
  
    rs = stmt.executeQuery();
    
    if (rs.next()) {
      return rs.getBytes("xml");
//      Blob dataBlob = rs.getBlob("xml");
//      return dataBlob.getBytes(1L, (int) dataBlob.length());
    } else {
      return null;
    }
  }
  
  public byte[] getAudioPrompt(String prompt) throws SQLException {
    String q = "SELECT data FROM audio_prompt WHERE prompt=?;";
    
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setString(1, prompt);
    ResultSet rs;
  
    rs = stmt.executeQuery();
    
    if (rs.next()) {
      return rs.getBytes("data");
//      Blob dataBlob = rs.getBlob("data");
//      return dataBlob.getBytes(1L, (int) dataBlob.length());
    } else {
      return null;
    }
  }
  
  public byte[] getAudioPrompt(int prompthash) throws SQLException {
    String q = "SELECT data FROM audio_prompt WHERE prompthash=?;";
    
    PreparedStatement stmt = con.prepareStatement(q);
    stmt.setInt(1, prompthash);
    ResultSet rs;
  
    rs = stmt.executeQuery();
    
    if (rs.next()) {
      return rs.getBytes("data");
//      Blob dataBlob = rs.getBlob("data");
//      return dataBlob.getBytes(1L, (int) dataBlob.length());
    } else {
      return null;
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
    String q = "REPLACE INTO audio_prompt (prompthash, prompt, " +
    "data) VALUES (?,?,?);";
    PreparedStatement stmt = con.prepareStatement(q);
    
    stmt.setInt(1, getPromptHash(prompt));
    stmt.setString(2, prompt);
    stmt.setObject(3, data);
    stmt.executeUpdate();
  }
  
  private String escape(String s) {
    return StringEscapeUtils.escapeSql(s);
  }
  
  private void createDb() throws SQLException {
    Connection con =
      DriverManager.getConnection(
                  DB_URL, DB_USER, DB_PASS);

    Statement stmt = con.createStatement();
    stmt.executeUpdate(
        "CREATE DATABASE IF NOT EXISTS " + DB_NAME + ";");
    con.close();
  }
  
  public void close() throws SQLException {
    con.close();
    con = null;
  }
  
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
            "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," + 
            "instanceid INT," + 
            "data MEDIUMBLOB," + 
            "FOREIGN KEY (instanceid) REFERENCES instance(id) );"
      );
    stmt.execute(
        "CREATE TABLE IF NOT EXISTS form ( " + 
            "name VARCHAR(100) NOT NULL PRIMARY KEY," +
            "xml MEDIUMTEXT );"
      );
    stmt.execute(
        "CREATE TABLE IF NOT EXISTS audio_prompt (" + 
            "prompthash INT NOT NULL PRIMARY KEY," +
            "prompt VARCHAR(10000)," + 
            "data BLOB );"
        );
  }
  
  protected void resetDb() throws SQLException {
    Statement stmt = con.createStatement();
    //stmt.execute("DROP DATABASE " + DB_NAME);
    stmt.execute("DROP TABLE instance_binary");
    stmt.execute("DROP TABLE instance;");
    stmt.execute("DROP TABLE form;");
    stmt.execute("DROP TABLE audio_prompt;");
    initDb();
  }

}