public class GameStateManager {
    private int currentLevel;
    private int score;
    private int timeRemaining;
    private boolean isGameActive;
    private static final int SCORE_PER_LEVEL = 10;
    private static final int BONUS_TIME_MULTIPLIER = 1; // Points per second remaining

    public GameStateManager() {
        resetGame();
    }

    public void resetGame() {
        currentLevel = 1;
        score = 0;
        timeRemaining = getInitialTime();
        isGameActive = false;
    }

    public void startLevel() {
        isGameActive = true;
        timeRemaining = getInitialTime();
    }

    public void completeLevel() {
        isGameActive = false;
        // Add base score for completing the level
        score += SCORE_PER_LEVEL;
        // Add bonus points for remaining time
        score += timeRemaining * BONUS_TIME_MULTIPLIER;
        currentLevel++;
    }

    public void updateTime(int seconds) {
        if (isGameActive) {
            timeRemaining = Math.max(0, timeRemaining - seconds);
            if (timeRemaining == 0) {
                isGameActive = false;
            }
        }
    }

    public boolean isGameOver() {
        return !isGameActive && timeRemaining == 0;
    }

    public boolean isLevelComplete() {
        return !isGameActive && timeRemaining > 0;
    }

    // Getters
    public int getCurrentLevel() { return currentLevel; }
    public int getScore() { return score; }
    public int getTimeRemaining() { return timeRemaining; }
    public boolean isGameActive() { return isGameActive; }
    public int getInitialTime() { return 5 * currentLevel; }
} 