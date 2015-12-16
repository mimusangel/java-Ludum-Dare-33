package fr.mimus.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.IOException;

import org.lwjgl.opengl.Display;

import fr.mimus.Audio;
import fr.mimus.Const;
import fr.mimus.LD33;
import fr.mimus.entities.Player;
import fr.nerss.mimus.render.gui.IGui;
import fr.nerss.mimus.render.gui.event.Button;
import fr.nerss.mimus.render.gui.event.IGuiEvent;
import fr.nerss.mimus.render.gui.event.IGuiEventListener;
import fr.nerss.mimus.utils.io.DataBuffer;

public class ShopMenu extends IGui implements IGuiEventListener {

	private final int priceSpeed = 200;
	private final int priceJump = 60;
	private final int priceTime = 20;
	private final int priceSpAtk = 180;
	private final int priceAtk = 950;
	
	LD33 ld33;
	Button replay;
	Button back;
	
	Button speed;
	Button jump;
	Button lifeTime;
	Button speedAttack;
	Button dmgAttack;

	Button treeHealLife;
	Button treeHealTime;
	Button doubleExp;
	Button shopReduce;
	Button fastRegen;
	
	public ShopMenu(LD33 ld33) {
		this.ld33 = ld33;
		ld33.setDebug(false);

		replay = new Button("Play !", 0, 0, 200, 40);
		replay.setListener(this);
		back = new Button("Back Main Menu", 0, 0, 200, 40);
		back.setListener(this);
		
		speed = new Button("Speed", 0, 0, 200, 40);
		speed.setListener(this);
		jump = new Button("Jump", 0, 0, 200, 40);
		jump.setListener(this);
		lifeTime = new Button("Energy Max", 0, 0, 200, 40);
		lifeTime.setListener(this);
		speedAttack = new Button("Speed Attack", 0, 0, 200, 40);
		speedAttack.setListener(this);
		dmgAttack = new Button("Dommage Attack", 0, 0, 200, 40);
		dmgAttack.setListener(this);
		
		treeHealLife = new Button("Decorations Heal life (500xp)", 0, 0, 200, 20);
		treeHealLife.setListener(this);
		treeHealTime = new Button("Decorations Heal Energy (1500xp)", 0, 0, 200, 20);
		treeHealTime.setListener(this);
		doubleExp = new Button("Double Exp (5000xp)", 0, 0, 200, 20);
		doubleExp.setListener(this);
		shopReduce = new Button("Reduce Price (10000xp)", 0, 0, 200, 20);
		shopReduce.setListener(this);
		fastRegen = new Button("Fast Regen' Energy (50000xp)", 0, 0, 200, 20);
		fastRegen.setListener(this);
		
		this.alignEventToColumn(Display.getWidth() - 205, Display.getHeight() - 95, 5, replay, back);
		this.alignEventToColumn(Display.getWidth() / 2 - 100, Display.getHeight() / 2 - 113, 5, speed, jump, lifeTime, speedAttack, dmgAttack);
		this.alignEventToColumn(Display.getWidth() - 205, Display.getHeight() / 2 - 113, 3, treeHealLife, treeHealTime, doubleExp, shopReduce, fastRegen);
		
		this.addEvents(replay, back, speed, jump, lifeTime, speedAttack, dmgAttack, treeHealLife, treeHealTime, doubleExp, shopReduce, fastRegen);
	}

