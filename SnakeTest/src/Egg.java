import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

/*
* 该Egg类文件中实现：
* 1.Egg的随机产生的位置
* 2.Egg的颜色为红色
* 3.Egg被吃掉后的重新生成
* 4.Egg检测被碰撞的范围
* */

public class Egg {
    //所在的位置
    private int row;
    private int col;
    //产生egg的范围大小为边框的大小
    private static final int BLOCK_WIDTH = SnakeFrame.BLOCK_WIDTH;
    private static final int BLOCK_HEIGHT = SnakeFrame.BLOCK_HEIGHT;

    //egg产生随机
    private static final Random r = new Random();

    //实例一个颜色
    private Color color = Color.RED;

    //设置Egg的位置属性为默认位置值
    public Egg(int row, int col) {
        this.row = row;
        this.col = col;
    }

    //Egg产生的位置是窗口行数和列数（因为窗口带有厚度，如果Egg随机生成在边框位置，会被遮住一部分）
    public Egg() {
        this((r.nextInt(SnakeFrame.ROW-2)+2),(r.nextInt(SnakeFrame.COL-2)+2));
    }

    //Egg被吃掉后，重新生成
    public void reAppear(){
        this.row = (r.nextInt(SnakeFrame.ROW-2)+2);
        this.col = (r.nextInt(SnakeFrame.COL-2)+2);
}

    //绘制Egg
    public void draw(Graphics g){
        //设置颜色即为实例的颜色
        Color c = g.getColor();
        g.setColor(color);
        g.fillRect(col*BLOCK_WIDTH, row*BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT);
        g.setColor(c);
    }
    //用于碰撞检测，getRect多矩形碰撞检测
    public Rectangle getRect(){
        //col*BLOCK_WIDTH, row*BLOCK_HEIGHT 为指定检测的位置范围
        //BLOCK_WIDTH, BLOCK_HEIGHT 为每个检测位置的大小
        return new Rectangle(col*BLOCK_WIDTH, row*BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT);
    }
}