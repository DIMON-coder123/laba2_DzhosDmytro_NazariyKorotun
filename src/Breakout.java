/*
 * File: Breakout.java
 * -------------------
 * Name: BreakOut game
 * Section Leader: Dzhos Dmytro + Korotun Nazariy
 *
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Breakout extends GraphicsProgram {


    /** Width and height of application window in pixels */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /** Dimensions of game board (usually the same) */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /** Dimensions of the paddle */
    private static final int PADDLE_WIDTH = 120;
    private static final int MIN_PADDLE_WIDTH = 30;
    private static final int PADDLE_HEIGHT = 10;

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

    /** Params for built-in functions*/

    private final RandomGenerator rgen = RandomGenerator.getInstance();
    private final int DELAY = 10;


    private int LIFES = 3;
    private final double speedBoost = 1;
    public static GRect PADDLE;
    public static GOval BALL;
    public static GImage heart1, heart2, heart3;
//    public static GLine Trace;
    private boolean gameOver = false;
    private int amountOfBricks = NBRICKS_PER_ROW * NBRICK_ROWS;
    public GRect FIELD;

    private void drawPaddle (double x, double y) {
        PADDLE = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
        PADDLE.setColor(Color.BLACK);
        PADDLE.setFilled(true);
        add(PADDLE);
    }

    private void changeWidthOfPaddle(int bricks) {
        PADDLE.setSize(MIN_PADDLE_WIDTH + (PADDLE_WIDTH - MIN_PADDLE_WIDTH)* bricks / (NBRICK_ROWS * NBRICK_ROWS), PADDLE_HEIGHT);
    }


    public void init() {
        addMouseListeners();
        addKeyListeners();
    }


    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE) {
            vx = 0;
            vy = 0;
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (!gameOver) {
            double x = e.getX();
            remove(PADDLE);
            PADDLE.setLocation(x, PADDLE.getY());
            checkCollisionWithLeftWall();
            checkCollisionWithRightWall();
            drawPaddle(PADDLE.getX(), PADDLE.getY());
            changeWidthOfPaddle(amountOfBricks);
        }
    }

    private void checkCollisionWithLeftWall() {
        if (PADDLE.getX() <= 0)
            PADDLE.setLocation(0, PADDLE.getY());
    }

    private void checkCollisionWithRightWall() {
        if (PADDLE.getX() >= FIELD.getWidth()- PADDLE.getWidth())
            PADDLE.setLocation(FIELD.getWidth()  - PADDLE.getWidth(), PADDLE.getY());
    }

    private void drawBall(double x, double y) {
        BALL = new GOval(x, y, BALL_RADIUS, BALL_RADIUS);
        BALL.setFilled(true);
        BALL.setColor(Color.BLACK);
        add(BALL);
    }

    private void setBallSpeed() {
        vx = rgen.nextDouble(1.0, 5.0);
        if (rgen.nextBoolean(0.5))
            vx = -vx;
        vy = rgen.nextDouble(1.0, 5.0);
        if (rgen.nextBoolean(0.5))
            vy = -vy;
    }

    private void moveBall() {
        remove(BALL);
        BALL.setLocation(BALL.getX() + vx, BALL.getY() + vy);
        BALL.move(vx, vy);
        drawBall(BALL.getX(), BALL.getY());
        pause(DELAY);
    }

