package org.chis.sim.userclasses;

import org.chis.sim.Constants;
import org.chis.sim.userclasses.Calculate.PIDF;

public class ModuleController{

    public ModuleState state;

    PIDF anglePIDF = new PIDF(0.1, 0, 0, 0, 0, 0);

    public ModuleController(ModuleState initialState){
        state = initialState;
    }

    public ModulePowers rotateModule(double targetModuleAngle){
        double closestModuleAngle = calcClosestModuleAngle(state.moduleAngle, targetModuleAngle);

        double rotPower = anglePIDF.loop(state.moduleAngle, closestModuleAngle); //TODO: replace with 2 state target: angle and linvelo

        return new ModulePowers(rotPower, rotPower);
    }

    public double calcClosestModuleAngle(double currentAngle, double targetAngle){
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

    public void updateState(double topEncoderPos, double bottomEncoderPos, double topEncoderVelo, double bottomEncoderVelo){
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


    public static class ModulePowers{
        public double topPower;
        public double bottomPower;
        public ModulePowers(double topPower, double bottomPower){
            this.topPower = topPower;
            this.bottomPower = bottomPower;
        }
    }

    public static class ModuleState{
        public double wheelAngle;
        public double moduleAngle;

        public double wheelAngVelo;
        public double moduleAngVelo;

        public boolean reversed;

        public ModuleState(double wheelAngle, double moduleAngle, double wheelAngVelo, double moduleAngVelo, boolean reversed){
            this.wheelAngle = wheelAngVelo;
            this.moduleAngle = moduleAngle;
            this.wheelAngVelo = wheelAngVelo;
            this.moduleAngVelo = moduleAngVelo;
            this.reversed = reversed;
        }

        public ModuleState(){
            this.wheelAngle = 0;
            this.moduleAngle = 0;
            this.wheelAngVelo = 0;
            this.moduleAngVelo = 0;
            this.reversed = false;
        }

    }
}