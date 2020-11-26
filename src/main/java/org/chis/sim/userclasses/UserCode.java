package org.chis.sim.userclasses;

import java.awt.Color;

import org.chis.sim.*;
import org.chis.sim.GraphicDebug.Serie;
import org.chis.sim.userclasses.joystickDrives.Drive;
import org.chis.sim.userclasses.joystickDrives.YPlusXDrive;
import org.chis.sim.userclasses.joystickDrives.Drive.DrivePowers;


public class UserCode{

    static GraphicDebug printOuts = new GraphicDebug();
    static Serie leftVeloSerie = new Serie(Color.BLUE, 3);
    static Serie rightVeloSerie = new Serie(Color.RED, 3);
    static GraphicDebug velocityWindow = new GraphicDebug("Velocity", 200, true, leftVeloSerie, rightVeloSerie);
    static Serie joystickSerie = new Serie(Color.GRAY, 10);
    static GraphicDebug joystickWindow = new GraphicDebug("Joystick Position", 1, false, joystickSerie);



    public static void initialize(){ //this function is run once when the robot starts
    }

    public static void execute(){ //this function is run 50 times a second (every 0.02 second)

        Drive drive = new YPlusXDrive();
        DrivePowers powers = drive.calcPowers(Controls.rawX, Controls.rawY, 0, 0, 0, 0, 0);

        double lPower = powers.lPower;
        double rPower = powers.rPower;

        Main.robot.leftGearbox.setPower(lPower);
        Main.robot.rightGearbox.setPower(rPower);


        //printing and graphing values for debugging
        printOuts.putNumber("LeftDist", getLeftDist());
        printOuts.putNumber("RightDist", getRightDist());

        leftVeloSerie.addPoint(Main.elaspedTime, getLeftVelo());
        rightVeloSerie.addPoint(Main.elaspedTime, getRightVelo());
        joystickSerie.addPoint(Controls.rawX, -Controls.rawY);
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