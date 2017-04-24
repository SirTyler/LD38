package me.sirtyler.ld38.ents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import me.sirtyler.ld38.world.GameWorld;

public class EntitySpaceShip extends Entity {

	public boolean isUsed = true;
	
	private TextureRegion[][] region;
	
	public EntitySpaceShip(float x, float y, GameWorld world) {
		super(x+16, y+24, world);
		region = TextureRegion.split(new Texture("spaceship.png"), 32, 48);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set(pos);
		
		body = world.bx2DWorld.createBody(bodyDef);
		body.setUserData(this);
		
		PolygonShape box = new PolygonShape();
		box.setAsBox(16f, 16f);
		
		
		FixtureDef sensorDef = new FixtureDef();
		sensorDef.shape = box;
		sensorDef.isSensor = true;
		
		body.createFixture(sensorDef);
		
		body.setFixedRotation(true);
		box.dispose();
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(region[0][0], body.getPosition().x-16, body.getPosition().y-24, 32, 48);
	}

	@Override
	public void dispose() {
		world.bx2DWorld.destroyBody(body);
	}

	public Body getBody() {
		return body;
	}
	
}
