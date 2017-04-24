package me.sirtyler.ld38;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import me.sirtyler.ld38.ui.LoseScreen;
import me.sirtyler.ld38.ui.MainMenu;
import me.sirtyler.ld38.ui.WinScreen;
import me.sirtyler.ld38.world.GameWorld;

public class LD38Game extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera cam;
	
	public static GameWorld world;
	private static MainMenu menu;
	private static LoseScreen lose;
	private static WinScreen win;
	public static int gamestate = 0;
	
	private Music bgMusic;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		cam = new OrthographicCamera();
		cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.update();
		
		world = new GameWorld();
		menu = new MainMenu();
		
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("bg_snd2.wav"));
		bgMusic.setVolume(0.5f);
		bgMusic.setLooping(true);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		cam.update();
		switch(gamestate) {
		case 0:
			// Main Menu
			if(bgMusic.isPlaying()) bgMusic.stop();
			menu.render(cam, batch);
			menu.update(Gdx.graphics.getDeltaTime());
			break;
		case 2:
			// Lose Screen
			if(bgMusic.isPlaying()) bgMusic.stop();
			if(lose == null) lose = new LoseScreen();
			lose.render(cam, batch);
			lose.update(Gdx.graphics.getDeltaTime());
			break;
		case 3:
			// Win Screen
			if(bgMusic.isPlaying()) bgMusic.stop();
			if(win == null) win = new WinScreen();
			win.render(cam, batch);
			win.update(Gdx.graphics.getDeltaTime());
			break;
		default:
			// Game Screen
			if(!bgMusic.isPlaying()) bgMusic.play();
			world.render(cam, batch);
			world.update(Gdx.graphics.getDeltaTime());
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		world.dispose();
	}
}
