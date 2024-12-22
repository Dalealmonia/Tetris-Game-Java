package Main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;
import mino.Block;
import mino.Mino;
import mino.MinoBar;
import mino.MinoL1;
import mino.MinoL2;
import mino.MinoSquare;
import mino.MinoT;
import mino.MinoZ1;
import mino.MinoZ2;

public class PlayManager {

    // Main Play Area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    // Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    Mino reservedMino; // To hold the reserved Mino
    boolean hasReservedMino = false; // To check if a Mino is reserved
    final int RESERVEDMINO_X = left_x + 200; // Position for Reserved Mino (top left corner)
    final int RESERVEDMINO_Y = top_y + 50;
    // Others
    public static int dropInterval = 60; //mino drop every 60 frames 
    boolean gameOver;

    // Effect
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    // Score
    int level = 1;
    int lines;
    int score;

    public PlayManager() {
        // Set the main play area's bounds
        // left and right are the left and right edges of the play area
        // top and bottom are the top and bottom edges of the play area
        left_x = (GamePanel.WIDTH / 2) - (WIDTH / 2); // center - width/2
        right_x = left_x + WIDTH; // left + width
        top_y = 50; // from top
        bottom_y = top_y + HEIGHT; // top + height
        MINO_START_X = left_x + (WIDTH / 2) - Block.SIZE; // center - block size
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 125;
        NEXTMINO_Y = top_y + 125;
        // Set the Starting Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }

    private Mino pickMino() {
        Mino mino = null;
        // Generate a random integer between 0 and 6
        int i = new Random().nextInt(7);
        
        // Select a Mino based on the random integer
        switch (i) {
            case 0: mino = new MinoL1(); break;      // Mino type L1
            case 1: mino = new MinoL2(); break;      // Mino type L2
            case 2: mino = new MinoSquare(); break;  // Mino type Square
            case 3: mino = new MinoBar(); break;     // Mino type Bar
            case 4: mino = new MinoT(); break;       // Mino type T
            case 5: mino = new MinoZ1(); break;      // Mino type Z1
            case 6: mino = new MinoZ2(); break;      // Mino type Z2
        }
        
        return mino;
    }

    private boolean shiftUsedThisMino = false; // Track if Shift was used for the current Mino

public void update() {
    // Check if the current Mino is active
    if (!currentMino.active) {
        // Add the Mino's blocks to the static blocks list
        staticBlocks.add(currentMino.b[0]);
        staticBlocks.add(currentMino.b[1]);
        staticBlocks.add(currentMino.b[2]);
        staticBlocks.add(currentMino.b[3]);

        // check if the game is over
        if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y){
            // means that a mino has reached the start/top position and the new mino couldn't move at all
            gameOver = true;
        }
        currentMino.deactivating = false;

        // Replace the current Mino with the Next Mino
        currentMino = nextMino;
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

        // Reset the Shift usage for the new Mino
        shiftUsedThisMino = false;

        // Check if line(s) can be deleted
        checkDelete();
    } else {
        currentMino.update();
    }

    // Check for Shift key press to reserve the current Mino
    if (KeyHandler.shiftPressed && !shiftUsedThisMino) {
        reserveCurrentMino();
        shiftUsedThisMino = true; // Prevent Shift for this Mino
    }

    // Handle instant drop when Space is pressed
    if (KeyHandler.spacePressed) {
        instantDrop();
        KeyHandler.spacePressed = false;
    }
}

public void reserveCurrentMino() {
    if (hasReservedMino) {
        // Swap the current Mino with the reserved Mino
        Mino temp = currentMino;
        currentMino = reservedMino;
        reservedMino = temp;

        // Reset the current Mino's position and deactivate the reserved Mino
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        currentMino.active = true; // Ensure the current Mino is active
        reservedMino.active = false; // Deactivate the reserved Mino
    } else {
        // Reserve the current Mino
        reservedMino = currentMino;
        hasReservedMino = true;

        // Replace the current Mino with the next Mino
        currentMino = nextMino;
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        currentMino.active = true; // Ensure the new current Mino is active
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }
}
public void restartGame() {
    staticBlocks.clear(); // Clear all static blocks

    currentMino = pickMino(); // Pick a new starting Mino
    currentMino.setXY(MINO_START_X, MINO_START_Y);
    nextMino = pickMino(); // Pick the next Mino
    nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

    reservedMino = null; // Clear the reserved Mino

    hasReservedMino = false; // No reserved Mino

    shiftUsedThisMino = false; // Reset shift flag

    level = 1; // Reset level
    lines = 0; // Reset line count
    score = 0; // Reset score

    dropInterval = 60; // Reset drop speed

    gameOver = false; // Clear game over state

    effectCounterOn = false; // Reset visual effects
    effectCounter = 0; // Reset effect timer
    effectY.clear(); // Clear effect lines

    KeyHandler.pausePressed = false;

}

    
    private void checkDelete(){
        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;
         // Loop through all rows and columns within the play area
        while(x<right_x && y<bottom_y){
            // Check each block in the staticBlocks list
            for(int i = 0; i<staticBlocks.size(); i++){
                // If a block exists at the current x, y position
                if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y ){
                    blockCount++; // Increment the block counter
                }
            }
            // Move to the next column in the row
            x+= Block.SIZE;
            // If we've reached the end of the row
            if(x == right_x){
                // Check if the row is full
                if(blockCount == 12){
                    effectCounterOn = true;
                    effectY.add(y);
                    //Removing all blocks in the current full row 
                    for(int i = staticBlocks.size()-1; i>-1; i--){
                        if(staticBlocks.get(i).y == y){
                            staticBlocks.remove(i);
                        }
                    }
                    lineCount++;
                    lines++;

                    // Drop Speed
                    if (lines % 10 == 0 && dropInterval > 1) {
                        level++;
                        if(dropInterval > 10){
                            dropInterval -= 20;
                        } else {
                            dropInterval -= 1;
                        }
                    }

                    for(int i = 0; i < staticBlocks.size(); i++) {
                        if(staticBlocks.get(i).y < y){
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }
                }

                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }
        //Add Score
        if (lineCount > 0){
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
        }
    }
    
    private void instantDrop() {
        boolean canDrop = true;
    
        // Move the Mino down until it collides with something
        while (canDrop) {
            for (Block block : currentMino.b) {
                // Check for bottom boundary or collision with static blocks
                if (block.y + Block.SIZE == bottom_y || isBlockColliding(block.x, block.y + Block.SIZE)) {
                    canDrop = false;
                    break;
                }
            }
    
            // If no collision, move block in the Mino down
            if (canDrop) {
                for (Block block : currentMino.b) {
                    block.y += Block.SIZE;
                }
            }
        }
    
        // Deactivate the current Mino after dropping
        currentMino.active = false;
    }
    
    // Helper function to check if a block collides with static blocks
    private boolean isBlockColliding(int x, int y) {
        for (Block staticBlock : staticBlocks) {
            if (staticBlock.x == x && staticBlock.y == y) {
                return true;
            }
        }
        return false;
    }
    
    public void draw(Graphics2D g2) {
        // Draw main play area
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x, top_y, WIDTH, HEIGHT);
        
        // Draw grid in the play area
        g2.setColor(Color.gray);
        for (int x = left_x; x <= right_x; x += Block.SIZE) {
        g2.drawLine(x, top_y, x, bottom_y); // Vertical grid lines
        }
        for (int y = top_y; y <= bottom_y; y += Block.SIZE) {
            g2.drawLine(left_x, y, right_x, y); // Horizontal grid lines
        }
        // Draw next block
        int x = right_x + 50;
        int y = top_y + 50;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x + 60, y - 10);

