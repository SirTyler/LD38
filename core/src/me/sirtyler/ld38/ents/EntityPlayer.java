package me.sirtyler.ld38.ents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import me.sirtyler.ld38.ExtraDebugRenderer;
import me.sirtyler.ld38.LD38Game;
import me.sirtyler.ld38.world.GameWorld;

public class EntityPlayer extends Entity {
	//Box 2D Player
	public Body body;
	float MAX_VELOCITY = 50f;
	
	TextureRegion[][] region;
	boolean isLeft = false, animMove = false, animJump = false;
	float animCount = 0f;
	
	Sound jumpSound;
	
	public float angle = 0f;
	public int health = 120, health_max = 120;
	public int fuel = 120, fuel_max = 120;
	public int points = 0;
	public boolean doRefuel = false;
	
	public EntityPlayer(GameWorld gameworld) {
		super(gameworld);
		region = TextureRegion.split(new Texture("astro.png"), 32, 32);
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.wav"));
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(Gdx.graphics.getWidth()/2f,Gdx.graphics.getHeight()/2f);
		
		body = world.bx2DWorld.createBody(bodyDef);
		body.setUserData(this);
		
		PolygonShape box = new PolygonShape();
		box.setAsBox(8f, 8f);
		CircleShape circle = new CircleShape();
		circle.setRadius(16f);
		
		FixtureDef fixDef = new FixtureDef();
		fixDef.shape = box;
		fixDef.density = 0.5f;
		fixDef.friction = 0.4f;
		fixDef.restitution = 0.6f;
		
		FixtureDef sensorDef = new FixtureDef();
		sensorDef.shape = circle;
		sensorDef.isSensor = true;
		
		body.createFixture(fixDef);
		body.createFixture(sensorDef);
		
		body.setFixedRotation(true);
		box.dispose();
		circle.dispose();
	}
	
	public Vector2 getCenter() {
		return body.getWorldCenter();
	}
	
	public void setPosition(float x, float y) {
		body.setTransform(x, y, 0f);
	}
	
	public void setPosition(Vector2 v) {
		body.setTransform(v, 0f);
	}
	
	public Vector2 getPosition() {
		return body.getPosition();
	}
	
	public float getAngle() {
		return body.getAngle();
	}
	
	@Override
	public void update(float deltaTime) {
		if(world.levelComplete) return;
		
		Vector2 vel = body.getLinearVelocity();
		Vector2 pos = body.getPosition();
		
		if(Gdx.input.justTouched()) {
			Vector2 mPos = new Vector2((Gdx.input.getX() - Gdx.graphics.getWidth()/2), (-Gdx.input.getY() + Gdx.graphics.getHeight()/2));
			if(world.debug) System.out.println("Clicked @ ["+mPos.x+"|"+mPos.y+"] ["+Gdx.input.getX() +"|"+Gdx.input.getY()+"]");
			//body.applyLinearImpulse(mPos.x, mPos.y, pos.x, pos.y, true);
		}
		
		if(Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
			Vector2 pushOff = new Vector2(world.bx2DWorld.getGravity());
			pushOff.rotate90(-1);
			body.applyLinearImpulse(pushOff, body.getWorldCenter(), true);
			if(vel.x > -MAX_VELOCITY || vel.y > -MAX_VELOCITY) body.applyLinearImpulse(-100f*(Math.signum(angle)), 0f, pos.x, pos.y, true);
			if(!isLeft) {
				region[0][0].flip(true, false);
				region[0][1].flip(true, false);
				region[0][2].flip(true, false);
				region[0][3].flip(true, false);
				region[0][4].flip(true, false);
			}
			isLeft = true;
			animMove = true;
		} else if(Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
			Vector2 pushOff = new Vector2(world.bx2DWorld.getGravity());
			pushOff.rotate90(1);
			body.applyLinearImpulse(pushOff, body.getWorldCenter(), true);
			//if(vel.x < MAX_VELOCITY || vel.y < MAX_VELOCITY) body.applyLinearImpulse(100f*(Math.signum(angle)), 0f, pos.x, pos.y, true);
			if(isLeft) {
				region[0][0].flip(true, false);
				region[0][1].flip(true, false);
				region[0][2].flip(true, false);
				region[0][3].flip(true, false);
				region[0][4].flip(true, false);
			}
			isLeft = false;
			animMove = true;
		} else animMove = false;
		
		if(Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) jumpSound.play(0.5f);
		if(Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
			Vector2 pushOff = new Vector2();
			if(world.nearest != null) {
				pushOff.set(world.nearest.getWorldCenter());
				pushOff.sub(body.getWorldCenter());
				pushOff.scl(-400f);
				animJump = true;
			} else {
				pushOff.set(world.bx2DWorld.getGravity());
				pushOff.scl(-400f);
				animJump = true;
			}
			if(fuel > 0) {
				body.applyLinearImpulse(pushOff, body.getWorldCenter(), true);
				fuel -= 1;
			}
		} else animJump = false;
		
		if(Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)) {
			Vector2 pushOff = new Vector2();
			if(world.nearest != null) {
				pushOff.set(world.nearest.getWorldCenter());
				pushOff.sub(body.getWorldCenter());
				pushOff.scl(+400f);
			} else {
				pushOff.set(world.bx2DWorld.getGravity());
				pushOff.scl(+400f);
			}
			if(fuel > 0) {
				body.applyLinearImpulse(pushOff, body.getWorldCenter(), true);
				fuel -= 10;
			}
		}
		
