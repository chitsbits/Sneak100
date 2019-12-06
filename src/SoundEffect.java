import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.sound.sampled.*;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/*
 * Class for managing sound and music
 */
class SoundEffect {

	AudioPlayer audPlayer;
	AudioStream audStream;

	public SoundEffect(String filename) {
		try {
			audPlayer = AudioPlayer.player;
			audStream = new AudioStream(new FileInputStream("assets/audio/" + filename));
			audPlayer.start(audStream);
		} catch (IOException e){
			e.printStackTrace();
			System.out.println("audio file not found: " + filename); 
		}
	}

	public void endSound() {
		audPlayer.stop(audStream);
	}
	
	public static void playMusic(String filename) {
		try{
			AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File("assets/audio/"+filename));
			Clip clip = AudioSystem.getClip();
			clip.open(audioInput);
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (IOException e){
			System.out.println("audio file not found: " + filename);
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			System.out.println("audio file not found: " + filename); 
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}