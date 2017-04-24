package me.sirtyler.ld38.ents;

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

import me.sirtyler.ld38.ExtraDebugRenderer;
import me.sirtyler.ld38.world.GameWorld;
import me.sirtyler.ld38.world.PlanetType;

public class EntityComet extends Entity {
	
	private Body body;
	private TextureRegion[][] region;
	private int type = 0;
	private float damage = 10f;

	public EntityComet(float x, float y, GameWorld world) {
		super(x, y, world);
		region = TextureRegion.split(new Texture("planets.png"), PlanetType.PLANET_SIZE, PlanetType.PLANET_SIZE);
		type = MathUtils.random(1);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(pos);
		
		body = world.bx2DWorld.createBody(bodyDef);
		body.setUserData(this);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(16f);
		
		FixtureDef fixDef = new FixtureDef();
		fixDef.shape = circle;
		fixDef.density = 0.5f;
		fixDef.friction = 0.4f;
		fixDef.restitution = 0.6f;
		
		body.createFixture(fixDef);
		circle.dispose();
	}
	
	public float getDamage() {
		return damage;
	}

	@Override
	public void update(float deltaTime) {
		Vector2 path = new Vector2(world.player.body.getWorldCenter());
		path.sub(body.getWorldCenter());
		body.applyLinearImpulse(path, body.getWorldCenter(), true);
	}

	@Override
	public void render(SpriteBatch batch) {
		Vector2 dPos = new Vector2((body.getPosition().x - 32), (body.getPosition().y - 32));
		batch.draw(region[1][type], dPos.x, dPos.y, 64f, 64f);
		
		ExtraDebugRenderer extdebug = new ExtraDebugRenderer();
		batch.end();
		if(world.debug) extdebug.drawDebugLine(body.getWorldCenter(), world.player.body.getWorldCenter(), 3, Color.RED, batch.getProjectionMatrix());
		batch.begin();
	}

	@Override
	public void dispose() {
		world.bx2DWorld.destroyBody(body);
	}

}
