package fr.mimus;

import java.io.File;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;
import fr.mimus.entities.Player;
import fr.mimus.gui.DeadMenu;
import fr.mimus.gui.MainGui;
import fr.mimus.gui.OptionMenu;
import fr.mimus.gui.ShopMenu;
import fr.nerss.mimus.game.Game2D;
import fr.nerss.mimus.render.Color4f;
import fr.nerss.mimus.render.Texture;
import fr.nerss.mimus.utils.inputs.Inputs;
import fr.nerss.mimus.utils.io.DataBuffer;
import fr.nerss.mimus.utils.maths.Vector2f;

public class LD33 extends Game2D {
	private static LD33 game;
	public static LD33 getInstance() {
		return game;
	}
	
	boolean gameStarted;
	
	Map map;
	Vector2f offset;
	Player player;
	long timeGameStart;
	public LD33(String title, int width, boolean resizable) {
		super(title, width, width * 9 / 16, resizable);
	}

	protected void init() {
		File file = new File(Const.ld33Path);
		if(!file.exists()) {
			file.mkdirs();
		}
		Const.loadOption();
		
		offset = new Vector2f();
//		glClearColor(0, 0.7f, 0.88f, 1);
		gameStarted = false;
		timeGameStart = 0;
		this.setGui(new MainGui(this));
	}

	protected void update(int tick) {
		
		if(Inputs.keyUnPress(Keyboard.KEY_ESCAPE)) {
			this.setGui(new OptionMenu(this, true));
		}
		if(Inputs.keyUnPress(Keyboard.KEY_F1)) {
			this.setDebug(!this.isDebug());
		}
		if(player != null && player.isDead()) {
			this.setGui(new DeadMenu(this));
			gameStarted = false;
		} 
		if(!player.isDead()) {
			if(!map.hasPlayerEntity()) {
				map.addEntity(player);
			}
		}
		if(gameStarted) {
			float midScreenX = (float) Display.getWidth() / 2f;
			float midScreenY = (float) Display.getHeight() / 2f;
			if(player.getX() + 16f >= midScreenX) {
				offset.x = midScreenX - (player.getX() + 16f);
			}
			if(player.getY() + 16f >= midScreenY) {
				offset.y = midScreenY - (player.getY() + 16f);
			}
			if(offset.x > 0) offset.x = 0;
			if(offset.y > 0) offset.y = 0;
			int my = -(map.height * 32 - Display.getHeight());
			if(offset.y < my) offset.y = my;
			int mx = -(map.width * 32 - Display.getWidth());
			if(offset.x < mx) offset.x = mx;
			
			map.update(tick, offset);
		}
	}

	protected void render() {
		if(map != null) {
			Const.sky.bind();
			float mx = (map.width * 32); // 1440
			float my = (map.height * 32); // 810
			float bx = Display.getWidth() > 1440f ? Display.getWidth() : 1440f;
			float by = Display.getHeight() > 810f ? Display.getHeight() : 810f;
			float xx = offset.x * ((bx - Display.getWidth()) / mx);
			float yy = offset.y * ((by - Display.getHeight()) / my);
			glBegin(GL_QUADS);
				glTexCoord2f(0, 0); glVertex2f(xx, 		yy);
				glTexCoord2f(1, 0); glVertex2f(xx + bx, yy);
				glTexCoord2f(1, 1); glVertex2f(xx + bx, yy + by);
				glTexCoord2f(0, 1); glVertex2f(xx, 		yy + by);
			glEnd();
			
			map.render(offset);
			Texture.unbind();
			if(!player.isDead()) {
				int barX = Display.getWidth() - 202;
				int barY = 2;
				glBegin(GL_QUADS);
				Color4f.DARK_GRAY.bind();
				glVertex2f(barX, barY);
				glVertex2f(barX + 200, barY);
				glVertex2f(barX + 200, barY + 20);
				glVertex2f(barX, barY + 20);
				int size = (player.getLifeTime() - player.getDeadTime()) * 198 / player.getLifeTime();
				Color4f.DARK_GREEN.bind();
				glVertex2f(barX + 2, barY + 2);
				glVertex2f(barX + size, barY + 2);
				glVertex2f(barX + size, barY + 9);
				glVertex2f(barX + 2, barY + 9);
				size = player.getLife() * 198 / 10;
				Color4f.RED.bind();
				glVertex2f(barX + 2, barY + 11);
				glVertex2f(barX + size, barY + 11);
				glVertex2f(barX + size, barY + 18);
				glVertex2f(barX + 2, barY + 18);
				glEnd();
				font.drawString("Energy", barX - 42, barY - 4);
				font.drawString("Life", barX - 42, barY + 8);
				Texture.unbind();
			}
			
			if(System.currentTimeMillis() - timeGameStart < 15000) {
				String history = "Hello young blop, save our world, save your world!";
				float alpha = 1f - (float) (System.currentTimeMillis() - timeGameStart) / 20000f;
				font.drawString(history, 10, 10, new Color4f(0, 0, 0, alpha));
				history = "Warning your energy is used if you move.";
				font.drawString(history, 10, 25, new Color4f(0, 0, 0, alpha));
				history = "Left click: single shot (Energy or Life Cost)";
				font.drawString(history, 20, 40, new Color4f(0, 0, 0, alpha));
				history = "Right click: Power Shot (Double Dommage, Energy Cost)";
				font.drawString(history, 20, 55, new Color4f(0, 0, 0, alpha));
				Color4f.WHITE.bind();
			}
		}
	}

	protected void debugRender() {
		font.drawString("FPS: " + this.getFPS(), 2, 2);
		font.drawString("UPS: " + this.getUPS(), 2, 15);
		if(gameStarted) {
			font.drawString("x: " + player.getX(), 2, 30);
			font.drawString("y: " + player.getY(), 2, 44);
			font.drawString("g: " + player.isInGound(), 2, 58);
			font.drawString("e: " + map.entitiesSize(), 2, 72);
		}
	}

	protected void dispose() {
		Const.saveOption();
	}
	
	public void startGame(DataBuffer data) {
		map = new Map("GFX/mapTest.png");
		if(player == null) {
			player = new Player(map.getSpawnX(), map.getSpawnY(), 10);
			if(data != null) {
				player.read(data);
			}
		} else {
			player.setX(map.getSpawnX());
			player.setY(map.getSpawnY());
			player.setDeadTime(0);
			player.setLife(10);
			offset = new Vector2f();
		}
		map.addEntity(player);
		if(data != null) {
			this.setGui(new ShopMenu(this));
		} else {
			gameStarted = true;
			timeGameStart = System.currentTimeMillis();
			Audio.music.loop();
			this.setGui(null);
		}
		
	}
	
	public Map getMap() {
		return map;
	}
	
	public static void main(String[] args) {
		game = new LD33("Blop Avenger", 720, true);
		game.start();
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player value) {
		player = value;
	}
}
