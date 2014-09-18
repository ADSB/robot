package io.chronize.adsb.robotgame;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

public abstract class WindowedTemplate extends GameTemplate {

	protected boolean _moveReady = false;
	protected int _keyframes;
	protected Timer _timer;
	protected Frame _parent;

	public WindowedTemplate() {
		super();
	}

	public static void createGameFrame(WindowedTemplate game, int width, int height) {
		game._parent = new Frame();

		game._parent.setBackground(Color.white);

		game.setPreferredSize(new Dimension(width, height));
		game._parent.add(game);
		game._parent.pack();

		game.addKeyListener(game);

		game._parent.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		game._parent.setResizable(false);

		game._parent.setVisible(true);
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