	public void update(int tick) {
		super.update(tick);
		float reduce = ld33.getPlayer().hasBonus(Player.BONUS_REDUCE_PRICE) ? 0.75f : 1f;
		speed.setTxt("Speed +0.1 ("+(int) (ld33.getPlayer().getSpeed() * priceSpeed * reduce) + "xp)");
		jump.setTxt("Jump +0.2 ("+(int) (ld33.getPlayer().getJump() * priceJump * reduce) + "xp)");
		lifeTime.setTxt("Energy Max +5 ("+(int) (ld33.getPlayer().getLifeTime() * priceTime * reduce) + "xp)");
		speedAttack.setTxt("Speed Attack +0.1 ("+(int) (ld33.getPlayer().getSpeedAttack() * priceSpAtk * reduce) + "xp)");
		dmgAttack.setTxt("Dommage Attack +1 ("+(int) (ld33.getPlayer().getDmgAttack() * priceAtk * reduce) + "xp)");

		treeHealLife.setLocked(ld33.getPlayer().hasBonus(Player.BONUS_TREE_HEAL_LIFE));
		treeHealTime.setLocked(ld33.getPlayer().hasBonus(Player.BONUS_TREE_HEAL_TIME));
		doubleExp.setLocked(ld33.getPlayer().hasBonus(Player.BONUS_DOUBLE_EXP));
		shopReduce.setLocked(ld33.getPlayer().hasBonus(Player.BONUS_REDUCE_PRICE));
		fastRegen.setLocked(ld33.getPlayer().hasBonus(Player.BONUS_FAST_REGEN));
		
		speed.setLocked(ld33.getPlayer().getSpeed()>= 4f);
		jump.setLocked(ld33.getPlayer().getJump()>= 10f);
		speedAttack.setLocked(ld33.getPlayer().getSpeedAttack()>= 8f);
	}
	
	public void mouseClick(IGuiEventUse button, IGuiEvent src) {
		if(button == IGuiEventUse.LEFT) {
			// Add boost
			float reduce = ld33.getPlayer().hasBonus(Player.BONUS_REDUCE_PRICE) ? 0.75f : 1f;
			if(src == speed) {
				int cost = (int) (ld33.getPlayer().getSpeed() * priceSpeed * reduce);
				if(ld33.getPlayer().getExp() >= cost) {
					ld33.getPlayer().setExp(ld33.getPlayer().getExp() - cost);
					ld33.getPlayer().setSpeed(ld33.getPlayer().getSpeed() + 0.1f);
					Audio.powerup.play();
				}
				return;
			}
			if(src == jump) {
				int cost = (int) (ld33.getPlayer().getJump() * priceJump * reduce);
				if(ld33.getPlayer().getExp() >= cost) {
					ld33.getPlayer().setExp(ld33.getPlayer().getExp() - cost);
					ld33.getPlayer().setJump(ld33.getPlayer().getJump() + 0.2f);
					Audio.powerup.play();
				}
				return;
			}
			if(src == lifeTime) {
				int cost = (int) (ld33.getPlayer().getLifeTime() * priceTime * reduce);
				if(ld33.getPlayer().getExp() >= cost) {
					ld33.getPlayer().setExp(ld33.getPlayer().getExp() - cost);
					ld33.getPlayer().setLifeTime(ld33.getPlayer().getLifeTime() + 5);
					Audio.powerup.play();
				}
				return;
			}
			if(src == speedAttack) {
				int cost = (int) (ld33.getPlayer().getSpeedAttack() * priceSpAtk * reduce);
				if(ld33.getPlayer().getExp() >= cost) {
					ld33.getPlayer().setExp(ld33.getPlayer().getExp() - cost);
					ld33.getPlayer().setSpeedAttack(ld33.getPlayer().getSpeedAttack() + 0.1f);
					Audio.powerup.play();
				}
				return;
			}
			if(src == dmgAttack) {
				int cost = (int) (ld33.getPlayer().getDmgAttack() * priceAtk * reduce);
				if(ld33.getPlayer().getExp() >= cost) {
					ld33.getPlayer().setExp(ld33.getPlayer().getExp() - cost);
					ld33.getPlayer().setDmgAttack(ld33.getPlayer().getDmgAttack() + 1);
					Audio.powerup.play();
				}
				return;
			}
			if(src == treeHealLife) {
				int cost = 500;
				if(ld33.getPlayer().getExp() >= cost) {
					ld33.getPlayer().setExp(ld33.getPlayer().getExp() - cost);
					ld33.getPlayer().addBonus(Player.BONUS_TREE_HEAL_LIFE);
					Audio.powerup.play();
				}
				return;
			}
			if(src == treeHealTime) {
				int cost = 1500;
				if(ld33.getPlayer().getExp() >= cost) {
					ld33.getPlayer().setExp(ld33.getPlayer().getExp() - cost);
					ld33.getPlayer().addBonus(Player.BONUS_TREE_HEAL_TIME);
					Audio.powerup.play();
				}
				return;
			}
			if(src == doubleExp) {
				int cost = 5000;
				if(ld33.getPlayer().getExp() >= cost) {
					ld33.getPlayer().setExp(ld33.getPlayer().getExp() - cost);
					ld33.getPlayer().addBonus(Player.BONUS_DOUBLE_EXP);
					Audio.powerup.play();
				}
				return;
			}
			if(src == shopReduce) {
				int cost = 10000;
				if(ld33.getPlayer().getExp() >= cost) {
					ld33.getPlayer().setExp(ld33.getPlayer().getExp() - cost);
					ld33.getPlayer().addBonus(Player.BONUS_REDUCE_PRICE);
					Audio.powerup.play();
				}
				return;
			}
			if(src == fastRegen) {
				int cost = 50000;
				if(ld33.getPlayer().getExp() >= cost) {
					ld33.getPlayer().setExp(ld33.getPlayer().getExp() - cost);
					ld33.getPlayer().addBonus(Player.BONUS_FAST_REGEN);
					Audio.powerup.play();
				}
				return;
			}
			// Menu
			if(src == replay) {
				DataBuffer buffer = new DataBuffer();
				ld33.getPlayer().write(buffer);
				buffer.flip();
				try {
					buffer.write(Const.ld33Path + "\\save.blop");
				} catch (IOException e) {
					e.printStackTrace();
				}
				ld33.startGame(null);
				return;
			}
			if(src == back) {
				DataBuffer buffer = new DataBuffer();
				ld33.getPlayer().write(buffer);
				buffer.flip();
				try {
					buffer.write(Const.ld33Path + "\\save.blop");
				} catch (IOException e) {
					e.printStackTrace();
				}
				ld33.setGui(new MainGui(ld33));
				return;
			}
		}
	}

