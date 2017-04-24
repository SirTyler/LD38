package me.sirtyler.ld38.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import me.sirtyler.ld38.LD38Game;
import me.sirtyler.ld38.ents.EntityPlanet;
import me.sirtyler.ld38.ents.EntityPlayer;

public class GameUI {
	
	private EntityPlayer player;
	private TextureRegion[][] region;
	private BitmapFont font;
	
	public EntityPlanet goal;
	
	public GameUI(EntityPlayer player) {
		this.player = player;
		region = TextureRegion.split(new Texture("ui.png"), 32, 32);
		font = new BitmapFont();
	}
	
	public void renderBar(OrthographicCamera camera, ShapeRenderer rend) {
		float w = camera.viewportWidth/2;
		float h = camera.viewportHeight/2;

		//Player Health Bar
		rend.setColor(Color.SALMON);
		rend.rect(camera.position.x-w+38, camera.position.y+h-25, 120-(player.health_max - player.health), 18);
		
		//Player Fuel Bar
		rend.setColor(Color.SKY);
		rend.rect(camera.position.x-w+38, camera.position.y+h-57, 120-(player.fuel_max - player.fuel), 18);
	}
	
	
	public void render(OrthographicCamera camera, SpriteBatch batch) {
		float w = camera.viewportWidth/2;
		float h = camera.viewportHeight/2;
		
		//Level Name
		font.getData().setScale(2f);
		font.draw(batch, "Level: "+(LD38Game.world.levelCount+1), camera.position.x+w-128, camera.position.y+h-32);
		
		//Player Health UI
		batch.draw(region[1][0], camera.position.x-w+32, camera.position.y+h-32, 64, 32);
		batch.draw(region[1][0], camera.position.x-w+160, camera.position.y+h, 0, 0, 32, 32, 2f, 1f, 180f);
		batch.draw(region[0][0], camera.position.x-w, camera.position.y+h-32, 32, 32);
		
		//Player Fuel UI
		batch.draw(region[1][0], camera.position.x-w+32, camera.position.y+h-64, 64, 32);
		batch.draw(region[1][0], camera.position.x-w+160, camera.position.y+h-32, 0, 0, 32, 32, 2f, 1f, 180f);
		batch.draw(region[0][1], camera.position.x-w, camera.position.y+h-64, 32, 32);
		
		//Goal Pointer
		Vector2 apos = goal.getPosition().cpy();
		Vector2 bpos = player.getCenter().cpy();
		
		float dy = bpos.y - apos.y;
		float dx = bpos.x - apos.x;
		float theta = 180 / MathUtils.PI * MathUtils.atan2(dx, -dy);
		//batch.draw(region[0][2], bpos.x-16, bpos.y+16, 16, 16, 32, 32, 1f, 1f, theta);
		
		float mx = 8;
		float my = 32;
		float angle = ((player.getAngle()*MathUtils.radDeg)-90)*MathUtils.degRad;
		float x = mx*MathUtils.cos(angle) - my*MathUtils.sin(angle);
		float y = mx*MathUtils.sin(angle) + my*MathUtils.cos(angle);
		batch.draw(region[0][2], bpos.x-16+x, bpos.y-16+y, 16, 16, 32, 32, 1f, 1f, theta);
	}
}
