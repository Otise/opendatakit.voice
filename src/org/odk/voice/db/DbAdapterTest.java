package org.odk.voice.db;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.odk.voice.db.DbAdapter.FormMetadata;
import org.odk.voice.db.DbAdapter.InstanceBinary;
import org.odk.voice.schedule.ScheduledCall;

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
      assertTrue(dba.deleteAudioPrompt(prompt2));
      assertNull(dba.getAudioPrompt(prompt2));
      assertNull(dba.getAudioPrompt("notinthedb"));
      assertNull(dba.getAudioPrompt(dba.getPromptHash("notinthedb")));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
  
  public void testForm(){
    try {
      String name = "uganda.xml";
      String title = "Health Survey Uganda";
      String name2 = "aids1.xml";
      String title2 = "AIDS Surveillance #1";
      byte[] data = new byte[]{1,2,3,4,5,6,7,8,9,0};
      byte[] data2 = new byte[]{2,1,0};
      
      dba.addForm(name, title, data);
      dba.addForm(name2, title2, data2);
      assertTrue(Arrays.equals(data, dba.getFormXml(name)));
      assertTrue(Arrays.equals(data2, dba.getFormXml(name2)));
      dba.addForm(name2, title2, data);
      assertTrue(Arrays.equals(data, dba.getFormXml(name2)));
      byte[] bin1 = "abcdefg".getBytes();
      dba.setFormBinary(name2, bin1);
      assertTrue(Arrays.equals(data, dba.getFormXml(name2)));
      assertTrue(Arrays.equals(bin1, dba.getFormBinary(name2)));
      assertNull(dba.getAudioPrompt("notinthedb"));
      List<FormMetadata> formNames = dba.getForms();
      assertEquals(2, formNames.size());
      assertTrue(formNames.get(0).getName().equals(name2));
      assertTrue(formNames.get(1).getName().equals(name));
      assertTrue(formNames.get(0).getTitle().equals(title2));
      assertTrue(formNames.get(1).getTitle().equals(title));
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
      assertTrue(dba.addBinaryToInstance(instanceId, "name1", "text/xml", binary1));
      assertTrue(dba.addBinaryToInstance(instanceId, "name2", "audio/wmv", binary2));
      List<InstanceBinary> binaries = dba.getBinariesForInstance(instanceId);
      assertTrue(binaries.size() == 2);
      boolean parity = binaries.get(0).name.equals("name1");
      assertEquals(binaries.get(0).name, parity ? "name1" : "name2");
      assertEquals(binaries.get(1).name, parity ? "name2" : "name1");
      assertEquals(binaries.get(0).mimeType, parity ? "text/xml" : "audio/wmv");
      assertEquals(binaries.get(1).mimeType, parity ? "audio/wmv" : "text/xml");
      assertTrue(Arrays.equals(binaries.get(0).binary, parity ? binary1 : binary2));
      assertTrue(Arrays.equals(binaries.get(1).binary, parity ? binary2 : binary1));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
  
//  public void testOutbound(){
//    try {
//      String num = "+1234567";
//      dba.addOutboundCall(num);
//      List<ScheduledCall> res = dba.getScheduledCalls(ScheduledCall.Status.PENDING);
//      assertEquals(1, res.size());
//      ScheduledCall call = res.get(0);
//      assertEquals(num, call.phoneNumber);
//      assertFalse(dba.setOutboundCallStatus(call.id + 1, ScheduledCall.Status.COMPLETE));
//      assertTrue(dba.setOutboundCallStatus(call.id, ScheduledCall.Status.NOT_COMPLETED));
//      assertEquals(1, dba.getScheduledCalls(null).size());
//      assertEquals(0, dba.getScheduledCalls(ScheduledCall.Status.PENDING).size());
//    } catch (Exception e) {
//      e.printStackTrace();
//      fail(e.getMessage());
//    }
//  }
}