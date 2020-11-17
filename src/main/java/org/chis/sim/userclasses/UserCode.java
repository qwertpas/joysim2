package org.chis.sim.userclasses;

import java.awt.Color;

import org.chis.sim.Constants;
import org.chis.sim.GraphicDebug;
import org.chis.sim.Main;
import org.chis.sim.GraphicDebug.Serie;


public class UserCode{

    static double lPower, rPower;

    public static void initialize(){ //this function is run once when the robot starts
        GraphicDebug.turnOnAll(); //displaying the graphs
    }

    public static void execute(){ //this function is run 50 times a second (every 0.02 second)

        lPower = 1;
        rPower = 1;
        

        setDrivePowers(lPower, rPower);

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
    static GraphicDebug velocityWindow = new GraphicDebug("Velocity", new Serie[]{currentVelocitySerie}, 100);

    
    private static void graph(){
        currentVelocitySerie.addPoint(Main.elaspedTime, 0.5*(getLeftVelo() + getRightVelo()));

        GraphicDebug.paintAll();
    }




}