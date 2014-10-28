package io.chronize.adsb.robotgame;

import java.awt.*;
import java.util.Random;

public class Robot extends GameEntity {

	public Robot() {
		// Robots are red
		_color = Color.decode("#f07070");

		// Start at random position
		Random random = new Random();
		_x = random.nextInt(RobotGame.getGridX());
		_y = random.nextInt(RobotGame.getGridY());
	}

	@Override
	public void draw(Graphics2D g) {
		_color = Color.getHSBColor(0.f, .85f, RobotGame.getBrightness() / 2 + .2f);
		super.draw(g);
	}

}
