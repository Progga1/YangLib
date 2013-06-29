package yang.pc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ListIterator;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import yang.sound.AbstractSound;
import yang.util.NonConcurrentList;


public class PCSound extends AbstractSound {

	private String name;
	private byte[] data;

	private NonConcurrentList<Clip> waitingList;
	private NonConcurrentList<Clip> playingList;

	private float volume;
	
	private Clip loop;

	public PCSound(String name, byte[] data) {
		this.name = name;
		this.data = data;
		waitingList = new NonConcurrentList<Clip>();
		playingList = new NonConcurrentList<Clip>();
		loop = null;
	}

	@Override
	public void init(float volume) {
		this.volume = -20;//volume;	//XXX sound hack
		Clip clip = getClip();
		waitingList.add(clip);
	}

	@Override
	public void play() {
		Clip clip = getClip();
		if (clip != null) {
			clip.start();
			playingList.add(clip);
		}
	}

	@Override
	public void playLoop() {
		if (loop != null) return;
		loop = getClip();
		if (loop != null) {
			loop.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}

	@Override
	public void stopLoop() {
		if (loop == null) return;
		loop.stop();
		waitingList.add(loop);
		loop = null;
	}

	@Override
	public boolean isLoaded() {
		return (data != null);		//always true??
	}

	private synchronized void recycle() {
		ListIterator<Clip> iter = playingList.listIterator();
		while (iter.hasNext()) {
			Clip clip = iter.next();
			if (!clip.isRunning()) {
				iter.remove();
				clip.setFramePosition(0);
				waitingList.add(clip);
			}
		}
	}

	private synchronized Clip getClip() {
		if (!isLoaded()) return null;	//never happens

		recycle(); // TODO: probably should be optimized

		Clip clip = null;
		if (waitingList.size() > 0) clip = waitingList.remove(0);
		if (clip == null) {
			try {
				clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(data)));
				FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				volumeControl.setValue(volume); // in dB
			} catch (LineUnavailableException e) {
				System.err.println("**ERROR** " + e.getMessage());
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				System.err.println("**ERROR** '" + name + "' is not a supported format.");
			} catch (IOException e) {
				System.err.println("**ERROR** " + e.getMessage());
				e.printStackTrace();
			}
		}

		return clip;
	}

}