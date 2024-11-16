/*
 * File: Breakout.java
 * -------------------
 * Name: BreakOut game
 * Section Leader: Dzhos Dmytro + Korotun Nazariy
 *
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.util.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import acm.graphics.GImage;
import acm.program.GraphicsProgram;
import acm.util.SoundClip;

/**
 * @implNote Breakout game class
 * @implNote Extends acm/graphics
 */
public class Breakout extends GraphicsProgram {


    /** Width of application window in pixels */
    public static final int APPLICATION_WIDTH = 400;

    /** Height of application window in pixels*/
    public static final int APPLICATION_HEIGHT = 600;

    /** Dimensions of game board (usually the same) */
    private static final int WIDTH = APPLICATION_WIDTH,
                             HEIGHT = APPLICATION_HEIGHT;

    /** Dimensions of the paddle */
    private static final int PADDLE_WIDTH = 120,
                             PADDLE_HEIGHT = 10;

    /** Offset of the paddle up from the bottom */
    private static final int PADDLE_Y_OFFSET = 30;

    /** Number of bricks per row */
    private static final int NBRICKS_PER_ROW = 10;

    /** Number of rows of bricks */
    private static final int NBRICK_ROWS = 10;

    /** Separation between bricks */
    private static final int BRICK_SEP = 4;

    /** Width of a brick */
    private static double BRICK_WIDTH;

    /** Height of a brick */
    private static final int BRICK_HEIGHT = 8;

    /** Radius of the ball in pixels */
    private static final int BALL_RADIUS = 10;

    /** Offset of the top brick row from the top */
    private static final int BRICK_Y_OFFSET = 70;

    /** Speed for the ball*/
    private double vx, vy;

    /** Params for built-in methods*/

    private final RandomGenerator rgen = RandomGenerator.getInstance();
    private final int DELAY = 10;

    /** Params for rendering in methods*/

    public static GImage heart1, heart2, heart3;
    private GRect FIELD;
    private GLabel score;
    private GImage backgroundImage;
    public static GRect PADDLE;
    public static GOval BALL;

    /** Variables for methods*/

    private int LIFES = 3;
    private final double speedBoost = 1;
    private boolean gameOver = false;
    private int amountOFBricks = NBRICKS_PER_ROW * NBRICK_ROWS;

    /** Variable for sound methods*/

    private SoundClip gameSoundClip;


    /* ||||||| START methods ||||||| */


    /** Subscribes to mouse events */
    public void init() {
        addMouseListeners();
    }

    /** Runs the Breakout game */
    public void run() {

        /* You fill this in, along with any subsidiary methods */

        this.setSize(WIDTH * 2, HEIGHT + 60);
        addBackgroundImage(0,0);
        addField(0,0);
        setBall();
        BRICK_WIDTH = (FIELD.getWidth() - (NBRICKS_PER_ROW + 1) * BRICK_SEP) / NBRICKS_PER_ROW;
        renderAllBricks();

        drawPaddle(FIELD.getWidth()  / 2 - PADDLE_WIDTH / 2, FIELD.getHeight() - PADDLE_Y_OFFSET);
        addGameSound();
        drawNeededHearts(3);

        while (LIFES > 0 && !gameOver && amountOFBricks > 0) {
            scoreLabel();
            moveBall();
            checkCollisionBallWithBrick();
            checkBallCollisionWithWalls();

            checkBallCollisionWithBottomWall();
            if (vx == 0 && vy == 0){
                pause(3000);
                remove(BALL);
                deleteNeededHearts(LIFES);
                setBall();
                LIFES--;
                if (LIFES == 0)
                    gameOver = true;
                drawNeededHearts(LIFES);
            }
            checkBallCollisionWithTopWall();
            checkCollisionWithPaddle();
            remove(score);
        }
        gameSoundClip.stop();
        pause(1000);
        scoreLabel();
        addFinalSound(amountOFBricks == 0 && LIFES > 0);
        setFinalScreen(amountOFBricks == 0 && LIFES > 0);

    }


    /* ||||||| MOUSE events methods ||||||| */


