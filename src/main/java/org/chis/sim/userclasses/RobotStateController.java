package org.chis.sim.userclasses;

import org.chis.sim.Util.Vector2D;
import org.chis.sim.Util.Vector2D.Type;
import org.chis.sim.userclasses.ModuleController.ModuleState;
import org.ejml.simple.SimpleMatrix;

public class RobotStateController {

    public RobotState robotState;

    public ModuleController leftController = new ModuleController(new ModuleState());
    public ModuleController rightController = new ModuleController(new ModuleState());

    public ModuleState leftTargetModuleState;
    public ModuleState rightTargetModuleState;

    public SimpleMatrix K = new SimpleMatrix(new double[][]{

        {  5, 2.18752e-15, -22.3607 },
     {  -1.0873e-15, 5, 8.4838e-15 },
     {  5, -1.22604e-15, 22.3607 },
     {  -1.936e-15, 5, 1.0406e-14 }

    });
    public SimpleMatrix u;


    public RobotStateController(RobotState robotState){
        this.robotState = robotState;
    }

    public void move(RobotState targetRobotState){

        u = robotState.getState().minus(targetRobotState.getState());
        SimpleMatrix negKu = K.mult(u).negative();
        
        Vector2D targetLeftVelo = new Vector2D(
            negKu.get(0), 
            negKu.get(1), 
            Type.CARTESIAN);

        Vector2D targetRightVelo = new Vector2D(
            negKu.get(2), 
            negKu.get(3), 
            Type.CARTESIAN);

        leftTargetModuleState = new ModuleState(targetLeftVelo.getAngle(), 0, 0, targetLeftVelo.getMagnitude());
        rightTargetModuleState = new ModuleState(targetRightVelo.getAngle(), 0, 0, targetRightVelo.getMagnitude());

        leftController.move(leftTargetModuleState);
        rightController.move(rightTargetModuleState);
    }

    public void updateState(
        Vector2D robotPosition,
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
        robotState.position = robotPosition;
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
        public Vector2D position;
        public double heading;
        public RobotState(Vector2D position, double heading){
            this.position = position;
            this.heading = heading;
        }
        public RobotState(){
            this.position = new Vector2D();
            this.heading = 0;
        }
        public SimpleMatrix getState(){
            return new SimpleMatrix(new double[][]{
                {position.x},
                {position.y},
                {heading}
            });
        }
    }

    









}