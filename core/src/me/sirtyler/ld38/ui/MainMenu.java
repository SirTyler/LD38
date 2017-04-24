package me.sirtyler.ld38.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import me.sirtyler.ld38.LD38Game;
import me.sirtyler.ld38.world.PlanetType;

@SuppressWarnings("unused")
public class MainMenu {
	
	private Button[] buttons = new Button[2];
	private Texture title, logo, ld, twitter, controls;
	private Vector2 center;
	private TextureRegion[][] region;
	private BitmapFont font;
	
	private float scaleFact = 0f, dir = 1, spin = 0, count = 0;
	private OrthographicCamera cam;
	
	private int animState = 0;
	private Sound sound;
	
	public MainMenu() {
		logo = new Texture("logo.png");
		ld = new Texture("ld.png");
		title = new Texture("menu.png");
		twitter = new Texture("twitter.png");
		controls = new Texture("controls.png");
		
		sound = Gdx.audio.newSound(Gdx.files.internal("play.wav"));
		
		center = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		region = TextureRegion.split(new Texture("planets.png"), PlanetType.PLANET_SIZE, PlanetType.PLANET_SIZE);
		
		font = new BitmapFont();
		
		buttons[0] = new Button(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2-32, "play");
		buttons[1] = new Button(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2-112, "quit");
	}
	
	public void render(OrthographicCamera camera, SpriteBatch batch) {
		cam = camera;
		LD38Game.world.resetCamera(cam);
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		switch(animState) {
		case 0:
			// Personal Logo
			batch.setColor(1f, 1f, 1f, (spin/100));
			batch.draw(logo, center.x-(logo.getWidth()/2*scaleFact), center.y-(logo.getHeight()/2*scaleFact), logo.getWidth()*scaleFact, logo.getHeight()*scaleFact);
			break;
		case 1:
			// Ludum Dare Logo
			batch.setColor(1f, 1f, 1f, (spin/100));
			batch.draw(ld, center.x-(ld.getWidth()/2*scaleFact), center.y-(ld.getHeight()/2*scaleFact), ld.getWidth()*scaleFact, ld.getHeight()*scaleFact);
			break;
		case 3:
			// Controls
			batch.setColor(1f, 1f, 1f, 1f);
			batch.draw(controls, center.x-(controls.getWidth()/2), center.y-(controls.getHeight()/2));
			break;
		case 2:
		default:
			// Main Menu
			buttons[0].render(batch);
			buttons[1].render(batch);
			batch.setColor(1f, 1f, 1f, 1f);
			batch.draw(title, center.x-96, center.y-8, 96*4, 64*4);
			batch.draw(region[0][5], center.x-160, center.y+64, 128/2, 128/2, 128, 128, scaleFact, scaleFact, spin);
			batch.draw(twitter, center.x-300, center.y-220);
		}
		
		batch.end();
	}
	
	public void update(float deltaTime) {
		switch(animState) {
		case 0:
			if(scaleFact < 1.3f) scaleFact = scaleFact + 0.01f;
			if(spin < 100) spin++;
			if(count++ > 300) {
				animState = 1;
				scaleFact = 0f;
				spin = 0;
				count = 0;
			}
			
			if(Gdx.input.justTouched()) {
				animState = 1;
				scaleFact = 0f;
				spin = 0;
				count = 0;
			}
			break;
		case 1:
			if(scaleFact < 1.3f) scaleFact = scaleFact + 0.01f;
			if(spin < 100) spin++;
			if(count++ > 300) {
				animState = 2;
				scaleFact = 1f;
				spin = 0;
				count = 0;
			}
			
			if(Gdx.input.justTouched()) {
				animState = 2;
				scaleFact = 1f;
				spin = 0;
				count = 0;
			}
			break;
		case 3:
			if(Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.ANY_KEY)) {
				LD38Game.gamestate = 1;
				animState = 2;
			}
			break;
		case 2:
		default:
			scaleFact = scaleFact + (dir*0.01f);
			if(scaleFact > 1.3f) dir = -1;
			if(scaleFact < 1f) dir = 1;
			if(spin++ > 360) {
				spin = 0;
			}
			
			Vector2 mPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
			Vector3 worldCord = new Vector3(mPos, 0);
			cam.unproject(worldCord);
			mPos = new Vector2(worldCord.x, worldCord.y);
			
			Button near = null;
			
			for(int i = 0; i < buttons.length; i++) {
				float x = buttons[i].getLocation().x - mPos.x;
				float y = buttons[i].getLocation().y - mPos.y;
				if(y < 30 && y > -30 && x < 100 && x > -100) {
					near = buttons[i];
					near.hover = true;
				} else buttons[i].hover = false;
			}
			
			if(Gdx.input.justTouched()) {
				System.out.println("Clicked ["+mPos.x+"|"+mPos.y+"]");
				if(near != null) {
					System.out.println("Clicked on "+near.getName());
					if(near.getName().equalsIgnoreCase("play")) {
						animState = 3;
						sound.play(0.5f);
					}
					if(near.getName().equalsIgnoreCase("quit")) {
						Gdx.app.exit();
					}
				}
			}
		}
	}
}
