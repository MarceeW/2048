import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Input implements MouseListener {

    private Point oldPos;
    private Point direction = new Point(0, 0);
    private JLabel newGame;

    public boolean clicked = true;
    private boolean canModDir = true;
    private boolean isInnewGameLabel = false;
    private boolean isNewGameClicked = false;

    public void setNewGameLabel(JLabel label) {
        this.newGame = label;
    }

    public boolean IsInNewGame() {
        return isInnewGameLabel;
    }

    public boolean IsClickedNewGame() {
        if (isNewGameClicked) {
            isNewGameClicked = false;
            return true;
        }
        return false;
    }

    public Point GetDirection() {
        return direction;
    }

    public void SetCanModDir(boolean set) {
        canModDir = set;
    }

    public void SetDir(Point set) {
        direction = set;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().equals(newGame)) isNewGameClicked = true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        oldPos = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point newPos = e.getPoint();
        if (canModDir) {
            int diffx = newPos.x - oldPos.x;
            int diffy = newPos.y - oldPos.y;
            if (0 > diffx && 0 > diffy) {
                if (diffx > diffy) {
                    direction.x = 0;
                    direction.y = -1;
                } else {
                    direction.x = -1;
                    direction.y = 0;
                }
            } else if (0 > diffx) {
                diffx *= -1;
                if (diffx > diffy) {
                    direction.x = -1;
                    direction.y = 0;
                } else {
                    direction.x = 0;
                    direction.y = 1;
                }
            } else if (0 > diffy) {
                diffy *= -1;
                if (diffx > diffy) {
                    direction.x = 1;
                    direction.y = 0;
                } else {
                    direction.x = 0;
                    direction.y = -1;
                }
            } else if (diffx > diffy) {
                direction.x = 1;
                direction.y = 0;
            } else if (diffy > diffx) {
                direction.x = 0;
                direction.y = 1;
            }
        }
        clicked = newPos.x == oldPos.x && newPos.y == oldPos.y;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource().equals(newGame)) isInnewGameLabel = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource().equals(newGame)) isInnewGameLabel = false;
    }
}