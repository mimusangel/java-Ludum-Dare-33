package fr.mimus.entities;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import org.lwjgl.opengl.Display;

import fr.mimus.Const;
import fr.mimus.LD33;
import fr.nerss.mimus.render.Color4f;
import fr.nerss.mimus.render.Sprites;
import fr.nerss.mimus.utils.colliders.Colliders;
import fr.nerss.mimus.utils.colliders.RectColliders;
import fr.nerss.mimus.utils.maths.Vector2f;

public class SimpleIA extends LivingEntity {
	long movingTime;
	int movingFrame;
	int id;
	float attract = 0;
	int viewDist;
	Vector2f view;
	float jumpInpulse;
	boolean needJump;
	float speed;
	int dmg;
	public SimpleIA(float x, float y, int life, int id, int viewDist, float speed, int dmg) {
		super(x, y, life);
		this.id = id;
		movingTime = 0;
		movingFrame = 0; 
		jumpInpulse = 0;
		needJump = false;
		view = new Vector2f();
		this.viewDist = viewDist;
		this.speed = speed;
		this.dmg = dmg;
	}

	public void render(Vector2f offset) {
		float yy = y + offset.y;
		if(yy <= - 32) return;
		if(yy > Display.getHeight() + 64) return;
		float xx = x + offset.x;
		if(xx <= - 32) return;
		if(xx > Display.getWidth()) return;
		if(System.currentTimeMillis() - dommageTime <= TIME_DOMMAGE/3) {
			Color4f.RED.bind();
		}
		Sprites sprt;
		switch(id) {
		case 1:
			sprt = Const.miner;
			break;
		case 2:
			sprt = Const.man1;
			break;
		default:
			sprt = Const.man0;
			break;
		}
		sprt.bind();
		glBegin(GL_QUADS);
			if(view.x >= 0) {
				sprt.getUV(movingFrame, 0).flipX().directRender(xx, yy - 64, 32, 64);
			} else {
				sprt.getUV(movingFrame, 0).directRender(xx, yy - 64, 32, 64);
			}
		glEnd();
		Color4f.WHITE.bind();
	}
	
	public boolean inScreen(Vector2f offset) {
		float yy = y + offset.y;
		if(yy <= - 32) return false;
		if(yy > Display.getHeight() + 64) return false;
		float xx = x + offset.x;
		if(xx <= - 32) return false;
		if(xx > Display.getWidth()) return false;
		return true;
	}
	
	public void update(int tick) {
		Player player = LD33.getInstance().getPlayer();
		float xxx = player.x - x;
		float yyy = player.y - y;
		boolean intDist = xxx * xxx + yyy * yyy <= (viewDist * 32) * (viewDist * 32);
		if(tick % 20 == 0) {
			if(intDist) {
				view.x = (player.x + 16) - (x + 16);
				view.y = (player.y + 16) - (y - 32);
			} else {
				view.x = (float) (Math.random() * 2f - 1f);
				view.y = 0;
			}
			view.normalize();
		}
		if(tick == 0 && intDist) {
			if(id == 2) {
				LD33.getInstance().getMap().addEntity(new GelaxBullet(x + 16, y - 32, 1, view.copy(), speed, dmg, this));
			}
		}
		if(System.currentTimeMillis() - movingTime <= 100) {
			if(tick % 6 == 0) {
				movingFrame++;
				if(movingFrame > 4) movingFrame = 0;
			}
		} else {
			movingFrame = 0;
		}
		
		if(needJump && jumpInpulse <= 0 && inGround) {
			jumpInpulse = 7f;
			inGround = false;
			attract = 0;
			needJump = false;
		}
		
		if(jumpInpulse <= 0) {
			if(inGround) attract = 0;
			attract += 0.5f;
			if(attract > 15f) attract = 15f;
			gravity(attract);
		} else {
			jumpInpulse -= 0.5f;
			move(new Vector2f(0, -1), jumpInpulse);
		}
		
		switch(id) {
		case 2:
			move(new Vector2f(view.x * -1, view.y), speed);
			movingTime = System.currentTimeMillis();
			break;
		default:
			move(view, speed);
			movingTime = System.currentTimeMillis();
			break;
		}
	}
	
	public void collid(Vector2f vec) {
		if(Math.abs(vec.x) > Math.abs(vec.y)) {
			this.y += vec.y;
			if(vec.y < 0) inGround = true;
		} else {
			this.x += vec.x;
			needJump = true;
		}
	}
	
	public Colliders getBound() {
		return new RectColliders(x + 9, y -45, 14, 41);
	}
	
	public int getDeadExp() {
		return 70 + id * 25;
	}

	public int getViewDist() {
		return viewDist;
	}

	public void setViewDist(int viewDist) {
		this.viewDist = viewDist;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public int getDmg() {
		return dmg;
	}

	public void setDmg(int dmg) {
		this.dmg = dmg;
	}

}
