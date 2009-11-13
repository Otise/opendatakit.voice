package org.odk.voice.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.odk.voice.storage.FileUtils;

import junit.framework.TestCase;

public class AudioSampleTest extends TestCase {

  public static final float CLIP_SEC = 0.2F;
  
  public void testSound(){
    String inputPath = "in.wav";
    String outputPath = "out.wav";
    AudioFileFormat inFileFormat;
    File inFile = new File(inputPath);
    if (!inFile.exists())
      fail(inFile.getAbsolutePath());
    File outFile = new File(outputPath);
    try {
      // query file type
      inFileFormat = AudioSystem.getAudioFileFormat(inFile);
      if (inFileFormat.getType() != AudioFileFormat.Type.WAVE) 
      {
        fail();
      }
      AudioInputStream inFileAIS = 
        AudioSystem.getAudioInputStream(inFile);

      float frameRate = inFileAIS.getFormat().getFrameRate();
      long frameLength = inFileAIS.getFrameLength();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
           AudioSystem.write(inFileAIS,
             AudioFileFormat.Type.WAVE, out);

      inFileAIS.close();
      byte[] full = out.toByteArray();
      //System.out.println("FrameRate: " + frameRate + ". FrameLength: " + frameLength + ". Size: " + full.length);
      byte[] clipped = 
        Arrays.copyOfRange(full, 0, (int)Math.round(Math.max(0,full.length - frameRate * CLIP_SEC)));
      FileUtils.writeFile(clipped, "out.wav", true);
    } catch (UnsupportedAudioFileException e) {
      fail("Error: " + inFile.getPath()
          + " is not a supported audio file type!");
      return;
    } catch (IOException e) {
      e.printStackTrace();
      fail("Error: failure attempting to read " 
        + inFile.getPath() + "!");
    }
  }

}

