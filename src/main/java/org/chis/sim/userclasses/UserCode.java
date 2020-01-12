package org.chis.sim.userclasses;

import java.awt.Color;

import org.chis.sim.Controls;
import org.chis.sim.GraphicDebug;
import org.chis.sim.Main;
import org.chis.sim.GraphicDebug.Serie;


public class UserCode{

    public static double lPower;
    public static double rPower;

    public static void initialize(){ //this function is run once when the robot starts
        GraphicDebug.turnOnAll(); //displaying the graphs
    }

    public static void execute(){ //this function is run 50 times a second (every 0.02 second)


        








        lPower = -Controls.rawY;
        rPower = -Controls.rawY;

        setDrivePowers(lPower, rPower); //power ranges from -1 to 1

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