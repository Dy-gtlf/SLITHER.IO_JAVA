import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

public class Nov06Result extends JPanel implements KeyListener {
	private Font font;
	private int winner;
	private String result = "";

	public Nov06Result(int winner) {
		setPreferredSize(new Dimension(Nov06Main.width, Nov06Main.height));
		setFocusable(true);
		addKeyListener(this);
		this.winner = winner;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			Nov06Main.change(new Nov06Title());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.clearRect(0, 0, Nov06Main.width, Nov06Main.height);
		g.setColor(Color.BLACK);
		font = new Font("ＭＳ ゴシック", Font.PLAIN, 60);
		g.setFont(font);
		Nov06Main.drawStringCenter(g, "Result", Nov06Main.width / 2, Nov06Main.height / 3);

		switch (winner) {
		case 1:
			result = "Player 1 won!";
			break;
		case 2:
			result = "Player 2 won!";
			break;
		default:
			result = "Draw!";
			break;
		}

		font = new Font("ＭＳ ゴシック", Font.PLAIN, 50);
		g.setFont(font);
		Nov06Main.drawStringCenter(g, result, Nov06Main.width / 2, Nov06Main.height / 3 + 100);

		font = new Font("ＭＳ ゴシック", Font.PLAIN, 40);
		g.setFont(font);
		Nov06Main.drawStringCenter(g, "Press space", Nov06Main.width / 2, Nov06Main.height / 3 + 200);
	}
}
