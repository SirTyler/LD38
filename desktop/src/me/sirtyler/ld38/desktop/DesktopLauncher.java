package me.sirtyler.ld38.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.sirtyler.ld38.LD38Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Moon Dash - LD38";
		new LwjglApplication(new LD38Game(), config);
	}
}
