package org.chis.sim.userclasses;

import java.awt.Color;

import org.chis.sim.*;
import org.chis.sim.GraphicDebug.Serie;
import org.chis.sim.userclasses.joystickDrives.Drive;
import org.chis.sim.userclasses.joystickDrives.YPlusXDrive;
import org.chis.sim.userclasses.joystickDrives.Drive.DrivePowers;


public class UserCode{

    static GraphicDebug printOuts = new GraphicDebug("Print Outs");
    static Serie currentVelocitySerie = new Serie(Color.BLUE, 3);
    static GraphicDebug velocityWindow = new GraphicDebug("Velocity", new Serie[]{currentVelocitySerie}, 200);

    public static void initialize(){ //this function is run once when the robot starts
        GraphicDebug.turnOnAll(); //displaying the graphs
    }

    public static void execute(){ //this function is run 50 times a second (every 0.02 second)

        Drive drive = new YPlusXDrive();
        DrivePowers powers = drive.calcPowers(Controls.rawX, Controls.rawY, 0, 0, 0, 0, 0);

        double lPower = powers.lPower;
        double rPower = powers.rPower;

        Main.robot.leftGearbox.setPower(lPower);
        Main.robot.rightGearbox.setPower(rPower);


        //printing and graphing values for debugging
        printOuts.addText("LeftDist", getLeftDist());
        printOuts.addText("RightDist", getRightDist());

        currentVelocitySerie.addPoint(Main.elaspedTime, 0.5*(getLeftVelo() + getRightVelo()));
        GraphicDebug.paintAll();    
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


    



}