        g2.drawRect(x, top_y * 7, 250, 300);
        x += 40; 
        y = top_y*7 + 90;
        g2.drawString("LEVEL: " + level, x, y ); y += 70;
        g2.drawString("LINES: " + lines, x, y); y += 70;
        g2.drawString("SCORE: " + score, x, y);
        
        // Draw current Mino
        if (currentMino != null) {
            currentMino.draw(g2);
        }

        // Draw the next Mino
        if (nextMino != null) {
            nextMino.draw(g2);
        }

        // Draw static blocks
        for (Block staticBlock : staticBlocks) {
            staticBlock.draw(g2);
        }

        // Draw reserved Mino section
        g2.drawRect(RESERVEDMINO_X, RESERVEDMINO_Y, 200, 200); // Reserved box
        g2.drawString("RESERVED", RESERVEDMINO_X + 30, RESERVEDMINO_Y - 10);

        // Draw reserved Mino
        if (reservedMino != null) {
            reservedMino.setXY(RESERVEDMINO_X + 65, RESERVEDMINO_Y +65); // Center in reserved box
            reservedMino.draw(g2);
        }
        // Draw Effect
        if (effectCounterOn) {
            effectCounter++; // Increment the effect duration counter
        
            // Set the drawing color to red
            g2.setColor(Color.red);
        
            // Iterate over all the y-coordinates of lines that were cleared
            for (int i = 0; i < effectY.size(); i++) {
                // Draw a red rectangle covering the cleared line
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }
        
            // Check if the effect has lasted for the specified duration (10 frames)
            if (effectCounter == 10) {
                effectCounter = 0;         // Reset the counter
                effectCounterOn = false;   // Turn off the visual effect
                effectY.clear();           // Clear the list of y-coordinates for cleared lines
            }
        }
        
        // Pause or game over message
        if (gameOver) {
            x = left_x + 125;
            y = top_y + 125;
            g2.setFont(new Font("Arial", Font.PLAIN, 80));
            g2.drawString("GAME OVER", x-125, y);
            g2.setFont(new Font("Arial", Font.PLAIN, 50));
            g2.drawString("Press R to Restart", x-100, y + 50);
        } else if (KeyHandler.pausePressed) {
            x = left_x + 70;
            y = top_y + 320;
            g2.setFont(new Font("Arial", Font.PLAIN, 80));
            g2.drawString("PAUSED", x-50, y);
            g2.setFont(new Font("Arial", Font.PLAIN, 50));
            g2.drawString("Press R to Restart", x-100, y + 50);
        }
        
    }
}
