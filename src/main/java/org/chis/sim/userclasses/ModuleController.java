package org.chis.sim.userclasses;

import org.chis.sim.userclasses.Calculate.PIDF;

class ModuleController{

    ModuleState state;

    PIDF anglePIDF = new PIDF(0.1, 0, 0, 0, 0, 0);

    ModuleController(ModuleState initialState){
        state = initialState;
    }

    ModulePowers rotate(double targetAngle){
        double closestAngle = calcClosestAngle(targetAngle, state.angVelo);

        double rotPower = anglePIDF.loop(state.angle, closestAngle); //TODO: replace with 2 state target: angle and linvelo

        return new ModulePowers(rotPower, rotPower);
    }

    double calcClosestAngle(double targetAngle, double currentAngle){
        double difference180 = (currentAngle - targetAngle) % 180; //angle error from (-180, 180)

        double closestAngle;
        if(Math.abs(difference180) < 90){ //chooses closer of the two acceptable angles closest to currentAngle
            closestAngle = currentAngle - difference180;
        }else{
            closestAngle = currentAngle - difference180 + Math.copySign(180, difference180);
        }

        double difference360 = (int)(closestAngle - targetAngle) % 360;
        state.reversed = (Math.abs(difference360) == 180); //reverses module direction 

        return closestAngle;
    }

    void updateState(double topEncoderValue, double bottomEncoderValue){
        state.angle = determineAngle(topEncoderValue, bottomEncoderValue);
    }

    private double determineAngle(double motorTop, double motorBottom){
        double gearRatio = 1024.0;
        double avgMotor = ((motorTop + motorBottom)/2.0);
        double angleValue = (avgMotor/gearRatio) * 360.0;
        return angleValue;
    }

    public class ModulePowers{
        double topPower;
        double bottomPower;
        ModulePowers(double topPower, double bottomPower){
            this.topPower = topPower;
            this.bottomPower = bottomPower;
        }
    }

    public class ModuleState{
        double linVelo;
        double angVelo;
        double angle;
        boolean reversed;
        ModuleState(double linVelo, double angVelo, double angle, boolean reversed){
            this.linVelo = linVelo;
            this.angVelo = angVelo;
            this.angle = angle;
            this.reversed = reversed;
        }
    }
}