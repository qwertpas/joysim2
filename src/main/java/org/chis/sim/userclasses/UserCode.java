package org.chis.sim.userclasses;

import java.awt.Color;

import org.chis.sim.Controls;
import org.chis.sim.GraphicDebug;
import org.chis.sim.Main;
import org.chis.sim.GraphicDebug.Serie;
import org.chis.sim.userclasses.DeltaVeloDrive.DeltaVeloState;
import org.chis.sim.userclasses.DeltaVeloDrive.DrivePowers;


public class UserCode{

    public static double lPower;
    public static double rPower;


    static DeltaVeloDrive drive = new DeltaVeloDrive();
    static DeltaVeloState currentState;
    static DeltaVeloState targetState;


    public static void initialize(){ //this function is run once when the robot starts
        GraphicDebug.turnOnAll(); //displaying the graphs
    }

    public static void execute(){ //this function is run 50 times a second (every 0.02 second)

        currentState = new DeltaVeloState(
            Main.robot.rightEncoderPosition() - Main.robot.leftEncoderPosition(), 
            Main.robot.leftEncoderVelocity(), 
            Main.robot.rightEncoderVelocity()
        );
        targetState = new DeltaVeloState(
            0, 
            -Controls.rawY, 
            -Controls.rawY
        );
        DrivePowers powers = drive.calcDrivePowers(currentState, targetState).scale(1);


        lPower = powers.lPower;
        rPower = powers.rPower;

        setDrivePowers(lPower, rPower); //power ranges from -1 to 1
        // setDrivePowers(-Controls.rawY, -Controls.rawY); //power ranges from -1 to 1


        graph(); //updating the graphs
    }

    private static void setDrivePowers(double lPower, double rPower){
        Main.robot.leftGearbox.setPower(lPower);
        Main.robot.rightGearbox.setPower(rPower);
    }


    // Motion graphs
    static Serie currentPositionSerie = new Serie(Color.BLUE, 3);
    static GraphicDebug positionWindow = new GraphicDebug("Position", new Serie[]{currentPositionSerie}, 100);

    static Serie currentVelocitySerie = new Serie(Color.BLUE, 3);
    static GraphicDebug velocityWindow = new GraphicDebug("Velocity", new Serie[]{currentVelocitySerie}, 100);
    
    private static void graph(){
        currentPositionSerie.addPoint(Main.elaspedTime, Main.robot.leftEncoderPosition());
        currentVelocitySerie.addPoint(Main.elaspedTime, Main.robot.leftEncoderVelocity());

        GraphicDebug.paintAll();
    }




}