    /**
     * Runs everytime mouse has been moved
     * @param e {@code MouseEvent} the event to be processed
     */
    public void mouseMoved(MouseEvent e) {
        if (!gameOver && PADDLE != null) {
            double x = e.getX();
            remove(PADDLE);
            PADDLE.setLocation(x, PADDLE.getY());
            checkCollisionWithLeftWall();
            checkCollisionWithRightWall();
            drawPaddle(PADDLE.getX(), PADDLE.getY());
        }
    }


    /* ||||||| CHECK collisions methods ||||||| */


    /**
     * Checks collision between paddle and left wall
     */
    private void checkCollisionWithLeftWall() {
        if (PADDLE.getX() <= 0) {
            PADDLE.setLocation(0, PADDLE.getY());
        }

    }

    /**
     * Checks collision between paddle and right wall
     */
    private void checkCollisionWithRightWall() {
        if (PADDLE.getX() >= FIELD.getWidth()- PADDLE.getWidth()) {
            PADDLE.setLocation(FIELD.getWidth()  - PADDLE.getWidth(), PADDLE.getY());
        }

    }

    /**
     * Checks collision between ball and left-right walls
     */
    private void checkBallCollisionWithWalls() {
        if (BALL.getX() < 0 || BALL.getX() > FIELD.getWidth()- BALL.getWidth()) {
            vx *= -speedBoost;
        }

    }

    /**
     * Checks collision between paddle and top wall
     */
    private void checkBallCollisionWithTopWall() {
        if (BALL.getY() < 0) {
            vy *= -speedBoost;
        }

    }

    /**
     * Checks collision between paddle and bottom wall
     */
    private void checkBallCollisionWithBottomWall() {
        if (BALL.getY() + BALL.getHeight() > getHeight()) {
            vy = 0;
            vx = 0;
            addLosingHeartSound();
        }
    }

    /**
     * Checks collision between paddle and ball
     */
    private void checkCollisionWithPaddle() {
        if (PADDLE.getBounds().contains(BALL.getX() + BALL.getWidth() , BALL.getY() + BALL.getHeight())) {
            addCollisionSound();
            vy *= -speedBoost;
        }
    }

    /**
     *
     * @param x (@code double)
     * @param y (@code double)
     * @return True if Ball collides with Brick
     */
    private boolean isCollidedWithBrick(double x, double y) {
        return ((getElementAt(x,y) != null)
                && (getElementAt(x,y) != FIELD)
                && (getElementAt(x,y) != PADDLE)
                && (getElementAt(x,y) != heart1)
                && (getElementAt(x,y) != heart2)
                && (getElementAt(x,y) != heart3)
                && (getElementAt(x,y)) != backgroundImage)
                && (getElementAt(x,y) != score);
    }


    /**
     * Checks collision between ball and brick
     */
    private void checkCollisionBallWithBrick() {
        if (isCollidedWithBrick(BALL.getX(), BALL.getY())) {
            addCollisionSound();
            remove(getElementAt(BALL.getX() , BALL.getY() ));
            vy *= -1;
            amountOFBricks--;
            pause(DELAY);
            return;
        }

        if (isCollidedWithBrick(BALL.getX() + BALL.getWidth(), BALL.getY())) {
            addCollisionSound();
            remove(getElementAt(BALL.getX() + BALL.getWidth(), BALL.getY()));
            vy *= -1;
            amountOFBricks--;
            pause(DELAY);
            return;
        }

        if (isCollidedWithBrick(BALL.getX() , BALL.getY() + BALL.getHeight())) {
            addCollisionSound();
            remove(getElementAt(BALL.getX() , BALL.getY() + BALL.getHeight()));
            vy *= -1;
            amountOFBricks--;
            pause(DELAY);
            return;
        }

        if (isCollidedWithBrick(BALL.getX() + BALL.getWidth(), BALL.getY() + BALL.getHeight())) {
            addCollisionSound();
            remove(getElementAt(BALL.getX() + BALL.getWidth(), BALL.getY() + BALL.getHeight()));
            vy *= -1;
            amountOFBricks--;
            pause(DELAY);
        }

    }


    /* ||||||| DRAW methods ||||||| */


