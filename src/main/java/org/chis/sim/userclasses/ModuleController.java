package org.chis.sim.userclasses;

import org.chis.sim.Constants;
import org.chis.sim.userclasses.Calculate.PIDF;

class ModuleController{

    ModuleState state;

    PIDF anglePIDF = new PIDF(0.1, 0, 0, 0, 0, 0);

    ModuleController(ModuleState initialState){
        state = initialState;
    }

    ModulePowers rotateModule(double targetModuleAngle){
        double closestModuleAngle = calcClosestModuleAngle(state.moduleAngle, targetModuleAngle);

        double rotPower = anglePIDF.loop(state.moduleAngle, closestModuleAngle); //TODO: replace with 2 state target: angle and linvelo

        return new ModulePowers(rotPower, rotPower);
    }

    double calcClosestModuleAngle(double currentAngle, double targetAngle){
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

    void updateState(double topEncoderPos, double bottomEncoderPos, double topEncoderVelo, double bottomEncoderVelo){
        state.wheelAngle = calcWheelAngle(topEncoderPos, bottomEncoderPos);
        state.moduleAngle = calcModuleAngle(topEncoderPos, bottomEncoderPos);
        state.wheelAngVelo = calcWheelAngle(topEncoderVelo, bottomEncoderVelo);
        state.moduleAngVelo = calcModuleAngle(topEncoderVelo, bottomEncoderVelo);
    }


    private double calcWheelAngle(double motorTop, double motorBottom){
        double avgMotorTicks = (motorTop - motorBottom) / 2.0; //wheel rotation is difference in motors / 2
        double avgMotorRevs = avgMotorTicks / Constants.TICKS_PER_REV.getDouble(); //convert encoder ticks to revolutions
        double moduleAngleRevs = avgMotorRevs / Constants.RINGS_GEAR_RATIO.getDouble(); //module angle, in revolutions
        double moduleAngleDeg = moduleAngleRevs * 360; //convert revolutions to degrees
        return moduleAngleDeg;
    }
    private double calcModuleAngle(double motorTop, double motorBottom){
        double avgMotorTicks = (motorTop + motorBottom) / 2.0; //module rotation is the average of the motors
        double avgMotorRevs = avgMotorTicks / Constants.TICKS_PER_REV.getDouble(); //convert encoder ticks to revolutions
        double moduleAngleRevs = avgMotorRevs / Constants.RINGS_GEAR_RATIO.getDouble(); //module angle, in revolutions
        double moduleAngleDeg = moduleAngleRevs * 360; //convert revolutions to degrees
        return moduleAngleDeg;
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
        double wheelAngle;
        double moduleAngle;

        double wheelAngVelo;
        double moduleAngVelo;

        boolean reversed;

        ModuleState(double wheelAngle, double moduleAngle, double wheelAngVelo, double moduleAngVelo, boolean reversed){
            this.wheelAngle = wheelAngVelo;
            this.moduleAngle = moduleAngle;
            this.wheelAngVelo = wheelAngVelo;
            this.moduleAngVelo = moduleAngVelo;
            this.reversed = reversed;
        }

    }
}