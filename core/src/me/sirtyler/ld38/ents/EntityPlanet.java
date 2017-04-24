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

public class EntityPlanet extends Entity {
	
	public PlanetType type;
	public EntitySpaceShip ship;
	public boolean doDamage = false, doHeal = false;
	
	public Body body;
	private TextureRegion[][] region;
	private int spin;
	
	public EntityPlanet(GameWorld gameworld) {
		this(0, 0, gameworld, PlanetType.SAFE);
	}
	
	public EntityPlanet(float x, float y, GameWorld gameworld) {
		this(x, y, gameworld, PlanetType.SAFE);
	}
	
	public EntityPlanet(float x, float y, GameWorld gameworld, int id) {
		this(x, y, gameworld, PlanetType.getPlanetByID(id));
	}
	
	public EntityPlanet(float x, float y, GameWorld gameworld, PlanetType type) {
		super(x, y, gameworld);
		this.type = type;
		region = TextureRegion.split(new Texture("planets.png"), PlanetType.PLANET_SIZE, PlanetType.PLANET_SIZE);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(pos);
		
		body = world.bx2DWorld.createBody(bodyDef);
		body.setUserData(this);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(PlanetType.PLANET_SIZE);
		
		FixtureDef fixDef = new FixtureDef();
		fixDef.shape = circle;
		fixDef.density = 0.5f;
		fixDef.friction = 0.4f;
		fixDef.restitution = 0.6f;
		
		body.createFixture(fixDef);
		circle.dispose();
		
		spin = MathUtils.random(360);
	}
	
	public Vector2 getPosition() {
		return body.getPosition();
	}

	public void dispose() {
		world.bx2DWorld.destroyBody(body);
	}

	@Override
	public void update(float deltaTime) {
		if(doDamage && world.player.health > 0) world.player.health -= type.getDamage();
		if(doHeal && world.player.health < world.player.health_max) world.player.health -= type.getDamage();
		if(spin++ > 360) spin = 0;
	}

	@Override
	public void render(SpriteBatch batch) {
		Vector2 dPos = new Vector2((body.getPosition().x - (PlanetType.PLANET_SIZE/2)), (body.getPosition().y - (PlanetType.PLANET_SIZE/2)));
		if(type != null) {
			batch.draw(region[0][type.getId()-1], dPos.x, dPos.y, PlanetType.PLANET_SIZE/2f, PlanetType.PLANET_SIZE/2f, PlanetType.PLANET_SIZE, PlanetType.PLANET_SIZE, 2f, 2f, spin);
			if(type.equals(PlanetType.GOAL)) {
				Vector2 shipPos = new Vector2(body.getWorldCenter().x - (PlanetType.PLANET_SIZE/2), body.getWorldCenter().y + (PlanetType.PLANET_SIZE/2));
				if(ship==null) ship = new EntitySpaceShip(shipPos.x, shipPos.y, world);
				ship.render(batch);
				ExtraDebugRenderer extdebug = new ExtraDebugRenderer();
				batch.end();
				if(world.debug) extdebug.drawDebugLine(body.getWorldCenter(), world.player.body.getWorldCenter(), 5, Color.GOLD, batch.getProjectionMatrix());
				batch.begin();
			}
		}
	}
}
