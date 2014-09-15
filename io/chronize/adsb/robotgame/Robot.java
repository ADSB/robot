package io.chronize.adsb.robotgame;

import java.awt.*;
import java.util.Random;

public class Robot extends GameEntity {

	public Robot() {
		_color = Color.decode("#f07070");

		Random random = new Random();
		_x = random.nextInt(RobotGame.GRID_X);
		_y = random.nextInt(RobotGame.GRID_Y);
	}

	@Override
	public void draw(Graphics2D g) {
		_color = Color.getHSBColor(0.f, .85f, RobotGame.getBrightness() / 2 + .2f);
		super.draw(g);
	}

}
