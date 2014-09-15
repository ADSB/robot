package io.chronize.adsb.robotgame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class RobotGame extends WindowedTemplate {

	public static final int WIDTH_X = 12;
	public static final int WIDTH_Y = 12;

	public static final int RADIUS_X = 37;
	public static final int RADIUS_Y = 37;

	public static final int GRID_X = 2 * RADIUS_X + 1;
	public static final int GRID_Y = 2 * RADIUS_Y + 1;

	public static final int SCREEN_X = GRID_X * (WIDTH_X + 1) + 1;
	public static final int SCREEN_Y = GRID_Y * (WIDTH_Y + 1) + 1;

	public static float cubicBezier(float t, float p0, float p1, float p2, float p3) {
		float v = 1 - t;
		return v * v * v * p0 + 3 * v * v * t * p1 + 3 * v * t * t * p2 + t * t * t * p3;
	}

	public static float getBrightness() {
		long brightness = System.currentTimeMillis() % 1000;
		if (brightness > 500)
			brightness = 1000 - brightness;

		// ease-out
		return cubicBezier((float) (brightness) / 500, 0.f, 0.f, .58f, 1.f);
	}

	private static final int STATE_MENU = 1;
	private static final int STATE_PLAYING = 2;
	private static final int STATE_OVER = 3;

	private static final Font TEXT_FONT = new Font("Courier", 0, 16);

	private static final String[] MENU_MESSAGES = {
		"Play Game",
		"Instructions",
		"Options",
		"Quit"
	};
	private static final String LOSE_MESSAGE = "Game Over";
	private static final String WIN_MESSAGE = "Victory";

	private static final Color BACKGROUND_COLOR = Color.decode("#202020");
	private static final Color GRID_COLOR = Color.decode("#404040");
	private static final Color TEXT_COLOR = Color.decode("#70f070");

	private static final int PELLET_CAP = 25;
	private static final int ROBOT_CAP = 50	;

	private int _selection;

	private boolean _win = false;
	private int _state = 0;

	private boolean _robotParity = false;
	private boolean[][] _robotMatrix;

	private ArrayList<Robot> _robots;
	private Player _player;
	private ArrayList<Pellet> _pellets;
	private ArrayList<Pellet> _queuedPellets;

	// cheat using the Konami Code
	private KonamiMarshal _cheatCode;

	public RobotGame() {
		super();
		_cheatCode = new KonamiMarshal();
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {

			}

			@Override
			public void keyPressed(KeyEvent keyEvent) {
				_cheatCode.pushKey(keyEvent.getKeyCode());
				if (_state == STATE_MENU) {
					switch (keyEvent.getKeyCode()) {
						case KeyEvent.VK_UP:
							_selection--;
							break;
						case KeyEvent.VK_DOWN:
							_selection++;
							break;
						case KeyEvent.VK_ENTER:
							switch (_selection) {
								case 0:
									start();
									break;
								case 1:

									break;
								case 2:

									break;
								case 3:
									System.exit(0);
									break;
								default:
									break;
							}
							break;
						default:
							break;
					}
					_selection %= 4;
					if (_selection < 0)
						_selection += 4;
				}
			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {

			}
		});
	}

	@Override
	public void init() {
		_selection = 0;
		_state = STATE_MENU;
	}

	@Override
	public void start() {
		_state = STATE_PLAYING;
		_cheatCode.resetQueue();
		_win = false;
		_player = new Player();
		_robots = new ArrayList<Robot>();
		for (int i = 0; i < ROBOT_CAP; i++) {
			_robots.add(new Robot());
		}
		_pellets = new ArrayList<Pellet>();
		for (int i = 0; i < PELLET_CAP; i++) {
			_pellets.add(new Pellet());
		}
	}

	@Override
	public void updateFrame(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		drawBackground(g);
		switch (_state) {
			case STATE_MENU:
				drawMenu(g);
				break;
			case STATE_PLAYING:
				drawGrid(g);

				if (_cheatCode.isComplete() || _pellets.size() == 0) {
					_win = true;
					_state = STATE_OVER;
					break;
				}

				if (_moveReady) {
					_robotMatrix = new boolean[GRID_X][GRID_Y];
					_robotParity = !_robotParity;
					_queuedPellets = new ArrayList<Pellet>();

					movePlayer();
					moveRobots();

					_moveReady = false;
				}

				for (Pellet p : _pellets) {
					if (p.getX() == _player.getX() && p.getY() == _player.getY())
						queuePelletRemoval(p);
					else
						p.draw(g);
				}

				commitPelletRemoval();

				_player.draw(g);

				for (Robot r : _robots) {
					r.draw(g);
					if (r.getX() == _player.getX() && r.getY() == _player.getY())
						_state = STATE_OVER;
				}

				/*//Debugging
				g.setColor(Color.WHITE);
				for (int i = 0; i < _robotMatrix.length; i++) {
					for (int j = 0; j < _robotMatrix[i].length; j++) {
						g.drawString(_robotMatrix[i][j] ? "1" : "0", i * (WIDTH_X + 1), (j + 1) * (WIDTH_Y + 1));
					}
				}*/

				break;
			case STATE_OVER:
				drawOver(g);
				if (isAKeyDown(KeyEvent.VK_ENTER))
					init();
				break;
			default:
				break;
		}
	}

	// ArrayList should not be modified during iteration in a multithreaded process
	private void queuePelletRemoval(Pellet p) {
		_queuedPellets.add(p);
	}

	// remove queued pellets
	private void commitPelletRemoval() {
		for (Pellet p : _queuedPellets) {
			_pellets.remove(p);
		}
	}

	private void movePlayer() {
		if (isAKeyDown(KeyEvent.VK_UP))
			_player.move(Player.MOVE_UP);
		if (isAKeyDown(KeyEvent.VK_RIGHT))
			_player.move(Player.MOVE_RIGHT);
		if (isAKeyDown(KeyEvent.VK_DOWN))
			_player.move(Player.MOVE_DOWN);
		if (isAKeyDown(KeyEvent.VK_LEFT))
			_player.move(Player.MOVE_LEFT);
	}

	private void moveRobots() {
		if (_robotParity) {
			int dx;
			int dy;

			// prevent robots from overlapping
			for (Robot r : _robots) {
				dx = r.getX() - _player.getX();
				dy = r.getY() - _player.getY();

				// comment conditionals for diagonal movement
				if (Math.abs(dx) > Math.abs(dy)) {
					if (dx > 0) {
						if (!_robotMatrix[r.getX() == 0 ? 0 : r.getX() - 1][r.getY()])
							r.move(Robot.MOVE_LEFT);
					}
					else if (dx < 0) {
						if (!_robotMatrix[r.getX() == GRID_X - 1 ? GRID_X - 1 : r.getX() + 1][r.getY()])
							r.move(Robot.MOVE_RIGHT);
					}
				}
				else {
					if (dy > 0) {
						if (!_robotMatrix[r.getX()][r.getY() == 0 ? 0 : r.getY() - 1])
							r.move(Robot.MOVE_UP);
					}
					else if (dy < 0)
						if (!_robotMatrix[r.getX()][r.getY() == 0 ? 0 : r.getY() + 1])
							r.move(Robot.MOVE_DOWN);
				}
				_robotMatrix[r.getX()][r.getY()] = true;
			}

		}
	}

	private void drawBackground(Graphics2D g) {
		g.setColor(BACKGROUND_COLOR);
		g.fill(new Rectangle(SCREEN_X, SCREEN_Y));
	}

	private void drawGrid(Graphics2D g) {
		g.setColor(GRID_COLOR);
		for (int i = 0; i <= GRID_X; i++) {
			g.drawLine(i * (WIDTH_X + 1), 0, i * (WIDTH_X + 1), SCREEN_Y - 1);
		}
		for (int i = 0; i <= GRID_Y; i++) {
			g.drawLine(0, i * (WIDTH_Y + 1), SCREEN_X - 1, i * (WIDTH_Y + 1));
		}
	}

	private void drawMenu(Graphics2D g) {
		g.setFont(TEXT_FONT);
		FontMetrics metrics = g.getFontMetrics();

		int height = metrics.getHeight();

		int selectX = (SCREEN_X - metrics.stringWidth(MENU_MESSAGES[_selection])) / 2 - 3;
		int selectY = (SCREEN_Y - 3 * height + (_selection - MENU_MESSAGES.length / 2 + 1) * height * 4) / 2 + 1;
		int selectH = height + 4;
		int selectW = metrics.stringWidth(MENU_MESSAGES[_selection]) + 6;

		g.setColor(Color.getHSBColor(.333f, getBrightness() / 2 + .4f, getBrightness() / 3 + .2f));

		g.fillRect(
			selectX,
			selectY,
			selectW,
			selectH
		);

		g.setColor(TEXT_COLOR);

		g.drawRect(
			selectX,
			selectY,
			selectW,
			selectH
		);

		for (int i = 0; i < MENU_MESSAGES.length; i++) {
			g.drawString(
				MENU_MESSAGES[i],
				(SCREEN_X - metrics.stringWidth(MENU_MESSAGES[i])) / 2,
				(SCREEN_Y - height + (i - MENU_MESSAGES.length / 2 + 1) * height * 4) / 2
			);
		}

	}

	private void drawInfo(Graphics2D g) {
		g.setFont(TEXT_FONT);
		FontMetrics metrics = g.getFontMetrics();

		int height = metrics.getHeight();

		int selectX = (SCREEN_X - metrics.stringWidth(MENU_MESSAGES[_selection])) / 2 - 3;
		int selectY = (SCREEN_Y - 3 * height + (_selection - MENU_MESSAGES.length / 2 + 1) * height * 4) / 2 + 1;
		int selectH = height + 4;
		int selectW = metrics.stringWidth(MENU_MESSAGES[_selection]) + 6;

		g.setColor(Color.getHSBColor(.333f, getBrightness() / 2 + .4f, getBrightness() / 3 + .2f));
		g.fillRect(
			selectX,
			selectY,
			selectW,
			selectH
		);

		g.setColor(TEXT_COLOR);

		g.drawRect(
			selectX,
			selectY,
			selectW,
			selectH
		);

		for (int i = 0; i < MENU_MESSAGES.length; i++) {
			g.drawString(
				MENU_MESSAGES[i],
				(SCREEN_X - metrics.stringWidth(MENU_MESSAGES[i])) / 2,
				(SCREEN_Y - height + (i - MENU_MESSAGES.length / 2 + 1) * height * 4) / 2
			);
		}

	}

	private void drawOver(Graphics2D g) {
		String message = (_win ? WIN_MESSAGE : LOSE_MESSAGE);

		g.setColor(TEXT_COLOR);
		g.setFont(TEXT_FONT);
		FontMetrics metrics = g.getFontMetrics();

		g.drawString(message, ((SCREEN_X - metrics.stringWidth(message)) / 2), ((SCREEN_Y - metrics.getHeight()) / 2));
	}


}
