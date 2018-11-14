import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;

import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Nov06Main extends JFrame {
	public static JFrame frame;
	public static int width = 1280; // ‰æ–Ê‚Ì•
	public static int height = 1000; // ‰æ–Ê‚Ì‚‚³
	public static Clip resultBGM;  //BGM
	public static Clip resultSYU;  //k¬

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			resultBGM = Nov06Sound.createClip(new File("src/sound/play.wav"));
			frame = new JFrame("SLITHER.IO_JAVA");
			frame.setResizable(false);
			frame.add(new Nov06Title());
			frame.pack();
			frame.setVisible(true);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});
	}

	//‰æ–ÊØ‚è‘Ö‚¦
	public static void change(JPanel panel) {
		//playBGM’â~
		Nov06Main.resultBGM.stop();
		//playBGMÄ¶
		resultBGM = Nov06Sound.createClip(new File("src/sound/play.wav"));
		
		SwingUtilities.invokeLater(() -> {
			frame.getContentPane().removeAll();
			frame.add(panel);
			panel.requestFocusInWindow();
			frame.validate(); // XV
			frame.repaint(); // Ä•`‰æ
		});
	}

	// •¶š—ñ‚ğ’†‰›‘µ‚¦‚Å•\¦
	public static void drawStringCenter(Graphics g, String text, int x, int y) {
		FontMetrics fm = g.getFontMetrics();
		Rectangle rectText = fm.getStringBounds(text, g).getBounds();
		x = x - rectText.width / 2;
		y = y - rectText.height / 2 + fm.getMaxAscent();
		g.drawString(text, x, y);
	}
}
