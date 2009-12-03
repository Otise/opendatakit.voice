package org.odk.voice.db;

import java.io.StringBufferInputStream;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.odk.voice.db.DbAdapter.InstanceBinary;

public class DbAdapterTest extends TestCase {
  
  DbAdapter dba = null;
  
  public void setUp() {
    try {
      dba = new DbAdapter();
      dba.resetDb();
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
  
  public void tearDown() {
    try {
      dba.close();
      dba = null;
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
  
  public void testAudioPrompt(){
    try {
      String prompt = "I went to the store last night, yes I did!\nI did!";
      String prompt2 = "2";
      byte[] data = new byte[]{1,2,3,4,5,6,7,8,9,0};
      byte[] data2 = "abcdefghijklmnopqrstuvwxyz".getBytes();
      dba.putAudioPrompt(prompt, data);
      dba.putAudioPrompt(prompt2, data2);
      //System.out.println(dba.getAudioPrompt(prompt));
      assertTrue(Arrays.equals(data, dba.getAudioPrompt(prompt)));
      assertTrue(Arrays.equals(data, dba.getAudioPrompt(dba.getPromptHash(prompt))));
      assertTrue(Arrays.equals(data2, dba.getAudioPrompt(prompt2)));
      byte[] data3 = "\n\r\t#@!*(&*&^".getBytes();
      dba.putAudioPrompt(prompt2, data3);
      assertTrue(Arrays.equals(data3, dba.getAudioPrompt(prompt2)));
      assertTrue(Arrays.equals(data3, dba.getAudioPrompt(dba.getPromptHash(prompt2))));
      assertNull(dba.getAudioPrompt("notinthedb"));
      assertNull(dba.getAudioPrompt(dba.getPromptHash("notinthedb")));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
  
  public void testForm(){
    try {
      String name = "Health Survey Uganda";
      String name2 = "AIDS Surveillance #1";
      byte[] data = new byte[]{1,2,3,4,5,6,7,8,9,0};
      byte[] data2 = new byte[]{2,1,0};
      
      dba.addForm(name, data);
      dba.addForm(name2, data2);
      assertTrue(Arrays.equals(data, dba.getFormXml(name)));
      assertTrue(Arrays.equals(data2, dba.getFormXml(name2)));
      dba.addForm(name2, data);
      assertTrue(Arrays.equals(data, dba.getFormXml(name2)));
      byte[] bin1 = "abcdefg".getBytes();
      dba.setFormBinary(name2, bin1);
      assertTrue(Arrays.equals(data, dba.getFormXml(name2)));
      assertTrue(Arrays.equals(bin1, dba.getFormBinary(name2)));
      assertNull(dba.getAudioPrompt("notinthedb"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
  
  public void testInstance(){
    try {
      String callerid = "+1234567#";
      int instanceId = dba.createInstance(callerid);
      assertNull(dba.getInstanceXml(12345));
      assertNull(dba.getInstanceXml(instanceId));
      String xml = "<xml>Some xml</xml>\n";
      dba.setInstanceXml(instanceId, xml.getBytes());
      assertTrue(Arrays.equals(xml.getBytes(), dba.getInstanceXml(instanceId)));
      int instanceId2 = dba.createInstance(callerid);
      int[] instances = dba.getUncompletedInstances(callerid);
      // System.out.println(instances[0] + "," + instances[1]);
      // System.out.println(instanceId + "," + instanceId2);
      assertTrue(instances.length == 2);
      assertTrue((instances[0] == instanceId && instances[1] == instanceId2) ||
                 (instances[0] == instanceId2 && instances[1] == instanceId));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
  
  public void testInstanceBinary(){
    try {
      String callerid = "abcdefg";
      int instanceId = dba.createInstance(callerid);
      byte[] binary1 = new byte[]{0,127,1,126,2,125};
      byte[] binary2 = "QWERTY".getBytes();
      int id1 = dba.addBinaryToInstance(instanceId, binary1);
      int id2 = dba.addBinaryToInstance(instanceId, binary2);
      List<InstanceBinary> binaries = dba.getBinariesForInstance(instanceId);
      assertTrue(binaries.size() == 2);
      boolean parity = binaries.get(0).id == id1;
      assertTrue(binaries.get(0).id == (parity ? id1 : id2));
      assertTrue(binaries.get(1).id == (parity ? id2 : id1));
      assertTrue(Arrays.equals(binaries.get(0).binary, parity ? binary1 : binary2));
      assertTrue(Arrays.equals(binaries.get(1).binary, parity ? binary2 : binary1));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
}