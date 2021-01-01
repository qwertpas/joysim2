package org.chis.sim.userclasses;

import java.awt.Color;
import java.util.ArrayList;

import org.chis.sim.*;
import org.chis.sim.Util.PID;
import org.chis.sim.Util.Vector2D;
import org.chis.sim.Util.Vector2D.Type;
import org.chis.sim.userclasses.pathplanning.PurePursuit;


public class UserCode{

    static GraphicDash graph1 = new GraphicDash("velocities", 300, true);
    static PID leftPID = new PID();
    static PID rightPID = new PID();

    public static void initialize(){ //this function is run once when the robot starts
        leftPID.setkP(2);
        leftPID.setkI(3, 1, 0.5);
        leftPID.setkD(0.0);
        rightPID.copyConstants(leftPID);
    }

    public static void execute(){ //this function is run 50 times a second (every 0.02 second)

        //plotting points relative to the robot
        ArrayList<Vector2D> path_robot= new ArrayList<Vector2D>();
        ArrayList<Vector2D> path_global= new ArrayList<Vector2D>();

        path_global.add(new Vector2D(-4, 0, Type.CARTESIAN));
        path_global.add(new Vector2D(4, 1, Type.CARTESIAN));
        path_global.add(new Vector2D(6, -2, Type.CARTESIAN));
        path_global.add(new Vector2D(4, -5, Type.CARTESIAN));
        path_global.add(new Vector2D(-6, -4, Type.CARTESIAN));
        path_global.add(new Vector2D(-6, 4, Type.CARTESIAN));
        path_global.add(new Vector2D(-1, 3, Type.CARTESIAN));

        for(Vector2D point : path_global){
            path_robot.add(point.subtract(Main.robot.getPos()).rotate(-Main.robot.heading));
        }

        double lookaheadDist = 1;
        double curvature = PurePursuit.getCurvature(path_robot, lookaheadDist);
        double r = 1/curvature;
        for(double t = -Math.PI; t < Math.PI; t += 0.1){
            double x = r * Math.cos(t);
            double y = r * Math.sin(t) + r;
            path_robot.add(new Vector2D(x, y, Type.CARTESIAN));
            // path_robot.add(new Vector2D(lookaheadDist*Math.cos(t), lookaheadDist*Math.sin(t), Type.CARTESIAN));
        }

        double distRemaining = path_global.get(path_global.size() - 1).subtract(Main.robot.getPos()).getMagnitude();
        double targetvC = 2;

        if(distRemaining < 1) targetvC = distRemaining;

        double track = Constants.HALF_DIST_BETWEEN_WHEELS;
        double targetvL = targetvC * (1 - track * curvature);
        double targetvR = targetvC * (1 + track * curvature);


        double currentvL = convertEncoderΤοDist(Main.robot.leftEncoderVelocity());
        double currentvR = convertEncoderΤοDist(Main.robot.rightEncoderVelocity());
        leftPID.loop(currentvL, targetvL);
        rightPID.loop(currentvR, targetvR);

        double leftPower = leftPID.getPower() + targetvL * 0.3;
        double rightPower = rightPID.getPower() + targetvR * 0.3;
        
        Main.robot.leftGearbox.setPower(leftPower);
        Main.robot.rightGearbox.setPower(rightPower);


        GraphicSim.drawPointsRobot(path_robot);
        GraphicSim.drawPointsGlobal(path_global);

        //printing values in separate window
        Printouts.put("x", Main.robot.x);
        Printouts.put("y", Main.robot.y);
        Printouts.put("Heading", Main.robot.heading);
        Printouts.put("lookahead", PurePursuit.getLookaheadPoint(path_robot, 0, 0, lookaheadDist));
        Printouts.put("curvature", curvature);
        Printouts.put("distRemaining", distRemaining);
        graph1.putNumber("targetvL", targetvL, Color.BLUE);
        graph1.putNumber("currentvL", convertEncoderΤοDist(Main.robot.leftEncoderVelocity()), Color.RED);


    }



    
    private static double convertEncoderΤοDist(double encoder){
        return encoder / Constants.TICKS_PER_REV.getDouble() / Constants.GEAR_RATIO.getDouble() * 2 * Math.PI * Constants.WHEEL_RADIUS.getDouble();
    } 


    



}