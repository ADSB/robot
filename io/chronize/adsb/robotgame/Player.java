package io.chronize.adsb.robotgame;

import java.awt.Color;
import java.awt.Graphics2D;

public class Player extends Sprite {

	private int xPos;
	private int yPos;

	public static final int PLAYER_WIDTH = 16;
	public static final int PLAYER_HEIGHT = 16;

	public Player() {
		xPos = RobotGame.SCREEN_X / 2;
		yPos = RobotGame.SCREEN_Y / 2;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.YELLOW);
		g.fillRect(xPos - PLAYER_WIDTH / 2, yPos - PLAYER_HEIGHT / 2, PLAYER_WIDTH, PLAYER_HEIGHT);
	}

	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}

	public void setXPos(int newX) {
		xPos = newX;
	}

	public void setYPos(int newY) {
		yPos = newY;
	}

	public boolean playerCrushed(Robot r) {
		if ((xPos + PLAYER_WIDTH / 2 > r.getXPos() - Robot.WIDTH / 2) && (xPos - PLAYER_WIDTH / 2 < r.getXPos() + Robot.WIDTH / 2))
			if ((yPos + PLAYER_HEIGHT / 2 > r.getYPos() - Robot.HEIGHT / 2) && (yPos - PLAYER_HEIGHT / 2 < r.getYPos() + Robot.HEIGHT / 2))
				return true;

		return false;
	}


}
