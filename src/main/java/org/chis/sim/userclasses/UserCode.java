package org.chis.sim.userclasses;

import java.awt.Color;

import org.chis.sim.Constants;
import org.chis.sim.Controls;
import org.chis.sim.GraphicDebug;
import org.chis.sim.Main;
import org.chis.sim.GraphicDebug.Serie;


public class UserCode{
    public static final double MAX_SPEED = 3.5;
    public static final double MAX_SPIN = 5;
    public static final double JOYSTICK_DEADBAND = 0.05;
    public static final double SPIN_DEADBAND = 0.1;

    public static double targetLinVelo, targetAngVelo;
    public static double targetDelta, targetLVelo, targetRVelo;
    public static double errorInDelta, errorInLVelo, errorInRVelo;
    public static boolean isGoingStraight = true;

    public static double lPower;
    public static double rPower;

    public static void initialize(){ //this function is run once when the robot starts
        GraphicDebug.turnOnAll(); //displaying the graphs
    }

    public static void execute(){ //this function is run 50 times a second (every 0.02 second)

        if(Math.abs(Controls.rawX) < JOYSTICK_DEADBAND && Math.abs(Controls.rawY) < JOYSTICK_DEADBAND){
            setDrivePowers(0, 0);
        }else{
            targetLinVelo = -Controls.rawY * MAX_SPEED;
            targetAngVelo = Controls.rawX * Controls.rawX * Math.copySign(MAX_SPIN, Controls.rawX);

            if(Math.abs(targetAngVelo) < SPIN_DEADBAND){
                isGoingStraight = true;
            }else{
                isGoingStraight = false;
                targetDelta = (getRightDist() - getLeftDist());
            }
            targetLVelo = targetLinVelo - targetAngVelo * Constants.HALF_DIST_BETWEEN_WHEELS;
            targetRVelo = targetLinVelo + targetAngVelo * Constants.HALF_DIST_BETWEEN_WHEELS;
            
            errorInDelta = targetDelta - (getRightDist() - getLeftDist());
            errorInLVelo = targetLVelo - getLeftVelo();
            errorInRVelo = targetRVelo - getRightVelo();

            lPower = 1 * (
                errorInDelta * -1 + 
                errorInLVelo * 1.2 +
                errorInRVelo * -0.1 +
                Math.copySign(0.1, getLeftVelo()) + getLeftVelo() / MAX_SPEED
            );
            rPower = 1 * (
                errorInDelta * 1 + 
                errorInLVelo * -0.1 +
                errorInRVelo * 1.2 + 
                Math.copySign(0.1, getRightVelo()) + getRightVelo() / MAX_SPEED
            );

            if(Math.abs(lPower) > 1 || Math.abs(rPower) > 1){
                double biggerValue = Math.max(Math.abs(lPower), Math.abs(rPower));
                lPower = lPower / biggerValue;
                rPower = rPower / biggerValue;
            }

            setDrivePowers(lPower, rPower);
        }

        

        graph(); //updating the graphs
    }

    private static void setDrivePowers(double lPower, double rPower){
        Main.robot.leftGearbox.setPower(lPower);
        Main.robot.rightGearbox.setPower(rPower);
    }

    private static double getLeftDist(){
        return convertEncoderΤοDist(Main.robot.leftEncoderPosition());
    }
    private static double getRightDist(){
        return convertEncoderΤοDist(Main.robot.rightEncoderPosition());
    }
    private static double getLeftVelo(){
        return convertEncoderΤοDist(Main.robot.leftEncoderVelocity());
    }
    private static double getRightVelo(){
        return convertEncoderΤοDist(Main.robot.rightEncoderVelocity());
    }

    private static double convertEncoderΤοDist(double encoder){
        return encoder / Constants.TICKS_PER_REV.getDouble() / Constants.GEAR_RATIO.getDouble() * 2 * Math.PI * Constants.WHEEL_RADIUS.getDouble();
    } 


    // Motion graphs
    

    static Serie currentVelocitySerie = new Serie(Color.BLUE, 3);
    static Serie targetVelocitySerie = new Serie(Color.RED, 3);
    static GraphicDebug velocityWindow = new GraphicDebug("Velocity", new Serie[]{currentVelocitySerie, targetVelocitySerie}, 100);

    static Serie currentDeltaSerie = new Serie(Color.BLUE, 3);
    static Serie targetDeltaSerie = new Serie(Color.RED, 3);
    static GraphicDebug positionWindow = new GraphicDebug("Delta", new Serie[]{currentDeltaSerie, targetDeltaSerie}, 100);

    static Serie currentAngVeloSerie = new Serie(Color.YELLOW, 3);
    static Serie targetAngVeloSerie = new Serie(Color.GREEN, 3);
    static GraphicDebug powerWindow = new GraphicDebug("AngVelo", new Serie[]{currentAngVeloSerie, targetAngVeloSerie}, 100);
    
    private static void graph(){
        currentVelocitySerie.addPoint(Main.elaspedTime, getLeftVelo());
        targetVelocitySerie.addPoint(Main.elaspedTime, targetLVelo);

        currentDeltaSerie.addPoint(Main.elaspedTime, getRightDist() - getLeftDist());
        targetDeltaSerie.addPoint(Main.elaspedTime, targetDelta);

        currentAngVeloSerie.addPoint(Main.elaspedTime, Main.robot.angVelo);
        targetAngVeloSerie.addPoint(Main.elaspedTime, targetAngVelo);

        GraphicDebug.paintAll();
    }




}