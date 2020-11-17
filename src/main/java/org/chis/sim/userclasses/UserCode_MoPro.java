package org.chis.sim.userclasses;

import java.awt.Color;
import java.util.Arrays;

import org.chis.sim.GraphicDebug;
import org.chis.sim.Main;
import org.chis.sim.Util;
import org.chis.sim.GraphicDebug.Serie;
import org.chis.sim.Util.MotionProfile;
import org.chis.sim.Util.MotionProfile.MotionProfilePoint;

public class UserCode_MoPro{

    // private static PID pid = new PID(0.007, 0, 0, 0);
    static volatile MotionProfile motionProfile;
    static volatile MotionProfilePoint motion;
    static volatile double power;

    static long time;

    public static void initialize(){ //don't delete this function; it is called by Main.java
        motionProfile = new MotionProfile(Util.metersToInches(3), //max velocity
                                          Util.metersToInches(2), //max acceleration
                                          Util.metersToInches(-2.5), //min acceleration
                                          360 ); //target distance

        
                    
        System.out.println("isTrapezoid profile: " + motionProfile.isTrapezoid);
        System.out.println("time: " + Arrays.toString(motionProfile.times));
        System.out.println("initted usercode");
        motion = motionProfile.getPoint(0); //initial motion point
        GraphicDebug.turnOnAll();
        time = System.nanoTime();
    }

    public static void execute(){ //don't delete this function; it is called by Main.java
        motion = motionProfile.getPoint(Main.elaspedTime);

        double fric_feed = 0.1;
        double x_error = motion.dist - Main.robot.leftEncoderPosition();
        double v_error = motion.velo - Util.metersToInches(Main.robot.linVelo);

        if(!motionProfile.done){
            power = (Math.copySign(fric_feed, Main.robot.linVelo)) + 
            (0.1 * x_error) +
            (0.1 * v_error);  
        } else {
            power = 0;
        }
        setDrivePowers(power, power);

        graph();
    }

    private static void setDrivePowers(double lPower, double rPower){
        Main.robot.leftGearbox.setPower(lPower);
        Main.robot.rightGearbox.setPower(rPower);
    }

    

    // Graphs
    static Serie currentPositionSerie = new Serie(Color.BLUE, 3);
    static Serie targetPositionSerie = new Serie(Color.RED, 3);
    static GraphicDebug positionWindow = new GraphicDebug("Position", new Serie[]{currentPositionSerie, targetPositionSerie}, 100);

    static Serie currentVelocitySerie = new Serie(Color.BLUE, 3);
    static Serie targetVelocitySerie = new Serie(Color.RED, 3);
    static GraphicDebug velocityWindow = new GraphicDebug("Velocity", new Serie[]{currentVelocitySerie, targetVelocitySerie}, 100);
    
    private static void graph(){
        currentPositionSerie.addPoint(Main.elaspedTime, Main.robot.leftEncoderPosition());
        targetPositionSerie.addPoint(Main.elaspedTime, motion.dist);

        currentVelocitySerie.addPoint(Main.elaspedTime, Util.metersToInches(Main.robot.linVelo));
        targetVelocitySerie.addPoint(Main.elaspedTime, motion.velo);
        
        GraphicDebug.paintAll();
    }




}