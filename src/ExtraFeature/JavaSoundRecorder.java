package ExtraFeature;

import javax.sound.sampled.*;

import java.io.*;
 
/**
 * A sample program is to demonstrate how to record sound in Java
 * author: www.codejava.net
 */
public class JavaSoundRecorder {
    // record duration, in milliseconds
    static final long RECORD_TIME = 12000;  // 10 s
    static final String projectFolder = "C:\\Users\\rithel\\Desktop\\CS2108\\Assignment2\\";
 
    boolean dir = new File(projectFolder + "data\\input\\temp").mkdir();
    // path of the wav file
    static final String tempfilename = projectFolder + "\\data\\input\\temp\\temp.wav";
    File wavFile = new File(tempfilename);
 
    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line;
 
    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
 
    /**
     * Captures the sound and record into a WAV file
     */
    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing
 
            //System.out.println("Start capturing...");
 
            AudioInputStream ais = new AudioInputStream(line);
 
            System.out.println("Start recording...");
 
            // start recording
            AudioSystem.write(ais, fileType, wavFile);
 
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
 
    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }
    public static void copyAudio(String sourceFileName, String destinationFileName, int startSecond, int secondsToCopy) {
    	AudioInputStream inputStream = null;
    	AudioInputStream shortenedStream = null;
    	try {
    	  File file = new File(sourceFileName);
    	  AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
    	  AudioFormat format = fileFormat.getFormat();
    	  
    	  inputStream = AudioSystem.getAudioInputStream(file);   	  
    	  long framesOfAudioToCopy = secondsToCopy * (int)format.getFrameRate();
    	  shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
    	  
    	  File destinationFile = new File(destinationFileName);
    	  AudioSystem.write(shortenedStream, fileFormat.getType(), destinationFile);
    	  
    	  inputStream.close();
    	  shortenedStream.close();
    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	} 
    }
 
    /**
     * Entry to run the program
     * @throws IOException 
     */
    public static String record() throws IOException {
        final JavaSoundRecorder recorder = new JavaSoundRecorder();
 
        // creates a new thread that waits for a specified
        // of time before stopping
        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(RECORD_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                recorder.finish();
            }
        });
 
        stopper.start();
 
        // start recording
        recorder.start();
        // copy 10s to new file
        String newFileName = projectFolder + "data\\input\\recorded.wav";
        copyAudio(tempfilename,newFileName,0,10);
        
        return newFileName;
    }
}