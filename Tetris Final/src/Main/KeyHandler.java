package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public static boolean upPressed, downPressed, leftPressed, rightPressed, pausePressed, spacePressed, shiftPressed, restartPressed;

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_R) {
            restartPressed = true;
        }

        if (code == KeyEvent.VK_N) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_P) { // Use 'P' for pause instead of space
            pausePressed = !pausePressed; // Toggle pause state
        }
        if (code == KeyEvent.VK_SPACE) { 
            spacePressed = true; // For instant drop
        }
        if (code == KeyEvent.VK_SHIFT) { 
            shiftPressed = true;
    }
}
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_R) {
            restartPressed = false;
        }

        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_SPACE) {
            spacePressed = false; // Reset space state if needed
        }
        if (code == KeyEvent.VK_SHIFT) {
            shiftPressed = false; // Reset shift state
        }
    }
}