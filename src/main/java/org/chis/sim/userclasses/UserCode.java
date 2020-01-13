package org.chis.sim.userclasses;

import java.awt.Color;

import org.chis.sim.Constants;
import org.chis.sim.Controls;
import org.chis.sim.GraphicDebug;
import org.chis.sim.Main;
import org.chis.sim.GraphicDebug.Serie;
import org.chis.sim.userclasses.DeltaVeloDrive.DeltaVeloState;
import org.chis.sim.userclasses.DeltaVeloDrive.DrivePowers;


public class UserCode{

    

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

        targetLinVelo = -Controls.rawY * 3.4;
        targetAngVelo = Controls.rawX * Controls.rawX * Math.copySign(5, Controls.rawX);

        if(Math.abs(targetAngVelo) < 0.5){
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

        lPower = 0.05 * (
            errorInDelta * -0.22 + 
            errorInLVelo * 1.2 +
            errorInRVelo * -1.15
        );
        rPower = 0.05 * (
            errorInDelta * 0.22 + 
            errorInLVelo * -1.15 +
            errorInRVelo * 1.2
        );

        setDrivePowers(lPower, rPower);
        // setDrivePowers(-Controls.rawY, -Controls.rawY);
        // setDrivePowers(-Controls.rawY, Controls.rawY);

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
        return (encoder / Constants.TICKS_PER_REV.getDouble()) * Constants.GEAR_RATIO.getDouble() * 2 * Math.PI * Constants.WHEEL_RADIUS.getDouble();
    } 


    // Motion graphs
    

    static Serie currentVelocitySerie = new Serie(Color.BLUE, 3);
    static Serie targetVelocitySerie = new Serie(Color.RED, 3);
    static GraphicDebug velocityWindow = new GraphicDebug("Velocity", new Serie[]{currentVelocitySerie, targetVelocitySerie}, 100);

    static Serie currentDeltaSerie = new Serie(Color.BLUE, 3);
    static Serie targetDeltaSerie = new Serie(Color.RED, 3);
    static GraphicDebug positionWindow = new GraphicDebug("Delta", new Serie[]{currentDeltaSerie, targetDeltaSerie}, 100);

    static Serie lPowerSerie = new Serie(Color.YELLOW, 3);
    static Serie rPowerSerie = new Serie(Color.GREEN, 3);
    static GraphicDebug powerWindow = new GraphicDebug("Power", new Serie[]{lPowerSerie, rPowerSerie}, 100);
    
    private static void graph(){
        currentVelocitySerie.addPoint(Main.elaspedTime, getLeftVelo());
        targetVelocitySerie.addPoint(Main.elaspedTime, targetLVelo);

        currentDeltaSerie.addPoint(Main.elaspedTime, getRightDist() - getLeftDist());
        targetDeltaSerie.addPoint(Main.elaspedTime, targetDelta);

        lPowerSerie.addPoint(Main.elaspedTime, lPower);
        rPowerSerie.addPoint(Main.elaspedTime, rPower);

        GraphicDebug.paintAll();
    }




}