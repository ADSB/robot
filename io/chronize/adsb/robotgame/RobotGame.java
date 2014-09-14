package io.chronize.adsb.robotgame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class RobotGame extends GameTemplate {

	public static final int STATE_MENU = 1;
	public static final int STATE_PLAYING = 2;
	public static final int STATE_OVER = 3;

	public static final int WIDTH_X = 8;
	public static final int WIDTH_Y = 8;

	public static final int RADIUS_X = 37;
	public static final int RADIUS_Y = 37;

	public static final int SCREEN_X = 800;
	public static final int SCREEN_Y = 800;

	public static final int ROBOT_SPEED = 4;
	public static final int PLAYER_SPEED = 5;

	private static final String START_MESSAGE = "Press Enter to Start.";
	private static final String LOSE_MESSAGE = "You Lose";
	private static final String WIN_MESSAGE = "You Win!";

	private boolean winState = false;

	public int state = 0;

	public Robot myRobot;
	public Player myPlayer = new Player();
	public Pellet[] myPellets = new Pellet[10];

	public static void main(String[] args) {
		RobotGame game = new RobotGame();
		game.init();
		RobotGame.createGameFrame(game, SCREEN_X, SCREEN_Y);
	}

	public RobotGame() {
		super();
	}

	@Override
	public void init() {
		state = STATE_MENU;
		myRobot = new Robot();
		for (int i = 0; i < 10; i++) {
			Pellet myPellet = new Pellet();
			myPellets[i] = myPellet;
		}
	}

	@Override
	public void start() {


	}

	@Override
	public void updateFrame(Graphics2D g) {
		drawBackground(g);
		switch (state) {
			case STATE_MENU:
				drawMenu(g);
				if (isAKeyDown(KeyEvent.VK_ENTER))
					state = STATE_PLAYING;
				break;

			case STATE_PLAYING:
				movePlayer();
				myRobot.move(myPlayer);
				myRobot.draw(g);
				myPlayer.draw(g);
				if (myPlayer.playerCrushed(myRobot)) state = STATE_OVER;

				int winFlag = 0;
				for (int i = 0; i < 10; i++) {
					if (myPellets[i].returnEaten()) winFlag += 1;
					myPellets[i].gotEaten(myPlayer);
					myPellets[i].draw(g);
				}

				if (winFlag == 10) {
					winState = true;
					state = STATE_OVER;
				}
				break;

			case STATE_OVER:
				g.setColor(Color.decode("#70f070"));
				drawOver(g);
				break;
			default:
				break;
		}
	}

	private void movePlayer() {
		if (isAKeyDown(KeyEvent.VK_RIGHT)) myPlayer.setXPos(myPlayer.getXPos() + PLAYER_SPEED);
		if (isAKeyDown(KeyEvent.VK_LEFT)) myPlayer.setXPos(myPlayer.getXPos() - PLAYER_SPEED);
		if (isAKeyDown(KeyEvent.VK_UP)) myPlayer.setYPos(myPlayer.getYPos() - PLAYER_SPEED);
		if (isAKeyDown(KeyEvent.VK_DOWN)) myPlayer.setYPos(myPlayer.getYPos() + PLAYER_SPEED);
	}

	private void drawBackground(Graphics2D g) {
		g.setColor(Color.decode("#202020"));
		g.fill(g.getDeviceConfiguration().getBounds());
	}

	private void drawMenu(Graphics2D g) {
		g.setColor(Color.decode("#70f070"));
		g.setFont(new Font("Copperplate Gothic Light", Font.BOLD, 32));
		FontMetrics metrics = g.getFontMetrics();
		g.drawString(START_MESSAGE, ((SCREEN_X - metrics.stringWidth(START_MESSAGE)) / 2), ((SCREEN_Y - metrics.getHeight()) / 2));
	}

	private void drawOver(Graphics2D g) {
		String message;
		if (winState) message = WIN_MESSAGE;
		else message = LOSE_MESSAGE;
		g.setColor(Color.decode("#70f070"));
		g.setFont(new Font("Copperplate Gothic Light", Font.BOLD, 48));
		FontMetrics metrics = g.getFontMetrics();
		g.drawString(message, ((SCREEN_X - metrics.stringWidth(message)) / 2), ((SCREEN_Y - metrics.getHeight()) / 2));
	}


}
