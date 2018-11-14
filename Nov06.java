import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.Clip;
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

class Player {
	int x; // プレイヤーの座標
	int y;
	int dx; // プレイヤーの向き
	int dy;
	Grid head; // 先頭の座標
	Queue<Grid> traces; // 軌跡の座標キュー
	int size; // プレイヤーの長さ
	int energy; // 加速ゲージ
	boolean live; // プレイヤーの生存
	boolean accele; // 進む
	boolean aflag; // 加速のフラグ

	public Player(int x, int y, int dx, int dy, int size) {
		head = new Grid(x, y);
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.size = size;
		traces = new ArrayDeque<>();
		energy = 50;
		live = true;
		accele = true;
		aflag = false;
	}
}

public class Nov06 extends JPanel implements Runnable, KeyListener {
	/**
	 *
	 */
	public static Clip resultSYU;  //縮小
	private static final long serialVersionUID = 1L;
	private Cell state[][]; // マスの色
	private int xSize, ySize; // ステージサイズ
	private int block; // 四角形のサイズ
	private Thread thread;

	private Player player1, player2; // プレイヤー
	private int queue_size; // 軌跡の長さ
	private int energy_max; // エネルギー上限値
	private int energy_min; // エネルギー下限値
	private Grid tmp; // 座標の一時変数
	private int winner;
	private int area;
	private int prepareCountDown; //開始前カウント

