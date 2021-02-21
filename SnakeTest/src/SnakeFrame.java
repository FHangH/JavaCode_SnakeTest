import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/*
* 该SnakeFrame类文件实现：
* 1.窗口，格子和界面的大小，颜色的实现
* 2.计分信息在界面提示的功能
* 3.通过update实时刷新界面
* 4.因游戏规则而结束时，弹出的信息提示窗口，关闭窗口，同时结束游戏
* 5.通过键盘按键的输入实现游戏的暂停，继续，重新开始（继续功能与暂停功能逻辑有问题为解决！！！）
* */
public class SnakeFrame extends Frame{
    //方格的宽度和长度
    //对于一个final变量，如果是基本数据类型的变量，则其数值一旦在初始化之后便不能更改；如果是引用类型的变量，则在对其初始化之后便不能再让其指向另一个对象
    //当用final作用于类的成员变量时，成员变量（注意是类的成员变量，局部变量只需要保证在使用之前被初始化赋值即可）
    // 必须在定义时或者构造器中进行初始化赋值，而且final变量一旦被初始化赋值之后，就不能再被赋值了
    public static final int BLOCK_WIDTH = 15 ;
    public static final int BLOCK_HEIGHT = 15 ;
    //界面的方格的行数和列数
    public static final int ROW = 40;
    public static final int COL = 40;

    //得分
    private int score = 0;

    //空格键计数（该功能未能完全实现）
    public int spaceKeyNum = 1;

    //获得当前计分的属性
    public int getScore() {
        return score;
    }

    //设置当前计分的属性，参数类型为int类型
    public void setScore(int score) {
        this.score = score;
    }

    //创建一个画图的线程对象 Thread：线程
    private MyPaintThread paintThread = new MyPaintThread();
    //创建一个在
    private Image offScreenImage = null;
    //创建一条蛇
    private Snake snake = new Snake(this);
    //创建egg
    private Egg egg = new Egg();
    //创建一个窗口 sf
    private static SnakeFrame sf = null;

    //主方法在此，程序由此进行开始
    public static void main(String[] args) {
        //程序开始先创建一个窗口
        sf = new SnakeFrame();
        //然后就是创建一个蛇和蛋在上面的界面
        sf.launch();
    }

    //创建一个蛇和蛋的舞台界面， launch：发射
    public void launch(){
        //设置界面标题
        this.setTitle("Snake");
        //设置界面大小
        this.setSize(ROW*BLOCK_HEIGHT, COL*BLOCK_WIDTH);
        //设置界面在电脑屏幕出现的位置，window系统默认左上角为（0，0）
        this.setLocation(600, 300);
        //界面底色为白色
        this.setBackground(Color.WHITE);
        //添加一个窗口监听
        this.addWindowListener(new WindowAdapter() {
            //重新窗口关闭事件（点击窗口的×，就结束程序）
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //设置窗口大小不可调整，（调整了就不能正常玩了，因为上面的窗口行数，列数，格子的宽高用final修饰，不可再次修改）
        this.setResizable(false);
        //窗口界面显示出来
        this.setVisible(true);

        //为界面添加，键盘输入按键监听事件
        this.addKeyListener(new KeyMonitor());
        //监听到有效的键盘按键输入时，调用线程绘制，
        new Thread(paintThread).start();
    }

    //定义一个bool类型的值，初值为false；
    private boolean b_gameOver = false;

    //当有程序调用该方法时，修改b_gameOver的值为true
    //下面会有程序调用该方法，用于游戏暂停
    public void gameOver(){
        b_gameOver = true;
    }

    //重写Update方法
    @Override
    //Update其实就是一个根据电脑性能进行实时帧率的刷新方法
    public void update(Graphics g) {
        if(offScreenImage==null){
            offScreenImage = this.createImage(ROW*BLOCK_HEIGHT, COL*BLOCK_WIDTH);
        }
        Graphics offg = offScreenImage.getGraphics();
        //先将内容画在虚拟画布上
        paint(offg);
        //然后将虚拟画布上的内容一起画在画布上
        g.drawImage(offScreenImage, 0, 0, null);

        snake.draw(g);
        boolean b_Success=snake.eatEgg(egg);
        //吃一个加1分
        if(b_Success){
            score+=1;
        }

        egg.draw(g);
        displaySomeInfor(g);

        //当上面b_gameOver的值为true，执行该程序
        if(b_gameOver){
            //绘制线程结束
            paintThread.dead();
            //弹出提示信息窗口，只有确认一个按键，点击后
            JOptionPane.showMessageDialog(this, "你输了", "游戏结束", JOptionPane.YES_OPTION);
            //结束整个正在运行的程序（游戏真正意义上的结束）
            System.exit(0);
        }
    }

    //在界面上显示一些提示信息
    public void displaySomeInfor(Graphics g){
        //先创建一个有颜色属性的对象
        Color c = g.getColor();
        //然后给对象设置颜色
        g.setColor(Color.RED);
        //信息为计分信息
        g.drawString("得分:"+score, 1*BLOCK_HEIGHT, 3*BLOCK_WIDTH);
        g.setColor(c);
    }

    //重画线程类（重新开始的程序，暂停和继续之间的切换有点问题）
    //implements: 实现
    private class MyPaintThread implements Runnable{
        //running不能改变，改变后此线程就结束了
        private static final boolean  running = true;
        //pause: 暂停
        private boolean  pause = false;
        //重写Runnable提供的run方法
        @Override
        public void run() {
            while(running){
                //如果pause 为true ，则暂停
                if(pause){
                    //进入等待
                    continue;
                }
                repaint();

                //捕获异常
                try {
                    Thread.sleep(100);
                }
                //处理异常
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //暂停
        public void pause(){
            if (spaceKeyNum % 2 == 0){
                pause = true;
            }
            else {
                paintThread.recover();
            }
        }

        //从暂停中恢复（继续功能为实现）
        public void recover(){
            pause = false;
            spaceKeyNum = 1;
        }

        //游戏结束，死亡,只能设置pause 为true，不能设置running = false，这样就导致重画的线程结束了;
        public void dead(){
            pause = true;
        }

        //重新开始
        public void reStart(){
            sf.b_gameOver = false;
            this.pause = false;
            snake = new Snake(sf);
        }
    }

    //通过空格键 F2键 B键控制游戏的进程
    private class KeyMonitor extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_SPACE){
                spaceKeyNum += 1;
                paintThread.pause();
            }
            //继续
            if(key == KeyEvent.VK_B){
                paintThread.recover();
            }
            //再开一局
            if(key == KeyEvent.VK_F2){
                paintThread.reStart();
            }
            else{
                snake.keyPressed(e);
            }
        }
    }
}
