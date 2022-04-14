import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


public class Tile extends Rectangle {

    public static final Color[] colors={
            new Color(238,228,218), //2
            new Color(237,224,200), //4
            new Color(242,177,121), //8
            new Color(245,149,99), //16
            new Color(246,124,95), //32
            new Color(246,94,59), //64
            new Color(237,207,114), //128
            new Color(237,204,97), //256
            new Color(237,200,80), //512
            new Color(237,197,63), //1024
            new Color(237,194,46), //2048
            new Color(62,57,51), //4096
            new Color(65, 76, 82), //8192
            new Color(105, 70, 203), //16384
    };

    public static final Color[] fannikaColors={
            new Color(179, 152, 198), //2
            new Color(209, 150, 252), //4
            new Color(140, 74, 191), //8
            new Color(246, 164, 244), //16
            new Color(135, 60, 133), //32
            new Color(90, 32, 89), //64
            new Color(66, 15, 65), //128
            new Color(64, 116, 165), //256
            new Color(30, 72, 87), //512
            new Color(90, 138, 147), //1024
            new Color(33, 62, 101), //2048
            new Color(74, 139, 133), //4096
            new Color(25, 187, 182), //8192
            new Color(38, 11, 113), //16384
    };

    public int score=2;
    public boolean needsToRemove;
    public boolean collided;

    public Point dir=new Point(0,0);

    private int renderW,renderH;
    private Point lastPos;

    public Tile(int x,int y,int size){
        super(x,y,size,size);
        lastPos=new Point(x,y);
        Random r=new Random();
        int chance=r.nextInt(10);
        if(chance==1)score=4;
    }
    public void ResetAnim(){
        renderH=0;
        renderW=0;
    }
    public void Render(Graphics2D g){

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setPaint(colors[log2(score)-1]);
        g.fillRoundRect(x+5,y+5,renderW-10,renderH-10,width/6,width/6);

        DrawString(g,Integer.toString(score),new Point(x,y));

        if(height>renderH){
            renderH+=10;
            renderW+=10;
        }
    }
    private boolean CheckCollision(ArrayList<Tile> tiles,int index){
        if(tiles.get(index).score==this.score && !collided && !tiles.get(index).collided) {
            tiles.get(index).score*=2;
            tiles.get(index).ResetAnim();
            tiles.get(index).collided=true;
            needsToRemove=true;
            System.out.println("removed");
            return true;
        }
        return false;
    }
    private void DrawString(Graphics2D g,String string,Point pos){
        g.setFont(new Font("Century Gothic",Font.BOLD,35));

        int w = g.getFontMetrics().stringWidth(string);
        int h = g.getFontMetrics().getHeight();

        if(8>score)g.setPaint(new Color(119,110,101));
        else
            g.setPaint(Color.WHITE);
        g.drawString(string,pos.x+width/2-w/2,pos.y+height/2+h/3);
    }
    public void SetDirection(Point direction){
        dir=direction;
    }
    public void Move(){
        int SPEED = 20;
        x+=dir.x* SPEED;
        y+=dir.y* SPEED;
    }
    public boolean IsMoved(){
        return lastPos.x==x && lastPos.y==y;
    }
    public boolean Collision(int windowsize, ArrayList<Tile> tiles){

        lastPos=new Point(x,y);

        if((0>=x && dir.x==-1) || (x>=windowsize-width && dir.x==1) || (0>=y && dir.y==-1) || (y>=windowsize-height && dir.y==1))return false;
        else if(dir.x==1 && tiles.contains(new Tile(this.x+width,this.y,this.width))) {
            int index=tiles.indexOf(new Tile(this.x+width,this.y,this.width));
            return CheckCollision(tiles,index);
        }
        else if(dir.x==-1 && tiles.contains(new Tile(this.x-width,this.y,this.width))) {
            int index=tiles.indexOf(new Tile(this.x-width,this.y,this.width));
            return CheckCollision(tiles,index);
        }
        else if(dir.y==1  && tiles.contains(new Tile(this.x,this.y+height,this.width))) {
            int index=tiles.indexOf(new Tile(this.x,this.y+height,this.width));
            return CheckCollision(tiles,index);
        }
        else if(dir.y==-1 && tiles.contains(new Tile(this.x,this.y-height,this.width))) {
            int index=tiles.indexOf(new Tile(this.x,this.y-height,this.width));
            return CheckCollision(tiles,index);
        }
        else return true;
    }
    public int log2(int N)
    {
        return (int)(Math.log(N) / Math.log(2));
    }
}