	//タイマー
	private Timer timer = new Timer();
	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			if ( prepareCountDown > 0 ) {
				prepareCountDown--;
			} else {
				prepareCountDown = 5;
				resultSYU = Nov06Sound.createClip(new File("src/sound/syu.wav"));
				update(area);
				area += 2;
			}
		}
	};

	private void update(int n) {
		int i, j;

		// ステージの枠
		for (j = n; j < ySize; j++) {
			state[n][j].color = state[xSize - (n + 1)][j].color = Color.BLACK;
		}
		for (i = n + 1; i < xSize - (n + 1); i++) {
			state[i][n].color = state[i][ySize - (n + 1)].color = Color.BLACK;
		}
		n++;
		for (j = n; j < ySize; j++) {
			state[n][j].color = state[xSize - (n + 1)][j].color = Color.BLACK;
		}
		for (i = n + 1; i < xSize - (n + 1); i++) {
			state[i][n].color = state[i][ySize - (n + 1)].color = Color.BLACK;
		}
	}

	// コンストラクター
	public Nov06() {
		setPreferredSize(new Dimension(Nov06Main.width, Nov06Main.height));
		setFocusable(true);
		addKeyListener(this);

		xSize = 128;
		ySize = 90;
		block = 10;
		energy_max = 50;
		energy_min = 0;
		queue_size = 100;
		area = 1;
		prepareCountDown = 5;
		state = new Cell[xSize][ySize];
		timer.scheduleAtFixedRate(task, 1000, 1000);


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
		player1 = new Player(8, 8, 0, 1, queue_size);
		player2 = new Player(xSize - 9, ySize - 9, 0, -1, queue_size);
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
				if (state[i][j].color != Color.BLACK && state[i][j].color != Color.WHITE) {
					g.fillOval(i * block, j * block, block, block);
				} else {
					g.fillRect(i * block, j * block, block, block);
				}
			}
		}
		String str = "";
		if (prepareCountDown == 0) {
			//START
			str = "縮小";
			g.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 30));
		} else {
			//カウントダウンの描画
			str = String.valueOf(prepareCountDown);
			g.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 60));
		}
		g.setColor(Color.BLACK);
		Nov06Main.drawStringCenter(g, str, Nov06Main.width / 2, Nov06Main.height - 50);

		g.setColor(Color.RED);
		g.fillRect(0, Nov06Main.height - 100, Nov06Main.width / 2 - 50, 100);
		g.setColor(Color.WHITE);
		g.fillRect(0, Nov06Main.height - 100, Nov06Main.width / 2 * (energy_max - player1.energy) / energy_max - 50, 100);
		g.setColor(Color.BLUE);
		g.fillRect(Nov06Main.width / 2 + 50, Nov06Main.height - 100, Nov06Main.width / 2 * player2.energy / energy_max, 100);
	}

	public void run() {
		Thread thisThread = Thread.currentThread();
		while (thisThread == thread) {
			requestFocus();
			// ゲームの継続
			while (player1.live && player2.live) {
				// プレイヤー1
				if (player1.accele || (player1.aflag && player1.energy > energy_min)) {
					player1.x += player1.dx;
					player1.y += player1.dy;
					state[player1.head.x][player1.head.y].color = Color.RED;
					if (state[player1.x][player1.y].color != Color.WHITE
							&& state[player1.x][player1.y].color != Color.RED) {
						player1.live = false;
						if (player1.x == player2.x && player1.y == player2.y) {
							state[player1.x][player1.y].color = Color.MAGENTA.darker();
							player2.live = false;
						}
					} else {
						if (state[player1.x][player1.y].color == Color.RED) {
							state[player1.x][player1.y].overlap++;
						}
						state[player1.x][player1.y].color = Color.ORANGE;
						player1.traces.offer(new Grid(player1.head.x, player1.head.y));
						player1.head = new Grid(player1.x, player1.y);
						if (player1.traces.size() > queue_size) {
							tmp = player1.traces.poll();
							if (state[tmp.x][tmp.y].overlap == 0 && state[tmp.x][tmp.y].color != Color.BLACK) {
								state[tmp.x][tmp.y].color = Color.WHITE;
							} else {
								state[tmp.x][tmp.y].overlap--;
							}
						}
					}
					if (player1.aflag == true && player1.energy > energy_min) {
						player1.energy--;
					}
					player1.accele = false;
				} else {
					if (player1.aflag == false && player1.energy < energy_max) {
						player1.energy++;
					}
					player1.accele = true;
				}
				if (!player1.live && !player2.live) {
					break;
				}
				// プレイヤー2
				if (player2.accele || (player2.aflag && player2.energy > energy_min)) {
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
							if (state[tmp.x][tmp.y].overlap == 0 && state[tmp.x][tmp.y].color != Color.BLACK) {
								state[tmp.x][tmp.y].color = Color.WHITE;
							} else {
								state[tmp.x][tmp.y].overlap--;
							}
						}
					}
					if (player2.aflag == true && player2.energy > energy_min) {
						player2.energy--;
					}
					player2.accele = false;
				} else {
					if (player2.aflag == false && player2.energy < energy_max) {
						player2.energy++;
					}
					player2.accele = true;
				}
				// 勝利判定
				if (!player1.live) {
					if (!player2.live) {
						Nov06Sound.createClip(new File("src/sound/bon.wav"));
						winner = 0;
					} else {
						Nov06Sound.createClip(new File("src/sound/bon.wav"));
						winner = 2;
					}
				} else if (!player2.live) {
					Nov06Sound.createClip(new File("src/sound/bon.wav"));
					winner = 1;
				}
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
			try {
				Thread.sleep(1750);
				timer.cancel();
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
			if (player1.dx != 1) {
				Nov06Sound.createClip(new File("src/sound/1p.wav"));
				player1.dx = -1;
				player1.dy = 0;
			}
			break;
		case 'S':
			if (player1.dy != -1) {
				Nov06Sound.createClip(new File("src/sound/1p.wav"));
				player1.dx = 0;
				player1.dy = 1;
			}
			break;
		case 'W':
			if (player1.dy != 1) {
				Nov06Sound.createClip(new File("src/sound/1p.wav"));
				player1.dx = 0;
				player1.dy = -1;
			}
			break;
		case 'D':
			if (player1.dx != -1) {
				Nov06Sound.createClip(new File("src/sound/1p.wav"));
				player1.dx = 1;
				player1.dy = 0;
			}
			break;
		case KeyEvent.VK_SHIFT:
			player1.aflag = true;
			break;
		case 'J':
			if (player2.dx != 1) {
				Nov06Sound.createClip(new File("src/sound/2p.wav"));
				player2.dx = -1;
				player2.dy = 0;
			}
			break;
		case 'K':
			if (player2.dy != -1) {
				Nov06Sound.createClip(new File("src/sound/2p.wav"));
				player2.dx = 0;
				player2.dy = 1;
			}
			break;
		case 'I':
			if (player2.dy != 1) {
				Nov06Sound.createClip(new File("src/sound/2p.wav"));
				player2.dx = 0;
				player2.dy = -1;
			}
			break;
		case 'L':
			if (player2.dx != -1) {
				Nov06Sound.createClip(new File("src/sound/2p.wav"));
				player2.dx = 1;
				player2.dy = 0;
			}
			break;
		case '/':
			player2.aflag = true;
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_SHIFT) {
			player1.aflag = false;
		}
		if (key == '/') {
			player2.aflag = false;
		}
	}

	public void keyTyped(KeyEvent e) {
	}
}