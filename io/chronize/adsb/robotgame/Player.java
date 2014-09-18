package io.chronize.adsb.robotgame;

import java.awt.*;

public class Player extends GameEntity {

	public Player() {
		_x = RobotGame.getRadiusX() + 1;
		_y = RobotGame.getRadiusY() + 1;
	}

	@Override
	public void draw(Graphics2D g) {
		_color = Color.getHSBColor(0.f, 0.f, RobotGame.getBrightness() / 4 + .2f);
		super.draw(g);
	}

}
