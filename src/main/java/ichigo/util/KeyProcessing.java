package ichigo.util;

import java.awt.event.*;
import java.util.*;


public class KeyProcessing implements KeyListener {
	HashSet<String> evacuatedKeySet = new HashSet<String>();
	HashSet<String> keySet = new HashSet<String>();

	public void keyPressed(KeyEvent e) {
		String keyText = KeyEvent.getKeyText(e.getKeyCode());
		keySet.add(keyText);
		System.out.println("pressed " + toString());
	}
	public void keyReleased(KeyEvent e) {
		String keyText = KeyEvent.getKeyText(e.getKeyCode());
		keySet.remove(keyText);
		System.out.println("released " + toString());
	}
	public void keyTyped(KeyEvent e) {
	}

	public void save() {
		evacuatedKeySet.clear();
		for (String e: keySet) {
			evacuatedKeySet.add(e);
		}
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String e: keySet) {
			sb.append(",");
			sb.append(e);
		}
		return sb.toString();
	}
}
