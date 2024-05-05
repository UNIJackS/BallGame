// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP102 - 2024T1, Assignment 6
 * Name:
 * Username:
 * ID:
 */

 import ecs100.*;
 import java.awt.Color;
 import java.util.*;
 
 /** Runs a simulation of launching balls at a target.
  */
 
 public class BallGame{
 
     public static final double GROUND = 450;
     public static final double LAUNCHER_X = 20;      // The initial position of the ball being launched
     public static final double LAUNCHER_HEIGHT = 20; // The initial height of the ball being launched
     public static final double RIGHT_END = 600;
     public static final double SHELF_X = 400;
     public static final double MAX_SPEED = 14;
 
     private Ball ball; // the ball that is being launched towards the target
     // needs to be in a field because two different methods need to access it.
 
     /** Setup the mouse listener and the buttons */
     public void setupGUI(){
         UI.setMouseListener(this::launch);         // the mouse will launch the ball
         UI.addButton("Run game", this::runGame);   // Initialises the game and runs the simulation loop
         UI.addButton("Quit", UI::quit);
         UI.setWindowSize(650,500);
         UI.setDivider(0);
     }	
 
     /**
      * Main loop
      * It creates a ball (to be launched) and a target ball (on a shelf)
      * It has a loop that repeatedly
      *   - Makes a new ball if the old one has gone off the right end.
      *   - Makes the ball and the target ball take one step
      *     (unless they are still on the launcher or shelf)
      *   - Checks whether the ball is hitting the target
      *   - redraws the state of the game
      * The loop stops when the target has gone off the right end and the ball is on the launcher.
      */
     public void runGame(){
         UI.printMessage("Click Mouse to launch the ball");
         this.ball = new Ball(LAUNCHER_X, LAUNCHER_HEIGHT);
 
         double shelf1Ht = 50+Math.random()*400;
         Ball target1 = new Ball(SHELF_X, shelf1Ht);
 
         this.drawGame(this.ball, target1, shelf1Ht);
         int count = 0; 
 
         // run until the target is gone (ie, off the right end)
         while (ball.getX()!=LAUNCHER_X || target1.getX()<RIGHT_END){
 
             //if the ball is over the right end, make a new one.
             if (ball.getX()>=RIGHT_END) {
                 this.ball = new Ball(LAUNCHER_X, LAUNCHER_HEIGHT);
                 count++;
             }
 
             //move the ball, if it isn't on the launcher
             if (ball.getX() > LAUNCHER_X){
                 this.ball.step();
             }            
 
             // move target if it isn't on the shelf
             if (target1.getX()!= SHELF_X){
                 target1.step();
             }
 
             //if ball is hitting the target ball on the shelf, then make it start moving too
             double dist = Math.hypot(target1.getX()-this.ball.getX(), target1.getHeight()-this.ball.getHeight());
             if (target1.getX()==SHELF_X && dist <= Ball.DIAM){
                 target1.setXSpeed(2);
                 target1.step();
             }
 
             //redraw the game and pause
             this.drawGame(this.ball, target1, shelf1Ht);
 
             UI.sleep(40); // pause of 40 milliseconds
 
         }
         UI.setFontSize(40);
         UI.drawString(count+" tries", 200, 200);
     }
 
     /**
      * Launch the current ball, if it is still in the catapult,
      * Speed is based on the position of the mouse relative to the ground.
      */
     public void launch(String action, double x, double y){
         if (action.equals("released")){
             if (this.ball==null) {
                 UI.printMessage("Press Core/Completion button first to create a ball");
                 return;  // the ball hasn't been constructed yet.
             }            
             if (this.ball.getX()==LAUNCHER_X && this.ball.getHeight()==LAUNCHER_HEIGHT){
                 double speedX = (x-LAUNCHER_X)/10;
                 double speedUp = (GROUND - LAUNCHER_HEIGHT - y)/10;
                 double speed = Math.hypot(speedUp, speedX);
                 //scale down if over the maximum allowed speed
                 if (speed> MAX_SPEED){
                     speedUp = speedUp * MAX_SPEED/speed;
                     speedX = speedX * MAX_SPEED/speed;
                 }
                 this.ball.setXSpeed(speedX);
                 this.ball.setYSpeed(speedUp);
                 this.ball.step();
             }
         }
     }
 
     /**
      * Draw the game: ball, target, ground, launcher and shelf.
      */
     public void drawGame(Ball ball, Ball target1, double shelf1Ht){        
         UI.clearGraphics();
         ball.draw();
         target1.draw();
 
         // draw ground, wall, launcher, and shelf
         UI.setColor(Color.black);
         UI.setLineWidth(2);
         UI.eraseRect(RIGHT_END, 0, RIGHT_END+100, GROUND);
         UI.drawLine(LAUNCHER_X, GROUND, RIGHT_END, GROUND);
         UI.drawLine(RIGHT_END, GROUND, RIGHT_END, 0);
         UI.drawLine(LAUNCHER_X, GROUND, LAUNCHER_X, GROUND-LAUNCHER_HEIGHT);
         UI.drawLine(LAUNCHER_X-Ball.DIAM/2, GROUND-LAUNCHER_HEIGHT, LAUNCHER_X+Ball.DIAM/2, GROUND-LAUNCHER_HEIGHT);
         UI.drawLine(SHELF_X-Ball.DIAM/2, GROUND-shelf1Ht, SHELF_X+Ball.DIAM/2, GROUND-shelf1Ht);
     }
 
     
     // Main
     /** Create a new BallGame object and setup the interface */
     public static void main(String[] arguments){
         BallGame bg = new BallGame();
         bg.setupGUI();
     }
 
 }
 