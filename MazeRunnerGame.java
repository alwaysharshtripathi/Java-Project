import javax.swing.*;
import java.awt.*;

public class MazeRunnerGame extends JFrame {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final String GAME_TITLE = "Maze Runner: Escape the Labyrinth";
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private HomeScreen homeScreen;
    private GamePanel gamePanel;
    private GameStateManager gameStateManager;

    public MazeRunnerGame() {
        // Set up the main window
        setTitle(GAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(800, 600)); // Set minimum window size
        setResizable(true); // Allow window resizing
        setLocationRelativeTo(null);

        // Initialize components
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        gameStateManager = new GameStateManager();
        homeScreen = new HomeScreen(this, gameStateManager);
        gamePanel = new GamePanel(this, gameStateManager);

        // Add panels to card layout
        mainPanel.add(homeScreen, "HOME");
        mainPanel.add(gamePanel, "GAME");

        // Add main panel to frame
        add(mainPanel);

        // Show home screen initially
        showHomeScreen();
    }

    public void showHomeScreen() {
        gamePanel.stopTimers();
        cardLayout.show(mainPanel, "HOME");
        homeScreen.requestFocusInWindow();
    }

    public void startGame() {
        cardLayout.show(mainPanel, "GAME");
        gamePanel.startNewGame();
        gamePanel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        // Run the game on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MazeRunnerGame game = new MazeRunnerGame();
            game.setVisible(true);
        });
    }
} 