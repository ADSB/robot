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

	/**
	 * Create new cheat code marshal
	 */
	public KonamiMarshal() {
		resetSequence();
	}

	/**
	 * Reset sequence position
	 */
	public void resetSequence() {
		_current = 0;
	}

	/**
	 * Push key to sequence
	 *
	 * @param key Key to push to sequence
	 */
	public void pushKey(int key) {
		if (_current != _code.length) {
			if (key == _code[_current])
				_current++;
			else
				_current = 0;
		}
	}

	/**
	 * Check if cheat code is complete
	 *
	 * @return true if cheat code is complete
	 */
	public boolean isComplete() {
		return _current == _code.length;
	}

}
