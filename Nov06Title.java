import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.Queue;

import javax.swing.JPanel;

public class Nov06Title extends JPanel implements KeyListener, Runnable {
	private Font font;
	private Thread thread;
	private Color state[][]; // マスの色
	private int xSize, ySize; // ステージサイズ
	private int block; // 四角形のサイズ
	private int xL, yL, xR, yR; // プレイヤーの座標
	private int dxL, dyL, dxR, dyR; // プレイヤーの向き

	private int queue_size = 200; // 軌跡の長さ
	private Queue<Grid> tracesL, tracesR; // 軌跡の座標キュー
	private Grid tmp; // 座標の一時変数
	private boolean flag = true;

	public Nov06Title() {
		setPreferredSize(new Dimension(Nov06Main.width, Nov06Main.height));
		setFocusable(true);
		addKeyListener(this);

		xSize = 160;
		ySize = 90;
		block = 8;
		state = new Color[xSize][ySize];
		startThread();
	}

	// 初期設定
	private void initialize() {
		int i, j;

		// ステージの枠
		for (j = 0; j < ySize; j++) {
			state[0][j] = state[xSize - 1][j] = Color.BLACK;
		}
		for (i = 1; i < xSize - 1; i++) {
			state[i][0] = state[i][ySize - 1] = Color.BLACK;
			for (j = 1; j < ySize - 1; j++) {
				state[i][j] = Color.WHITE;
			}
		}
		xL = yL = 50;
		xR = xSize - 51;
		yR = ySize - 51;
		dxL = dxR = 0;
		dyL = 1;
		dyR = -1;
		tracesL = tracesR = new ArrayDeque<>();
	}

	public void startThread() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stopThread() {
		if (thread != null) {
			thread = null;
		}
	}

	public void run() {
		Thread thisThread = Thread.currentThread();
		while (thisThread == thread) {
			initialize();
			requestFocus();
			// ニョロニョロの継続
			while (flag) {
				// 方向の決定
				decideNextDirection();
				// プレイヤー1
				xL += dxL;
				yL += dyL;
				if (state[xL][yL] != Color.WHITE) {
				} else {
					state[xL][yL] = Color.RED;
					tracesL.offer(new Grid(xL, yL));
					if (tracesL.size() > queue_size) {
						tmp = tracesL.poll();
						state[tmp.x][tmp.y] = Color.WHITE;
					}
				}
				// プレイヤー2
				xR += dxR;
				yR += dyR;
				if (state[xR][yR] != Color.WHITE) {
					if (xR == xL && yR == yL) {
						state[xL][yL] = Color.MAGENTA.darker();
					}
				} else {
					state[xR][yR] = Color.BLUE;
					tracesR.offer(new Grid(xR, yR));
					if (tracesR.size() > queue_size) {
						tmp = tracesR.poll();
						state[tmp.x][tmp.y] = Color.WHITE;
					}
				}
				repaint();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_SPACE) {
			flag = false;
			Nov06Main.change(new Nov06());
		} else if (key == KeyEvent.VK_ESCAPE) {
			Nov06Main.frame.setVisible(false);
			Nov06Main.frame.dispose();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.clearRect(0, 0, Nov06Main.width, Nov06Main.height);
		int i, j;
		for (i = 0; i < xSize; i++) {
			for (j = 0; j < ySize; j++) {
				g.setColor(state[i][j]);
				g.fillRect(i * block, j * block, block, block);
			}
		}

		g.setColor(Color.BLACK);
		Graphics2D g2d = (Graphics2D) g;
		BasicStroke stroke = new BasicStroke(5);
		g2d.setStroke(stroke);
		g2d.drawRoundRect(125, Nov06Main.height / 3 - 70, Nov06Main.width - 250, 150, 10, 10);
		g2d.drawRoundRect(300, (int) (Nov06Main.height / 1.5 - 50), Nov06Main.width - 600, 150, 10, 10);

		font = new Font("ＭＳ ゴシック", Font.BOLD, 60);
		g.setFont(font);
		Nov06Main.drawStringCenter(g, "✞SLITHER.IO_JAVA✞", Nov06Main.width / 2, Nov06Main.height / 3);

		font = new Font("ＭＳ ゴシック", Font.BOLD, 25);
		g.setFont(font);
		g.drawString("MADE BY 卍暗黒騎士団卍", Nov06Main.width / 2 + 50, Nov06Main.height / 3 + 60);

		font = new Font("ＭＳ ゴシック", Font.BOLD, 40);
		g.setFont(font);
		Nov06Main.drawStringCenter(g, "SPACE : PLAY ", Nov06Main.width / 2, (int) (Nov06Main.height / 1.5));
		Nov06Main.drawStringCenter(g, "  ESC : CLOSE", Nov06Main.width / 2, (int) (Nov06Main.height / 1.5 + 50));
	}

	private void decideNextDirection() {
	}
}
