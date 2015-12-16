package fr.mimus;

import java.io.File;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import fr.nerss.mimus.render.SideTiles;
import fr.nerss.mimus.render.Sprites;
import fr.nerss.mimus.render.Texture;
import fr.nerss.mimus.utils.io.DataBuffer;

public class Const {
	public static String ld33Path = System.getProperty("user.home") + "\\Ludume Dare 33\\Blop Avenger\\";
	public static Texture sky;
	public static Texture bg;
	
	public static Sprites blop;
	public static Sprites man0;
	public static Sprites man1;
	public static Sprites miner;
	public static Sprites finalBoss;
	
	public static SideTiles grass;
	public static SideTiles stone;
	public static SideTiles wood;
	
	public static Texture trunk;
	public static Texture tree;
	public static Texture rock;
	public static Sprites home;
	public static Texture bullet;
	public static Texture bulletRock;
	public static Texture arrow;
	public static Texture bgStone;
	public static Texture bgLadder;
	public static Texture bgWood;
	public static Texture bgWoodLadder;

	public static int KEY_LEFT = Keyboard.KEY_Q;
	public static int KEY_RIGHT = Keyboard.KEY_D;
	public static int KEY_UP = Keyboard.KEY_Z;
	public static int KEY_DOWN = Keyboard.KEY_S;
	public static int KEY_JUMP = Keyboard.KEY_SPACE;

	public static int AUDIO_MUSIC = 50;
	public static int AUDIO_EFFECT = 65;
	
	public static int PARTICLE = 1;
	
	public static void loadOption() {
		File file = new File(ld33Path + "option.blop");
		if(!file.exists()) {
			saveOption();
			return;
		}
		DataBuffer data = new DataBuffer();
		try {
			data.read(ld33Path + "option.blop");
			KEY_LEFT = data.getInt();
			KEY_RIGHT = data.getInt();
			KEY_UP = data.getInt();
			KEY_DOWN = data.getInt();
			KEY_JUMP = data.getInt();
			AUDIO_MUSIC = data.getInt();
			AUDIO_EFFECT = data.getInt();
			PARTICLE = data.getInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveOption() {
		DataBuffer data = new DataBuffer();
		try {
			data.put(KEY_LEFT);
			data.put(KEY_RIGHT);
			data.put(KEY_UP);
			data.put(KEY_DOWN);
			data.put(KEY_JUMP);
			data.put(AUDIO_MUSIC);
			data.put(AUDIO_EFFECT);
			data.put(PARTICLE);
			data.flip();
			data.write(ld33Path + "option.blop");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static {
		try {
			bg = new Texture("GFX/bg.png");
			sky = new Texture("GFX/sky.png");
			blop = new Sprites("GFX/blop.png", 32 ,32);

			man0 = new Sprites("GFX/man0.png", 32 ,64);
			man1 = new Sprites("GFX/man1.png", 32 ,64);
			miner = new Sprites("GFX/miner.png", 32 ,64);
			finalBoss = new Sprites("GFX/finalBoss.png", 512 ,356);
			
			grass = new SideTiles("GFX/grass.png");
			stone = new SideTiles("GFX/stone.png");
			wood = new SideTiles("GFX/wood.png");
			
			trunk = new Texture("GFX/trunk.png");
			tree = new Texture("GFX/tree.png");
			rock = new Texture("GFX/rock.png");
			home = new Sprites("GFX/home.png", 128, 96);
			bullet = new Texture("GFX/bullet.png");
			bulletRock = new Texture("GFX/bulletRock.png");
			arrow = new Texture("GFX/arrow.png");
			bgStone = new Texture("GFX/bg_stone.png");
			bgLadder = new Texture("GFX/bg_ladder.png");
			bgWood = new Texture("GFX/bg_wood.png");
			bgWoodLadder = new Texture("GFX/bg_ladder_wood.png");

			Const.loadOption();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
