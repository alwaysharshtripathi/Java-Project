# Maze Runner: Escape the Labyrinth

A sophisticated 2D maze game built in Java using Swing/AWT where players navigate through increasingly complex mazes while racing against time. The game features dynamic maze generation, real-time gameplay, and an intuitive user interface.

## 🎮 Game Features

- Dynamic maze generation with increasing complexity
- Time-based gameplay with countdown timer
- Comprehensive score tracking system
- Modern, intuitive user interface with side panel
- Arrow key controls for player movement
- Progressive level system with adaptive difficulty
- Directional arrow indicator for navigation
- Pause/Resume functionality
- Dynamic viewport that follows the player
- Smooth 60 FPS gameplay
- Responsive window with minimum size enforcement

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Any Java IDE (recommended: IntelliJ IDEA, Eclipse, or VS Code with Java extensions)

### Running the Game
1. Clone this repository
2. Open the project in your preferred IDE
3. Compile and run `MazeRunnerGame.java`
4. Use arrow keys to navigate the maze
5. Reach the goal before time runs out!

## 🎯 Game Controls

- ↑ (Up Arrow): Move up
- ↓ (Down Arrow): Move down
- ← (Left Arrow): Move left
- → (Right Arrow): Move right
- Pause Button: Pause/Resume game
- New Game Button: Start a new game
- Exit Button: Return to home screen

## 🏆 Scoring System

- Base points for completing each level
- Time-based bonus points
- Score persistence between levels
- Progressive difficulty rewards
- Level completion tracking

## 🎨 Project Structure

- `MazeRunnerGame.java`: Main game class and entry point
  - Manages window properties and screen transitions
  - Implements card layout for different screens
  - Handles game initialization

- `HomeScreen.java`: Home screen UI and menu
  - Provides game start functionality
  - Manages menu interactions

- `GamePanel.java`: Main gameplay panel
  - Implements core game mechanics
  - Manages real-time rendering
  - Handles player input and collision detection
  - Controls game loop and timers
  - Implements side panel with game information

- `Maze.java`: Maze generation and management
  - Implements recursive backtracker algorithm
  - Handles dynamic maze sizing
  - Manages player movement validation
  - Implements goal placement logic

- `Player.java`: Player state and movement
  - Manages player position
  - Handles movement mechanics
  - Tracks player state

- `GameStateManager.java`: Game state management
  - Tracks game progression
  - Manages scoring system
  - Handles level progression
  - Controls game state (active/paused)

## 🎨 Visual Design

- Modern color scheme:
  - Walls: RGB(44, 62, 80)
  - Paths: RGB(236, 240, 241)
  - Goal: RGB(231, 76, 60)
  - Player: RGB(33, 150, 243)
  - Start: RGB(46, 204, 113)
- Custom-styled UI elements
- Responsive layout
- Clear visual hierarchy
- Dynamic viewport management

## 🛠️ Technical Implementation

This project uses standard Java libraries:
- `javax.swing` for GUI components
- `java.awt` for graphics and event handling
- Custom implementations for:
  - Maze generation algorithm
  - Collision detection
  - Viewport management
  - Game state handling
  - Score management

## 📝 License

This project is open source and available under the MIT License.
