package io.chronize.adsb.robotgame;

public class GameClient {

	public static void main(String[] args) {
		RobotGame game = new RobotGame();
		game.init();
		RobotGame.createGameFrame(game, RobotGame.SCREEN_X, RobotGame.SCREEN_Y);
	}

}
