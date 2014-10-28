package io.chronize.adsb.robotgame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

public class RobotGame extends WindowedTemplate {

	private static int WIDTH_X = 12;
	private static int WIDTH_Y = 12;

	private static int RADIUS_X = 37;
	private static int RADIUS_Y = 37;

	private static int GRID_X = 2 * RADIUS_X + 1;
	private static int GRID_Y = 2 * RADIUS_Y + 1;

	private static int SCREEN_X = GRID_X * (WIDTH_X + 1) + 1;
	private static int SCREEN_Y = GRID_Y * (WIDTH_Y + 1) + 1;

	/**
	 * Get horizontal width of grid units.
	 *
	 * @return horizontal width
	 */
	public static int getWidthX() {
		return WIDTH_X;
	}

	/**
	 * Get vertical width of grid units.
	 *
	 * @return vertical width
	 */
	public static int getWidthY() {
		return WIDTH_Y;
	}

	/**
	 * Get horizontal radius of grid.
	 *
	 * @return horizontal radius
	 */
	public static int getRadiusX() {
		return RADIUS_X;
	}

	/**
	 * Get vertical radius of grid.
	 *
	 * @return vertical radius
	 */
	public static int getRadiusY() {
		return RADIUS_Y;
	}

	/**
	 * Get horizontal units in grid.
	 *
	 * @return horizontal units
	 */
	public static int getGridX() {
		return GRID_X;
	}

	/**
	 * Get vertical units in grid.
	 *
	 * @return vertical units
	 */
	public static int getGridY() {
		return GRID_Y;
	}

	/**
	 * Get horizontal width in pixels of screen.
	 *
	 * @return horizontal pixels
	 */
	public static int getScreenX() {
		return SCREEN_X;
	}

	/**
	 * Get vertical width in pixels of screen.
	 *
	 * @return vertical pixels
	 */
	public static int getScreenY() {
		return SCREEN_Y;
	}

	/**
	 * Propagate changes to screen properties and update accordingly.
	 */
	private void recalculateMetrics() {
		GRID_X = 2 * RADIUS_X + 1;
		GRID_Y = 2 * RADIUS_Y + 1;
		SCREEN_X = GRID_X * (WIDTH_X + 1) + 1;
		SCREEN_Y = GRID_Y * (WIDTH_Y + 1) + 1;
		setPreferredSize(new Dimension(SCREEN_X, SCREEN_Y));
		_parent.pack();
	}

	/**
	 * Cubic Bezier function.
	 *
	 * @param t Parametric input
	 * @param p0 0th point
	 * @param p1 1st point
	 * @param p2 2nd point
	 * @param p3 3rd point
	 *
	 * @return bezier value
	 */
	public static float cubicBezier(float t, float p0, float p1, float p2, float p3) {
		float v = 1 - t;
		return v * v * v * p0 + 3 * v * v * t * p1 + 3 * v * t * t * p2 + t * t * t * p3;
	}

	/**
	 * Get universal graphics brightness percentage as an ease-out function of time.
	 *
	 * @return brightness percentage
	 */
	public static float getBrightness() {
		long brightness = System.currentTimeMillis() % 1000;
		if (brightness > 500)
			brightness = 1000 - brightness;

		// ease-out
		return cubicBezier((float) (brightness) / 500, 0.f, 0.f, .58f, 1.f);
	}

	private static final int STATE_MENU = 1;
	private static final int STATE_INSTRUCTIONS = 2;
	private static final int STATE_OPTIONS = 3;
	private static final int STATE_PLAYING = 4;
	private static final int STATE_OVER = 5;

	// Graphics font static initializer
	private static Font TEXT_FONT;
	static {
		try {
			TEXT_FONT = Font.createFont(Font.PLAIN, new File("drift.ttf")).deriveFont(24.f);
		}
		catch (Exception e) {
			TEXT_FONT = new Font("Courier", Font.PLAIN, 24);
			// throw new RuntimeException("Font load unsuccessful");
		}
	}

	private static final String[] MENU_MESSAGES = {
		"Play Game",
		"Instructions",
		"Options",
		"Quit"
	};
	private static final String[] INSTRUCTION_MESSAGE = {
		"The player will start at the center of the screen.",
		"Avoid RED robots.",
		"Pick up GREEN pellets.",
		"Use arrow keys to move the player."
	};
	private static final String[] OPTIONS_MESSAGES = {
		"Pellets",
		"Robots",
		"Robot Handicap",
		"Diagonal Robot Movement",
		"Show Grid",
		"Antialiasing",
		"Horizontal Width",
		"Vertical Width",
		"Horizontal Radius",
		"Vertical Radius"
	};
	private static final String LOSE_MESSAGE = "Game Over";
	private static final String WIN_MESSAGE = "Victory";

