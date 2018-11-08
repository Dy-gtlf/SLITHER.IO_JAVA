import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

public class Nov06Title extends JPanel implements KeyListener {
	private Font font;

	public Nov06Title() {
		setPreferredSize(new Dimension(Nov06Main.width, Nov06Main.height));
		setFocusable(true);
		addKeyListener(this);
		font = new Font("Monospaced", Font.PLAIN, 20);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			Nov06Main.change(new Nov06());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.clearRect(0, 0, Nov06Main.width, Nov06Main.height);
		g.setFont(font);
		g.setColor(Color.BLACK);
		g.drawString("SLITHER.IO_JAVA!", 400, 350);
		g.drawString("Press space to play", 400, 400);
	}
}
