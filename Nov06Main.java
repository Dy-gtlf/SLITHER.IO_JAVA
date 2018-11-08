import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Nov06Main extends JFrame {
	public static JFrame frame;
	public static int width = 1000; // 画面の幅
	public static int height = 600; // 画面の高さ

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			frame = new JFrame("SLITHER.IO_JAVA!");
			frame.setResizable(false);
			frame.add(new Nov06Title());
			frame.pack();
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});
	}

	//画面切り替え用メソッド
	public static void change(JPanel panel) {
		//ContentPaneにはめ込まれたパネルを削除
		SwingUtilities.invokeLater(() -> {
			frame.getContentPane().removeAll();
			frame.add(panel);
			panel.requestFocusInWindow();
			frame.validate();//更新
			frame.repaint();//再描画
		});
	}
}
