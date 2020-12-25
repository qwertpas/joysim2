package org.chis.sim.userclasses;

import java.awt.Color;
import java.util.ArrayList;

import org.chis.sim.*;
import org.chis.sim.Util.Vector2D;
import org.chis.sim.Util.Vector2D.Type;
import org.chis.sim.GraphicDebug.Serie;
import org.chis.sim.userclasses.joystickDrives.CheesyDrive;
import org.chis.sim.userclasses.joystickDrives.Drive;
import org.chis.sim.userclasses.joystickDrives.YPlusXDrive;
import org.chis.sim.userclasses.joystickDrives.Drive.DrivePowers;


public class UserCode{

    static GraphicDebug printOuts = new GraphicDebug();

    static Drive drive = new YPlusXDrive();

    public static void initialize(){ //this function is run once when the robot starts
    }

    public static void execute(){ //this function is run 50 times a second (every 0.02 second)

        DrivePowers powers = drive.calcPowers(Controls.rawX, Controls.rawY, 0, 0, 0, 0, 0);

        Main.robot.leftGearbox.setPower(powers.lPower);
        Main.robot.rightGearbox.setPower(powers.rPower);

        // Main.robot.leftGearbox.setPower(0);
        // Main.robot.rightGearbox.setPower(0);

        //printing values in separate window
        printOuts.putNumber("x", Main.robot.x);
        printOuts.putNumber("y", Main.robot.y);
        printOuts.putNumber("Heading", Main.robot.heading);

        //plotting points relative to the robot
        ArrayList<Vector2D> path = new ArrayList<Vector2D>();
        for(double t = -2; t < 2; t += 0.05){

            //Parametric equation in terms of t
            double x_t = t;
            double y_t = t*t;

            Vector2D pathpoint_global = new Vector2D(x_t, y_t, Type.CARTESIAN);

            Vector2D pathpoint_robot = pathpoint_global.subtract(Main.robot.getPos()).rotate(-Main.robot.heading);
            // Vector2D pathpoint_robot = pathpoint_global.rotate(Main.robot.heading);

            // path.add(pathpoint_global);
            path.add(pathpoint_robot);
        }
        GraphicSim.drawPoints(path);
    }




    
    private static double convertEncoderΤοDist(double encoder){
        return encoder / Constants.TICKS_PER_REV.getDouble() / Constants.GEAR_RATIO.getDouble() * 2 * Math.PI * Constants.WHEEL_RADIUS.getDouble();
    } 


    



}