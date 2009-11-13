package org.odk.voice.audio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.odk.voice.storage.FileUtils;

public class AudioSample {

  private File audioFile;
  
  public AudioSample (String path) {
    this.audioFile = new File(path);
    if (!audioFile.exists())
      throw new IllegalArgumentException("Audio file does not exist.");
    
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
    if (clipBegin < 0 || clipEnd < 0)
      throw new IllegalArgumentException("clipBegin and clipEnd must be non-negative");

    AudioFileFormat inFileFormat = AudioSystem.getAudioFileFormat(audioFile);
    if (inFileFormat.getType() != AudioFileFormat.Type.WAVE) 
    {
      throw new UnsupportedAudioFileException("Only wave files supported");
    }
    
    AudioInputStream inFileAIS = 
      AudioSystem.getAudioInputStream(audioFile);

    float frameRate = inFileAIS.getFormat().getFrameRate();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    AudioSystem.write(inFileAIS,
           AudioFileFormat.Type.WAVE, out);

    inFileAIS.close();
    byte[] full = out.toByteArray();
    byte[] clipped = new byte[0];
    if (frameRate * (clipBegin + clipEnd) < full.length) {
      clipped = Arrays.copyOfRange(full, 
          0, //(int) (frameRate * clipBegin), 
          (int) (full.length - frameRate * clipEnd));
    }
    FileUtils.writeFile(clipped, audioFile.getAbsolutePath(), true);
  }
}
