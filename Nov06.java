import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.Queue;
import javax.swing.JPanel;

class Grid {
	int x;
	int y;

	public Grid(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

class Cell {
	Color color;
	int overlap;

	public Cell(Color color) {
		this.color = color;
		overlap = 0;
	}
}

public class Nov06 extends JPanel implements Runnable, KeyListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Cell state[][]; // マスの色
	private int xSize, ySize; // ステージサイズ
	private int block; // 四角形のサイズ
	private int xL, yL, xR, yR; // プレイヤーの座標
	private int dxL, dyL, dxR, dyR; // プレイヤーの向き
	private boolean liveL, liveR; // プレイヤーの生存
	private Thread thread;
	private String message;
	private Font font;

	private int queue_size = 100; // 軌跡の長さ
	private Queue<Grid> tracesL, tracesR; // 軌跡の座標キュー
	private Grid tmp; // 座標の一時変数
	private Grid headL, headR; // 先頭の一時変数
	private int winner;

	// 初期設定
	private void initialize() {
		int i, j;
		for (i = 0; i < xSize; i++) {
			for (j = 0; j < ySize; j++) {
				state[i][j] = new Cell(Color.WHITE);
			}
		}
		// ステージの枠
		for (j = 0; j < ySize; j++) {
			state[0][j].color = Color.BLACK;
			state[xSize - 1][j].color = Color.BLACK;
		}
		for (i = 1; i < xSize - 1; i++) {
			state[i][0].color = state[i][ySize - 1].color = Color.BLACK;
		}
		xL = yL = 2;
		headL = new Grid(xL, yL);
		xR = xSize - 3;
		yR = ySize - 3;
		headR = new Grid(xR, yR);
		dxL = dxR = 0;
		dyL = 1;
		dyR = -1;
		liveL = liveR = true;
		tracesL = tracesR = new ArrayDeque<>();
	}

	// コンストラクター
	public Nov06() {
		setPreferredSize(new Dimension(Nov06Main.width, Nov06Main.height));

		xSize = 100;
		ySize = 80;
		block = 6;
		state = new Cell[xSize][ySize];

		message = "Game started!";
		font = new Font("Monospaced", Font.PLAIN, 12);
		setFocusable(true);
		addKeyListener(this);
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
		super.paintComponent(g);
		g.clearRect(0, 0, Nov06Main.width, Nov06Main.height);
		int i, j;
		for (i = 0; i < xSize; i++) {
			for (j = 0; j < ySize; j++) {
				g.setColor(state[i][j].color);
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
				state[headL.x][headL.y].color = Color.RED;
				if (state[xL][yL].color != Color.WHITE && state[xL][yL].color != Color.RED) {
					liveL = false;
				} else {
					if (state[xL][yL].color == Color.RED) {
						state[xL][yL].overlap++;
					}
					state[xL][yL].color = Color.ORANGE;
					tracesL.offer(new Grid(headL.x, headL.y));
					headL = new Grid(xL, yL);
					if (tracesL.size() > queue_size) {
						tmp = tracesL.poll();
						if (state[tmp.x][tmp.y].overlap == 0) {
							state[tmp.x][tmp.y].color = Color.WHITE;
						} else {
							state[tmp.x][tmp.y].overlap--;
						}
					}
				}
				// プレイヤー2
				xR += dxR;
				yR += dyR;
				state[headR.x][headR.y].color = Color.BLUE;
				if (state[xR][yR].color != Color.WHITE && state[xR][yR].color != Color.BLUE) {
					liveR = false;
					if (xR == xL && yR == yL) {
						liveL = false;
						state[xL][yL].color = Color.MAGENTA.darker();
					}
				} else {
					if (state[xR][yR].color == Color.BLUE) {
						state[xR][yR].overlap++;
					}
					state[xR][yR].color = Color.CYAN;
					tracesR.offer(new Grid(headR.x, headR.y));
					headR = new Grid(xR, yR);
					if (tracesR.size() > queue_size) {
						tmp = tracesR.poll();
						if (state[tmp.x][tmp.y].overlap == 0) {
							state[tmp.x][tmp.y].color = Color.WHITE;
						} else {
							state[tmp.x][tmp.y].overlap--;
						}
					}
				}
				// 勝利判定
				if (!liveL) {
					if (!liveR) {
						message = "Draw!";
						winner = 0;
					} else {
						message = "Player 2 won!";
						winner = 2;
					}
				} else if (!liveR) {
					message = "Player 1 won!";
					winner = 1;
				}
				repaint();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			try {
				Thread.sleep(1750);
				Nov06Main.change(new Nov06Result(winner));
				break;
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
}