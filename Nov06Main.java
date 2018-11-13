import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Nov06Main extends JFrame {
	public static JFrame frame;
	public static int width = 1280; // 画面の幅
	public static int height = 720; // 画面の高さ

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			frame = new JFrame("SLITHER.IO_JAVA!");
			frame.setResizable(false);
			frame.add(new Nov06Title());
			frame.pack();
			frame.setVisible(true);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});
	}

	//画面切り替え
	public static void change(JPanel panel) {
		SwingUtilities.invokeLater(() -> {
			frame.getContentPane().removeAll();
			frame.add(panel);
			panel.requestFocusInWindow();
			frame.validate(); // 更新
			frame.repaint(); // 再描画
		});
	}

	// 文字列を中央揃えで表示
	public static void drawStringCenter(Graphics g, String text, int x, int y) {
		FontMetrics fm = g.getFontMetrics();
		Rectangle rectText = fm.getStringBounds(text, g).getBounds();
		x = x - rectText.width / 2;
		y = y - rectText.height / 2 + fm.getMaxAscent();
		g.drawString(text, x, y);
	}
}
