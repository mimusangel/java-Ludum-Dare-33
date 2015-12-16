package fr.mimus.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.IOException;

import org.lwjgl.opengl.Display;

import fr.mimus.Const;
import fr.mimus.LD33;
import fr.nerss.mimus.render.gui.IGui;
import fr.nerss.mimus.render.gui.event.Button;
import fr.nerss.mimus.render.gui.event.IGuiEvent;
import fr.nerss.mimus.render.gui.event.IGuiEventListener;
import fr.nerss.mimus.utils.io.DataBuffer;

public class DeadMenu extends IGui implements IGuiEventListener {
	LD33 ld33;
	Button replay;
	Button shop;
	Button back;
		
	public DeadMenu(LD33 ld33) {
		this.ld33 = ld33;
		ld33.getPlayer().dead++;
		ld33.setDebug(false);
		DataBuffer buffer = new DataBuffer();
		ld33.getPlayer().write(buffer);
		buffer.flip();
		try {
			buffer.write(Const.ld33Path + "\\save.blop");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		replay = new Button("Again !", 0, 0, 200, 40);
		replay.setListener(this);
		shop = new Button("Shop Menu", 0, 0, 200, 40);
		shop.setListener(this);
		back = new Button("Back Main Menu", 0, 0, 200, 40);
		back.setListener(this);
		
		
		this.alignEventToColumn(Display.getWidth() / 2 - 100, Display.getHeight() - 140, 5, replay, shop, back);
		
		this.addEvents(replay, shop, back);
	}

	public void update(int tick) {
		super.update(tick);
	}
	
	public void mouseClick(IGuiEventUse button, IGuiEvent src) {
		if(button == IGuiEventUse.LEFT) {			
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
			if(src == shop) {
				ld33.setGui(new ShopMenu(ld33));
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
		this.alignEventToColumn(Display.getWidth() / 2 - 100, Display.getHeight() - 140, 5, replay, shop, back);
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
		fontTitle.drawString("Your Monster Are DEAD !!!", Display.getWidth() / 2 - fontTitle.getWidth("Your Monster Are DEAD !!!") / 2, 30);
		
		if(ld33.getPlayer().getDeadTime() >= ld33.getPlayer().getLifeTime()) {
			fontTitle.drawString("You no longer have Energy...", Display.getWidth() / 2 - fontTitle.getWidth("You no longer have Energy...") / 2, Display.getHeight() / 2 - 10);
		} else {
			fontTitle.drawString("You have no life...", Display.getWidth() / 2 - fontTitle.getWidth("You have no life...") / 2, Display.getHeight() / 2 - 10);
		}
		
		
		fontTitle.drawString("Exp:", 5, 60);
		fontTitle.drawString(""+ld33.getPlayer().getExp(), 180, 60);
	}

	public void drawForeground() {
		
	}
}