package org.chis.sim.userclasses;

import org.chis.sim.Constants;
import org.chis.sim.Util.Vector2D;
import org.chis.sim.Util.Vector2D.Type;
import org.chis.sim.userclasses.ModuleController.ModuleState;
import org.ejml.simple.SimpleMatrix;

public class RobotController {

    public RobotState robotState;

    public ModuleController leftController = new ModuleController(new ModuleState());
    public ModuleController rightController = new ModuleController(new ModuleState());

    public ModuleState leftTargetModuleState;
    public ModuleState rightTargetModuleState;

    public RobotController(RobotState robotState){
        this.robotState = robotState;
    }

    public void move(RobotState targetRobotState){
        RobotState modifiedTargetState = targetRobotState.copy();
        modifiedTargetState.heading = calcClosestHeading(robotState.heading, targetRobotState.heading);

        double angVelo = 20*(modifiedTargetState.heading - robotState.heading);
        
        Vector2D targetLeftVelo = new Vector2D(
            modifiedTargetState.linVelo.x - angVelo * Constants.HALF_DIST_BETWEEN_WHEELS, 
            modifiedTargetState.linVelo.y, 
            Type.CARTESIAN);

        Vector2D targetRightVelo = new Vector2D(
            modifiedTargetState.linVelo.x + angVelo * Constants.HALF_DIST_BETWEEN_WHEELS, 
            modifiedTargetState.linVelo.y, 
            Type.CARTESIAN);

        leftTargetModuleState = new ModuleState(targetLeftVelo.getAngle(), 0, 0, targetLeftVelo.getMagnitude());
        rightTargetModuleState = new ModuleState(targetRightVelo.getAngle(), 0, 0, targetRightVelo.getMagnitude());

        leftController.move(leftTargetModuleState);
        rightController.move(rightTargetModuleState);
    }

    public double calcClosestHeading(double currentHeading, double targetHeading){
        double difference2Pi = (currentHeading - targetHeading) % (2 * Math.PI); //angle error from (-180, 180)
        double closestHeading;
        if(Math.abs(difference2Pi) < (Math.PI)){ //chooses closer of the two acceptable angles closest to currentAngle
            closestHeading = currentHeading - difference2Pi;
        }else{
            closestHeading = currentHeading - difference2Pi + Math.copySign(2 * Math.PI, difference2Pi);
        }
        return closestHeading;
    }

    public static void main(String[] args) {
        var cont = new RobotController(new RobotState());
        System.out.println(cont.calcClosestHeading(Math.PI+.1, 0));
    }

    public void updateState(
        double robotHeading,     
        double leftTopEncoderPosition,
        double leftBottomEncoderPosition,
        double leftTopEncoderVelocity,
        double leftBottomEncoderVelocity,
        double rightTopEncoderPosition,
        double rightBottomEncoderPosition,
        double rightTopEncoderVelocity,
        double rightBottomEncoderVelocity
    ){
        robotState.heading = robotHeading;
        leftController.updateState(
            leftTopEncoderPosition, 
            leftBottomEncoderPosition, 
            leftTopEncoderVelocity, 
            leftBottomEncoderVelocity);
        rightController.updateState(
            rightTopEncoderPosition, 
            rightBottomEncoderPosition, 
            rightTopEncoderVelocity, 
            rightBottomEncoderVelocity);
    }



    public static class RobotState{
        public Vector2D linVelo;
        public double heading;
        public RobotState(Vector2D linVelo, double heading){
            this.linVelo = linVelo;
            this.heading = heading;
        }
        public RobotState(){
            this.linVelo = new Vector2D();
            this.heading = 0;
        }
        public RobotState copy(){
            return new RobotState(linVelo, heading);
        }
    }

    









}