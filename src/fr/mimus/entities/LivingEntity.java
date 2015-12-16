package fr.mimus.entities;

import fr.mimus.Audio;
import fr.mimus.LD33;
import fr.mimus.Map;
import fr.nerss.mimus.utils.maths.Vector2f;


public abstract class LivingEntity extends Entity {
	public static final int TIME_DOMMAGE = 400;
	protected long dommageTime;
	protected int life;
	protected boolean inGround;
	protected Entity lastDommageSource;
	public LivingEntity(float x, float y, int life) {
		super(x, y);
		dommageTime = 0;
		this.life = life;
		lastDommageSource = null;
		inGround = false;
	}
	
	public boolean isDead() {
		return this.life <= 0;
	}
	
	public void dommage(Entity src, int dmg) {
		if(System.currentTimeMillis() - dommageTime > TIME_DOMMAGE) {
			dommageTime = System.currentTimeMillis();
			lastDommageSource = src;
			this.life -= dmg;
			if(isDead() && src instanceof Player) {
				((Player) src).addExp(getDeadExp());
				Audio.hit.play();
			}
		}
	}
	
	public void move(Vector2f vec, float speed) {
		x += vec.x * speed;
		y += vec.y * speed;
		if(x < 0) x = 0;
	}
	
	public void gravity(float attract) {
		Vector2f dir = new Vector2f(0, 1);
		move(dir, attract);
		inGround = false;
	}
	
	public void collid(Vector2f vec) {
		if(Math.abs(vec.x) > Math.abs(vec.y)) {
			this.y += vec.y;
			if(vec.y < 0) inGround = true;
		} else {
			this.x += vec.x;
		}
	}

	public boolean isInGound() {
		return inGround;
	}

	public void setInGound(boolean inGound) {
		this.inGround = inGound;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}
	
	public int getDeadExp() {
		return 1;
	}
	

	public boolean inLadder() {
		int v = LD33.getInstance().getMap().getData((int) ((x + 16) / 32), (int) ((y + 16) / 32), 0, 0);
		return v == Map.BACKGROUND_LADDER || v == Map.BACKGROUND_WOOD_LADDER;
	}
	
	public boolean inScreen(Vector2f offset) {
		return true;
	}

}
