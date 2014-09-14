package io.chronize.adsb.robotgame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

public class Robot extends Sprite {

	public static final int WIDTH = 25;
	public static final int HEIGHT = 25;

	private int screenWidth;
	private int screenHeight;
	private int xPos;
	private int yPos;

	public Robot() {
		Random random = new Random();
		xPos = random.nextInt(RobotGame.SCREEN_X);
		yPos = random.nextInt(RobotGame.SCREEN_Y);
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.GRAY);
		g.fillRect(xPos - WIDTH / 2, yPos - HEIGHT / 2, WIDTH, HEIGHT);
	}

	public int moveUpOrDown(Player P) {
		if (yPos > P.getYPos())
			return -1;
		if (yPos < P.getYPos())
			return 1;
		return 0;
	}

	public int moveLeftOrRight(Player P) {
		if (xPos > P.getXPos())
			return -1;
		if (xPos < P.getXPos())
			return 1;
		return 0;
	}

	public void move(Player P) {
		if (Math.abs(xPos - P.getXPos()) > RobotGame.ROBOT_SPEED)
			xPos += RobotGame.ROBOT_SPEED * moveLeftOrRight(P);
		else xPos += Math.abs(xPos - P.getXPos()) * moveLeftOrRight(P);

		if (Math.abs(yPos - P.getYPos()) > RobotGame.ROBOT_SPEED)
			yPos += RobotGame.ROBOT_SPEED * moveUpOrDown(P);
		else yPos += Math.abs(yPos - P.getYPos()) * moveUpOrDown(P);
	}

	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}


}
