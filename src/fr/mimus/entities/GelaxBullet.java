package fr.mimus.entities;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.Display;

import fr.mimus.Const;
import fr.mimus.LD33;
import fr.nerss.mimus.render.Color4f;
import fr.nerss.mimus.utils.colliders.Colliders;
import fr.nerss.mimus.utils.colliders.RectColliders;
import fr.nerss.mimus.utils.maths.Vector2f;

public class GelaxBullet extends Entity {
	int id;
	Vector2f dir;
	int dommage;
	float speed;
	Entity src;
	long time;
	public GelaxBullet(float x, float y, int id, Vector2f dir, float speed, int dommage, Entity src) {
		super(x, y);
		this.id = id;
		this.dir = dir;
		this.speed = speed;
		this.dommage = dommage;
		this.src = src;
		this.time = System.currentTimeMillis();
	}

	public void render(Vector2f offset) {
		float yy = y + offset.y;
		if(yy <= -6) return;
		if(yy > Display.getHeight() + 6) return;
		float xx = x + offset.x;
		if(xx <= -6) return;
		if(xx > Display.getWidth() + 6) return;
		float size = 2f;
		if(id == 3) {
			Const.bullet.bind();
			size = 4f;
		} else if(id == 2) {
			Const.arrow.bind();
			size = 6f;
		} else if(id == 1) {
			Const.bulletRock.bind();
		} else {
			Const.bullet.bind();
		}
		glBegin(GL_QUADS);
			glTexCoord2f(0, 0); glVertex2f(x - size + offset.x, y - size + offset.y);
			glTexCoord2f(1, 0); glVertex2f(x + size + offset.x, y - size + offset.y);
			glTexCoord2f(1, 1); glVertex2f(x + size + offset.x, y + size + offset.y);
			glTexCoord2f(0, 1); glVertex2f(x - size + offset.x, y + size + offset.y);
		glEnd();
	}

	public void update(int tick) {
		if(id == 0 && tick % 6 == 0) {
			LD33.getInstance().getMap().addEntity(new ParticleEntity(x, y, 200, new Vector2f(-dir.x, ((float) Math.random() * 2f - 1f)), ((float) Math.random() * 5f), ((float) Math.random() * 1.5f + 0.5f), 2f, Color4f.LIGHT_GREEN.copy().mul((float) Math.random() * 0.5f + 0.5f), true));
		}
		if((id == 1 || id == 3) && tick % 6 == 0) {
			LD33.getInstance().getMap().addEntity(new ParticleEntity(x, y, 200, new Vector2f(-dir.x, ((float) Math.random() * 2f - 1f)), ((float) Math.random() * 5f), ((float) Math.random() * 1.5f + 0.5f), 2f, Color4f.LIGHT_GRAY.copy().mul((float) Math.random() * 0.5f + 0.5f), true));
		}
		
		this.x += dir.x * speed;
		this.y += dir.y * speed;
	}

	public boolean isDead() {
		return System.currentTimeMillis() - time > 5000;
	}
	
	public Colliders getBound() {
		float size = 2f;
		if(id == 2) {
			size = 6f;
		} else if(id == 3) {
			size = 4f;
		}
		return new RectColliders(x - size, y - size, size * 2, 4f);
	}

	public Entity getSource() {
		return src;
	}

	public int getDommage() {
		return dommage;
	}
}
