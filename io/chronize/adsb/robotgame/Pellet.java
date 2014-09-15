package io.chronize.adsb.robotgame;

import java.awt.*;
import java.util.Random;

public class Pellet extends GameEntity {

	public Pellet() {
		_color = Color.decode("#70f070");

		Random random = new Random();
		_x = random.nextInt(RobotGame.GRID_X);
		_y = random.nextInt(RobotGame.GRID_Y);
	}

	@Override
	public void draw(Graphics2D g) {
		_color = Color.getHSBColor(.333f, RobotGame.getBrightness() / 2 + .4f, RobotGame.getBrightness() / 2 + .4f);
		super.draw(g);
	}

}
