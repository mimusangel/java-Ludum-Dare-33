package fr.mimus;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.Display;

import fr.mimus.entities.Entity;
import fr.mimus.entities.FinalBoss;
import fr.mimus.entities.GelaxBullet;
import fr.mimus.entities.ParticleEntity;
import fr.mimus.entities.Player;
import fr.mimus.entities.SimpleIA;
import fr.mimus.mapentity.*;
import fr.nerss.mimus.game.IMap;
import fr.nerss.mimus.render.QuadUV;
import fr.nerss.mimus.utils.colliders.Colliders;
import fr.nerss.mimus.utils.colliders.RectColliders;
import fr.nerss.mimus.utils.maths.Vector2f;

public class Map extends IMap {
	public static final int NONE = 0xffffffff;

	public static final int GRASS = 0xff00ff00;
	public static final int STONE = 0xff808080;
	public static final int WOOD = 0xff683717;
	public static final int TREE = 0xff400000;
	public static final int ROCK = 0xff666699;
	public static final int HOME = 0xffff6600;
	public static final int SPAWN = 0xff800080;
	public static final int SPAWN_MAN0 = 0xffffff00;
	public static final int SPAWN_MAN1 = 0xffff80ff;
	public static final int SPAWN_MINER = 0xffffff80;
	public static final int SPAWN_FINAL_BOSS = 0xff000000;
	public static final int BACKGROUND_STONE = 0xffC0C0C0;
	public static final int BACKGROUND_LADDER = 0xffc0ffc0;
	public static final int BACKGROUND_WOOD_LADDER = 0xffad5c27;
	public static final int BACKGROUND_WOOD = 0xff8b4a1f;
	int height;
	int width;
	int data[];
	int dataB[];
	float spawnX;
	float spawnY;
	ArrayList<Entity> entities;
	Colliders[] mapCollider;
	
