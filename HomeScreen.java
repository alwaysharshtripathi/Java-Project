import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class HomeScreen extends JPanel {
    private final MazeRunnerGame game;
    private final GameStateManager gameStateManager;
    private static final Color BACKGROUND_COLOR = new Color(40, 44, 52);
    private static final Color BUTTON_COLOR = new Color(61, 90, 254);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private Image backgroundImage;

    public HomeScreen(MazeRunnerGame game, GameStateManager gameStateManager) {
        this.game = game;
        this.gameStateManager = gameStateManager;
        setBackground(BACKGROUND_COLOR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        try {
            backgroundImage = ImageIO.read(new File("src/main/resources/images/maze_background.png"));
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
            e.printStackTrace();
        }
        initializeComponents();
    }

    private void initializeComponents() {
        // Add some space at the top
        add(Box.createVerticalStrut(100));

        // Game Title
        JLabel titleLabel = new JLabel("The Wayout!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Escape the Labyrinth");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(subtitleLabel);

        add(Box.createVerticalStrut(80));

        // New Game Button
        JButton newGameButton = createStyledButton("New Game");
        newGameButton.addActionListener(e -> {
            gameStateManager.resetGame();
            game.startGame();
        });
        add(newGameButton);

        add(Box.createVerticalStrut(20));

        // Instructions Button
        JButton instructionsButton = createStyledButton("Instructions");
        instructionsButton.addActionListener(e -> showInstructions());
        add(instructionsButton);

        add(Box.createVerticalStrut(20));

        // Exit Button
        JButton exitButton = createStyledButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(TEXT_COLOR);
        button.setBackground(new Color(0, 0, 0, 100)); // Semi-transparent black background
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TEXT_COLOR, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 50));
        button.setPreferredSize(new Dimension(200, 50));
        button.setOpaque(false); // Make sure the button is non-opaque
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 0, 0, 150)); // Darker semi-transparent on hover
                button.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 0, 0, 100)); // Back to original translucent
                button.repaint();
            }
        });

        return button;
    }

    private void showInstructions() {
        String instructions = """
            🎮 How to Play:
            
            • Use WASD and arrow keys to move through the maze.
                W or ↑ - Up
                A or ← - Left
                S or ↓ - Down
                D or → - Right
            • Reach the goal before time runs out.
            • Each level gets progressively harder
            • Score points for completing levels
            • Bonus points for remaining time
            
            Good luck, Maze Runner!""";

        JOptionPane.showMessageDialog(this,
            instructions,
            "Game Instructions",
            JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background image if available
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        
        g2d.dispose();
    }
} 