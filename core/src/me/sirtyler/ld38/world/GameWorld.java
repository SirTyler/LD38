package me.sirtyler.ld38.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import me.sirtyler.ld38.LD38Game;
import me.sirtyler.ld38.ents.Entity;
import me.sirtyler.ld38.ents.EntityComet;
import me.sirtyler.ld38.ents.EntityPlanet;
import me.sirtyler.ld38.ents.EntityPlayer;
import me.sirtyler.ld38.ents.EntitySpaceShip;
import me.sirtyler.ld38.ui.GameUI;

public class GameWorld {
	
	public World bx2DWorld;
	Array<Vector2[]> stars = new Array<Vector2[]>();
	
	Array<Entity> alive = new Array<Entity>();
	Array<Entity> dead = new Array<Entity>();
	
	public EntityPlayer player;
	public GameUI gameUI;
	public boolean debug = false;
	public boolean levelComplete = false, followPlayer = true;
	
	public int levelCount = 0;
	public int animState = 0, animCounter = 0, anim = 0;
	private int cometTimer_Max = 500;
	private int cometTimer = 500;
	
	private Vector3 oldCamLoc;
	private EntityPlanet startPlanet;
	public Body nearest = null;
	
	public GameWorld() {
		bx2DWorld = new World(new Vector2(0, -10), true);
		bx2DWorld.setContactListener(new PlayerListener());
		
		player = new EntityPlayer(this);
		alive.add(player);
		
		gameUI = new GameUI(player);
		
		genRandomPlanets();
		genStars();
	}
	
	public void render(OrthographicCamera camera, SpriteBatch batch) {
		if(oldCamLoc == null) oldCamLoc = camera.position.cpy();
		
		ShapeRenderer temp = new ShapeRenderer();
		temp.setProjectionMatrix(camera.combined);
		temp.begin(ShapeType.Filled);
		
		for(int i = 0; i < stars.size; i++) {
			Vector2 pos = stars.get(i)[0];
			Vector2 info = stars.get(i)[1];
			switch((int) info.x) {
			case 0:
				temp.setColor(Color.WHITE);
				break;
			case 1:
				temp.setColor(Color.CYAN);
				break;
			case 2:
				temp.setColor(Color.GOLD);
				break;
			case 3:
				temp.setColor(Color.VIOLET);
				break;
			case 4:
				temp.setColor(Color.LIME);
				break;
			case 5:
				temp.setColor(Color.SALMON);
				break;
			}
			if((cometTimer%2)==1)
				temp.circle(pos.x, pos.y, MathUtils.random(1f, info.y));
			else
				temp.circle(pos.x, pos.y, 1f);
		}
		
		temp.end();
		
		
		switch(animState) {
		case 1:
			//Level Change
			camera.zoom = (1f / 3f);
			camera.update();
			
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			
			EntityPlanet goal = gameUI.goal;
			
			if(animCounter++ < 500 && anim == 0) {
				batch.setColor(1f, 1f, 1f, 1f-((float) animCounter/500f));
				goal.render(batch);
				player.render(batch);
			} else if(animCounter++ > 500 && anim == 0) {
				animCounter = 0;
				anim = 1;
				batch.setColor(1f, 1f, 1f, 0f);
				restart();
				camera.position.set(player.getPosition(),0);
				camera.update();
				levelCount++;
			} else if(animCounter++ < 500 && anim == 1) {
				batch.setColor(1f, 1f, 1f, ((float) animCounter/500f));
				startPlanet.render(batch);
				player.render(batch);
			} else {
				anim = 0;
				animCounter = 0;
				animState = 0;
				batch.setColor(1f, 1f, 1f, 1f);
				camera.zoom = 1f;
				camera.update();
			}
			
			/*if(animCounter++ < 200 && anim == 0) {
				goal.render(batch);
				player.render(batch);
			} else if(animCounter++ > 200 && anim == 0){
				anim = 1;
				animCounter = 0;
				goal.render(batch);
				player.render(batch);
			} else if(animCounter++ < 100 && anim == 1) {
				goal.render(batch);
			} else if(animCounter++ < 800 && anim == 1) {
				goal.render(batch);
				ship.getBody().setTransform(ship.getBody().getPosition().x, ship.getBody().getPosition().y+1f, 0f);
				camera.position.set(ship.getBody().getPosition(), 0);
			} else if(animCounter++ > 800 && anim == 1) {
				anim = 2;
				animCounter = 0;
				ship.render(batch);
				restart();
				ship.getBody().setTransform(startPlanet.getPosition().x, startPlanet.getPosition().y+800f, 0f);
			} else if(animCounter++ < 1000 && anim == 2) {
				ship.getBody().setTransform(startPlanet.getPosition().x, startPlanet.getPosition().y-1f, 0f);
				camera.position.set(ship.getBody().getPosition(), 0);
				ship.render(batch);
				startPlanet.render(batch);
			} else if(animCounter++ > 1000 && anim == 2) {
				anim = 3;
				animCounter = 0;
				ship.render(batch);
				startPlanet.render(batch);
			} else if(animCounter++ < 200 && anim == 3) {
				ship.render(batch);
				startPlanet.render(batch);
				player.render(batch);
			} else if(animCounter++ > 200 && anim == 3) {
				anim = 4;
				animCounter = 0;
				ship.render(batch);
				startPlanet.render(batch);
				player.render(batch);
			} else if(animCounter++ < 600 && anim == 4) {
				ship.getBody().setTransform(startPlanet.getPosition().x, startPlanet.getPosition().y+1f, 0f);
				ship.render(batch);
				startPlanet.render(batch);
				player.render(batch);
			} else if(animCounter++ > 1000) {
				anim = 0;
				animCounter = 0;
				animState = 0;
				camera.zoom = 1f;
				camera.update();
			}*/
			
			batch.end();
			break;
		case 0:
		default:
			//Normal Game
			batch.setColor(1f, 1f, 1f, 1f);
			camera.zoom = 1f;
			camera.update();
			
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			
			for(Entity e : alive) {
				if(e instanceof EntityPlayer) {
					if(followPlayer) camera.position.set(((EntityPlayer) e).getPosition(),0);
					camera.update();
				} else e.render(batch);
			}
			player.render(batch);
			
			Box2DDebugRenderer debugRend = new Box2DDebugRenderer();
			if(debug) debugRend.render(bx2DWorld, camera.combined);
			batch.end();
			
			if(!levelComplete) {
				temp.setProjectionMatrix(camera.combined);
				temp.begin(ShapeType.Filled);
				gameUI.renderBar(camera, temp);
				temp.end();
			
				batch.setProjectionMatrix(camera.combined);
				batch.begin();
				gameUI.render(camera, batch);
				batch.end();
			}
		}
		
	}
	
