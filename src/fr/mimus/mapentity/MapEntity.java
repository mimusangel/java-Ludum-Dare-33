package fr.mimus.mapentity;

import org.lwjgl.opengl.Display;

import fr.mimus.entities.LivingEntity;
import fr.nerss.mimus.utils.maths.Vector2f;

public abstract class MapEntity extends LivingEntity {

	public MapEntity(float x, float y, int life) {
		super(x, y, life);
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
}
