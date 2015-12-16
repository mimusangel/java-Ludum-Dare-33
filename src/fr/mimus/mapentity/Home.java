package fr.mimus.mapentity;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import org.lwjgl.opengl.Display;

import fr.mimus.Const;
import fr.mimus.LD33;
import fr.mimus.Map;
import fr.mimus.entities.Entity;
import fr.mimus.entities.Player;
import fr.mimus.entities.SimpleIA;
import fr.nerss.mimus.utils.colliders.Colliders;
import fr.nerss.mimus.utils.colliders.RectColliders;
import fr.nerss.mimus.utils.maths.Vector2f;

public class Home extends MapEntity {
	
	int maxLife;
	int quart;
	public Home(float x, float y, int life) {
		super(x, y, life);
		maxLife = life;
		quart = life / 4;
	}

	public void render(Vector2f offset) {
		float yy = y * 32 + offset.y;
		if(yy <= -160) return;
		if(yy > Display.getHeight() + 160) return;
		float xx = x * 32 + offset.x;
		if(xx <= -213) return;
		if(xx > Display.getWidth()) return;
		Const.home.bind();
		glBegin(GL_QUADS);
			Const.home.getUV(life <= 0 ? 3 : (maxLife - life) / quart, 0).directRender(xx, yy - 128, 213, 160);
		glEnd();
	}

	public void update(int tick) {

	}
	
	public Colliders getBound() {
		return new RectColliders(x * 32 + 26, y * 32 - 110, 161, 160);
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
					player.setDeadTime(player.getDeadTime() - 10);
					if(player.getDeadTime() < 0) {
						player.setDeadTime(0);
					}
				}
			}
			if(this.life <= 0) {
				int spawnNumber = (int) (Math.random() * 6) + 1;
				Map map = LD33.getInstance().getMap();
				for(int i = 0; i < spawnNumber; i++) {
					float rand = (float) (Math.random() * 3f);
					if(rand > 2) {
						map.addEntity(new SimpleIA(x * 32 + i * 24, y * 32 - 64, 18, 2, 14, 2, 2));
					} else if(rand > 1) {
						map.addEntity(new SimpleIA(x * 32 + i * 24, y * 32 - 64, 9, 1, 7, 3, 2));
					} else {
						map.addEntity(new SimpleIA(x * 32 + i * 24, y * 32 - 64, 3, 0, 5, 2, 1));
					}
				}
			}
		}
	}
	
	public int getDeadExp() {
		return 40;
	}

}
