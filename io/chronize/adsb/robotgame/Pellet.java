package io.chronize.adsb.robotgame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class Pellet extends Sprite {

	private int xPos;
	private int yPos;
	private boolean isEaten;

	public static final int P_WIDTH = 10;
	public static final int P_HEIGHT = 10;

	public Pellet() {
		isEaten = false;
		Random random = new Random();
		xPos = random.nextInt(RobotGame.SCREEN_X);
		yPos = random.nextInt(RobotGame.SCREEN_Y);
	}

	@Override
	public void draw(Graphics2D g) {
		if (!isEaten) {
			g.setColor(Color.BLUE);
			g.fillRect(xPos - P_WIDTH / 2, yPos - P_HEIGHT / 2, P_WIDTH, P_HEIGHT);
		}
	}

	public void gotEaten(Player p) {
		if ((p.getXPos() + Player.PLAYER_WIDTH / 2 > xPos - P_WIDTH / 2) && (p.getXPos() - Player.PLAYER_WIDTH / 2 < xPos + P_WIDTH / 2))
			if ((p.getYPos() + Player.PLAYER_HEIGHT / 2 > yPos - P_HEIGHT / 2) && (p.getYPos() - Player.PLAYER_HEIGHT / 2 < yPos + P_HEIGHT / 2))
				isEaten = true;
	}

	public boolean returnEaten() {
		return isEaten;
	}

}