	public void update(float delta) {
		if(cometTimer++ >= cometTimer_Max && !levelComplete) {
			int x = Math.abs(MathUtils.random(1000) - 32);
			int y = Math.abs(MathUtils.random(1000) - 32);
			alive.add(new EntityComet(x, y, this));
			cometTimer = 0;
		}
		
		for(Entity e : alive) {
			e.update(delta);
		}
		
		Array<Body> list = new Array<Body>();
		bx2DWorld.getBodies(list);
			
		float lastDist = 1000f;
		for(Body b : list) {
			if(!(b.getUserData() instanceof EntityPlanet)) continue;
			Vector2 pCenter = player.body.getWorldCenter();
			Vector2 bCenter = b.getWorldCenter();
			float newDist = bCenter.dst(pCenter);
			if(newDist > 0f && newDist < 126f && newDist < lastDist) {
				//if(nearest != b) System.out.println("Nearest Planet at ["+b.getWorldCenter().x+"|"+b.getWorldCenter().y+"]");
				nearest = b;
				lastDist = newDist;
			}
		}
		
		Vector2 newGravity = new Vector2(0f, 0f);
		float angle = 0f;
		if(nearest != null) {
			newGravity.set(nearest.getWorldCenter());
			newGravity.sub(player.body.getWorldCenter());
			newGravity.scl(2f);
			bx2DWorld.setGravity(newGravity);
				
			float dx = player.body.getWorldCenter().x - nearest.getWorldCenter().x;
			float dy = player.body.getWorldCenter().y - nearest.getWorldCenter().y;
			angle = MathUtils.atan2(dy, dx);
			player.body.setTransform(player.body.getPosition().x, player.body.getPosition().y, angle);
		}
		player.angle = angle;
		
		if(levelComplete) {
			newGravity.scl(5f);
			bx2DWorld.setGravity(newGravity);
			
			float dx = player.body.getWorldCenter().x - nearest.getWorldCenter().x;
			float dy = player.body.getWorldCenter().y - nearest.getWorldCenter().y;
			angle = MathUtils.atan2(dy, dx);
			player.body.setTransform(player.body.getPosition().x, player.body.getPosition().y, angle);

			for(Entity ent : alive) {
				if(ent instanceof EntityPlanet) {
					EntityPlanet p = (EntityPlanet) ent;
					if(p.type.equals(PlanetType.GOAL)) continue;
				}
				if(ent instanceof EntityPlayer) continue;
				
				kill(ent);
			}
		}
		
		bx2DWorld.step(1/60f, 6, 2);
		
		for(int i = 0; i < dead.size; i++) {
			dead.get(i).dispose();
			dead.removeIndex(i);
		}
		
		if(levelComplete && levelCount >= 5) LD38Game.gamestate = 3;
		else if(levelComplete && animState == 0) animState = 1;
		
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			restart();
			LD38Game.gamestate = 0;
		}
	}
	
	public void restart() {
		levelComplete = false;
		
		for(Entity e : alive) {
			if(!(e instanceof EntityPlayer)) e.dispose();
		}
		
		Array<Body> list = new Array<Body>();
		bx2DWorld.getBodies(list);
		for(Body b : list) {
			if(b.getUserData() instanceof EntityPlayer) continue;
			else if(b.getUserData() instanceof EntitySpaceShip) continue;
			bx2DWorld.destroyBody(b);
		}
		
		alive.clear();
		dead.clear();
		
		alive.add(player);
		player.health = player.health_max;
		player.fuel = player.fuel_max;
		player.setPosition(Gdx.graphics.getWidth()/2f,Gdx.graphics.getHeight()/2f);
		
		genRandomPlanets();
	}

	public OrthographicCamera resetCamera(OrthographicCamera cam) {
		if(oldCamLoc != null)
			cam.position.set(oldCamLoc);
		cam.zoom = 1f;
		cam.update();
		return cam;
	}
	
	public Entity kill(Entity entity) {
		if(alive.contains(entity, false)) {
			alive.removeValue(entity, false);
			dead.add(entity);
		}
		return entity;
	}
	
	private void genRandomPlanets() {
		int max = MathUtils.random(15)+5;
		//int max = 2;
		Vector2[] locs = new Vector2[max];
		
		if(debug) System.out.println("Generate "+max+" Planets");
		
		Vector2 p = new Vector2(Gdx.graphics.getWidth()/2f, (Gdx.graphics.getWidth()/2f)-138);
		EntityPlanet planet = new EntityPlanet(p.x, p.y, this);
		alive.add(planet);
		nearest = planet.body;
		startPlanet = planet;
		locs[0] = p;
		
		EntityPlanet farthest = planet;
		
		boolean gen = false;
		for(int i = 1; i < max; i++) {
			int x = Math.abs(MathUtils.random(1000) - 32);
			int y = Math.abs(MathUtils.random(1000) - 32);
			Vector2 n = new Vector2(x,y);
			
			for(Vector2 chk : locs) {
				if(chk != null) {
					if(debug) System.out.println("Check Distance ["+i+"] "+n.dst(chk));
					if(n.dst(chk) < 128 || n.dst(chk) > 768) {
						gen = false;
						break;
					}
					else gen = true;
				}
			}
			if(gen == true) {
				int id = MathUtils.random(PlanetType.values().length-2)+1;
				PlanetType type = PlanetType.getPlanetByID(id);
				if(debug) System.out.println(id + ":"+type+"["+x+","+y+"]");
				EntityPlanet rPlanet = new EntityPlanet(x, y, this, type);
				locs[i] = n;
				alive.add(rPlanet);
				if(planet.getPosition().dst(rPlanet.getPosition()) > planet.getPosition().dst(farthest.getPosition())) {
					farthest = rPlanet;
				}
				gen = false;
			} else i--;
		}
		farthest.type = PlanetType.GOAL;
		gameUI.goal = farthest;
	}
	
	public void genStars() {
		int max = MathUtils.random(5000)+1000;
		//int max = 1;
		
		for(int i = 0; i < max; i++) {
			Vector2 v = new Vector2(MathUtils.random(1000), MathUtils.random(1000));
			Vector2[] vv = {v, new Vector2(MathUtils.random(5), MathUtils.random(3))};
			stars.add(vv);
		}
	}
	
	public void dispose() {
		
	}
}
