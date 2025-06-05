import java.util.Random;
import java.util.ArrayList;
import java.awt.Point;
import java.util.List;

public class Maze {
    public static final int WALL = 1;
    public static final int PATH = 0;
    public static final int GOAL = 2;
    public static final int PLAYER = 3;
    public static final int START = 4;

    private int[][] grid;
    private int width;
    private int height;
    private Player player;
    private Point goalPosition;
    private Point startPosition;
    private Random random;
    private Point farthestPoint; // Track the farthest point from start for goal placement

    public Maze(int level) {
        // Start with 7x7 for level 1, add 2 cells every level
        // Ensure odd dimensions
        width = 7 + (level - 1) * 2;
        height = 7 + (level - 1) * 2;
        width = width % 2 == 0 ? width + 1 : width;
        height = height % 2 == 0 ? height + 1 : height;
        
        grid = new int[height][width];
        random = new Random();
        farthestPoint = new Point(1, 1); // Initialize with start position
        generateMaze();
    }

    private void generateMaze() {
        // Initialize with walls
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = WALL;
            }
        }

        // Create outer walls
        for (int x = 0; x < width; x++) {
            grid[0][x] = WALL;
            grid[height - 1][x] = WALL;
        }
        for (int y = 0; y < height; y++) {
            grid[y][0] = WALL;
            grid[y][width - 1] = WALL;
        }

        // Start position at (1,1)
        startPosition = new Point(1, 1);
        grid[1][1] = PATH;
        player = new Player(1, 1);

        // Generate maze using recursive backtracker
        boolean[][] visited = new boolean[height][width];
        visited[1][1] = true;
        carvePath(1, 1, visited);

        // Find the farthest point from start using a more robust method
        findFarthestPoint();

        // Place goal at the farthest point
        grid[farthestPoint.y][farthestPoint.x] = GOAL;
        goalPosition = new Point(farthestPoint);

        // Set start position
        grid[startPosition.y][startPosition.x] = START;
    }

    private void findFarthestPoint() {
        // Use a queue for breadth-first search to find the farthest point
        boolean[][] visited = new boolean[height][width];
        Point[][] parent = new Point[height][width];
        int[][] distance = new int[height][width];
        
        // Queue for BFS
        java.util.Queue<Point> queue = new java.util.LinkedList<>();
        queue.add(startPosition);
        visited[startPosition.y][startPosition.x] = true;
        distance[startPosition.y][startPosition.x] = 0;
        
        // Directions: right, down, left, up
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        
        Point currentFarthest = startPosition;
        int maxDistance = 0;
        
        while (!queue.isEmpty()) {
            Point current = queue.poll();
            
            // Check all four directions
            for (int[] dir : directions) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];
                
                // If valid position, not visited, and is a path
                if (isValidPosition(newX, newY) && !visited[newY][newX] && grid[newY][newX] == PATH) {
                    visited[newY][newX] = true;
                    parent[newY][newX] = current;
                    distance[newY][newX] = distance[current.y][current.x] + 1;
                    
                    // Update the farthest point if this is further
                    if (distance[newY][newX] > maxDistance) {
                        maxDistance = distance[newY][newX];
                        currentFarthest = new Point(newX, newY);
                    }
                    
                    queue.add(new Point(newX, newY));
                }
            }
        }
        
        // Set the farthest point
        farthestPoint = currentFarthest;
    }

    private void carvePath(int x, int y, boolean[][] visited) {
        // Directions: right, down, left, up
        int[][] directions = {{2, 0}, {0, 2}, {-2, 0}, {0, -2}};
        shuffleArray(directions);

        // Try each direction
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            // Check if the new position is valid and unvisited
            if (isValidPosition(newX, newY) && !visited[newY][newX]) {
                // Carve path by setting the wall between current and new position to PATH
                int wallX = x + dir[0] / 2;
                int wallY = y + dir[1] / 2;
                grid[wallY][wallX] = PATH;
                grid[newY][newX] = PATH;
                visited[newY][newX] = true;

                // Recursively continue from the new position
                carvePath(newX, newY, visited);
            }
        }
    }

    private void shuffleArray(int[][] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private boolean isValidPosition(int x, int y) {
        return x > 0 && x < width - 1 && y > 0 && y < height - 1;
    }

    public boolean movePlayer(int dx, int dy) {
        int newX = player.getPosition().x + dx;
        int newY = player.getPosition().y + dy;

        if (isValidPosition(newX, newY) && grid[newY][newX] != WALL) {
            player.move(dx, dy);
            return true;
        }
        return false;
    }

    public boolean isGoalReached() {
        return player.getPosition().equals(goalPosition);
    }

    // Getters
    public int[][] getGrid() { return grid; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Point getPlayerPosition() { return player.getPosition(); }
    public Point getGoalPosition() { return goalPosition; }
    public Point getStartPosition() { return startPosition; }
} 