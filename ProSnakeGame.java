import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class ProSnakeGame extends JPanel implements ActionListener {

    private final int TILE_SIZE = 25;
    private final int WIDTH = 600;
    private final int HEIGHT = 600;

    private int[] x = new int[WIDTH * HEIGHT];
    private int[] y = new int[WIDTH * HEIGHT];

    private int bodyParts;
    private int foodX, foodY;
    private int score;

    private char direction;
    private boolean running;

    private Timer timer;
    private Random random;

    // ================= CONSTRUCTOR =================
    ProSnakeGame() {
        random = new Random();

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());

        startGame();

        // 🔥 IMPORTANT → panel focus lega → ENTER restart kaam karega
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    // ================= START GAME =================
    public void startGame() {
        bodyParts = 3;
        score = 0;
        direction = 'R';
        running = true;

        // snake start position center me
        for (int i = 0; i < bodyParts; i++) {
            x[i] = WIDTH / 2 - (i * TILE_SIZE);
            y[i] = HEIGHT / 2;
        }

        newFood();

        timer = new Timer(80, this); // smooth speed
        timer.start();
    }

    // ================= FOOD =================
    public void newFood() {
        foodX = random.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE;
        foodY = random.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE;
    }

    // ================= DRAW =================
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        if (running) {

            // 🔲 Grid
            g.setColor(Color.DARK_GRAY);
            for (int i = 0; i < WIDTH; i += TILE_SIZE) {
                g.drawLine(i, 0, i, HEIGHT);
                g.drawLine(0, i, WIDTH, i);
            }

            // 🍎 Food
            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, TILE_SIZE, TILE_SIZE);

            // 🐍 Snake head
            g.setColor(Color.GREEN);
            g.fillRoundRect(x[0], y[0], TILE_SIZE, TILE_SIZE, 10, 10);

            // 🐍 Body gradient
            for (int i = 1; i < bodyParts; i++) {
                g.setColor(new Color(0, 120 + (i * 3) % 135, 0));
                g.fillRoundRect(x[i], y[i], TILE_SIZE, TILE_SIZE, 8, 8);
            }

            // 🏆 Score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Score: " + score, 10, 20);

        } else {
            gameOver(g);
        }
    }

    // ================= MOVE =================
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] -= TILE_SIZE;
            case 'D' -> y[0] += TILE_SIZE;
            case 'L' -> x[0] -= TILE_SIZE;
            case 'R' -> x[0] += TILE_SIZE;
        }
    }

    // ================= CHECK FOOD =================
    public void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            bodyParts++;
            score++;
            newFood();

            // speed increase every 5 score
            if (score % 5 == 0) {
                timer.setDelay(Math.max(40, timer.getDelay() - 5));
            }
        }
    }

    // ================= COLLISION =================
    public void checkCollision() {

        // self collision
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        // wall collision
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) timer.stop();
    }

    // ================= GAME OVER =================
    public void gameOver(Graphics g) {

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over",
                (WIDTH - metrics.stringWidth("Game Over")) / 2,
                HEIGHT / 2 - 20);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score,
                (WIDTH - 100) / 2,
                HEIGHT / 2 + 20);

        g.drawString("Press ENTER to Restart",
                (WIDTH - 220) / 2,
                HEIGHT / 2 + 50);
    }

    // ================= GAME LOOP =================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    // ================= KEY CONTROLS =================
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            // 🔄 Restart
            if (!running && e.getKeyCode() == KeyEvent.VK_ENTER) {
                startGame();
                return;
            }

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> { if (direction != 'R') direction = 'L'; }
                case KeyEvent.VK_RIGHT -> { if (direction != 'L') direction = 'R'; }
                case KeyEvent.VK_UP -> { if (direction != 'D') direction = 'U'; }
                case KeyEvent.VK_DOWN -> { if (direction != 'U') direction = 'D'; }
            }
        }
    }

    // ================= MAIN =================
    public static void main(String[] args) {

        JFrame frame = new JFrame("PRO Snake Game 🐍");

        ProSnakeGame gamePanel = new ProSnakeGame();
        frame.add(gamePanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
