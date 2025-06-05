import java.awt.Point;

public class Player {
    private Point position;

    public Player(int x, int y) {
        this.position = new Point(x, y);
    }

    public void move(int dx, int dy) {
        position.translate(dx, dy);
    }

    public Point getPosition() {
        return new Point(position);  // Return a copy to prevent external modification
    }

    public void setPosition(int x, int y) {
        position.setLocation(x, y);
    }
} 