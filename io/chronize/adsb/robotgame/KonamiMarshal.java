package io.chronize.adsb.robotgame;

import java.awt.event.KeyEvent;

public class KonamiMarshal {

	private static int[] _code = {
		KeyEvent.VK_UP,
		KeyEvent.VK_UP,
		KeyEvent.VK_DOWN,
		KeyEvent.VK_DOWN,
		KeyEvent.VK_LEFT,
		KeyEvent.VK_RIGHT,
		KeyEvent.VK_LEFT,
		KeyEvent.VK_RIGHT,
		KeyEvent.VK_B,
		KeyEvent.VK_A
	};
	private int _current;

	public KonamiMarshal() {
		resetQueue();
	}

	public void resetQueue() {
		_current = 0;
	}

	public void pushKey(int key) {
		if (_current != _code.length) {
			if (key == _code[_current])
				_current++;
			else
				_current = 0;
		}
	}

	public boolean isComplete() {
		return _current == _code.length;
	}

}
