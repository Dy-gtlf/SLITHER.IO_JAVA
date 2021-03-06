import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

public class Nov06Title extends JPanel implements KeyListener, Runnable {
	private Font font;
	private Thread thread;
	private Cell state[][]; // マス
	private int xSize, ySize; // ステージサイズ
	private int block; // 四角形のサイズ
	private int queue_size; // 軌跡の長さ
	private boolean flag, isOpen;
	private Player player1, player2;
	Nov06Sound s; // Nov06Sound sを用意

	public Nov06Title() {
		setPreferredSize(new Dimension(Nov06Main.width, Nov06Main.height));
		setFocusable(true);
		addKeyListener(this);

		xSize = 128;
		ySize = 100;
		block = 10;
		queue_size = 50;
		state = new Cell[xSize][ySize];
		player1 = new Player(8, 15, 0, 1, queue_size);
		player2 = new Player(xSize - 9, ySize - 16, 0, -1, queue_size);
		flag = true;
		isOpen = false;
		int i, j;
		
		s = new Nov06Sound(getClass().getResource("sound/play.wav"));
		s.Play();

		// ステージの枠
		for (i = 0; i < xSize; i++) {
			for (j = 0; j < ySize; j++) {
				state[i][j] = new Cell(Color.WHITE);
			}
		}
		for (j = 0; j < ySize; j++) {
			state[0][j].color = Color.BLACK;
			state[xSize - 1][j].color = Color.BLACK;
		}
		for (i = 1; i < xSize - 1; i++) {
			state[i][0].color = state[i][ySize - 1].color = Color.BLACK;
		}
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

	public void run() {
		Thread thisThread = Thread.currentThread();
		while (thisThread == thread) {
			Grid tmp;
			while (flag) {
				// プレイヤー1
				player1.x += player1.dx;
				player1.y += player1.dy;
				state[player1.head.x][player1.head.y].color = Color.RED;
				if (state[player1.x][player1.y].color != Color.WHITE
						&& state[player1.x][player1.y].color != Color.RED) {
					player1.live = false;
				} else {
					if (state[player1.x][player1.y].color == Color.RED) {
						state[player1.x][player1.y].overlap++;
					}
					state[player1.x][player1.y].color = Color.ORANGE;
					player1.traces.offer(new Grid(player1.head.x, player1.head.y));
					player1.head = new Grid(player1.x, player1.y);
					if (player1.traces.size() > queue_size) {
						tmp = player1.traces.poll();
						if (state[tmp.x][tmp.y].overlap == 0) {
							state[tmp.x][tmp.y].color = Color.WHITE;
						} else {
							state[tmp.x][tmp.y].overlap--;
						}
					}
				}
				// プレイヤー2
				player2.x += player2.dx;
				player2.y += player2.dy;
				state[player2.head.x][player2.head.y].color = Color.BLUE;
				if (state[player2.x][player2.y].color != Color.WHITE
						&& state[player2.x][player2.y].color != Color.BLUE) {
					player2.live = false;
					if (player2.x == player1.x && player2.y == player1.y) {
						player2.live = false;
						state[player2.x][player2.y].color = Color.MAGENTA.darker();
					}
				} else {
					if (state[player2.x][player2.y].color == Color.BLUE) {
						state[player2.x][player2.y].overlap++;
					}
					state[player2.x][player2.y].color = Color.CYAN;
					player2.traces.offer(new Grid(player2.head.x, player2.head.y));
					player2.head = new Grid(player2.x, player2.y);
					if (player2.traces.size() > queue_size) {
						tmp = player2.traces.poll();
						if (state[tmp.x][tmp.y].overlap == 0) {
							state[tmp.x][tmp.y].color = Color.WHITE;
						} else {
							state[tmp.x][tmp.y].overlap--;
						}
					}
				}
				repaint();
				// 方向転換
				decideNextDirection(player1);
				decideNextDirection(player2);
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
		if (key == KeyEvent.VK_SPACE && !isOpen) {
			Nov06Sound c = new Nov06Sound(getClass().getResource("sound/count.wav"));
			c.Play();
			// spaceでゲーム画面
			flag = false;
			Nov06Main.change(new Nov06(s));
		} else if (key == 'Q') {
			if (isOpen) {
				isOpen = false;
				Nov06Sound q1 = new Nov06Sound(getClass().getResource("sound/q1.wav"));
				q1.Play();
			} else {
				isOpen = true;
				Nov06Sound q2 = new Nov06Sound(getClass().getResource("sound/q2.wav"));
				q2.Play();
			}
		} else if (key == KeyEvent.VK_ESCAPE && !isOpen) {
			// escで終了
			Nov06Main.frame.setVisible(false);
			Nov06Main.frame.dispose();
			s.Stop();
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
				g.setColor(state[i][j].color);
				if (state[i][j].color != Color.BLACK && state[i][j].color != Color.WHITE) {
					g.fillOval(i * block, j * block, block, block);
				} else {
					g.fillRect(i * block, j * block, block, block);
				}
			}
		}

		Graphics2D g2d = (Graphics2D) g;
		BasicStroke stroke = new BasicStroke(5);
		g2d.setStroke(stroke);

		if (isOpen) {
			g2d.drawRoundRect(200, 200, Nov06Main.width - 400, Nov06Main.height - 400, 10, 10);

			font = new Font("ＭＳ ゴシック", Font.BOLD, 40);
			g.setFont(font);

			g.setColor(Color.BLACK);
			Nov06Main.drawStringCenter(g, "ルール説明", Nov06Main.width / 2, Nov06Main.height / 2 - 250);
			Nov06Main.drawStringCenter(g, "相手の頭を自分の体に衝突させて勝利を掴め!", Nov06Main.width / 2, Nov06Main.height / 2 - 200);
			Nov06Main.drawStringCenter(g, "加速ゲージがある間は二倍速で移動可能", Nov06Main.width / 2, Nov06Main.height / 2 - 150);

			g.setColor(Color.RED);
			Nov06Main.drawStringCenter(g, "Player 1              ", Nov06Main.width / 2, Nov06Main.height / 2 - 50);
			Nov06Main.drawStringCenter(g, "方向転換 : W A S D    ", Nov06Main.width / 2, Nov06Main.height / 2);
			Nov06Main.drawStringCenter(g, "  加速   : SHIFT長押し", Nov06Main.width / 2, Nov06Main.height / 2 + 50);
			g.setColor(Color.BLUE);
			Nov06Main.drawStringCenter(g, "Player 2              ", Nov06Main.width / 2, Nov06Main.height / 2 + 100);
			Nov06Main.drawStringCenter(g, "方向転換 : I J K L    ", Nov06Main.width / 2, Nov06Main.height / 2 + 150);
			Nov06Main.drawStringCenter(g, "  加速   :  /   長押し", Nov06Main.width / 2, Nov06Main.height / 2 + 200);
			g.setColor(Color.BLACK);
			font = new Font("ＭＳ ゴシック", Font.BOLD, 25);
			g.setFont(font);
			Nov06Main.drawStringCenter(g, "Q : 操作説明を閉じる", Nov06Main.width / 2, Nov06Main.height / 2 + 250);
		} else {
			g.setColor(Color.BLACK);
			g2d.drawRoundRect(150, Nov06Main.height / 3 - 70, Nov06Main.width - 300, 150, 10, 10);

			font = new Font("ＭＳ ゴシック", Font.BOLD, 60);
			g.setFont(font);
			Nov06Main.drawStringCenter(g, "✞SLITHER.IO_JAVA✞", Nov06Main.width / 2, Nov06Main.height / 3);

			font = new Font("ＭＳ ゴシック", Font.BOLD, 25);
			g.setFont(font);
			g.drawString("MADE BY 卍暗黒騎士団卍", Nov06Main.width / 2 + 50, Nov06Main.height / 3 + 60);

			font = new Font("ＭＳ ゴシック", Font.BOLD, 40);
			g.setFont(font);
			g2d.drawRoundRect(400, (int) (Nov06Main.height / 1.5 - 100), Nov06Main.width - 800, 200, 10, 10);
			Nov06Main.drawStringCenter(g, "SPACE : PLAY ", Nov06Main.width / 2, (int) (Nov06Main.height / 1.5 - 50));
			Nov06Main.drawStringCenter(g, "  Q   : HELP ", Nov06Main.width / 2, (int) (Nov06Main.height / 1.5));
			Nov06Main.drawStringCenter(g, " ESC  : CLOSE", Nov06Main.width / 2, (int) (Nov06Main.height / 1.5 + 50));
		}
	}

	// タイトルの周りを周回させる
	private void decideNextDirection(Player player) {
		if (player.x == 8 && player.y == 15) {
			if (player.dy != -1) {
				player.dx = 0;
				player.dy = 1;
			}
		} else if (player.x == 8 && player.y == ySize - 16) {
			if (player.dx != -1) {
				player.dx = 1;
				player.dy = 0;
			}
		} else if (player.x == xSize - 9 && player.y == ySize - 16) {
			if (player.dy != 1) {
				player.dx = 0;
				player.dy = -1;
			}
		} else if (player.x == xSize - 9 && player.y == 15) {
			if (player.dx != 1) {
				player.dx = -1;
				player.dy = 0;
			}
		}
	}
}
