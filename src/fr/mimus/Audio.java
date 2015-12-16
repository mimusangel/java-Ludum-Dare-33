package fr.mimus;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Audio {

	public static Audio shoot = new Audio("SFX/shoot.wav", 0);
	public static Audio jump = new Audio("SFX/jump.wav", 0);
	public static Audio powerup = new Audio("SFX/powerup.wav", 0);
	public static Audio hit = new Audio("SFX/hit.wav", 0);
	public static Audio music = new Audio("SFX/music.wav", 1);
	
	Clip clip;
	FloatControl gainControl;
	String path;
	int type = 0;
	
	public Audio(Audio a) {
		clip=a.clip;
		gainControl=a.gainControl;
		path=a.path;
		type=a.type;
	}
	
	public Audio(String p, int t) {
		path=p;
		type=t;
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File(p));
			clip = AudioSystem.getClip();
			clip.open(ais);
			gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			setGain(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}

	public Audio setGain(int p) {
		float m = gainControl.getMaximum() + Math.abs(gainControl.getMinimum());
		float v = (((float) p * 100f) / m) + gainControl.getMinimum();
		setGain(v);
		return this;
	}

	public Audio setGain(float g) {
		if(g<gainControl.getMinimum()) g=gainControl.getMinimum();
		if(g>gainControl.getMaximum()) g=gainControl.getMaximum();
		gainControl.setValue(g);
		return this;
	}
	
	public void defineGain() {
		if(type == 1) {
			setGain(Const.AUDIO_MUSIC);
		} else {
			setGain(Const.AUDIO_EFFECT);
		}
	}
	
	public Audio play() {
		defineGain();
		clip.setFramePosition(0);
		clip.start();
		return this;
	}
	
	public Audio playOnly() {
		defineGain();
		if(clip.getFramePosition() > 0 && clip.getFramePosition() < clip.getFrameLength()) return this;
		clip.setFramePosition(1);
		clip.start();
		return this;
	}
	
	public Audio loop() {
		defineGain();
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		return this;
	}
	
	public Audio stop() {
		clip.close();
		return this;
	}
}
