import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

/*
* 该类文件中主要实现：
* 1.蛇的活动范围
* 2.蛇的初始位置和默认方向
* 3.通过键盘的方向键操控蛇的移动
* 4.判断蛇与边框和尾部是否发生碰撞
* */


public class Snake {
    //实例化蛇的活动范围为窗口的范围大小
    private static final int BLOCK_WIDTH = SnakeFrame.BLOCK_WIDTH;
    private static final int BLOCK_HEIGHT = SnakeFrame.BLOCK_HEIGHT;

    //实例化一个头部节点
    private Node head = null;
    //实例化一个尾部节点
    private Node tail = null;

    //实例化窗口对象sf，获得窗口的属性，用于被调用
    private SnakeFrame sf;

    //蛇初始时只有一个头，默认位置为3行4列，默认方向向上
    private Node node = new Node(20,20,Direction.U);

    //蛇身的大小
    private int size = 0;

    //初始化蛇在窗口中的默认位置
    public Snake(SnakeFrame sf) {
        //蛇头的位置
        head = node;
        //蛇尾的位置
        tail = node;
        //当按键监听事件发生时，节点位置发生变化，也就是节点在窗口中移动
        size ++;
        this.sf = sf ;
    }

    //画蛇， Graphics为图形
    public void draw(Graphics g){
        //判断蛇头节点的值是否为空值
        if (head==null){
            return ;
        }
        //返回后执行move()节点移动方法
        move();
        //移动时刷新节点的位置，当输入按键时，节点产生位置变化，此时节点有值
        //node = node.next为同时将下一个节点的值做为当前节点的值
        for (Node node = head; node != null; node = node.next){
            //蛇头节点有值，就画出这个新节点
            node.draw(g);
        }
    }

    //此函数的功能，先在头部添加一个节点，然后删除尾部的节点，这样就完成了移动
    public void move() {
        addNodeInHead();
        //检查是否死忙
        checkDead();
        //删除尾节点
        deleteNodeInTail();
    }

    //检查是否死忙
    private void checkDead() {
        //头结点的边界检查
        //头节点行坐标小于2 或大于窗口行坐标 或 列坐标小于0，以此类推
        if (head.row < 2 || head.row > SnakeFrame.ROW || head.col < 0 || head.col > SnakeFrame.COL){
            //发生碰撞就调用gameover方法，结束游戏
            this.sf.gameOver();
        }

        //头结点与其它结点相撞也是死忙
        //通过for循环，初始节点值尾当前蛇头的下一个节点值，并判断是否等于空值，也就是节点的下一个节点是否有值
        for (Node node = head.next; node != null; node = node.next){
            //如果下一个节点有值，判断下一个节点的行坐标与列坐标是否与蛇头节点相同，如果相同表示头尾重合，蛇吃到尾巴了
            if(head.row == node.row && head.col == node.col){
                //结束游戏
                this.sf.gameOver();
            }
        }
    }

    //删除尾部节点的方法
    private void deleteNodeInTail() {
        //同样和下面的置换节点的变量值原理一样
        //先将尾节点的值给空节点
        Node node = tail.pre;
        //再将尾值设为空
        tail = null;
        //下一个节点设为空
        node.next = null;
        //再将当前节点赋值给尾值
        tail = node;
    }

    //在蛇的头部添加节点
    private void addNodeInHead() {
        Node node = null;
        //蛇头节点的方向，并控制节点的移动
        switch(head.dir){
            case L:
                node = new Node(head.row,head.col-1,head.dir);
                break;
            case U:
                node = new Node(head.row-1,head.col,head.dir);
                break;
            case R:
                node = new Node(head.row,head.col+1,head.dir);
                break;
            case D:
                node = new Node(head.row+1,head.col,head.dir);
                break;
        }

        //蛇的节点发生位置变化时，通过中间变量pre将得到的下一个节点信息给当前节点
        //再将空值节点重新赋值给头节点，以满足上面draw()方法的要求
        node.next = head;
        head.pre = node;
        head = node;
    }

    //枚举方向
    public enum Direction {
        //L左，U上，R右，D下
        L,U,R,D
    }

    //监听输入按键事件判断节点的位置移动
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch(key){
            //按键事件为左
            case KeyEvent.VK_LEFT :
                //判断蛇头节点的方向不是右，才能向左，实现了例如：蛇在前进时，不能直接按后退键调方向
                //其他同理
                if(head.dir != Direction.R){
                    head.dir = Direction.L;
                }
                break;
            case KeyEvent.VK_UP :
                if(head.dir != Direction.D){
                    head.dir = Direction.U;
                }
                break;
            case KeyEvent.VK_RIGHT :
                if(head.dir != Direction.L){
                    head.dir = Direction.R;
                }
                break;
            case KeyEvent.VK_DOWN :
                if(head.dir != Direction.U){
                    head.dir = Direction.D;
                }
                break;
        }
    }

    //给蛇添加一个检测碰撞，原理与Egg的检测碰撞相同
    public Rectangle getRect(){
        return new Rectangle(head.col*BLOCK_WIDTH, head.row*BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT);
    }

    //检测是否吃到Egg，返回值为bool类型
    public boolean eatEgg(Egg egg){
        //this表示当前snake自身
        // 判断当前snake与egg进行矩形相交检测，当方式碰撞为true，执行后面的语句
        if(this.getRect().intersects(egg.getRect())){
            //检测碰撞后，添加蛇头的节点，代表蛇变长了一个单位
            addNodeInHead();
            //发生碰撞后，egg被吃掉，所以要重新调用生成egg的方法
            egg.reAppear();
            return true;
        }
        //没有发生碰撞就直接结束该方法
        else{
            return false;
        }
    }

    //设置节点
    public class Node {
        //节点的位置
        private int row;
        private int col;
        //方向
        private Direction dir ;

        //为上面置换节点变量值准备
        //实例一个中间变量 pre：预先
        private Node pre;
        //下一个节点的变量
        private Node next;

        //设置节点的位置和默认的方向
        public Node(int row, int col, Direction dir) {
            //表示为当前snake的默认位置和默认方向
            this.row = row;
            this.col = col;
            this.dir = dir;
        }

        //这一段就是画蛇的
        public void draw(Graphics g){
            //先创建一个Color类的对象c，并获得颜色的属性
            Color c = g.getColor();
            //修改蛇的颜色为黑色
            g.setColor(Color.BLACK);
            //蛇的样子时填充矩形，大小采用SnakeFrame类中预先实例好的格子的大小
            g.fillRect(col*BLOCK_WIDTH, row*BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT);
            //用黑色画出来
            g.setColor(c);
        }
    }
}