import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.Timer;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private final MazeRunnerGame game;
    private final GameStateManager gameStateManager;
    private Maze maze;
    private Timer gameTimer;
    private Timer updateTimer;
    private static final int CELL_SIZE = 40; // Fixed cell size as before
    private static final int UPDATE_INTERVAL = 1000; // 1 second for timer updates
    private static final int SIDE_PANEL_WIDTH = 200; // Width of the side panel
    private static final Color WALL_COLOR = new Color(44, 62, 80);
    private static final Color PATH_COLOR = new Color(236, 240, 241);
    private static final Color GOAL_COLOR = new Color(231, 76, 60);
    private static final Color PLAYER_COLOR = new Color(33, 150, 243);
    private static final Color START_COLOR = new Color(46, 204, 113);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color SIDE_PANEL_COLOR = new Color(0x1a1a2e);
    private static final Color SIDE_PANEL_TEXT_COLOR = new Color(236, 240, 241);
    private static final Color GRID_LINE_COLOR = new Color(20, 20, 20); // Darker grid lines
    private static final Color ARROW_COLOR = new Color(255, 0, 0, 200); // Brighter red
    private static final int ARROW_SIZE = 40; // Increased arrow size

    // Remove viewport smoothing as we want immediate centering
    private Point viewportOffset;
    private JPanel arrowPanel; // Add this field to access the panel

    private JButton pauseButton;
    private JButton newGameButton;
    private JButton exitButton;
    private boolean isPaused = false;

    private JPanel sidePanel;
    private JLabel levelLabel;
    private JLabel scoreLabel;
    private JLabel timeLabel;

    public GamePanel(MazeRunnerGame game, GameStateManager gameStateManager) {
        this.game = game;
        this.gameStateManager = gameStateManager;
        setFocusable(true);
        addKeyListener(this);
        setBackground(PATH_COLOR);
        setLayout(new BorderLayout());

        // Create and add the side panel (with buttons shifted to bottom)
        createSidePanel();

        // Initialize timers
        gameTimer = new Timer(UPDATE_INTERVAL, this);
        updateTimer = new Timer(16, e -> {
            if (!isPaused) {
                updateViewport();
                updateSidePanel(); // Update the side panel info
                repaint();
            }
        }); // ~60 FPS with viewport updates
    }

    public void stopTimers() {
        if (gameTimer != null) gameTimer.stop();
        if (updateTimer != null) updateTimer.stop();
    }

    private void createSidePanel() {
        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, getHeight()));
        sidePanel.setBackground(SIDE_PANEL_COLOR);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Title
        JLabel titleLabel = new JLabel("Game Info");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(SIDE_PANEL_TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(titleLabel);
        sidePanel.add(Box.createVerticalStrut(20));

        // Divider line
        JSeparator divider = new JSeparator();
        divider.setForeground(SIDE_PANEL_TEXT_COLOR.darker());
        divider.setMaximumSize(new Dimension(SIDE_PANEL_WIDTH - 20, 2));
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(divider);
        sidePanel.add(Box.createVerticalStrut(20));

        // Game info labels
        Font infoFont = new Font("Arial", Font.BOLD, 24);
        levelLabel = createInfoLabel("Level: 1", infoFont);
        scoreLabel = createInfoLabel("Score: 0", infoFont);
        timeLabel = createInfoLabel("Time: 60s", infoFont);

        sidePanel.add(levelLabel);
        sidePanel.add(Box.createVerticalStrut(15));
        sidePanel.add(scoreLabel);
        sidePanel.add(Box.createVerticalStrut(15));
        sidePanel.add(timeLabel);
        sidePanel.add(Box.createVerticalStrut(30));

        // Add directional arrow panel
        arrowPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (maze != null) {
                    drawDirectionalArrow(g);
                }
            }
        };
        arrowPanel.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH - 20, 120));
        arrowPanel.setMaximumSize(new Dimension(SIDE_PANEL_WIDTH - 20, 120));
        arrowPanel.setBackground(SIDE_PANEL_COLOR);
        arrowPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(arrowPanel);
        sidePanel.add(Box.createVerticalStrut(20));

        // Insert a glue spacer to push the buttons to the bottom
        sidePanel.add(Box.createVerticalGlue());

        // Style for buttons
        Dimension buttonSize = new Dimension(160, 40);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        // Create and style buttons
        pauseButton = createStyledButton("|| Pause", buttonSize, buttonFont);
        newGameButton = createStyledButton(" New Game", buttonSize, buttonFont);
        exitButton = createStyledButton("⌂ Exit", buttonSize, buttonFont);

        // Add action listeners
        pauseButton.addActionListener(e -> togglePause());
        newGameButton.addActionListener(e -> {
            if (isPaused) togglePause();
            gameStateManager.resetGame();
            startNewGame();
        });
        exitButton.addActionListener(e -> game.showHomeScreen());

        // Add buttons to panel with spacing
        sidePanel.add(pauseButton);
        sidePanel.add(Box.createVerticalStrut(15));
        sidePanel.add(newGameButton);
        sidePanel.add(Box.createVerticalStrut(15));
        sidePanel.add(exitButton);

        // Add side panel to the main panel
        add(sidePanel, BorderLayout.EAST);
    }

    private JLabel createInfoLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(SIDE_PANEL_TEXT_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton createStyledButton(String text, Dimension size, Font font) {
        JButton button = new JButton(text);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setFont(font);
        button.setForeground(SIDE_PANEL_TEXT_COLOR);
        button.setBackground(SIDE_PANEL_COLOR.darker());
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SIDE_PANEL_TEXT_COLOR, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SIDE_PANEL_COLOR.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(SIDE_PANEL_COLOR.darker());
            }
        });
        
        return button;
    }

    private void updateSidePanel() {
        if (levelLabel != null && scoreLabel != null && timeLabel != null) {
            levelLabel.setText("Level: " + gameStateManager.getCurrentLevel());
            scoreLabel.setText("Score: " + gameStateManager.getScore());
            timeLabel.setText("Time: " + gameStateManager.getTimeRemaining() + "s");
        }
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            pauseButton.setText("> Resume");
            gameTimer.stop();
            updateTimer.stop();
            // Ensure the game panel keeps focus when paused
            requestFocusInWindow();
        } else {
            pauseButton.setText("|| Pause");
            gameTimer.start();
            updateTimer.start();
            // Ensure the game panel gets focus when resumed
            requestFocusInWindow();
        }
    }

    private void updateViewport() {
        // Always center on player
        centerViewportOnPlayer();
    }

    private int getMazeAreaWidth() {
        return getWidth() - SIDE_PANEL_WIDTH;
    }

    public void startNewGame() {
        maze = new Maze(gameStateManager.getCurrentLevel());
        
        // Force immediate viewport update to player position
        SwingUtilities.invokeLater(() -> {
            centerViewportOnPlayer();
            repaint();
        });
        
        gameStateManager.startLevel();
        gameTimer.start();
        updateTimer.start();
        requestFocusInWindow();
    }

    private void centerViewportOnPlayer() {
        if (maze == null) return;
        
        Point playerPos = maze.getPlayerPosition();
        int mazeAreaWidth = getMazeAreaWidth();
        int mazeAreaHeight = getHeight();
        int totalMazeWidth = maze.getWidth() * CELL_SIZE;
        int totalMazeHeight = maze.getHeight() * CELL_SIZE;
        
        // Calculate the pixel position of the player
        int playerPixelX = playerPos.x * CELL_SIZE;
        int playerPixelY = playerPos.y * CELL_SIZE;
        
        // Calculate the viewport offset needed to center the player
        int targetOffsetX = playerPixelX - (mazeAreaWidth / 2);
        int targetOffsetY = playerPixelY - (mazeAreaHeight / 2);
        
        // Calculate maximum allowed offsets
        int maxOffsetX = Math.max(0, totalMazeWidth - mazeAreaWidth);
        int maxOffsetY = Math.max(0, totalMazeHeight - mazeAreaHeight);
        
        // Ensure the player is always visible by adjusting the viewport
        // Add a margin to keep the player away from the edges
        int margin = CELL_SIZE * 2;
        
        // Clamp the viewport to keep the player visible with margins
        int clampedOffsetX = Math.max(0, Math.min(targetOffsetX, maxOffsetX));
        int clampedOffsetY = Math.max(0, Math.min(targetOffsetY, maxOffsetY));
        
        // Adjust the viewport to ensure player stays within visible area with margins
        if (playerPixelX - clampedOffsetX < margin) {
            clampedOffsetX = Math.max(0, playerPixelX - margin);
        } else if (playerPixelX - clampedOffsetX > mazeAreaWidth - margin) {
            clampedOffsetX = Math.min(maxOffsetX, playerPixelX - mazeAreaWidth + margin);
        }
        
        if (playerPixelY - clampedOffsetY < margin) {
            clampedOffsetY = Math.max(0, playerPixelY - margin);
        } else if (playerPixelY - clampedOffsetY > mazeAreaHeight - margin) {
            clampedOffsetY = Math.min(maxOffsetY, playerPixelY - mazeAreaHeight + margin);
        }
        
        // Create new viewport offset
        viewportOffset = new Point(clampedOffsetX, clampedOffsetY);
        
        // Force immediate repaint
        repaint();
    }

    private void drawDirectionalArrow(Graphics g) {
        if (maze == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Get player and goal positions
        Point playerPos = maze.getPlayerPosition();
        Point goalPos = maze.getGoalPosition();

        // Calculate angle between player and goal
        double dx = goalPos.x - playerPos.x;
        double dy = goalPos.y - playerPos.y;
        double angle = Math.atan2(dy, dx);

        // Calculate center of the arrow panel
        int centerX = arrowPanel.getWidth() / 2;
        int centerY = arrowPanel.getHeight() / 2;

        // Save the original transform
        AffineTransform originalTransform = g2d.getTransform();

        // Draw a circle background
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillOval(centerX - ARROW_SIZE, centerY - ARROW_SIZE, 
                    ARROW_SIZE * 2, ARROW_SIZE * 2);

        // Translate to center and rotate
        g2d.translate(centerX, centerY);
        g2d.rotate(angle);

        // Draw a simple triangular arrow
        g2d.setColor(ARROW_COLOR);
        int[] xPoints = {ARROW_SIZE/2, -ARROW_SIZE/2, -ARROW_SIZE/2};
        int[] yPoints = {0, -ARROW_SIZE/3, ARROW_SIZE/3};
        g2d.fillPolygon(xPoints, yPoints, 3);

        // Restore original transform
        g2d.setTransform(originalTransform);

        // Add a label
        g2d.setColor(SIDE_PANEL_TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        String label = "Goal Direction";
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        g2d.drawString(label, (arrowPanel.getWidth() - labelWidth) / 2, 
                      centerY + ARROW_SIZE + 20);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill the maze area (excluding the side panel) with black
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getMazeAreaWidth(), getHeight());

        // Draw maze in the remaining space
        if (maze != null) {
            int mazeAreaWidth = getMazeAreaWidth();
            int mazeAreaHeight = getHeight();
            int totalMazeWidth = maze.getWidth() * CELL_SIZE;
            int totalMazeHeight = maze.getHeight() * CELL_SIZE;

            // Calculate center offsets only if maze is smaller than viewport
            int centerX = Math.max(0, (mazeAreaWidth - totalMazeWidth) / 2);
            int centerY = Math.max(0, (mazeAreaHeight - totalMazeHeight) / 2);

            // Save the original transform
            AffineTransform originalTransform = g2d.getTransform();

            // First translate to center the maze in the window (only if maze is smaller)
            if (totalMazeWidth < mazeAreaWidth && totalMazeHeight < mazeAreaHeight) {
                g2d.translate(centerX, centerY);
            }

            // Then apply viewport offset to center on player
            if (viewportOffset != null) {
                g2d.translate(-viewportOffset.x, -viewportOffset.y);
            }

            // Draw maze background
            g2d.setColor(PATH_COLOR);
            g2d.fillRect(0, 0, totalMazeWidth, totalMazeHeight);

            // Draw grid lines
            g2d.setColor(GRID_LINE_COLOR);
            g2d.setStroke(new BasicStroke(2.0f));
            for (int x = 0; x <= maze.getWidth(); x++) {
                g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, totalMazeHeight);
            }
            for (int y = 0; y <= maze.getHeight(); y++) {
                g2d.drawLine(0, y * CELL_SIZE, totalMazeWidth, y * CELL_SIZE);
            }
            g2d.setStroke(new BasicStroke(1.0f));

            // Draw maze cells
            int[][] grid = maze.getGrid();
            for (int y = 0; y < maze.getHeight(); y++) {
                for (int x = 0; x < maze.getWidth(); x++) {
                    int cellX = x * CELL_SIZE;
                    int cellY = y * CELL_SIZE;

                    if (maze.getStartPosition().x == x && maze.getStartPosition().y == y) {
                        g2d.setColor(START_COLOR);
                    } else if (maze.getGoalPosition().x == x && maze.getGoalPosition().y == y) {
                        g2d.setColor(GOAL_COLOR);
                    } else {
                        switch (grid[y][x]) {
                            case Maze.WALL:
                                g2d.setColor(WALL_COLOR);
                                break;
                            default:
                                g2d.setColor(PATH_COLOR);
                                break;
                        }
                    }
                    g2d.fillRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
                }
            }

            // Draw player
            Point player = maze.getPlayerPosition();
            int px = player.x * CELL_SIZE;
            int py = player.y * CELL_SIZE;
            g2d.setColor(PLAYER_COLOR);
            int margin = CELL_SIZE / 6;
            g2d.fillOval(px + margin, py + margin, CELL_SIZE - 2 * margin, CELL_SIZE - 2 * margin);

            // Restore the original transform
            g2d.setTransform(originalTransform);
        }

        // Update the arrow panel
        if (arrowPanel != null) {
            arrowPanel.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == gameTimer) {
            gameStateManager.updateTime(1);
            
            if (gameStateManager.isGameOver()) {
                gameTimer.stop();
                updateTimer.stop();
                showGameOverDialog();
            }
        }
    }

    private void showGameOverDialog() {
        String message = "Time's up! Your score: " + gameStateManager.getScore();
        int choice = JOptionPane.showConfirmDialog(this,
            message + "\nWould you like to try again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            // Instead of resetting the game, just restart the current level
            gameStateManager.startLevel();
            startNewGame();
        } else {
            game.showHomeScreen();
        }
    }

    private void checkLevelComplete() {
        if (maze.isGoalReached()) {
            // Stop timers but keep updating the display
            gameTimer.stop();
            updateTimer.stop();
            
            // Force one final repaint to show player on goal
            repaint();
            
            // Add a minimal delay to show the player on goal (reduced to 50ms)
            Timer delayTimer = new Timer(50, e -> {
                // Calculate points before completing level
                int basePoints = 10;  // Base points for completing level
                int timeBonus = gameStateManager.getTimeRemaining();  // Bonus points for remaining time
                int totalPoints = basePoints + timeBonus;
                
                // Complete the level and update score
                gameStateManager.completeLevel();
                
                // Create detailed score message
                String message = String.format("""
                    🎉 Level Complete! 🎉
                    
                    Score Breakdown:
                    • Level Completion: +%d points
                    • Time Bonus: +%d points
                    • Points Earned: %d points
                    
                    Total Score: %d points
                    
                    Continue to next level?""",
                    basePoints, timeBonus, totalPoints, gameStateManager.getScore());
                
                int choice = JOptionPane.showConfirmDialog(this,
                    message,
                    "Level Complete",
                    JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    startNewGame();
                } else {
                    game.showHomeScreen();
                }
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Always handle ESC key regardless of game state
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            togglePause();
            return;
        }
        
        // Only handle other keys if game is active and not paused
        if (!gameStateManager.isGameActive() || isPaused) return;

        int dx = 0, dy = 0;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                dy = -1;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                dy = 1;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                dx = -1;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                dx = 1;
                break;
            case KeyEvent.VK_P:
                togglePause();
                return;
        }

        if (maze.movePlayer(dx, dy)) {
            // Force immediate viewport update after movement
            SwingUtilities.invokeLater(() -> {
                centerViewportOnPlayer();
                repaint();
            });
            checkLevelComplete();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
} 