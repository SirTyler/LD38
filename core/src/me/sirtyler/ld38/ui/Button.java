package me.sirtyler.ld38.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Button {
	
	private Vector2 location;
	private String name;
	
	private TextureRegion[][] region;
	private BitmapFont font;
	
	public boolean hover = false;
	
	public Button(float x, float y, String name) {
		this(new Vector2(x, y), name);
	}
	
	public Button(Vector2 location, String name) {
		this.location = location;
		this.name = name;
		region = TextureRegion.split(new Texture("ui.png"), 32, 32);
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(3);
	}

	public Vector2 getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}
	
	public void render(SpriteBatch batch) {
		batch.draw(region[1][(hover?3:1)], location.x-64, location.y, 32, 32, 32, 32, 3f, 3f, 0f);
		batch.draw(region[1][2], location.x, location.y, 32, 32, 32, 32, 3f, 3f, 0f);
		batch.draw(region[1][(hover?3:1)], location.x, location.y-96, 32, 32, 32, 32, 3f, 3f, 180f);
		
		font.draw(batch, name, location.x-48, location.y+4);
	}
}