		if(doRefuel && fuel < fuel_max) {
			fuel += 1;
		}
		
		if(health <= 0) LD38Game.gamestate = 2;
	}
	
	public void dispose() {
		world.bx2DWorld.destroyBody(body);
	}

	@Override
	public void render(SpriteBatch batch) {
		//batch.draw(image, body.getPosition().x - 16f, body.getPosition().y -16f, 16f, 15f, 32f, 32f, 1f, 1f, (body.getAngle()*MathUtils.radDeg)-90, 0, 0, 32, 32, isLeft, false);
		if(animCount++ > 50) animCount = 0;
		
		if(world.levelComplete) {
			batch.draw(region[0][5+(animCount>25?1:0)], body.getPosition().x - 16f, body.getPosition().y - 16f, 16f, 15f, 32f, 32f, 1f, 1f, (body.getAngle()*MathUtils.radDeg)-90);
		} else if(animJump) {
			TextureRegion r = region[0][3+(animCount>25?1:0)];
			//r.flip(isLeft, false);
			batch.draw(r, body.getPosition().x - 16f, body.getPosition().y - 16f, 16f, 15f, 32f, 32f, 1f, 1f, (body.getAngle()*MathUtils.radDeg)-90);
		} else if(animMove) {
			TextureRegion r = region[0][1+(animCount>25?1:0)];
			//r.flip(isLeft, false);
			batch.draw(r, body.getPosition().x - 16f, body.getPosition().y - 16f, 16f, 15f, 32f, 32f, 1f, 1f, (body.getAngle()*MathUtils.radDeg)-90);
		} else {
			batch.draw(region[0][0], body.getPosition().x - 16f, body.getPosition().y - 16f, 16f, 15f, 32f, 32f, 1f, 1f, (body.getAngle()*MathUtils.radDeg)-90);
		}
		
		ExtraDebugRenderer extdebug = new ExtraDebugRenderer();
		if(world.debug) {
			extdebug.drawString(("Health:"+health+"\nFuel:"+health+"\nPos:["+Math.round(body.getPosition().x)+","+Math.round(body.getPosition().y)+"]"), new Vector2(body.getPosition().x+16, body.getPosition().y+32), Color.WHITE, batch);
			if(world.nearest != null) {
				batch.end();
				extdebug.drawDebugLine(body.getWorldCenter(), world.nearest.getWorldCenter(), batch.getProjectionMatrix());
				batch.begin();
			}
		}
	}
}
