package me.sirtyler.ld38.ents;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import me.sirtyler.ld38.world.GameWorld;

public abstract class Entity {
	
	protected Body body;
	protected Vector2 pos, gravityDir;
	protected float velocityY = 0, velocityX = 0;
	
	protected GameWorld world;
	protected boolean grounded = false;
	
	public Entity(GameWorld world) {
		this(0,0,world);
	}
	
	public Entity(float x, float y, GameWorld world) {
		this.pos = new Vector2(x, y);
		this.world = world;
	}
	
	public abstract void update(float deltaTime);
	
	public abstract void render(SpriteBatch batch);
	
	public abstract void dispose();
}
