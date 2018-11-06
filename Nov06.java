import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class Grid {
	int x;
	int y;

	public Grid(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

public class Nov06 extends JPanel implements Runnable, KeyListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Color state[][]; // マスの色
	private int xSize, ySize; // ステージサイズ
	private int block; // 四角形のサイズ
	private int xL, yL, xR, yR; // プレイヤーの座標
	private int dxL, dyL, dxR, dyR; // プレイヤーの向き
	private boolean liveL, liveR; // プレイヤーの生存
	private Thread thread;
	private String message;
	private Font font;

	private int width, height; // 画面サイズ

	private int queue_size = 400; // 軌跡の長さ
	private Queue<Grid> tracesL, tracesR; // 軌跡の座標キュー
	private Grid tmp; // 座標の一時変数

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
		xL = yL = 2;
		xR = xSize - 3;
		yR = ySize - 3;
		dxL = dxR = 0;
		dyL = 1;
		dyR = -1;
		liveL = liveR = true;
		tracesL = tracesR = new ArrayDeque<>();
	}

	// コンストラクター
	public Nov06() {
		setPreferredSize(new Dimension(800, 700));

		xSize = 100;
		ySize = 80;
		block = 6;
		state = new Color[xSize][ySize];
		message = "Game started!";
		font = new Font("Monospaced", Font.PLAIN, 12);
		setFocusable(true);
		addKeyListener(this);
		Dimension size = getPreferredSize();
		width = size.width;
		height = size.height;

		startThread();
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

	@Override
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, width, height);

		int i, j;
		for (i = 0; i < xSize; i++) {
			for (j = 0; j < ySize; j++) {
				g.setColor(state[i][j]);
				g.fillRect(i * block, j * block, block, block);
			}
		}
		g.setFont(font);
		g.setColor(Color.GREEN.darker());
		g.drawString(message, 2 * block, block * (ySize + 3));
		g.setColor(Color.RED.darker());
		g.drawString("Left:  W, A, S, D", 2 * block, block * (ySize + 6));
		g.setColor(Color.BLUE.darker());
		g.drawString("Right: I, J, K, L", 2 * block, block * (ySize + 9));
	}

	public void run() {
		Thread thisThread = Thread.currentThread();
		while (thisThread == thread) {
			initialize();
			requestFocus();
			// ゲームの継続
			while (liveL && liveR) {
				// プレイヤー1
				xL += dxL;
				yL += dyL;
				if (state[xL][yL] != Color.WHITE) {
					liveL = false;
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
					liveR = false;
					if (xR == xL && yR == yL) {
						liveL = false;
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
				// 勝利判定
				if (!liveL) {
					if (!liveR) {
						message = "Draw!";
					} else {
						message = "R won!";
					}
				} else if (!liveR) {
					message = "L won!";
				}
				repaint();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			try {
				Thread.sleep(1750);
			} catch (InterruptedException e) {
			}
		}
	}

	// キー入力判定
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case 'A':
			if (dxL != 1) {
				dxL = -1;
				dyL = 0;
			}
			break;
		case 'S':
			if (dyL != -1) {
				dxL = 0;
				dyL = 1;
			}
			break;
		case 'W':
			if (dyL != 1) {
				dxL = 0;
				dyL = -1;
			}
			break;
		case 'D':
			if (dxL != -1) {
				dxL = 1;
				dyL = 0;

			}
			break;
		case 'J':
			if (dxR != 1) {
				dxR = -1;
				dyR = 0;
			}
			break;
		case 'K':
			if (dyR != -1) {
				dxR = 0;
				dyR = 1;
			}
			break;
		case 'I':
			if (dyR != 1) {
				dxR = 0;
				dyR = -1;
			}
			break;
		case 'L':
			if (dxR != -1) {
				dxR = 1;
				dyR = 0;
			}
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {

			JFrame frame = new JFrame("SLITHER.IO_JAVA!");
			frame.add(new Nov06());
			frame.pack();
			frame.setVisible(true);

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});
	}
}