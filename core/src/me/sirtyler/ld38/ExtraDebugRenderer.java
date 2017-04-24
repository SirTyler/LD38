package me.sirtyler.ld38;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class ExtraDebugRenderer extends ShapeRenderer {
	
	private BitmapFont font = new BitmapFont();
	
	public void drawString(String string, Vector2 position, Color color, SpriteBatch batch) {
		font.setColor(color);
		font.draw(batch, string, position.x, position.y);
	}
	
	public void drawDebugLine(Vector2 start, Vector2 end, int lineWidth, Color color, Matrix4 projectionMatrix) {
		Gdx.gl.glLineWidth(lineWidth);
		this.setProjectionMatrix(projectionMatrix);
		this.begin(ShapeRenderer.ShapeType.Line);
		this.setColor(color);
		this.line(start, end);
		this.end();
		Gdx.gl.glLineWidth(1);
	}
	
	public void drawDebugLine(Vector2 start, Vector2 end, Matrix4 projectionMatrix) {
		Gdx.gl.glLineWidth(1);
		this.setProjectionMatrix(projectionMatrix);
		this.begin(ShapeRenderer.ShapeType.Line);
		this.setColor(Color.WHITE);
		this.line(start, end);
		this.end();
		Gdx.gl.glLineWidth(1);
	}
	
}
