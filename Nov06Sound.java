import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Nov06Sound {
	public boolean isLoop;
	public Clip clip;
	public Nov06Sound(URL url) {
		this(url,false);
	}

	public Nov06Sound(URL url,boolean isLoop) {
		this.isLoop = isLoop;
		load(url);
	}

	private void load(URL url) {
		AudioInputStream ais;
		try {
			ais = AudioSystem.getAudioInputStream(url);
			AudioFormat af = ais.getFormat();
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
			clip = (Clip)AudioSystem.getLine(dataLine);
			clip.loop(isLoop ? clip.LOOP_CONTINUOUSLY : 0);
			clip.open(ais);
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Nov06Sound setLoopPoint(int point) {
		this.clip.setLoopPoints(point,-1);
		return this;
	}

	public void Play() {
		clip.start();
		while(clip.isActive())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public void Stop() {
		clip.stop();
		clip.close();
	}
}