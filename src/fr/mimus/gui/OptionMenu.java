package fr.mimus.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;
import fr.mimus.Audio;
import fr.mimus.Const;
import fr.mimus.LD33;
import fr.nerss.mimus.render.Color4f;
import fr.nerss.mimus.render.gui.IGui;
import fr.nerss.mimus.render.gui.event.Button;
import fr.nerss.mimus.render.gui.event.HScrollbar;
import fr.nerss.mimus.render.gui.event.IGuiEvent;
import fr.nerss.mimus.render.gui.event.IGuiEventListener;
import fr.nerss.mimus.utils.inputs.Inputs;

public class OptionMenu extends IGui implements IGuiEventListener {
	LD33 ld33;
	Button save;
	Button back;
		
	Button up;
	Button down;
	Button right;
	Button left;
	Button jump;

	HScrollbar music;
	HScrollbar effect;
	
	Button particle;
	
	int keyBind;
	boolean inGame;
	public OptionMenu(LD33 ld33, boolean inGame) {
		this.ld33 = ld33;
		this.inGame = inGame;
		Const.loadOption();
		ld33.setDebug(false);
		save = new Button("Save and Back", 0, 0, 200, 40);
		save.setListener(this);
		back = new Button("Back", 0, 0, 200, 40);
		back.setListener(this);
		
		up = new Button("UP: ", 0, 0, 200, 20);
		up.setListener(this);
		down = new Button("DOWN: ", 0, 0, 200, 20);
		down.setListener(this);
		right = new Button("RIGHT: ", 0, 0, 200, 20);
		right.setListener(this);
		left = new Button("LEFT: ", 0, 0, 200, 20);
		left.setListener(this);
		jump = new Button("JUMP: ", 0, 0, 200, 20);
		jump.setListener(this);
		
		music = new HScrollbar(400, 77, 200, 12);
		music.setMaxValue(100);
		music.setLabel(true);
		music.setValue(Const.AUDIO_MUSIC);
		music.setListener(this);
		effect = new HScrollbar(400, 92, 200, 12);
		effect.setMaxValue(100);
		effect.setLabel(true);
		effect.setValue(Const.AUDIO_EFFECT);
		effect.setListener(this);
		
		particle = new Button("Particle: On", 350, 107, 250, 20);
		particle.setListener(this);
		
		this.alignEventToColumn(Display.getWidth() / 2 - 100, Display.getHeight() - 95, 5, save, back);
		this.alignEventToColumn(50, 80, 5, up, down, right, left, jump);
		
		this.addEvents(save, back, up, down, right, left, jump, music, effect, particle);
		keyBind = 0;
	}

	public void update(int tick) {
		super.update(tick);
		up.setTxt("Up: " + Inputs.getInputName(Const.KEY_UP));
		down.setTxt("Down: " + Inputs.getInputName(Const.KEY_DOWN));
		right.setTxt("Right: " + Inputs.getInputName(Const.KEY_RIGHT));
		left.setTxt("Left: " + Inputs.getInputName(Const.KEY_LEFT));
		jump.setTxt("Jump: " + Inputs.getInputName(Const.KEY_JUMP));
		particle.setTxt("Particle: " + (Const.PARTICLE == 1 ? "On" : "Off"));
		Const.AUDIO_MUSIC = music.getValue();
		Const.AUDIO_EFFECT = effect.getValue();
		
		Audio.music.setGain(Const.AUDIO_MUSIC);
		
		if(keyBind != 0) {
			int key = Inputs.nextInput();
			if(key > -1 && key < 1000) {
				if(key == Keyboard.KEY_ESCAPE) {
					keyBind = 0;
				} else {
					switch(keyBind) {
					case 1:
						Const.KEY_UP = key;
						break;
					case 2:
						Const.KEY_DOWN = key;
						break;
					case 3:
						Const.KEY_RIGHT = key;
						break;
					case 4:
						Const.KEY_LEFT = key;
						break;
					case 5:
						Const.KEY_JUMP = key;
						break;
					}
					keyBind = 0;
				}
			}
		}
	}
	
	public void mouseClick(IGuiEventUse button, IGuiEvent src) {
		if(keyBind != 0) return;
		if(button == IGuiEventUse.LEFT) {		
			if(src == up) {
				keyBind = 1;
				return;
			}
			if(src == down) {
				keyBind = 2;
				return;
			}
			if(src == right) {
				keyBind = 3;
				return;
			}
			if(src == left) {
				keyBind = 4;
				return;
			}
			if(src == jump) {
				keyBind = 5;
				return;
			}
			if(src == particle) {
				Const.PARTICLE = Const.PARTICLE == 1 ? 0 : 1;
				return;
			}
			if(src == save) {
				Const.saveOption();
				ld33.setGui(inGame ? null : new MainGui(ld33));
				return;
			}
			if(src == effect) {
				Audio.shoot.playOnly();
				return;
			}
			if(src == back) {
				Const.loadOption();
				ld33.setGui(inGame ? null : new MainGui(ld33));
				return;
			}
		}
	}

	public void mouseHover(IGuiEvent src) {
	}

	public void updateRezise() {
		this.alignEventToColumn(Display.getWidth() / 2 - 100, Display.getHeight() - 95, 5, save, back);
		this.alignEventToColumn(50, 80, 5, up, down, right, left, jump);
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
		fontTitle.drawString("Key Binding:", 50, 50);
		fontTitle.drawString("Audio:", 350, 50);
		font.drawString("Music:", 350, 75);
		font.drawString("Effect:", 350, 90);
	}

	public void drawForeground() {
		if(keyBind != 0) {
			int xx = Display.getWidth() / 2 - 250;
			int yy = Display.getHeight() / 2 - 150;
			glBegin(GL_QUADS);
				Color4f.DARK_GRAY.bind();
				glVertex2f(xx, 		yy);
				glVertex2f(xx + 500,yy);
				glVertex2f(xx + 500,yy + 300);
				glVertex2f(xx, 		yy + 300);
				Color4f.GRAY.bind();
				glVertex2f(xx + 2,	yy + 2);
				glVertex2f(xx + 498,yy + 2);
				glVertex2f(xx + 498,yy + 298);
				glVertex2f(xx + 2,	yy + 298);
			glEnd();
			String text = "Press a Key (Esc to cancel)";
			fontTitle.drawString(text,
					Display.getWidth() / 2 - fontTitle.getWidth(text) / 2,
					Display.getHeight() / 2 - fontTitle.getHeight(text) / 2);
		}
	}
}