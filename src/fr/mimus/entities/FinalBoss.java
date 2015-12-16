package fr.mimus.entities;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import org.lwjgl.opengl.Display;

import fr.mimus.Const;
import fr.mimus.LD33;
import fr.mimus.Map;
import fr.mimus.gui.WinMenu;
import fr.nerss.mimus.render.Sprites;
import fr.nerss.mimus.utils.colliders.Colliders;
import fr.nerss.mimus.utils.colliders.RectColliders;
import fr.nerss.mimus.utils.maths.Vector2f;

public class FinalBoss extends LivingEntity {
	int quart;
	int maxLife;
	Vector2f view;
	public FinalBoss(float x, float y) {
		super(x, y, 500);
		view = new Vector2f();
		maxLife = 500;
		quart = 125;
	}

	public void render(Vector2f offset) {
		float yy = y + offset.y;
		if(yy <= - 32) return;
		if(yy > Display.getHeight() + 64) return;
		float xx = x + offset.x;
		if(xx <= - 32) return;
		if(xx > Display.getWidth()) return;
		Sprites sprt = Const.finalBoss;
		sprt.bind();
		glBegin(GL_QUADS);
		sprt.getUV(life <= 0 ? 3 : (maxLife - life) / quart, 0).directRender(xx, yy - 320, 512, 356);
		glEnd();
	}

	public void update(int tick) {
		Player player = LD33.getInstance().getPlayer();
		float xxx = (player.x + 16) - (x + 16);
		float yyy = (player.y + 16) - (y + 16);
		boolean intDist = xxx * xxx + yyy * yyy <= (20 * 32) * (20 * 32);
		if(intDist) {
			if(tick % 40 == 0) {
				player.setDeadTime(player.getDeadTime() - 1);
				if(player.getDeadTime() < 0) {
					player.setDeadTime(0);
				}
				Map map = LD33.getInstance().getMap();
				float rand = (float) (Math.random() * 3f);
				if(rand > 2) {
					map.addEntity(new SimpleIA(x - 64, y - 10, 18, 2, 14, 2, 2));
				} else if(rand > 1) {
					map.addEntity(new SimpleIA(x - 64, y - 10, 9, 1, 7, 3, 2));
				} else {
					map.addEntity(new SimpleIA(x - 64, y - 10, 3, 0, 5, 2, 1));
				}
			}
		}
		view.x = (player.x + 16) - (x - 16);
		view.y = (player.y + 16) - (y - 96);
		view.normalize();
		intDist = xxx * xxx + yyy * yyy <= (30 * 32) * (30 * 32);
		if(tick % 30 == 0 && intDist) {
			LD33.getInstance().getMap().addEntity(new GelaxBullet(x - 16, y - 96, 2, view.copy(), 4, 5, this));
		}
	}
	

	public void dommage(Entity src, int dmg) {
		if(System.currentTimeMillis() - dommageTime > TIME_DOMMAGE) {
			dommageTime = System.currentTimeMillis();
			lastDommageSource = src;
			this.life -= dmg;
			if(isDead() && src instanceof Player) {
				((Player) src).addExp(getDeadExp());
			}
			// Fin du jeu
			if(isDead()) {
				LD33.getInstance().setGui(new WinMenu(LD33.getInstance()));
			}
		}
	}
	
	public int getDeadExp() {
		return 10000;
	}
	
	public Colliders getBound() {
		return new RectColliders(x + 38, y - 320 + 73, 437, 283);
	}
}
