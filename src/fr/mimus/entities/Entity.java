package fr.mimus.entities;

import fr.nerss.mimus.utils.colliders.Colliders;
import fr.nerss.mimus.utils.colliders.RectColliders;
import fr.nerss.mimus.utils.maths.Vector2f;

public abstract class Entity {
	protected float x;
	protected float y;
	public Entity(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public abstract void render(Vector2f offset);
	public abstract void update(int tick);

	public void collid(Vector2f vec) {
		if(Math.abs(vec.x) > Math.abs(vec.y)) {
			this.y += vec.y;
		} else {
			this.x += vec.x;
		}
	}
	
	public boolean isDead() {
		return false;
	}
	public void dommage(Entity src, int dmg) {}
	
	public Colliders getBound() {
		return new RectColliders(x, y, 32, 32);
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void dead() {
		
	}
}