	private static final Color BACKGROUND_COLOR = Color.decode("#202020");
	private static final Color GRID_COLOR = Color.decode("#404040");
	private static final Color TEXT_COLOR = Color.decode("#70f070");

	// configurable in Options
	// widths and radii are configurable as well
	private int PELLET_CAP = 20;
	private int ROBOT_CAP = 20;
	private boolean ROBOT_HANDICAP = false;
	private boolean DIAGONAL_MOVEMENT = false;
	private boolean SHOW_GRID = false;
	private boolean ANTIALIASING = true;

	/**
	 * Get configurable option value in Options menu
	 *
	 * @param i Index
	 *
	 * @return option value
	 */
	private String getOption(int i) {
		switch (i) {
			case 0:
				return PELLET_CAP + "";
			case 1:
				return ROBOT_CAP + "";
			case 2:
				return ROBOT_HANDICAP + "";
			case 3:
				return DIAGONAL_MOVEMENT + "";
			case 4:
				return SHOW_GRID + "";
			case 5:
				return ANTIALIASING + "";
			case 6:
				return WIDTH_X + "";
			case 7:
				return WIDTH_Y + "";
			case 8:
				return RADIUS_X + "";
			case 9:
				return RADIUS_Y + "";
			default:
				return "";
		}
	}

	private int _selection;
	private int _optionSelection;

	private boolean _win = false;
	private int _state = 0;

	private boolean _paused = false;

	private boolean _robotParity = false;
	private boolean[][] _robotMatrix;

	private ArrayList<Robot> _robots;
	private Player _player;
	private ArrayList<Pellet> _pellets;
	private ArrayList<Pellet> _queuedPellets;

	// cheat using the Konami Code
	private KonamiMarshal _cheatCode;

