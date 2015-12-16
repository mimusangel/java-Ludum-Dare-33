package fr.mimus.gui;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

import fr.mimus.Const;
import fr.mimus.LD33;
import fr.nerss.mimus.render.gui.IGui;
import fr.nerss.mimus.render.gui.event.Button;
import fr.nerss.mimus.render.gui.event.IGuiEvent;
import fr.nerss.mimus.render.gui.event.IGuiEventListener;
import fr.nerss.mimus.utils.io.DataBuffer;

public class MainGui extends IGui implements IGuiEventListener {
	
	LD33 ld33;
	Button newGame;
	Button loadGame;
	Button option;
	Button quit;
	
	public MainGui(LD33 ld33) {
		this.ld33 = ld33;
		ld33.setDebug(false);
		newGame = new Button("New Game", 0, 0, 200, 40);
		newGame.setListener(this);
		loadGame = new Button("Load Game", 0, 0, 200, 40);
		loadGame.setListener(this);
		if(!(new File(Const.ld33Path + "\\save.blop")).exists()) {
			loadGame.setLocked(true);
		}
		option = new Button("Option", 0, 0, 200, 40);
		option.setListener(this);
		quit = new Button("Exit", 0, 0, 200, 40);
		quit.setListener(this);
		
		this.alignEventToColumn(Display.getWidth() / 2 - 100, Display.getHeight() / 2 - 93, 5, newGame, loadGame, option, quit);
		this.addEvents(newGame, loadGame, option, quit);
	}
	
	public void mouseClick(IGuiEventUse button, IGuiEvent src) {
		if(button == IGuiEventUse.LEFT) {
			if(src == newGame) {
				if(!(new File(Const.ld33Path + "\\save.blop")).exists()) {
					ld33.startGame(null);
				} else {
					int rep = JOptionPane.showConfirmDialog(
				            null,
				            "Delete your party?",
				            "Current Game",
				            JOptionPane.YES_NO_OPTION);
					if(rep == JOptionPane.YES_OPTION) {
						ld33.setPlayer(null);
						ld33.startGame(null);
					}
				}
				return;
			}
			if(src == loadGame) {
				DataBuffer data = new DataBuffer();
				try {
					data.read(Const.ld33Path + "\\save.blop");
					ld33.startGame(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
			if(src == option) {
				ld33.setGui(new OptionMenu(ld33, false));
				return;
			}
			if(src == quit) {
				ld33.stop();
				return;
			}
		}
	}

	public void mouseHover(IGuiEvent src) {
		
	}

	public void updateRezise() {
		this.alignEventToColumn(Display.getWidth() / 2 - 100, Display.getHeight() / 2 - 93, 5, newGame, loadGame, option, quit);
		
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
	}

	public void drawForeground() {

	}
}
