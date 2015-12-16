package fr.mimus.entities;

import org.lwjgl.opengl.Display;

import fr.nerss.mimus.render.Color4f;
import fr.nerss.mimus.render.Texture;
import fr.nerss.mimus.utils.colliders.Colliders;
import fr.nerss.mimus.utils.colliders.RectColliders;
import fr.nerss.mimus.utils.maths.Vector2f;
import static org.lwjgl.opengl.GL11.*;

public class ParticleEntity extends LivingEntity {
	Vector2f dir;
	float attract;
	float size;
	Color4f color;
	float speed;
	boolean collided;
	boolean glue;
	public ParticleEntity(float x, float y, int life, Vector2f dir, float impulse, float speed, float size, Color4f color, boolean glue) {
		super(x, y, life);
		this.dir = dir;
		this.attract = -impulse;
		this.size = size;
		this.color = color;
		this.speed = speed;
		collided = false;
		this.glue = glue;
	}

	public void render(Vector2f offset) {
		float yy = y + offset.y;
		if(yy <= -6) return;
		if(yy > Display.getHeight() + 6) return;
		float xx = x + offset.x;
		if(xx <= -6) return;
		if(xx > Display.getWidth() + 6) return;
		Texture.unbind();
		color.bind();
		glPointSize(size);
		glBegin(GL_POINTS);
			glVertex2f(xx, yy);
		glEnd();
		Color4f.WHITE.bind();
	}
	
	public void update(int tick) {
		life--;
		
		if(!glue || (glue && !collided)) {
			move(dir, speed);
			
			if(collided || inGround) attract = 0;
			attract += 0.2f;
			if(attract > 15f) attract = 15f;
			gravity(attract);
		}
	}
	
	public void collid(Vector2f vec) {
		if(Math.abs(vec.x) > Math.abs(vec.y)) {
			this.y += vec.y;
			if(vec.y < 0) inGround = true;
		} else {
			this.x += vec.x;
		}
		collided = true;
	}
	
	public Colliders getBound() {
		return new RectColliders(x - 1f, y - 3f, 2f, 2f);
	}
}