    /**
     * Draws a paddle on screen in specific position
     * @param x {@code double} - position X
     * @param y {@code double} - position Y
     */
    private void drawPaddle (double x, double y) {
        PADDLE = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
        PADDLE.setColor(Color.BLACK);
        PADDLE.setFilled(true);
        add(PADDLE);
    }

    /**
     * Draws a ball on screen in specific position
     * @param x {@code double} - position X
     * @param y {@code double} - position Y
     */
    private void drawBall(double x, double y) {
        BALL = new GOval(x, y, BALL_RADIUS, BALL_RADIUS);
        BALL.setFilled(true);
        BALL.setColor(Color.BLACK);
        add(BALL);
    }

    /**
     * Draws first heart on screen in specific position
     * @param x {@code double} - position X
     * @param y {@code double} - position Y
     */
    private void addHeart1(double x, double y) {
        heart1 = new GImage("heart-removebg-preview.png", x ,y);
        heart1.scale(0.1, 0.1);
        add(heart1);
    }

    /**
     * Draws second heart on screen in specific position
     * @param x {@code double} - position X
     * @param y {@code double} - position Y
     */
    private void addHeart2(double x, double y) {
        heart2 = new GImage("heart-removebg-preview.png", x  + heart1.getWidth(), y);
        heart2.scale(0.1, 0.1);
        add(heart2);
    }

    /**
     * Draws third heart on screen in specific position
     * @param x {@code double} - position X
     * @param y {@code double} - position Y
     */
    private void addHeart3(double x, double y) {
        heart3 = new GImage("heart-removebg-preview.png", x + 2 * heart1.getWidth(), y);
        heart3.scale(0.1, 0.1);
        add(heart3);
    }

    /**
     * Preparing method for draw first heart
     * @param x {@code double} - position X
     * @param y {@code double} - position Y
     */
    private void drawOneHeart(double x, double y) {
        addHeart1(x, y);
    }

    /**
     * Preparing method for draw first and second hearts
     * @param x {@code double} - position X
     * @param y {@code double} - position Y
     */
    private void drawTwoHearts(double x, double y) {
        addHeart1(x, y);
        addHeart2(x, y);
    }

    /**
     * Preparing method for draw all hearts
     * @param x {@code double} - position X
     * @param y {@code double} - position Y
     */
    private void drawAllHearts(double x, double y) {
        addHeart1(x, y);
        addHeart2(x, y);
        addHeart3(x, y);
    }

    /**
     * Draws hearts depending on amount that left
     * @param amount {@code int} - amount of hearts left
     */
    private void drawNeededHearts(int amount) {
        if (amount == 1)
            drawOneHeart(FIELD.getWidth(),0);
        if (amount == 2)
            drawTwoHearts(FIELD.getWidth(),0);
        if (amount == 3)
            drawAllHearts(FIELD.getWidth(),0);
    }

    /**
     * Draws a brick on screen in specific position with specific color
     * @param x {@code double} - position X
     * @param y {@code double} - position Y
     * @param c {@code Color} - color of brick
     */
    private void drawBrick(double x, double y, Color c) {
        GRect brick = new GRect(x,
                y,
                BRICK_WIDTH,
                BRICK_HEIGHT);
        brick.setColor(c);
        brick.setFilled(true);
        add(brick);
    }

    /**
     * Draws all bricks on screen
     */
    private void renderAllBricks() {
        Color c = Color.red;
        for (int i = 0; i < NBRICKS_PER_ROW; i++) {
            for (int j = 0; j < NBRICK_ROWS; j++) {
                c = switch (j) {
                    case 0, 1 -> Color.RED;
                    case 2, 3 -> Color.ORANGE;
                    case 4, 5 -> Color.YELLOW;
                    case 6, 7 -> Color.GREEN;
                    case 8, 9 -> Color.CYAN;
                    default -> c;
                };
                drawBrick(
                        (i + 1) * BRICK_SEP + i * BRICK_WIDTH,
                        BRICK_Y_OFFSET + j * BRICK_HEIGHT + j * BRICK_SEP,
                        c
                );
            }
        }
    }

    /**
     * Draws main field of game
     * @param x {@code double} - position X
     * @param y {@code double} - position Y
     */
    private void addField(double x, double y) {
        FIELD = new GRect(x, y, x + 3 * WIDTH / 2, y + HEIGHT);
        add(FIELD);
    }

