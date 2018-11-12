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

class Player {
	int x; // プレイヤーの座標
	int y;
	int dx; // プレイヤーの向き
	int dy;
	Queue<Grid> traces;
	int size;

	public Player(int x, int y, int dx, int dy, int size) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.size = size;
		traces = new ArrayDeque<>();
	}
}

public class Nov06Title extends JPanel implements KeyListener, Runnable {
	private Font font;
	private Thread thread;
	private Cell state[][]; // マス
	private int xSize, ySize; // ステージサイズ
	private int block; // 四角形のサイズ
	private int queue_size; // 軌跡の長さ
	private boolean flag = true;
	private Player player1, player2;

	public Nov06Title() {
		setPreferredSize(new Dimension(Nov06Main.width, Nov06Main.height));
		setFocusable(true);
		addKeyListener(this);

		xSize = 160;
		ySize = 90;
		block = 8;
		queue_size = 100;
		state = new Cell[xSize][ySize];
		startThread();
	}

	// 初期設定
	private void initialize() {
		int i, j;

		// ステージの枠
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
			Grid tmp;
			while (flag) {
				// 方向の決定
				// プレイヤー1
				player1.x += player1.dx;
				player1.y += player1.dy;
				if (state[player1.x][player1.y].color != Color.WHITE) {
				} else {
					state[player1.x][player1.y].color = Color.RED;
					player1.traces.offer(new Grid(player1.x, player1.y));
					if (player1.traces.size() > queue_size) {
						tmp = player1.traces.poll();
						state[tmp.x][tmp.y].color = Color.WHITE;
					}
				}
				// プレイヤー2
				player2.x += player2.dx;
				player2.y += player2.dy;
				if (state[player2.x][player2.y].color != Color.WHITE) {
					if (player1.x == player2.x && player1.y == player2.y) {
						state[player2.x][player2.y].color = Color.MAGENTA.darker();
					}
				} else {
					state[player2.x][player2.y].color = Color.BLUE;
					player2.traces.offer(new Grid(player2.x, player2.y));
					if (player2.traces.size() > queue_size) {
						tmp = player2.traces.poll();
						state[tmp.x][tmp.y].color = Color.WHITE;
					}
				}
				repaint();
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
				g.setColor(state[i][j].color);
				g.fillRect(i * block, j * block, block, block);
			}
		}

		g.setColor(Color.BLACK);
		Graphics2D g2d = (Graphics2D) g;
		BasicStroke stroke = new BasicStroke(5);
		g2d.setStroke(stroke);
		g2d.drawRoundRect(125, Nov06Main.height / 3 - 70, Nov06Main.width - 250, 150, 10, 10);
		g2d.drawRoundRect(400, (int) (Nov06Main.height / 1.5 - 50), Nov06Main.width - 800, 150, 10, 10);

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

	// タイトルの周りを周回させる
	private void decideNextDirection(Player player) {
		if (player.x == 8 && player.y == 8) {
			if (player.dy != -1) {
				player.dx = 0;
				player.dy = 1;
			}
		} else if (player.x == 8 && player.y == ySize - 9) {
			if (player.dx != -1) {
				player.dx = 1;
				player.dy = 0;
			}
		} else if (player.x == xSize - 9 && player.y == ySize - 9) {
			if (player.dy != 1) {
				player.dx = 0;
				player.dy = -1;
			}
		} else if (player.x == xSize - 9 && player.y == 8) {
			if (player.dx != 1) {
				player.dx = -1;
				player.dy = 0;
			}
		}
	}
}
