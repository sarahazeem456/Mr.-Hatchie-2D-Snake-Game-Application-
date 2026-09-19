import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private final int unitSize = 25;
    private int screenWidth, screenHeight;
    private Snake snake;
    private Point food;
    private Point blueFood;
    private Point crab;
    private boolean running = false;
    private boolean showMenu = true;
    private boolean paused = false;
    private int score = 0;
    private int highScore = 0;
    private String snakeName = "Unnamed";
    private String snakeSkin = "Classic";
    private final Random rand = new Random();
    private long lastCrabTime = 0;
    private long lastBlueFoodTime = 0;
    private final long crabInterval = 10000;
    private final long blueFoodDuration = 7000;
    private Rectangle playAgainButton;
    private Font bubbleFont = new Font("Comic Sans MS", Font.BOLD, 60);

    public SnakeGame(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;
        setPreferredSize(screenSize);
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(120, this);
        timer.start();
        snake = new Snake();
        spawnFood();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (showMenu && playAgainButton != null && playAgainButton.contains(e.getPoint())) {
                    snakeName = JOptionPane.showInputDialog(null, "Enter your snake's name:", "Name Snake", JOptionPane.PLAIN_MESSAGE);
                    if (snakeName == null || snakeName.trim().isEmpty()) snakeName = "Unnamed";
                    Object[] skins = {"Classic", "Fire", "Forest", "Rainbow"};
                    snakeSkin = (String) JOptionPane.showInputDialog(null, "Choose Snake Skin:", "Snake Skin", JOptionPane.PLAIN_MESSAGE, null, skins, skins[0]);
                    if (snakeSkin == null) snakeSkin = "Classic";
                    showMenu = false;
                    running = true;
                    resetGame();
                } else if (!running && !showMenu && playAgainButton != null && playAgainButton.contains(e.getPoint())) {
                    Object[] skins = {"Classic", "Fire", "Forest", "Rainbow"};
                    snakeSkin = (String) JOptionPane.showInputDialog(null, "Choose Snake Skin:", "Snake Skin", JOptionPane.PLAIN_MESSAGE, null, skins, skins[0]);
                    if (snakeSkin == null) snakeSkin = "Classic";
                    showMenu = false;
                    running = true;
                    resetGame();
                }
            }
        });
    }

    public void resetGame() {
        snake = new Snake();
        score = 0;
        spawnFood();
        crab = null;
        blueFood = null;
        lastCrabTime = System.currentTimeMillis();
        lastBlueFoodTime = System.currentTimeMillis();
    }

    public void spawnFood() {
        food = randomPoint();
    }

    public void spawnBlueFood() {
        blueFood = randomPoint();
        lastBlueFoodTime = System.currentTimeMillis();
    }

    public void spawnCrab() {
        crab = randomPoint();
    }

    private Point randomPoint() {
        int cols = screenWidth / unitSize;
        int rows = screenHeight / unitSize;
        int x = rand.nextInt(cols) * unitSize;
        int y = rand.nextInt(rows) * unitSize;
        return new Point(x, y);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (showMenu) {
            drawMenu(g);
        } else if (running) {
            drawGame(g);
        } else {
            drawGameOver(g);
        }
    }

    public void drawMenu(Graphics g) {
        setBackground(new Color(255, 255, 204));
        int bounce = (int)(10 * Math.sin(System.currentTimeMillis() * 0.005));
        g.setColor(new Color(173, 216, 230));
        g.setFont(bubbleFont);

        String title = "Mr. Hatchie";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        int titleY = 150 + bounce;
        g.drawString(title, titleX, titleY);

        int buttonWidth = 200;
        int buttonHeight = 60;
        int buttonX = (getWidth() - buttonWidth) / 2;
        int buttonY = titleY + 80;

        playAgainButton = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        g.setColor(Color.darkGray);
        g.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 20, 20);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Play", buttonX + 65, buttonY + 40);
    }

    public void drawGame(Graphics g) {
        drawCheckerboard(g);
        g.setColor(new Color(139, 179, 139));
        g.fillOval(food.x, food.y, unitSize, unitSize);

        if (blueFood != null) {
            g.setColor(Color.blue);
            g.fillOval(blueFood.x, blueFood.y, unitSize, unitSize);
        }

        if (crab != null) {
            g.setColor(Color.orange);
            g.fillOval(crab.x, crab.y, unitSize, unitSize);
            g.setColor(Color.black);
            g.drawString("🦀", crab.x + 4, crab.y + 18);
        }

        snake.draw(g);

        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Snake: " + snakeName, getWidth() - 200, 20);
        if (paused) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Paused", getWidth() / 2 - 70, getHeight() / 2);
        }
    }

    public void drawCheckerboard(Graphics g) {
        Color c1 = new Color(255, 255, 204);
        Color c2 = new Color(240, 240, 190);
        for (int y = 0; y < screenHeight; y += unitSize) {
            for (int x = 0; x < screenWidth; x += unitSize) {
                g.setColor(((x + y) / unitSize) % 2 == 0 ? c1 : c2);
                g.fillRect(x, y, unitSize, unitSize);
            }
        }
    }

    public void drawGameOver(Graphics g) {
        setBackground(new Color(255, 255, 204));
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Game Over!", getWidth() / 2 - 120, 250);
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("High Score: " + highScore, getWidth() / 2 - 100, 300);
        g.drawString(snakeName + "'s Score: " + score, getWidth() / 2 - 120, 340);
        playAgainButton = new Rectangle(getWidth() / 2 - 100, 400, 200, 50);
        g.setColor(Color.magenta);
        g.fillRoundRect(playAgainButton.x, playAgainButton.y, playAgainButton.width, playAgainButton.height, 20, 20);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Play Again", playAgainButton.x + 40, playAgainButton.y + 30);
    }

    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            snake.move();

            // Food collision
            if (snake.getHead().equals(food)) {
                snake.grow();
                score++;
                if (score > highScore) highScore = score;
                spawnFood();
            }

            // Blue food collision
            if (blueFood != null && snake.getHead().equals(blueFood)) {
                snake.grow();
                snake.grow();
                snake.grow();
                score += 3;
                if (score > highScore) highScore = score;
                blueFood = null;
            }

            // Blue food timers
            if (System.currentTimeMillis() - lastBlueFoodTime > 12000) {
                spawnBlueFood();
            } else if (blueFood != null && System.currentTimeMillis() - lastBlueFoodTime > blueFoodDuration) {
                blueFood = null;
            }

            // Crab collision (Exact tile match or physical overlap)
            if (crab != null) {
                Rectangle headRect = new Rectangle(snake.getHead().x, snake.getHead().y, unitSize, unitSize);
                Rectangle crabRect = new Rectangle(crab.x, crab.y, unitSize, unitSize);

                if (headRect.intersects(crabRect)) {
                    running = false;
                }
            }

            // Wall collision and self-collision
            if (snake.isSelfCollision() || 
                snake.getHead().x < 0 || snake.getHead().y < 0 || 
                snake.getHead().x >= screenWidth || snake.getHead().y >= screenHeight) {
                running = false;
            }

            // Crab spawn timer
            if (System.currentTimeMillis() - lastCrabTime > crabInterval) {
                spawnCrab();
                lastCrabTime = System.currentTimeMillis();
            }
        }
        repaint();
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> snake.setDirection("UP");
            case KeyEvent.VK_DOWN -> snake.setDirection("DOWN");
            case KeyEvent.VK_LEFT -> snake.setDirection("LEFT");
            case KeyEvent.VK_RIGHT -> snake.setDirection("RIGHT");
            case KeyEvent.VK_SPACE -> paused = !paused;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    class Snake {
        private ArrayList<Point> body;
        private String direction = "RIGHT";

        public Snake() {
            body = new ArrayList<>();
            body.add(new Point(100, 100));
            body.add(new Point(75, 100));
            body.add(new Point(50, 100));
        }

        public void move() {
            Point head = new Point(getHead());
            switch (direction) {
                case "UP" -> head.y -= unitSize;
                case "DOWN" -> head.y += unitSize;
                case "LEFT" -> head.x -= unitSize;
                case "RIGHT" -> head.x += unitSize;
            }
            body.add(0, head);
            body.remove(body.size() - 1);
        }

        public void grow() {
            body.add(new Point(body.get(body.size() - 1)));
        }

        public void draw(Graphics g) {
            for (int i = 0; i < body.size(); i++) {
                Point p = body.get(i);
                if (i == 0) {
                    g.setColor(Color.black);
                    g.fillOval(p.x, p.y, unitSize, unitSize);
                    g.setColor(Color.white);
                    int eyeSize = unitSize / 4;
                    g.fillOval(p.x + 4, p.y + 4, eyeSize, eyeSize);
                    g.fillOval(p.x + unitSize - eyeSize - 4, p.y + 4, eyeSize, eyeSize);
                    g.setColor(Color.black);
                    g.fillOval(p.x + 6, p.y + 6, 4, 4);
                    g.fillOval(p.x + unitSize - 10, p.y + 6, 4, 4);
                } else {
                    switch (snakeSkin) {
                        case "Classic" -> g.setColor(Color.black);
                        case "Fire" -> g.setColor(Color.red);
                        case "Forest" -> g.setColor(new Color(34, 139, 34));
                        case "Rainbow" -> g.setColor(Color.getHSBColor((float) i / body.size(), 1.0f, 1.0f));
                    }
                    g.fillRect(p.x, p.y, unitSize, unitSize);
                }
            }
        }

        public Point getHead() {
            return body.get(0);
        }

        public void setDirection(String newDir) {
            if ((newDir.equals("UP") && !direction.equals("DOWN")) ||
                (newDir.equals("DOWN") && !direction.equals("UP")) ||
                (newDir.equals("LEFT") && !direction.equals("RIGHT")) ||
                (newDir.equals("RIGHT") && !direction.equals("LEFT"))) {
                direction = newDir;
            }
        }

        public boolean isSelfCollision() {
            Point head = getHead();
            for (int i = 1; i < body.size(); i++) {
                if (head.equals(body.get(i))) return true;
            }
            return false;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Mr. Hatchie - Snake Game");
        SnakeGame game = new SnakeGame(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.add(game);
        frame.pack();
        frame.setVisible(true);
    }
}