import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {

    private final int WINDOWSIZE;
    private final int HUDSIZE=75;
    private final int TILESIZE;
    private final ArrayList<Tile> TILES=new ArrayList<>();

    private final Thread gameThread;
    private final Input gameInput;
    private final JLabel newGame;
    private final Save saver;

    private Image backGround;

    private boolean isRunning=false;
    private boolean canAcceptInput=true;
    private final int[][] table=new int[4][4];
    private Point direction;
    private Point lastDir;

    private int animAlpha=0;
    private int GameScore;
    private int HighScore;

    GamePanel(Input GameInput,JLabel NewGame,int windowSize) throws IOException {
        gameThread=new Thread(this);
        gameInput=GameInput;
        saver=new Save();
        saver.Read();
        WINDOWSIZE=windowSize;
        TILESIZE=WINDOWSIZE/4;

        newGame=NewGame;
        gameInput.setNewGameLabel(newGame);

        this.setPreferredSize(new Dimension(WINDOWSIZE,WINDOWSIZE+HUDSIZE));
        this.addMouseListener(gameInput);
        direction=new Point(0,0);
        lastDir=direction;

        StartGame();

    }
    public void paintComponent(Graphics g){
        Image image = createImage(WINDOWSIZE, WINDOWSIZE + HUDSIZE);
        Graphics2D graphics2D=(Graphics2D) image.getGraphics();
        Render(graphics2D);
        g.drawImage(image,0,0,null);
    }
    private void Render(Graphics2D g){
        if(backGround!=null)g.drawImage(backGround,0,0,null);
        else RenderBackGround();
        RenderHud(g);
        for (Tile tile : TILES) {
            tile.Render(g);
        }
        if(IsGameOver())GameOverScreen(g);
    }
    private void RenderHud(Graphics2D g){
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int newGameAlpha=0;
        if(gameInput.IsInNewGame())newGameAlpha=50;

        g.setPaint(new Color(0, 0, 0,70+newGameAlpha));
        g.fillRoundRect(newGame.getX(),newGame.getY(),newGame.getWidth(),newGame.getHeight(),3,3);

        g.setPaint(new Color(0, 0, 0,70));
        g.fillRoundRect(20,WINDOWSIZE+3,75,65,3,3);
        g.fillRoundRect(105,WINDOWSIZE+3,90,65,3,3);

        g.setPaint(new Color(215, 202, 192));
        g.setFont(new Font("Century Gothic",Font.BOLD,15));

        int w = g.getFontMetrics().stringWidth("NEW GAME");
        int h = g.getFontMetrics().getHeight();
        g.drawString("NEW GAME",newGame.getX()+w/9,newGame.getY()+h-2);

        g.setFont(new Font("Century Gothic",Font.BOLD,13));
        g.drawString("SCORE",37,WINDOWSIZE+17);
        g.drawString("HIGHSCORE",112,WINDOWSIZE+17);

        g.setFont(new Font("Century Gothic",Font.BOLD,17));
        w = g.getFontMetrics().stringWidth(Integer.toString(GameScore));

        g.drawString(Integer.toString(GameScore),20+75/2-w/2,WINDOWSIZE+45);

        w = g.getFontMetrics().stringWidth(Integer.toString(HighScore));

        g.drawString(Integer.toString(HighScore),105+90/2-w/2,WINDOWSIZE+45);

    }
    private void GameOverScreen(Graphics2D g){
        if(120>animAlpha)animAlpha+=2;
        GradientPaint gradientWhite = new GradientPaint(0, 300, new Color(255,255,255,animAlpha),
                0, WINDOWSIZE, new Color(255, 255, 255,0));
        g.setPaint(gradientWhite);
        g.fillRect(0,300,WINDOWSIZE,100);
        g.setPaint(new Color(255,255,255,animAlpha));
        g.fillRect(0,0,WINDOWSIZE,300);

        g.setFont(new Font("Century Gothic",Font.BOLD,35));

        int w = g.getFontMetrics().stringWidth("G A M E  O V E R");
        int h = g.getFontMetrics().getHeight();

        g.setPaint(new Color(45, 39, 35));
        g.drawString("G A M E  O V E R",WINDOWSIZE/2-w/2,WINDOWSIZE/2+h/3);
    }
    private void RenderBackGround(){
        backGround=createImage(WINDOWSIZE,WINDOWSIZE+HUDSIZE);
        Graphics2D g=(Graphics2D) backGround.getGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint hudcolor = new GradientPaint(0, WINDOWSIZE,(new Color(187, 173, 160)),
                0, WINDOWSIZE+HUDSIZE, new Color(128, 118, 110));

        g.setPaint(hudcolor);
        g.fillRect(0,WINDOWSIZE,WINDOWSIZE,HUDSIZE);

        g.setPaint(new Color(187, 173, 160));
        g.fillRect(0,0,WINDOWSIZE,WINDOWSIZE);
        g.setPaint(new Color(214, 205, 196));
        for (int i = 0; i < WINDOWSIZE; i+=TILESIZE) {
            for (int j = 0; j < WINDOWSIZE; j+=TILESIZE) {
                g.fillRoundRect(i+5,j+5,TILESIZE-10,TILESIZE-10,TILESIZE/6,TILESIZE/6);
            }
        }
    }
    private void NewGame(){
        GameScore=0;
        animAlpha=0;
        TILES.clear();
        FillTable();
        StartGame();
    }
    private void ResetCollidedTiles(){
        for (Tile tile :TILES) {
            tile.collided=false;
        }
    }
    private int CheckCanMoveTiles() {
        FillTable();
        int cantMoveTiles = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int checkedPosScore = TILES.get(table[i][j] - 1).score;
                if (3 > i && 3 > j) {
                    if (TILES.get(table[i + 1][j] - 1).score != checkedPosScore && TILES.get(table[i][j + 1] - 1).score != checkedPosScore)
                        cantMoveTiles++;
                } else if (j == 3 && 3 > i) {
                    if (TILES.get(table[i][j - 1] - 1).score != checkedPosScore && TILES.get(table[i + 1][j] - 1).score != checkedPosScore)
                        cantMoveTiles++;
                } else if (3 > j) {
                    if (TILES.get(table[i - 1][j] - 1).score != checkedPosScore && TILES.get(table[i][j + 1] - 1).score != checkedPosScore)
                        cantMoveTiles++;
                } else {
                    if (TILES.get(table[i - 1][j] - 1).score != checkedPosScore && TILES.get(table[i][j - 1] - 1).score != checkedPosScore)
                        cantMoveTiles++;
                }
            }
        }
        return cantMoveTiles;
    }
    private void ManageInput(){
        if(canAcceptInput){
            if(direction.x!=0 || direction.y!=0)lastDir = direction;
            gameInput.SetDir(new Point(0,0));
            ResetCollidedTiles();
        }
        gameInput.SetCanModDir(canAcceptInput);
        direction = gameInput.GetDirection();

        if(!gameInput.clicked && canAcceptInput) {
            SpawnTile();
            gameInput.clicked=true;
        }
        for (Tile tile :TILES) {
            tile.SetDirection(direction);
        }
    }
    private void Update(){
        if(gameInput.IsClickedNewGame())NewGame();
        int stoppedTiles=0;
        for (int i = 0; i < TILES.size(); i++) {
            if(TILES.get(i).Collision(WINDOWSIZE,TILES))TILES.get(i).Move();
            if(TILES.get(i).IsMoved())stoppedTiles++;
            if(TILES.get(i).needsToRemove) {
                GameScore+=TILES.get(i).score*2;
                if(GameScore>HighScore)HighScore=GameScore;
                TILES.remove(TILES.get(i));
            }
        }
        canAcceptInput=stoppedTiles==TILES.size();
    }
    private void StartGame(){
        isRunning=true;
        for (int i = 0; i < 2; i++) {
            SpawnTile();
        }
        if(!gameThread.isAlive())gameThread.start();
    }
    private void FillTable(){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                table[i][j]=0;
            }
        }
        for (int i = 0; i < TILES.size(); i++) {
            table[TILES.get(i).x / TILESIZE][TILES.get(i).y / TILESIZE] = i+1;
        }
    }
    private boolean IsGameOver(){
        if(TILES.size()==16 && canAcceptInput){
            int cantMoveTiles=CheckCanMoveTiles();
            System.out.println("checking gameover: cantmovetiles: "+ cantMoveTiles+" size "+TILES.size());
            return cantMoveTiles==16;
        }
        else return false;
    }
    private void SpawnTile(){
        if(16>TILES.size()){
            FillTable();
            Random r=new Random();
            int x=r.nextInt(4);
            int y=r.nextInt(4);
            while (table[x][y]!=0){
                x=r.nextInt(4);
                y=r.nextInt(4);
            }
            TILES.add(new Tile(x*TILESIZE,y*TILESIZE,TILESIZE));
        }
    }
    public void SaveGame() throws IOException {
        saver.Write();
    }

    @Override
    public void run() {
        long old=System.nanoTime();
        double fps=80.0;
        double ups=80.0;
        double nsfps=1000000000/fps;
        double nsups=1000000000/ups;
        double deltau=0;
        double deltaf=0;

        while(isRunning){
            long now=System.nanoTime();
            deltaf+=(now-old)/nsfps;
            deltau+=(now-old)/nsups;
            old=now;
            if(deltau>=1){
                Update();
                ManageInput();
                deltau--;
            }
            if(deltaf>=1){
                repaint();
                deltaf--;
            }
        }
    }
    private class Save {
        String path="C:/Users/2048.dat";
        private void Write() throws IOException {
            DataOutputStream dos=new DataOutputStream(new FileOutputStream(path));
            dos.writeInt(HighScore);
        }
        private void Read() throws IOException {
            File f=new File(path);
            if(f.exists()){
                DataInputStream inputStream=new DataInputStream(new FileInputStream(path));
                HighScore=inputStream.readInt();
            }
        }
    }
}