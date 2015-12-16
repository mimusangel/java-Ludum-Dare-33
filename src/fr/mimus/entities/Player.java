package fr.mimus.entities;

import static org.lwjgl.opengl.GL11.*;
import fr.mimus.Audio;
import fr.mimus.Const;
import fr.mimus.LD33;
import fr.nerss.mimus.render.Color4f;
import fr.nerss.mimus.utils.colliders.Colliders;
import fr.nerss.mimus.utils.colliders.RectColliders;
import fr.nerss.mimus.utils.inputs.Inputs;
import fr.nerss.mimus.utils.io.DataBuffer;
import fr.nerss.mimus.utils.maths.Vector2f;

public class Player extends LivingEntity {
	public static final int BONUS_TREE_HEAL_LIFE = 0x0001;
	public static final int BONUS_TREE_HEAL_TIME = 0x0002;
	public static final int BONUS_REDUCE_PRICE = 0x0004;
	public static final int BONUS_DOUBLE_EXP = 0x0008;
	public static final int BONUS_FAST_REGEN = 0x0010;
	
	long movingTime;
	int movingFrame;
	float jumpInpulse;
	float attract = 0;
	Vector2f view;
	
	int deadTime;
	
	// Stat
	int exp;
	
	// Capacity
	public float speed;
	public float jump;
	public int lifeTime;
	public float speedAttack;
	public int dmgAttack;
	public int bonus;
	public int dead;
	
	public long timerRegen;
	
	public Player(float x, float y, int life) {
		super(x, y, life);
		movingTime = 0;
		movingFrame = 0; 
		jumpInpulse = 0;
		view = new Vector2f();
		
		exp = 0;
		speed = 2f;
		jump = 6f;
		lifeTime = 60;
		speedAttack = 2.2f;
		dmgAttack = 1;
		bonus = 0;
		dead = 0;
		timerRegen = System.currentTimeMillis();
	}

