package org.chis.sim.userclasses;

import java.awt.Color;

import org.chis.sim.Constants;
import org.chis.sim.Controls;
import org.chis.sim.GraphicDebug;
import org.chis.sim.Main;
import org.chis.sim.GraphicDebug.Serie;
import org.chis.sim.userclasses.joystickDrives.*;
import org.chis.sim.userclasses.joystickDrives.Drive.DrivePowers;


public class UserCode{

    public static Drive drive;
    public static double lPower, rPower;

    public static void initialize(){ //this function is run once when the robot starts
        GraphicDebug.turnOnAll(); //displaying the graphs
        switch(Constants.DRIVE_OPTION.getInt()){
            case 0:
                drive = new DeltaVeloDrive();
                break;
            case 1:
                drive = new ConstantRadiusDrive();
                break;
            case 2:
                drive = new YPlusXDrive();
                break;
            case 3:
                drive = new SimpleForwardDrive();
                break;
        }
    }

    public static void execute(){ //this function is run 50 times a second (every 0.02 second)

        

        DrivePowers powers = drive.calcPowers(Controls.rawX, Controls.rawY, Controls.rawZ, getLeftDist(), getRightDist(), getLeftVelo(), getRightVelo());
        lPower = powers.lPower;
        rPower = powers.rPower;

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
    static Serie targetVelocitySerie = new Serie(Color.RED, 3);
    static GraphicDebug velocityWindow = new GraphicDebug("Velocity", new Serie[]{currentVelocitySerie, targetVelocitySerie}, 100);

    static Serie currentDeltaSerie = new Serie(Color.BLUE, 3);
    static Serie targetDeltaSerie = new Serie(Color.RED, 3);
    static GraphicDebug positionWindow = new GraphicDebug("Delta", new Serie[]{currentDeltaSerie, targetDeltaSerie}, 100);

    static Serie currentAngVeloSerie = new Serie(Color.BLUE, 3);
    static Serie targetAngVeloSerie = new Serie(Color.RED, 3);
    static GraphicDebug powerWindow = new GraphicDebug("AngVelo", new Serie[]{currentAngVeloSerie, targetAngVeloSerie}, 100);
    
    private static void graph(){
        currentVelocitySerie.addPoint(Main.elaspedTime, getLeftVelo());
        targetVelocitySerie.addPoint(Main.elaspedTime, drive.targetLVelo);

        currentDeltaSerie.addPoint(Main.elaspedTime, getRightDist() - getLeftDist());
        targetDeltaSerie.addPoint(Main.elaspedTime, drive.targetDelta);

        currentAngVeloSerie.addPoint(Main.elaspedTime, Main.robot.angVelo);
        targetAngVeloSerie.addPoint(Main.elaspedTime, drive.targetAngVelo);

        GraphicDebug.paintAll();
    }




}