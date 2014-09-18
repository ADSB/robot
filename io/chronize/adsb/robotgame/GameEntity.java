package io.chronize.adsb.robotgame;

import java.awt.*;

public abstract class GameEntity extends Sprite {

	public static final int MOVE_UP = 1;
	public static final int MOVE_RIGHT = 2;
	public static final int MOVE_DOWN = 3;
	public static final int MOVE_LEFT = 4;

	protected int _x;
	protected int _y;

	protected Color _color;

	public int getX() {
		return _x;
	}
	public int getY() {
		return _y;
	}

	public void move(int direction) {
		switch (direction) {
			case MOVE_UP:
				if (_y > 0)
					_y--;
				break;
			case MOVE_RIGHT:
				if (_x < RobotGame.getGridX() - 1)
					_x++;
				break;
			case MOVE_DOWN:
				if (_y < RobotGame.getGridY() - 1)
					_y++;
				break;
			case MOVE_LEFT:
				if (_x > 0)
					_x--;
				break;
			default:
				break;
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(_color);
		g.fillRect(_x * (RobotGame.getWidthX() + 1) + 1, _y * (RobotGame.getWidthY() + 1) + 1, RobotGame.getWidthX(), RobotGame.getWidthY());
	}
}