	public void write(DataBuffer buffer) {
		try {
			buffer.put(exp);
			buffer.put(speed);
			buffer.put(jump);
			buffer.put(lifeTime);
			buffer.put(speedAttack);
			buffer.put(dmgAttack);
			buffer.put(bonus);
			buffer.put(dead);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void read(DataBuffer buffer) {
		try {
			exp = buffer.getInt();
			speed = buffer.getFloat();
			jump = buffer.getFloat();
			lifeTime = buffer.getInt();
			speedAttack = buffer.getFloat();
			dmgAttack = buffer.getInt();
			bonus = buffer.getInt();
			dead = buffer.getInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void render(Vector2f offset) {
		view.x = Inputs.getMouseX() - (x + offset.x + 16);
		view.y = Inputs.getMouseY() - (y + offset.y + 10);
		view.normalize();
		Const.blop.bind();
		if(System.currentTimeMillis() - dommageTime <= TIME_DOMMAGE/3) {
			Color4f.RED.bind();
		}
		glBegin(GL_QUADS);
			float scale = 1f - ((float) deadTime / (float) lifeTime);
			float size = 32f * scale;
			if(view.x >= 0) {
				Const.blop.getUV(movingFrame, view.y > 0 ? 1 : 0).directRender(x + offset.x - size/2, y + offset.y - size/2, 32 + size, 32 + size);
			} else {
				Const.blop.getUV(movingFrame, view.y > 0 ? 1 : 0).flipX().directRender(x + offset.x - size/2, y + offset.y - size/2, 32 + size, 32 + size);
			}
		glEnd();
		Color4f.WHITE.bind();
	}

	public void update(int tick) {
		if(deadTime >= lifeTime) {
			deadTime = lifeTime;
//			if(tick == 0) life--;
		}
		if(System.currentTimeMillis() - timerRegen > (this.hasBonus(BONUS_FAST_REGEN) ? 2000 : 5000)) {
			deadTime -= 1;
			if(deadTime < 0) deadTime = 0;
			timerRegen = System.currentTimeMillis();
		}
		if(System.currentTimeMillis() - movingTime <= 100) {
			if(tick % 6 == 0) {
				movingFrame++;
				if(movingFrame > 2) movingFrame = 0;
			}
		} else {
			movingFrame = 0;
		}
		
		Vector2f dir = new Vector2f();
		if(inLadder()) {
			inGround = false;
			if(Inputs.keyDown(Const.KEY_UP)) {
				dir.y = -1f;
				movingTime = System.currentTimeMillis();
			}
			if(Inputs.keyDown(Const.KEY_DOWN)) {
				dir.y = 1f;
				movingTime = System.currentTimeMillis();
			}
		}
		if(Inputs.keyDown(Const.KEY_LEFT)) {
			dir.x = -1;
			movingTime = System.currentTimeMillis();
		}
		if(Inputs.keyDown(Const.KEY_RIGHT)) {
			dir.x = 1;
			movingTime = System.currentTimeMillis();
		}
		dir.normalize();
		move(dir, speed);
		
//		if(tick == 0 && !dir.isNull()) {
//			if(deadTime >= lifeTime) {
//				life--;
//			} else {
//				deadTime++;
//			}
//			timerRegen = System.currentTimeMillis();
//		}
		
		if(!inGround && tick % 2 == 0) {
			Vector2f pdir = new Vector2f(((float) Math.random() * 2f - 1f), (float) Math.random());
			pdir.normalize();
			LD33.getInstance().getMap().addEntity(new ParticleEntity(x + 16, y + 16, 200, pdir, ((float) Math.random() * 5f), ((float) Math.random() * 1.5f + 0.5f), 2f, Color4f.LIGHT_GREEN.copy().mul((float) Math.random() * 0.5f + 0.5f), true));
		}
		
		if(Inputs.keyDown(Const.KEY_JUMP) && jumpInpulse <= 0 && inGround) {
			jumpInpulse = jump;
			inGround = false;
			attract = 0;
			Audio.jump.play();
		}

		if(Inputs.mouseUnPress(0)) {
			LD33.getInstance().getMap().addEntity(new GelaxBullet(x + 16, y + 10, 0, view.copy(), speedAttack, dmgAttack, this));
			Audio.shoot.play();
			if(deadTime >= lifeTime) {
				life--;
			} else {
				deadTime++;
			}
			timerRegen = System.currentTimeMillis();
		}
		if(Inputs.mouseUnPress(1)) {
			if(deadTime < lifeTime - 1) {
				LD33.getInstance().getMap().addEntity(new GelaxBullet(x + 16, y + 10, 3, view.copy(), speedAttack, dmgAttack * 2, this));
				Audio.shoot.play();
				deadTime+= 2;
				timerRegen = System.currentTimeMillis();
			}
			
		}
	}
	
	public void gravity() {
		if(jumpInpulse <= 0) {
			if(!inLadder()) {
				if(inGround) attract = 0;
				attract += 0.5f;
				if(attract > 10f) attract = 10f;
				gravity(attract);
			} else {
				attract = 0;
			}
		} else {
			move(new Vector2f(0, -1), jumpInpulse);
			jumpInpulse -= 0.5f;
		}
	}
	
	public void addExp(int exp) {
		this.exp += exp * (this.hasBonus(BONUS_DOUBLE_EXP) ? 2 : 1);
	}
	
	public Colliders getBound() {
		return new RectColliders(x + 9, y + 4, 14, 13);
	}

	public int getDeadTime() {
		return deadTime;
	}

	public void setDeadTime(int deadTime) {
		this.deadTime = deadTime;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
		this.speed = Math.round(this.speed * 10f) / 10f;
	}

	public float getJump() {
		return jump;
	}

	public void setJump(float jump) {
		this.jump = jump;
		this.jump = Math.round(this.jump * 10f) / 10f;
	}

	public int getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(int lifeTime) {
		this.lifeTime = lifeTime;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public float getSpeedAttack() {
		return speedAttack;
	}

	public void setSpeedAttack(float speedAttack) {
		this.speedAttack = speedAttack;
		this.speedAttack = Math.round(this.speedAttack * 10f) / 10f;
	}

	public int getDmgAttack() {
		return dmgAttack;
	}

	public void setDmgAttack(int dmgAttack) {
		this.dmgAttack = dmgAttack;
	}
	
	public void addBonus(int id) {
		if(!hasBonus(id)) {
			bonus = bonus | id;
		}
	}
	
	public boolean hasBonus(int id) {
		return (bonus & id) == id;
	}
}
