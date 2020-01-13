package org.chis.sim.userclasses;

import org.chis.sim.Constants;
import org.ejml.simple.SimpleMatrix;

public class DeltaVeloDrive{

    public double targetDelta = 0;
    public DeltaVeloState targetState;

    public DeltaVeloState calcTargetState(DeltaVeloState currentState, double targetLinVelo, double targetAngVelo){
        if(targetAngVelo > 1){
            targetDelta = currentState.deltaPos;
        }
        double targetLVelo = targetLinVelo - targetAngVelo * Constants.HALF_DIST_BETWEEN_WHEELS;
        double targetRVelo = targetLinVelo + targetAngVelo * Constants.HALF_DIST_BETWEEN_WHEELS;
        targetState = new DeltaVeloState(targetDelta, targetLVelo, targetRVelo);
        return targetState;
    }

    public DrivePowers calcDrivePowers(DeltaVeloState currentState, double targetLinVelo, double targetAngVelo){

        DeltaVeloState targetState = calcTargetState(currentState, targetLinVelo, targetAngVelo);

        double deltaError = currentState.deltaPos - targetState.deltaPos;
        double lVeloError = currentState.lVelo - targetState.lVelo;
        double rVeloError = currentState.rVelo - targetState.rVelo;
        
        double lCorrectionPower = 
            lVeloError * -0.01 +
            deltaError * 0
        ;
        double rCorrectionPower = 
            rVeloError * -0.01 -
            deltaError * 0
        ;
        return new DrivePowers(lCorrectionPower, rCorrectionPower);
    }

    public static class DeltaVeloState{
        public double deltaPos;
        public double lVelo;
        public double rVelo;

        public DeltaVeloState(double deltaPos, double lVelo, double rVelo){
            this.deltaPos = deltaPos;
            this.lVelo = lVelo;
            this.rVelo = rVelo;
        }
        
        public SimpleMatrix get(){
            return new SimpleMatrix(new double[][]{
                { deltaPos },
                { lVelo },
                { rVelo }
            });
        }

        public boolean goingStraight(){
            return Math.abs(lVelo - rVelo) < 100;
        }
    }

    public class DrivePowers{
        public double lPower, rPower;
        public DrivePowers(double lPower, double rPower){
            this.lPower = lPower;
            this.rPower = rPower;
        }
        public DrivePowers scale(double factor){
            return new DrivePowers(lPower * factor, rPower * factor);
        }
    }
}