	public void mouseHover(IGuiEvent src) {
		
	}

	public void updateRezise() {
		this.alignEventToColumn(Display.getWidth() - 205, Display.getHeight() - 95, 5, replay, back);
		this.alignEventToColumn(Display.getWidth() / 2 - 100, Display.getHeight() / 2 - 113, 5, speed, jump, lifeTime, speedAttack, dmgAttack);
		this.alignEventToColumn(Display.getWidth() - 205, Display.getHeight() / 2 - 113, 3, treeHealLife, treeHealTime, doubleExp, shopReduce, fastRegen);
	}

	public void drawBackground() {
		Const.bg.bind();
		glBegin(GL_QUADS);
			glTexCoord2f(0, 0); glVertex2f(0, 					0);
			glTexCoord2f(1, 0); glVertex2f(Display.getWidth(), 	0);
			glTexCoord2f(1, 1); glVertex2f(Display.getWidth(), 	Display.getHeight());
			glTexCoord2f(0, 1); glVertex2f(0, 					Display.getHeight());
		glEnd();
		
		fontTitle.drawString("Blop Avenger", Display.getWidth() / 2 - fontTitle.getWidth("Blop Avenger") / 2, 5);

		fontTitle.drawString("Exp:", 5, 60);
		fontTitle.drawString(""+ld33.getPlayer().getExp(), 180, 60);
		fontTitle.drawString("Speed:", 5, 82);
		fontTitle.drawString(""+ld33.getPlayer().getSpeed(), 180, 82);
		fontTitle.drawString("Jump Height:", 5, 104);
		fontTitle.drawString(""+ld33.getPlayer().getJump(), 180, 104);
		fontTitle.drawString("Energy Max:", 5, 126);
		fontTitle.drawString(""+ld33.getPlayer().getLifeTime(), 180, 126);
		fontTitle.drawString("Speed Attack:", 5, 148);
		fontTitle.drawString(""+ld33.getPlayer().getSpeedAttack(), 180, 148);
		fontTitle.drawString("Dommage Attack:", 5, 170);
		fontTitle.drawString(""+ld33.getPlayer().getDmgAttack(), 180, 170);
	}

	public void drawForeground() {
		
	}
}
