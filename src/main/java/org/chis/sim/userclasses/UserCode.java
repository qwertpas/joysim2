package org.chis.sim.userclasses;

import java.awt.Color;

import org.chis.sim.*;
import org.chis.sim.GraphicDebug.Serie;


public class UserCode{


    public static void initialize(){ //this function is run once when the robot starts
        GraphicDebug.turnOnAll(); //displaying the graphs
    }

    public static void execute(){ //this function is run 50 times a second (every 0.02 second)
        double lPower = -Controls.rawY - Controls.rawX;
        double rPower = -Controls.rawY + Controls.rawX;

        Main.robot.leftGearbox.setPower(lPower);
        Main.robot.rightGearbox.setPower(rPower);

        graph(); //updating the graphs
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