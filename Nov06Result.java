import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JPanel;

public class Nov06Result extends JPanel implements KeyListener {
	private Font font;
	private int winner;
	private String result = "";

	public Nov06Result(int winner) {
		setPreferredSize(new Dimension(Nov06Main.width, Nov06Main.height));
		setFocusable(true);
		addKeyListener(this);
		//BGM停止
		Nov06Main.resultBGM.stop();
		this.winner = winner;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Nov06Main.change(new Nov06Title());
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.clearRect(0, 0, Nov06Main.width, Nov06Main.height);
		g.setColor(Color.BLACK);

		//play.wavのBGMを止める
		Nov06Main.resultBGM.stop();
		
		Graphics2D g2d = (Graphics2D) g;
		BasicStroke stroke = new BasicStroke(5);
		g2d.setStroke(stroke);
		g2d.drawRoundRect(Nov06Main.width / 2 - 300, Nov06Main.height / 2 - 200, 600, 350, 10, 10);

		font = new Font("ＭＳ ゴシック", Font.PLAIN, 60);
		g.setFont(font);
		Nov06Main.drawStringCenter(g, "Result", Nov06Main.width / 2, Nov06Main.height / 3);

		font = new Font("ＭＳ ゴシック", Font.PLAIN, 40);
		g.setFont(font);
		Nov06Main.drawStringCenter(g, "Press any key", Nov06Main.width / 2, Nov06Main.height / 3 + 200);

		switch (winner) {
		case 1:
			g.setColor(Color.RED);
			result = "Player 1 won!";
			Nov06Sound.createClip(new File("src/sound/hapyou.wav"));
			Nov06Main.resultBGM.stop();
			Nov06Main.resultBGM.stop();
			break;
		case 2:
			g.setColor(Color.BLUE);
			result = "Player 2 won!";
			Nov06Sound.createClip(new File("src/sound/hapyou.wav"));
			Nov06Main.resultBGM.stop();
			Nov06Main.resultBGM.stop();
			break;
		default:
			result = "Draw!";
			Nov06Sound.createClip(new File("src/sound/draw.wav"));
			Nov06Main.resultBGM.stop();
			Nov06Main.resultBGM.stop();
			break;
		}

		font = new Font("ＭＳ ゴシック", Font.PLAIN, 50);
		g.setFont(font);
		Nov06Main.drawStringCenter(g, result, Nov06Main.width / 2, Nov06Main.height / 3 + 100);

	}
}
