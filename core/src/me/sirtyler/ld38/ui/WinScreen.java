package me.sirtyler.ld38.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import me.sirtyler.ld38.LD38Game;

@SuppressWarnings("unused")
public class WinScreen {
	private Button[] buttons = new Button[2];
	private Texture imgA, imgB;
	private Vector2 center;
	private OrthographicCamera cam;
	
	private float count = 0, tick = 0;
	private boolean fadeOut = false;
	
	private BitmapFont font;
	
	public WinScreen() {
		imgA = new Texture("win_A.png");
		imgB = new Texture("win_B.png");
		
		center = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		
		buttons[0] = new Button(Gdx.graphics.getWidth()/2-140,Gdx.graphics.getHeight()/2-64, "play");
		buttons[1] = new Button(Gdx.graphics.getWidth()/2-140,Gdx.graphics.getHeight()/2-144, "quit");
		
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		font.getData().setScale(4);
		
		LD38Game.world.update(Gdx.graphics.getDeltaTime());
	}
	
	public void render(OrthographicCamera camera, SpriteBatch batch) {
		cam = camera;
		LD38Game.world.resetCamera(cam);
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		batch.draw(imgA, 0, 0, camera.viewportWidth, camera.viewportHeight);
		if(fadeOut) batch.setColor(1f, 1f, 1f, 1f-(count/100));
		else batch.setColor(1f, 1f, 1f, (count/100));
		batch.draw(imgB, 0, 0, camera.viewportWidth, camera.viewportHeight);
		batch.setColor(1f, 1f, 1f, 1f);
		
		buttons[0].render(batch);
		buttons[1].render(batch);
		
		font.draw(batch, "You Win", (camera.viewportWidth/2)+32, camera.viewportHeight-64);
		
		batch.end();
	}
	
	public void update(float deltaTime) {
		if(count < 100) count++;
		if(tick++ > 300) {
			tick = 0;
			count = 0;
			fadeOut = !fadeOut;
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
					LD38Game.world.restart();
					LD38Game.gamestate = 1;
					LD38Game.world.levelCount = 0;
				}
				if(near.getName().equalsIgnoreCase("quit")) {
					Gdx.app.exit();
				}
			}
		}
	}
	
}
