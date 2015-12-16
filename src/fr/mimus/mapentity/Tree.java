package fr.mimus.mapentity;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.opengl.Display;

import fr.mimus.Const;
import fr.mimus.entities.Entity;
import fr.mimus.entities.Player;
import fr.nerss.mimus.utils.colliders.Colliders;
import fr.nerss.mimus.utils.colliders.RectColliders;
import fr.nerss.mimus.utils.maths.Vector2f;

public class Tree extends MapEntity {

	public Tree(int x, int y) {
		super(x, y, 1);
	}

	public void render(Vector2f offset) {
		float yy = y * 32 + offset.y;
		if(yy <= - 32) return;
		if(yy > Display.getHeight()) return;
		float xx = x * 32 + offset.x;
		if(xx <= - 32) return;
		if(xx > Display.getWidth()) return;
		if(this.life <= 0) {
			Const.trunk.bind();
		} else {
			Const.tree.bind();
		}
		glBegin(GL_QUADS);
			glTexCoord2f(0, 0); glVertex2f(xx, 		yy - 26);
			glTexCoord2f(1, 0); glVertex2f(xx + 32,	yy - 26);
			glTexCoord2f(1, 1); glVertex2f(xx + 32, yy + 38);
			glTexCoord2f(0, 1); glVertex2f(xx, 		yy + 38);
		glEnd();
//		RectColliders c = (RectColliders) getBound();
//		Texture.unbind();
//		glBegin(GL_QUADS);
//			glVertex2f(c.x + offset.x, 		c.y + offset.y);
//			glVertex2f(c.x + c.w + offset.x,c.y + offset.y);
//			glVertex2f(c.x + c.w + offset.x,c.y + c.h + offset.y);
//			glVertex2f(c.x + offset.x, 		c.y + c.h + offset.y);
//		glEnd();
	}

	public void update(int tick) {

	}
	
	public boolean isDead() {
		return false;
	}
	
	public Colliders getBound() {
		if(this.life <= 0) return null;
		return new RectColliders(x * 32 + 5, y * 32 - 26 + 1, 22, 62);
	}
	
	public void dommage(Entity src, int dmg) {
		if(System.currentTimeMillis() - dommageTime > TIME_DOMMAGE) {
			dommageTime = System.currentTimeMillis();
			lastDommageSource = src;
			this.life -= dmg;
			if(this.life <= 0 && src instanceof Player) {
				Player player = (Player) src;
				player.addExp(getDeadExp());
				if(player.hasBonus(Player.BONUS_TREE_HEAL_LIFE)) {
					player.setLife(player.getLife() + 1);
					if(player.getLife() > 10) {
						player.setLife(10);
					}
				}
				if(player.hasBonus(Player.BONUS_TREE_HEAL_TIME)) {
					player.setDeadTime(player.getDeadTime() - 3);
					if(player.getDeadTime() < 0) {
						player.setDeadTime(0);
					}
				}
			}
		}
	}
	
	public int getDeadExp() {
		return 5;
	}
}
