import java.util.ArrayList;
import java.util.Random;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class JFlood extends JFrame {
	private ArrayList<BoardItem> boardItems = new ArrayList<BoardItem>();
	private ArrayList<Color> colors = new ArrayList<Color>();
	private DrawingPanel drawingPanel;
	private int boardSize = 14;
	private int maxMoves = 25;
	private boolean gameOver;
	private boolean win;
	private boolean loose;
	private int moves;

	public JFlood() {
		setTitle("JFlood");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel();

		int size = 560;
		drawingPanel = new DrawingPanel(size, size);

		drawingPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				onMousePressed(e.getPoint());
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (gameOver && key == KeyEvent.VK_SPACE) {
					startNewGame();
				}
			}
		});

		mainPanel.add(drawingPanel);
		add(mainPanel);

		pack();
		setLocationByPlatform(true);
		setVisible(true);

		initColors();
		initBoardItems();
		startNewGame();
	}

	private void initColors() {
		colors.add(new Color(255,   0,   0));
		colors.add(new Color(  0, 255,   0));
		colors.add(new Color(  0,   0, 255));
		colors.add(new Color(255, 255,   0));
		colors.add(new Color(  0, 255, 255));
		colors.add(new Color(255,   0, 255));
	}

	private void initBoardItems() {
		int itemSize =  drawingPanel.getWidth() / boardSize;
		int x = 0;
		int y = 0;

		for (int i = 0; i < boardSize * boardSize; i++) {
			BoardItem item = new BoardItem();

			item.setBounds(x, y, itemSize, itemSize);
			boardItems.add(item);

			x += itemSize;
			if (x > itemSize * boardSize - 1) {
				x = 0;
				y += itemSize;
			}
		}
	}

	private void startNewGame() {
		gameOver = false;
		win = false;
		loose = false;
		moves = 0;
		Random random = new Random();

		for (BoardItem i : boardItems) {
			i.setColor(random.nextInt(colors.size()));
		}
		drawingPanel.repaint();
	}

	private boolean checkWinning() {
		for (BoardItem i : boardItems) {
			if (i.getColor() != boardItems.get(0).getColor()) {
				return false;
			}
		}
		return true;
	}

	private void onMousePressed(Point p) {
		if (gameOver) {
			return;
		}

		for (BoardItem i : boardItems) {
			if (i.contains(p)) {
				moves++;

				// debug
				System.out.println("Moves: " + moves);

				floodFill(0, 0, boardItems.get(0).getColor(), i.getColor());
				drawingPanel.repaint();

				if (checkWinning()) {
					gameOver = true;
					win = true;

					// debug
					System.out.println("Game Over: Win!");
				}

				if (!win && moves == maxMoves) {
					gameOver = true;
					loose = true;

					// debug
					System.out.println("Game Over: Loose!");
				}

				break;
			}
		}
	}

	private void floodFill(int i, int j, int oldColor, int newColor) {
		if (boardItems.get(j * boardSize + i).getColor() != oldColor || newColor == oldColor) {
			return;
		}

		boardItems.get(j * boardSize + i).setColor(newColor);

		if (j + 1 < boardSize) {
			floodFill(i, j + 1, oldColor, newColor);
		}
		if (j - 1 >= 0) {
			floodFill(i, j - 1, oldColor, newColor);
		}
		if (i - 1 >= 0) {
			floodFill(i - 1, j, oldColor, newColor);
		}
		if (i + 1 < boardSize) {
			floodFill(i + 1, j, oldColor, newColor);
		}
	}

	private class DrawingPanel extends JPanel {
		public DrawingPanel(int width, int height) {
			setPreferredSize(new Dimension(width, height));
                }

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			for (BoardItem i : boardItems) {
				g.setColor(colors.get(i.getColor()));
				g.fillRect(i.x, i.y, i.width, i.height);
			}
		}
	}

	public static void main(String[] args) {
		new JFlood();
	}
}