	public Map(String path) {
		spawnX = 0;
		spawnY = 0;
		try {
			BufferedImage image = ImageIO.read(new File(path));
			height = image.getHeight();
			width = image.getWidth();
			data = new int[width * height];
			dataB = new int[width * height];
			image.getRGB(0, 0, width, height, data, 0, width);
			
			
			entities = new ArrayList<Entity>();
			mapCollider = new Colliders[width * height];
			
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					int value = getData(x, y, 0, NONE);
				if(value == NONE) continue;
					switch(value) {
					case TREE:
						entities.add(new Tree(x, y));
						setData(x, y, 0, NONE);
						break;
					case ROCK:
						entities.add(new Rock(x, y));
						setData(x, y, 0, BACKGROUND_STONE);
						break;
					case HOME:
						entities.add(new Home(x, y, 40));
						setData(x, y, 0, NONE);
						break;
					case SPAWN:
						spawnX = x * 32f;
						spawnY = y * 32f;
						setData(x, y, 0, NONE);
						break;
					case SPAWN_MAN0:
						entities.add(new SimpleIA(x * 32, y * 32, 3, 0, 5, 2, 1));
						setData(x, y, 0, NONE);
						break;
					case SPAWN_MAN1:
						entities.add(new SimpleIA(x * 32, y * 32, 18, 2, 14, 2, 2));
						setData(x, y, 0, NONE);
						break;
					case SPAWN_MINER:
						entities.add(new SimpleIA(x * 32, y * 32, 9, 1, 7, 3, 2));
						setData(x, y, 0, BACKGROUND_STONE);
						break;
					case SPAWN_FINAL_BOSS:
						entities.add(new FinalBoss(x * 32, y * 32));
						setData(x, y, 0, NONE);
						break;
					case BACKGROUND_STONE:
						break;
					case BACKGROUND_LADDER:
						break;
					case BACKGROUND_WOOD:
						break;
					case BACKGROUND_WOOD_LADDER:
						break;
					default:
						mapCollider[y * width + x] = new RectColliders(x * 32, y * 32, 32, 32);
						break;
					}
				}
			}
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					dataB[y * width + x] = getDiffB(x, y, 0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getDiffB(int x, int y, int layer) {
		int dataB = 0;
		
		if(getData(x - 1, y, layer, NONE) != NONE) {
			dataB = dataB | 0x1;
		}
		if(getData(x + 1, y, layer, NONE) != NONE) {
			dataB = dataB | 0x2;
		}
		if(getData(x, y - 1, layer, NONE) != NONE) {
			dataB = dataB | 0x4;
		}
		if(getData(x, y + 1, layer, NONE) != NONE) {
			dataB = dataB | 0x8;
		}

		return dataB;
	}
	
	public int getData(int x, int y, int layer, int def) {
		if(x < 0 || y < 0 || x >= width || y >= height) return def;
		return data[y * width + x];
	}

	public boolean setData(int x, int y, int layer, int id) {
		if(x < 0 || y < 0 || x >= width || y >= height) return false;
		data[y * width + x] = id;
		return true;
	}
	
	public void update(int tick, Vector2f offset) {
		int i = 0;
		while(i < entities.size()) {
			Entity entity = entities.get(i);
			if(entity.isDead()) {
				entities.remove(i);
			} else {
				if(!(entity instanceof MapEntity)) {
					entity.update(tick);
					if(entity.getBound() != null) {
						int minX = (int) (entity.getX() / 32) - 3;
						int maxX = minX + 7;
						int minY = (int) (entity.getY() / 32) - 3;
						int maxY = minY + 7;

						if(minX < 0) minX = 0;
						if(minY < 0) minX = 0;
						if(maxX > width) maxX = width;
						if(maxY > height) maxY = height;
						for(int y = minY; y < maxY; y++) {
							for(int x = minX; x < maxX; x++) {
								if(y * width + x < 0 || y * width + x >= mapCollider.length) continue;
								Colliders colliders = mapCollider[y * width + x];
								if(colliders == null) continue;
								Vector2f vec = entity.getBound().isCollid(colliders);
								if(vec != null && !vec.isNull()) {
									if(entity instanceof GelaxBullet) {
										entities.remove(i);
										i--;
										break;
									} else {
										entity.collid(vec);
									}
								}
							}
						}
						if(!(entity instanceof ParticleEntity)) {
							for(int j = 0; j < entities.size(); j++) {
								Entity entity2 = entities.get(j);
								if(entity == entity2) continue;
								if(entity2.getBound() == null) continue;
								if(entity2 instanceof ParticleEntity) continue;
								if(entity instanceof Player) {
									if(entity2 instanceof GelaxBullet && entity == ((GelaxBullet)entity2).getSource()) {
										continue;
									}
								}
								Vector2f vec = entity.getBound().isCollid(entity2.getBound());
								if(vec != null && !vec.isNull()) {
									entity.collid(vec);
									if(entity instanceof SimpleIA) {
										if(entity2 instanceof Player) {
											entity2.dommage(entity, ((SimpleIA) entity).getDmg());
											break;
										}
									}
									
									if(entity instanceof GelaxBullet) {
										if(entity2 != ((GelaxBullet)entity).getSource()) {
											entity2.dommage(((GelaxBullet)entity).getSource(), ((GelaxBullet)entity).getDommage());
											entity.dead();
											entities.remove(i);
											i--;
										}
										break;
									} 
								}
							}
						}
					}
					if(entity instanceof Player) {
						((Player) entity).gravity();
						if(entity.getBound() != null) {
							int minX = (int) (entity.getX() / 32) - 3;
							int maxX = minX + 7;
							int minY = (int) (entity.getY() / 32) - 3;
							int maxY = minY + 7;

							if(minX < 0) minX = 0;
							if(minY < 0) minX = 0;
							if(maxX > width) maxX = width;
							if(maxY > height) maxY = height;
							for(int y = minY; y < maxY; y++) {
								for(int x = minX; x < maxX; x++) {
									if(y * width + x < 0 || y * width + x >= mapCollider.length) continue;
									Colliders colliders = mapCollider[y * width + x];
									if(colliders == null) continue;
									Vector2f vec = entity.getBound().isCollid(colliders);
									if(vec != null && !vec.isNull()) {
										if(entity instanceof GelaxBullet) {
											entities.remove(i);
											i--;
											break;
										} else {
											entity.collid(vec);
										}
									}
								}
							}
							if(!(entity instanceof ParticleEntity)) {
								for(int j = 0; j < entities.size(); j++) {
									Entity entity2 = entities.get(j);
									if(entity == entity2) continue;
									if(entity2.getBound() == null) continue;
									if(entity2 instanceof ParticleEntity) continue;
									if(entity instanceof Player) {
										if(entity2 instanceof GelaxBullet && entity == ((GelaxBullet)entity2).getSource()) {
											continue;
										}
									}
									Vector2f vec = entity.getBound().isCollid(entity2.getBound());
									if(vec != null && !vec.isNull()) {
										entity.collid(vec);
										if(entity instanceof SimpleIA) {
											if(entity2 instanceof Player) {
												entity2.dommage(entity, ((SimpleIA) entity).getDmg());
												break;
											}
										}
										
										if(entity instanceof GelaxBullet) {
											if(entity2 != ((GelaxBullet)entity).getSource()) {
												entity2.dommage(((GelaxBullet)entity).getSource(), ((GelaxBullet)entity).getDommage());
												entity.dead();
												entities.remove(i);
												i--;
											}
											break;
										} 
									}
								}
							}
						}
					}
				}
				if(entity.isDead()) {
					entities.remove(i);
				} else {
					i++;
				}
			}
		}
	}
	
	public void render(Vector2f offset) {
		int minX = (int) (Math.abs(offset.x) / 32) - 1;
		int maxX = minX + Display.getWidth() / 32 + 3;
		int minY = (int) (Math.abs(offset.y) / 32) - 1;
		int maxY = minY + Display.getHeight() / 32 + 3;

		if(minX < 0) minX = 0;
		if(minY < 0) minX = 0;
		if(maxX > width) maxX = width;
		if(maxY > height) maxY = height;
		
		for(int y = minY; y < maxY; y++) {
			float yy = y * 32 + offset.y;
			if(yy <= - 32) continue;
			if(yy > Display.getHeight()) break;
			for(int x = minX; x < maxX; x++) {
				float xx = x * 32 + offset.x;
				if(xx <= - 32) continue;
				if(xx > Display.getWidth()) break;
				int value = getData(x, y, 0, NONE);
				if(value == NONE) continue;
				switch(value) {
				case GRASS:
					Const.grass.bind();
					break;
				case STONE:
					Const.stone.bind();
					break;
				case WOOD:
					Const.wood.bind();
					break;
				case BACKGROUND_STONE:
					Const.bgStone.bind();
					glBegin(GL_QUADS);
						(new QuadUV(0, 1, 0, 1)).directRender(xx, yy, 32, 32);
					glEnd();
					continue;
				case BACKGROUND_LADDER:
					Const.bgLadder.bind();
					glBegin(GL_QUADS);
						(new QuadUV(0, 1, 0, 1)).directRender(xx, yy, 32, 32);
					glEnd();
					continue;
				case BACKGROUND_WOOD:
					Const.bgWood.bind();
					glBegin(GL_QUADS);
						(new QuadUV(0, 1, 0, 1)).directRender(xx, yy, 32, 32);
					glEnd();
					continue;
				case BACKGROUND_WOOD_LADDER:
					Const.bgWoodLadder.bind();
					glBegin(GL_QUADS);
						(new QuadUV(0, 1, 0, 1)).directRender(xx, yy, 32, 32);
					glEnd();
					continue;
				default:
					continue;
				}
				glBegin(GL_QUADS);
					Const.grass.getUV(dataB[y * width + x]).directRender(xx, yy, 32, 32);
				glEnd();
			}
		}
		
		
		for(Entity entity : entities) {
			entity.render(offset);
		}
	}
	
	public void addEntity(Entity entity) {
		if(entity instanceof ParticleEntity && Const.PARTICLE != 1) return;
		entities.add(entity);
	}
	public boolean hasPlayerEntity() {
		for(int j = 0; j < entities.size(); j++) {
			Entity entity = entities.get(j);
			if(entity instanceof Player) {
				return true;
			}
		}
		return false;
	}
	public int entitiesSize() {
		return entities.size();
	}
	public float getSpawnX() {
		return spawnX;
	}

	public float getSpawnY() {
		return spawnY;
	}
}
