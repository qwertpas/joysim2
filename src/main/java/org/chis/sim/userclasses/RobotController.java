package org.chis.sim.userclasses;

import org.chis.sim.Constants;
import org.chis.sim.Util.Vector2D;
import org.chis.sim.Util.Vector2D.Type;
import org.chis.sim.userclasses.ModuleController.ModuleState;

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
        Vector2D targetLeftVelo = new Vector2D(
            targetRobotState.linVelo.x - Constants.HALF_DIST_BETWEEN_WHEELS * targetRobotState.angVelo, 
            targetRobotState.linVelo.y, 
            Type.CARTESIAN);

        Vector2D targetRightVelo = new Vector2D(
            targetRobotState.linVelo.x + Constants.HALF_DIST_BETWEEN_WHEELS * targetRobotState.angVelo, 
            targetRobotState.linVelo.y, 
            Type.CARTESIAN);

        leftTargetModuleState = new ModuleState(targetLeftVelo.getAngle(), 0, 0, targetLeftVelo.getMagnitude());
        rightTargetModuleState = new ModuleState(targetRightVelo.getAngle(), 0, 0, targetRightVelo.getMagnitude());

        leftController.move(leftTargetModuleState);
        rightController.move(rightTargetModuleState);
    }

    public void updateModuleStates(
        double leftTopEncoderPosition,
        double leftBottomEncoderPosition,
        double leftTopEncoderVelocity,
        double leftBottomEncoderVelocity,
        double rightTopEncoderPosition,
        double rightBottomEncoderPosition,
        double rightTopEncoderVelocity,
        double rightBottomEncoderVelocity
    ){
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
        public double angVelo;
        public RobotState(Vector2D linVelo, double angVelo){
            this.linVelo = linVelo;
            this.angVelo = angVelo;
        }
        public RobotState(){
            this.linVelo = new Vector2D();
            this.angVelo = 0;
        }
    }

    









}