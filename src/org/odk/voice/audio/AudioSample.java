package org.odk.voice.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.odk.voice.storage.FileUtils;

public class AudioSample {

  private byte[] data;
  
  public AudioSample (byte[] data) {
    this.data = data;
  }
  
  public byte[] getAudio(){
    return data;
  }
  
  /**
   * Clips from the beginning and end of the audio file.
   * 
   * NOTE: clipping from the beginning is not currently supported.
   * 
   * @param clipBegin Amount to clip from the beginning, in seconds.
   * @param clipEnd Amount to clip from the end, in seconds.
   * @throws IOException
   * @throws UnsupportedAudioFileException
   */
  public void clipAudio(float clipBegin, float clipEnd) throws IOException, UnsupportedAudioFileException {
    InputStream in = new ByteArrayInputStream(data);
    
    if (clipBegin < 0 || clipEnd < 0)
      throw new IllegalArgumentException("clipBegin and clipEnd must be non-negative");

    AudioFileFormat inFileFormat = AudioSystem.getAudioFileFormat(in);
    if (inFileFormat.getType() != AudioFileFormat.Type.WAVE) 
    {
      throw new UnsupportedAudioFileException("Only wave files supported");
    }
    
    AudioInputStream inFileAIS = 
      AudioSystem.getAudioInputStream(in);

    float frameRate = inFileAIS.getFormat().getFrameRate();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    AudioSystem.write(inFileAIS,
           AudioFileFormat.Type.WAVE, out);

    inFileAIS.close();
    byte[] full = out.toByteArray();
    byte[] clipped = new byte[0];
    if (frameRate * (clipBegin + clipEnd) < full.length) {
      data = Arrays.copyOfRange(full, 
          0, //(int) (frameRate * clipBegin), 
          (int) (full.length - frameRate * clipEnd));
    }
  }
}
