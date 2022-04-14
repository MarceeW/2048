import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GameFrame extends JFrame implements WindowListener {

    private final GamePanel gamePanel;

    GameFrame() throws IOException {
        Input gameInput = new Input();

        JLabel newGame = new JLabel();
        newGame.addMouseListener(gameInput);
        int WINDOWSIZE = 400;
        newGame.setBounds(WINDOWSIZE -150, WINDOWSIZE +25,100,25);

        gamePanel=new GamePanel(gameInput, newGame, WINDOWSIZE);

        this.setIconImage(CreateIcon());
        this.setTitle("2048");
        this.setFocusable(true);
        this.addWindowListener(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(newGame);
        this.add(gamePanel);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }
    private Image CreateIcon(){
        int size=300;
        BufferedImage retImage=new BufferedImage(size, size,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=(Graphics2D) retImage.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setPaint(Tile.colors[4]);
        g.fillRoundRect(0,0,size,size,size/2,size/2);
        g.setFont(new Font("Century Gothic",Font.BOLD,(int)(size*0.4)));

        int w = g.getFontMetrics().stringWidth("2048");
        int h = g.getFontMetrics().getHeight();

        g.setPaint(Color.WHITE);
        g.drawString("2048",size/2-w/2,(int)(size*0.466)+h/3);
        return retImage;
    }
    public static void main(String[] args) throws IOException {
        new GameFrame();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            gamePanel.SaveGame();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}