	/**
	 * Construct with a new window, cheat code marshal, and keylisteners
	 */
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
				switch (_state) {
					case STATE_MENU:
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
										_state = STATE_INSTRUCTIONS;
										break;
									case 2:
										_state = STATE_OPTIONS;
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
						_selection %= MENU_MESSAGES.length;
						if (_selection < 0)
							_selection += MENU_MESSAGES.length;
						break;
					case STATE_PLAYING:
						switch (keyEvent.getKeyCode()) {
							case KeyEvent.VK_SPACE:
								_paused = !_paused;
								break;
							default:
								break;
						}
						break;
					case STATE_OPTIONS:
						switch (keyEvent.getKeyCode()) {
							case KeyEvent.VK_UP:
								_optionSelection--;
								break;
							case KeyEvent.VK_DOWN:
								_optionSelection++;
								break;
							case KeyEvent.VK_LEFT:
								switch (_optionSelection) {
									case 0:
										if (PELLET_CAP > 1)
											PELLET_CAP--;
										break;
									case 1:
										if (ROBOT_CAP > 1)
											ROBOT_CAP--;
										break;
									case 2:
										ROBOT_HANDICAP = !ROBOT_HANDICAP;
										break;
									case 3:
										DIAGONAL_MOVEMENT = !DIAGONAL_MOVEMENT;
										break;
									case 4:
										SHOW_GRID = !SHOW_GRID;
										break;
									case 5:
										ANTIALIASING = !ANTIALIASING;
										break;
									case 6:
										if (WIDTH_X > 1) {
											WIDTH_X--;
											recalculateMetrics();
										}
										break;
									case 7:
										if (WIDTH_Y > 1) {
											WIDTH_Y--;
											recalculateMetrics();
										}
										break;
									case 8:
										if (RADIUS_X > 1) {
											RADIUS_X--;
											recalculateMetrics();
										}
										break;
									case 9:
										if (RADIUS_Y > 1) {
											RADIUS_Y--;
											recalculateMetrics();
										}
										break;
									default:
										break;
								}
								break;
							case KeyEvent.VK_RIGHT:
								switch (_optionSelection) {
									case 0:
										PELLET_CAP++;
										break;
									case 1:
										ROBOT_CAP++;
										break;
									case 2:
										ROBOT_HANDICAP = !ROBOT_HANDICAP;
										break;
									case 3:
										DIAGONAL_MOVEMENT = !DIAGONAL_MOVEMENT;
										break;
									case 4:
										SHOW_GRID = !SHOW_GRID;
										break;
									case 5:
										ANTIALIASING = !ANTIALIASING;
										break;
									case 6:
										WIDTH_X++;
										recalculateMetrics();
										break;
									case 7:
										WIDTH_Y++;
										recalculateMetrics();
										break;
									case 8:
										RADIUS_X++;
										recalculateMetrics();
										break;
									case 9:
										RADIUS_Y++;
										recalculateMetrics();
										break;
									default:
										break;
								}
								break;
							default:
								break;
						}
						_optionSelection %= OPTIONS_MESSAGES.length;
						if (_optionSelection < 0) {
							_optionSelection += OPTIONS_MESSAGES.length;
						}
					case STATE_INSTRUCTIONS:
					case STATE_OVER:
						switch (keyEvent.getKeyCode()) {
							case KeyEvent.VK_ENTER:
								_state = STATE_MENU;
								break;
							default:
								break;
						}
						break;
					default:
						break;
				}
			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {

			}
		});
	}

	/**
	 * Initialize the window state to Main menu
	 */
	@Override
	public void init() {
		_selection = 0;
		_state = STATE_MENU;
	}

	/**
	 * Enter game mode and create new entities
	 */
	@Override
	public void start() {
		_state = STATE_PLAYING;
		_cheatCode.resetSequence();
		_win = false;
		_player = new Player();
		_robots = new ArrayList<Robot>();
		_paused = false;
		for (int i = 0; i < ROBOT_CAP; i++) {
			_robots.add(new Robot());
		}
		_pellets = new ArrayList<Pellet>();
		for (int i = 0; i < PELLET_CAP; i++) {
			_pellets.add(new Pellet());
		}
	}

	/**
	 * Update the frame depending on game state
	 *
	 * @param g Graphics object
	 */
	@Override
	public void updateFrame(Graphics2D g) {
		// set antialiasing
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, ANTIALIASING ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
		drawBackground(g);
		switch (_state) {
			case STATE_MENU:
				drawMenu(g);
				break;
			case STATE_INSTRUCTIONS:
				drawInstructions(g);
				break;
			case STATE_OPTIONS:
				drawOptions(g);
				break;
			case STATE_PLAYING:
				drawGame(g);
				break;
			case STATE_OVER:
				drawOver(g);
				break;
			default:
				break;
		}
	}

	/**
	 * Prepare Pellet for removal
	 *
	 * @param p Pellet to remove
	 */
	private void queuePelletRemoval(Pellet p) {
		// ArrayList should not be modified during iteration in a multithreaded process
		_queuedPellets.add(p);
	}

	/**
	 * Remove all pellets queued for removal
	 */
	private void commitPelletRemoval() {
		// remove queued pellets
		for (Pellet p: _queuedPellets) {
			_pellets.remove(p);
		}
	}

	/**
	 * Move the player based on pressed keys
	 */
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

	/**
	 * Move robots based on player position
	 */
	private void moveRobots() {
		if (!ROBOT_HANDICAP || _robotParity) {
			// move if robots are not handicapped or are ready to move
			int dx;
			int dy;

			// overlap matrix to prevent robots from overlapping
			for (Robot r: _robots) {
				dx = r.getX() - _player.getX();
				dy = r.getY() - _player.getY();

				if (Math.abs(dx) > Math.abs(dy)) {
					if (dx > 0) {
						if (!_robotMatrix[r.getX() == 0 ? 0 : r.getX() - 1][r.getY()])
							r.move(Robot.MOVE_LEFT);
					}
					else if (dx < 0) {
						if (!_robotMatrix[r.getX() == GRID_X - 1 ? GRID_X - 1 : r.getX() + 1][r.getY()])
							r.move(Robot.MOVE_RIGHT);
					}
					if (!DIAGONAL_MOVEMENT)
						continue;
				}
				if (dy > 0) {
					if (!_robotMatrix[r.getX()][r.getY() == 0 ? 0 : r.getY() - 1])
						r.move(Robot.MOVE_UP);
				}
				else if (dy < 0)
					if (!_robotMatrix[r.getX()][r.getY() == 0 ? 0 : r.getY() + 1])
						r.move(Robot.MOVE_DOWN);
				_robotMatrix[r.getX()][r.getY()] = true;
			}

		}
	}

	/**
	 * Draw the background
	 *
	 * @param g Graphics object
	 */
	private void drawBackground(Graphics2D g) {
		g.setColor(BACKGROUND_COLOR);
		g.fill(new Rectangle(SCREEN_X, SCREEN_Y));
	}

	/**
	 * Draw the grid
	 *
	 * @param g Graphics object
	 */
	private void drawGrid(Graphics2D g) {
		g.setColor(GRID_COLOR);
		for (int i = 0; i <= GRID_X; i++) {
			g.drawLine(i * (WIDTH_X + 1), 0, i * (WIDTH_X + 1), SCREEN_Y - 1);
		}
		for (int i = 0; i <= GRID_Y; i++) {
			g.drawLine(0, i * (WIDTH_Y + 1), SCREEN_X - 1, i * (WIDTH_Y + 1));
		}
	}

	/**
	 * Draw the game
	 *
	 * @param g Graphics object
	 */
	private void drawGame(Graphics2D g) {
		if (SHOW_GRID) {
			drawGrid(g);
		}

		if (_cheatCode.isComplete() || _pellets.size() == 0) {
			// end game if cheat code is complete or no pellets left
			_win = true;
			_state = STATE_OVER;
		}
		else {
			if (!_paused && _moveReady) {
				_robotMatrix = new boolean[GRID_X][GRID_Y];
				_robotParity = !_robotParity;
				_queuedPellets = new ArrayList<Pellet>();

				movePlayer();
				moveRobots();

				_moveReady = false;
			}

		}

		for (Pellet p: _pellets) {
			if (p.getX() == _player.getX() && p.getY() == _player.getY()) {
				// don't draw and prepare for removal if player on top of pellet
				queuePelletRemoval(p);
			}
			else {
				p.draw(g);
			}
		}

		commitPelletRemoval();

		_player.draw(g);

		for (Robot r: _robots) {
			r.draw(g);
			if (r.getX() == _player.getX() && r.getY() == _player.getY()) {
				// end game if robot on top of player
				_state = STATE_OVER;
			}
		}

		if (_paused) {
			drawPaused(g);
		}

		/*//Debugging
		g.setColor(Color.WHITE);
		for (int i = 0; i < _robotMatrix.length; i++) {
			for (int j = 0; j < _robotMatrix[i].length; j++) {
				g.drawString(_robotMatrix[i][j] ? "1" : "0", i * (WIDTH_X + 1), (j + 1) * (WIDTH_Y + 1));
			}
		}*/
	}

	/**
	 * Draw the game while paused
	 *
	 * @param g Graphics object
	 */
	private void drawPaused(Graphics2D g) {
		g.setFont(TEXT_FONT);
		g.setColor(TEXT_COLOR);
		FontMetrics metrics = g.getFontMetrics();

		g.drawString("Paused", (SCREEN_X - metrics.stringWidth("Paused")) / 2, (SCREEN_Y - metrics.getHeight()) / 2);
	}

	/**
	 * Draw the main menu
	 *
	 * @param g Graphics object
	 */
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

	/**
	 * Draw the instructions menu
	 *
	 * @param g Graphics object
	 */
	private void drawInstructions(Graphics2D g) {
		g.setFont(TEXT_FONT);
		g.setColor(TEXT_COLOR);

		FontMetrics metrics = g.getFontMetrics();

		int height = metrics.getHeight();

		for (int i = 0; i < INSTRUCTION_MESSAGE.length; i++) {
			g.drawString(
				INSTRUCTION_MESSAGE[i],
				(SCREEN_X - metrics.stringWidth(INSTRUCTION_MESSAGE[i])) / 2,
				(SCREEN_Y - height + (i - INSTRUCTION_MESSAGE.length / 2 + 1) * height * 4) / 2
			);
		}

	}

	/**
	 * Draw the options menu
	 *
	 * @param g Graphics object
	 */
	private void drawOptions(Graphics2D g) {
		g.setFont(TEXT_FONT);
		FontMetrics metrics = g.getFontMetrics();

		String plusOption = OPTIONS_MESSAGES[_optionSelection] + ": " + getOption(_optionSelection);

		int height = metrics.getHeight();

		int selectX = (SCREEN_X - metrics.stringWidth(plusOption)) / 2 - 3;
		int selectY = (SCREEN_Y - 3 * height + (_optionSelection - OPTIONS_MESSAGES.length / 2 + 1) * height * 4) / 2 + 1;
		int selectH = height + 4;
		int selectW = metrics.stringWidth(plusOption) + 6;

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

		for (int i = 0; i < OPTIONS_MESSAGES.length; i++) {
			plusOption = OPTIONS_MESSAGES[i] + ": " + getOption(i);
			g.drawString(
				plusOption,
				(SCREEN_X - metrics.stringWidth(plusOption)) / 2,
				(SCREEN_Y - height + (i - OPTIONS_MESSAGES.length / 2 + 1) * height * 4) / 2
			);
		}

	}

	/**
	 * Draw the game over message
	 *
	 * @param g Graphics object
	 */
	private void drawOver(Graphics2D g) {
		String message = (_win ? WIN_MESSAGE : LOSE_MESSAGE);

		g.setColor(TEXT_COLOR);
		g.setFont(TEXT_FONT);
		FontMetrics metrics = g.getFontMetrics();

		g.drawString(message, ((SCREEN_X - metrics.stringWidth(message)) / 2), ((SCREEN_Y - metrics.getHeight()) / 2));
	}

}