//    private void drawTraceLine(double x, double y) {
//        Trace = new GLine(x, y, x + vx, y + vy);
//        add(Trace);
//    }

    private void checkBallCollisionWithWalls() {
        if (BALL.getX() < 0 || BALL.getX() > FIELD.getWidth()- BALL.getWidth())
            vx *= -speedBoost;
    }

    private void checkBallCollisionWithTopWall() {
        if (BALL.getY() < 0) {
            vy *= -speedBoost;
        }

    }

    private void checkBallCollisionWithBottomWall() {
        if (BALL.getY() + BALL.getHeight() > getHeight()) {
            vy = 0;
            vx = 0;
        }
    }

    public void checkCollisionWithPaddle() {
        if (PADDLE.getBounds().contains(BALL.getX() + BALL.getWidth(), BALL.getY() + BALL.getHeight())) {
            vy *= -speedBoost;
        }

    }

    private void setBall() {
        drawBall(FIELD.getWidth() / 2 - BALL_RADIUS / 2, getHeight() / 2 - BALL_RADIUS / 2 );
        setBallSpeed();
    }

    private void addHeart1(double x, double y) {
        heart1 = new GImage("heart-removebg-preview.png", x ,y);
        heart1.scale(0.1, 0.1);
        add(heart1);
    }

    private void addHeart2(double x, double y) {
        heart2 = new GImage("heart-removebg-preview.png", x  + heart1.getWidth(), y);
        heart2.scale(0.1, 0.1);
        add(heart2);
    }

    private void addHeart3(double x, double y) {
        heart3 = new GImage("heart-removebg-preview.png", x + 2 * heart1.getWidth(), y);
        heart3.scale(0.1, 0.1);
        add(heart3);
    }

    private void deleteALlHearts() {
        remove(heart1);
        remove(heart2);
        remove(heart3);
    }

    private void drawAllHearts(double x, double y) {
        addHeart1(x, y);
        addHeart2(x, y);
        addHeart3(x, y);
    }

    private void drawTwoHearts(double x, double y) {
        addHeart1(x, y);
        addHeart2(x, y);
    }

    private void deleteTwoHearts() {
        remove(heart1);
        remove(heart2);
    }

    private void drawOneHeart(double x, double y) {
        addHeart1(x, y);
    }

    private void deleteOneHeart() {
        remove(heart1);
    }

    private void drawNeededHearts(int amount) {
        if (amount == 1)
            drawOneHeart(FIELD.getWidth(),0);
        if (amount == 2)
            drawTwoHearts(FIELD.getWidth(),0);
        if (amount == 3)
            drawAllHearts(FIELD.getWidth(),0);
    }

    private void deleteNeededHearts(int amount) {
        if (amount == 1)
            deleteOneHeart();
        if (amount == 2)
            deleteTwoHearts();
        if (amount == 3)
            deleteALlHearts();
    }

    private void drawBrick(double x, double y, Color c) {
        GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
        brick.setColor(c);
        brick.setFilled(true);
        add(brick);
    }

    private void renderALlBricks() {
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
                drawBrick((i + 1) * BRICK_SEP + i * BRICK_WIDTH,  BRICK_Y_OFFSET + j * BRICK_HEIGHT + j * BRICK_SEP, c);

            }

        }

    }


    // TO FIX
    public void checkCollisionBallWithBrick() {
        if ((getElementAt(BALL.getX() ,BALL.getY()) != null)
                && (getElementAt(BALL.getX(),BALL.getY()) != FIELD)
                && (getElementAt(BALL.getX(),BALL.getY() ) != PADDLE)
        ) {
            remove(getElementAt(BALL.getX() , BALL.getY()));
            vy *= -1;
            amountOfBricks--;
            changeWidthOfPaddle(amountOfBricks);
            pause(DELAY);
        }
        if ((getElementAt(BALL.getX() + BALL.getWidth(),BALL.getY()) != null)
                && (getElementAt(BALL.getX() + BALL.getWidth(),BALL.getY()) != FIELD)
                && (getElementAt(BALL.getX()+ BALL.getWidth(),BALL.getY() ) != PADDLE)
        ) {
            remove(getElementAt(BALL.getX() + BALL.getWidth(), BALL.getY() ));
            vy *= -1;
            amountOfBricks--;
            changeWidthOfPaddle(amountOfBricks);
            pause(DELAY);
        } {
            if ((getElementAt(BALL.getX() ,BALL.getY() + BALL.getHeight()) != null)
                    && (getElementAt(BALL.getX() ,BALL.getY() + BALL.getHeight()) != FIELD)
                    && (getElementAt(BALL.getX(),BALL.getY() + BALL.getHeight()) != PADDLE)
            ) {
                remove(getElementAt(BALL.getX() , BALL.getY() + BALL.getHeight()));
                vy *= -1;
                amountOfBricks--;
                changeWidthOfPaddle(amountOfBricks);
                pause(10);
            }
            if ((getElementAt(BALL.getX() + BALL.getWidth(),BALL.getY() + BALL.getHeight()) != null)
                    && (getElementAt(BALL.getX() + BALL.getWidth(),BALL.getY() + BALL.getHeight()) != FIELD)
                    && (getElementAt(BALL.getX()+ BALL.getWidth(),BALL.getY() + BALL.getHeight()) != PADDLE)
            ) {
                remove(getElementAt(BALL.getX() + BALL.getWidth(), BALL.getY() + BALL.getHeight()));
                vy *= -1;
                amountOfBricks--;
                changeWidthOfPaddle(amountOfBricks);
                pause(10);
            }


        }



    }

    private void addField(double x, double y) {
        FIELD = new GRect(x, y, x + 3 * WIDTH / 2, y + HEIGHT);
        add(FIELD);
    }

    private void resultLabel(String str) {
        GLabel label = new GLabel(str, FIELD.getWidth(), FIELD.getHeight() / 2);
        label.setFont("Italic-30");
        add(label);
    }

    /* Method: run() */
    /** Runs the Breakout program. */
    public void run() {
        /* You fill this in, along with any subsidiary methods */
        this.setSize(WIDTH * 2, HEIGHT + 60);
        addField(0,0);
        setBall();

        BRICK_WIDTH = (FIELD.getWidth() - (NBRICKS_PER_ROW + 1) * BRICK_SEP) / NBRICKS_PER_ROW;
        renderALlBricks();
        drawPaddle(FIELD.getWidth()  / 2 - PADDLE_WIDTH / 2, FIELD.getHeight() - PADDLE_Y_OFFSET);


        drawNeededHearts(3);
        while (LIFES > 0 && !gameOver && amountOfBricks > 0) {

            moveBall();
            //drawTraceLine(BALL.getX() + BALL.getWidth(), BALL.getY() + BALL.getHeight());
            checkCollisionBallWithBrick();
            checkBallCollisionWithWalls();

            checkBallCollisionWithBottomWall();
            if (vx == 0 && vy == 0){
                pause(1000);
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
        }


        setFinalScreen(amountOfBricks == 0 && LIFES > 0);
    }

    private void setFinalScreen(boolean winner){
        GImage finalScreen;
        if (winner)
            finalScreen = new GImage("winner.jpg");
        else
            finalScreen = new GImage("losser.jpg");
        finalScreen.setSize(getWidth(), getHeight());
        add(finalScreen);
    }


}
