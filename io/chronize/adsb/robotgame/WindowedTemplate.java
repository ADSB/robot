package io.chronize.adsb.robotgame;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public abstract class WindowedTemplate extends GameTemplate {

	protected boolean _moveReady = false;
	protected int _keyframes;
	protected Timer _timer;

	public WindowedTemplate() {
		super();
	}

	public static void createGameFrame(GameTemplate game, int width, int height) {
		Frame frame = new Frame();

		frame.setBackground(Color.white);

		game.setPreferredSize(new Dimension(width, height));
		frame.add(game);
		frame.pack();

		game.addKeyListener(game);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.setVisible(true);
		game.requestFocus();
	}

	@Override
	public void run() {
		_timer = new Timer();
		_timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				repaint();
				if (++_keyframes > 3) {
					_keyframes = 0;
					_moveReady = true;
				}
			}
		}, 0, 16);
	}

	/*
	@Override
	public boolean isAKeyDown(int key) {
		if (_moveReady && super.isAKeyDown(key)) {
			_moveReady = false;
			return true;
		}
		else
			return false;
	}
	*/

}