    /**
     * Draws a background in position
     * @param x {@code int} - position X
     * @param y {@code int} - position Y
     */
    private void addBackgroundImage(int x, int y) {
        backgroundImage = new GImage("backgroundImage.jpg",x, y);
        backgroundImage.setSize(getWidth(),getHeight());
        backgroundImage.sendToFront();
        add(backgroundImage);
    }

    /**
     *  Sets and draws a 'Final Screen'
     * @param winner (@code boolean) Result of the game, true - for the win, false - for the loose
     */
    private void setFinalScreen(boolean winner){
        GImage finalScreen;
        if (winner)
            finalScreen = new GImage("winner.jpg");
        else
            finalScreen = new GImage("losser.jpg");
        finalScreen.setSize(FIELD.getWidth(), FIELD.getHeight());
        add(finalScreen);
    }

    /**
     * Draws a score label
     */
    private void scoreLabel() {
        int allBricks = NBRICKS_PER_ROW * NBRICK_ROWS;
        String scoreStr = "Your score:" + (allBricks - amountOFBricks);
        score = new GLabel(scoreStr, FIELD.getWidth(), HEIGHT - 5);
        score.setFont("Italic-26");
        add(score);
    }


    /* ||||||| SET methods ||||||| */


    /**
     * Sets ball's speed
     */
    private void setBallSpeed() {
        vx = rgen.nextDouble(1.0, 4);
        if (rgen.nextBoolean(0.5))
            vx = -vx;
        vy = rgen.nextDouble(1.0, 4);
        if (rgen.nextBoolean(0.5))
            vy = -vy;
    }

    /**
     * Moves ball at location
     */
    private void moveBall() {
        remove(BALL);
        BALL.setLocation(BALL.getX() + vx, BALL.getY() + vy);
        BALL.move(vx, vy);
        drawBall(BALL.getX(), BALL.getY());
        pause(DELAY);
    }

    /**
     * Draws ball and sets it's speed
     */
    private void setBall() {
        drawBall(FIELD.getWidth() / 2 - BALL_RADIUS / 2, getHeight() / 2 - BALL_RADIUS / 2 );
        setBallSpeed();
    }


    /* ||||||| SOUND methods ||||||| */


    /**
     * Adds sound on background
     */
    private void addGameSound() {
        gameSoundClip = new SoundClip("soundEffects/gameSound.wav");
        gameSoundClip.setVolume(1);
        gameSoundClip.loop();
    }

    /**
     * Adds sound to collision event
     */
    private void addCollisionSound() {
        SoundClip soundClip = new SoundClip("soundEffects/ballCollidesSound.wav");
        soundClip.setVolume(1);
        soundClip.play();
    }

    /**
     * Adds sound to loosing one heart
     */
    private void addLosingHeartSound() {
        SoundClip soundClip = new SoundClip("soundEffects/losingHeartSound.wav");
        soundClip.setVolume(1);
        soundClip.play();
    }

    /**
     * Adds sound after the game
     */
    private void addFinalSound(boolean winner) {
        SoundClip finalSound = winner ? new SoundClip("soundEffects/winningSound.wav") : new SoundClip("soundEffects/gameOverSound.wav");
        finalSound.setVolume(1);
        finalSound.play();
    }


    /* ||||||| DELETE methods ||||||| */


    /**
     * Removes first heart from screen
     */
    private void deleteOneHeart() {
        remove(heart1);
    }

    /**
     * Removes first and second hearts from screen
     */
    private void deleteTwoHearts() {
        remove(heart1);
        remove(heart2);
    }

    /**
     * Removes all hearts from screen
     */
    private void deleteALlHearts() {
        remove(heart1);
        remove(heart2);
        remove(heart3);
    }

    /**
     * Removes hearts depending on amount that have been used
     * @param amount {@code int} - amount of hearts that have been used
     */
    private void deleteNeededHearts(int amount) {
        if (amount == 1)
            deleteOneHeart();
        if (amount == 2)
            deleteTwoHearts();
        if (amount == 3)
            deleteALlHearts();
    }
}