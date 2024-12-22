package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import Main.KeyHandler;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{      //implement Runnable to use a thread   // when you implement Runnable, you need to add a run method
    public static final int WIDTH = 1280;
    public static final int LENGTH = 720;

    //FPS
    final int FPS = 60;

    Thread gameThread;                                          //to run the game loop
    PlayManager pm;

    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;


    public GamePanel(){

        //Game Panel Settings
        this.setPreferredSize(new Dimension(WIDTH, LENGTH));
        this.setBackground(Color.BLACK);
        this.setLayout(null);
        //implement KeyListener;
        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);
        pm = new PlayManager();

    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();                                     //when a game starts, it automaticaly calls the run method
    }


    @Override
    public void run(){

        //Game Loop
        double drawInterval = 1000000000/FPS;                   //to draw screen 60 times per second
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        long timer = 0;
        int drawCount = 0;
        long i = System.nanoTime()/1000000000;                  //just so I can see ano ang nanotime sng time sa pag print
        
        //Game loop : update in such as object info and draw screen with object info
        while (gameThread != null) {
            
            currentTime = System.nanoTime();
            // System.out.println(currentTime);

            delta += (currentTime-lastTime) / drawInterval;
            timer += (currentTime-lastTime);
            lastTime = currentTime;
            
            if(delta >= 1){// update character position
                update();
                //draw the screen with updated information
                repaint(); //to call paintComponent()
                delta--;
                drawCount++;
            }
 
            if (timer >= 1000000000) {
                System.out.println("Iteration: " + i + " FPS: " + drawCount);
                
                //System.out.println("Timer: " + timer );
                drawCount = 0;
                timer = 0;
            }
        }       
    }

    private void update(){
        if (KeyHandler.restartPressed) {
            pm.restartGame(); // Call the restart method in PlayManager
            KeyHandler.restartPressed = false; // Reset the restart key flag
        }
    
        if (!KeyHandler.pausePressed && !pm.gameOver) {
            pm.update();
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        pm.draw(g2);
        
    }
}
