package org.chis.sim.userclasses;

import org.chis.sim.Constants;
import org.chis.sim.userclasses.Calculate.PIDF;
import org.ejml.simple.SimpleMatrix;

public class ModuleController{

    public ModuleState state;
    public ModuleState modifiedTargetState;
    public boolean reversed;

    private PIDF anglePIDF = new PIDF(0.5, 0, 0, 0.1, 0, 0);

    private SimpleMatrix K = new SimpleMatrix(new double[][] {
        { 7.0711,    0,    0,    2.1},
        { 7.0711,    0,    0,    -2.1}
    });

    public ModuleController(ModuleState initialState){
        state = initialState;
    }

    public ModulePowers move(ModuleState targetState){
        modifiedTargetState = targetState.copy(); //modify to find optimal angle with same results
        modifiedTargetState.moduleAngle = calcClosestModuleAngle(state.moduleAngle, targetState.moduleAngle);
        if(reversed) modifiedTargetState.wheelAngVelo = -targetState.wheelAngVelo;

        SimpleMatrix negKu = K.mult(state.getState().minus(modifiedTargetState.getState())).negative();
        double topVoltage = negKu.get(0, 0);
        double bottomVoltage = negKu.get(1, 0);
        return new ModulePowers(topVoltage/12.0, bottomVoltage/12.0);
    }

    public static void main(String[] args) {
        var mc = new ModuleController(new ModuleState(0, 0, 0, 0));
        System.out.println(mc.calcClosestModuleAngle(1.58, Math.PI));
    }

    public ModulePowers rotateModule(double targetModuleAngle){
        double closestModuleAngle = calcClosestModuleAngle(state.moduleAngle, targetModuleAngle);

        double rotPower = anglePIDF.loop(state.moduleAngle, closestModuleAngle);

        return new ModulePowers(rotPower, rotPower);
    }

    public double calcClosestModuleAngle(double currentAngle, double targetAngle){
        double differencePi = (currentAngle - targetAngle) % Math.PI; //angle error from (-180, 180)

        double closestAngle;
        if(Math.abs(differencePi) < (Math.PI / 2.0)){ //chooses closer of the two acceptable angles closest to currentAngle
            closestAngle = currentAngle - differencePi;
        }else{
            closestAngle = currentAngle - differencePi + Math.copySign(Math.PI, differencePi);
        }

        double difference2Pi = (closestAngle - targetAngle) % (2 * Math.PI);
        reversed = Math.abs(difference2Pi) > (Math.PI / 2.0); //if the difference is closer to 180, reverse direction 

        return closestAngle;
    }

    public void updateState(double topEncoderPos, double bottomEncoderPos, double topEncoderVelo, double bottomEncoderVelo){
        state.moduleAngle = calcModuleAngle(topEncoderPos, bottomEncoderPos);
        state.moduleAngVelo = calcModuleAngle(topEncoderVelo, bottomEncoderVelo);
        state.wheelAngVelo = calcWheelAngle(topEncoderVelo, bottomEncoderVelo);
        state.wheelAngle = calcWheelAngle(topEncoderPos, bottomEncoderPos);
    }


    private double calcWheelAngle(double motorTop, double motorBottom){
        double avgMotorTicks = (motorTop - motorBottom) / 2.0; //wheel rotation is difference in motors / 2
        double avgMotorRevs = avgMotorTicks / Constants.TICKS_PER_REV.getDouble(); //convert encoder ticks to revolutions
        double moduleAngleRevs = avgMotorRevs / Constants.RINGS_GEAR_RATIO.getDouble(); //module angle, in revolutions
        double moduleAngleDeg = moduleAngleRevs * 2 * Math.PI; //convert revolutions to radians
        return moduleAngleDeg;
    }
    private double calcModuleAngle(double motorTop, double motorBottom){
        double avgMotorTicks = (motorTop + motorBottom) / 2.0; //module rotation is the average of the motors
        double avgMotorRevs = avgMotorTicks / Constants.TICKS_PER_REV.getDouble(); //convert encoder ticks to revolutions
        double moduleAngleRevs = avgMotorRevs / Constants.RINGS_GEAR_RATIO.getDouble(); //module angle, in revolutions
        double moduleAngleDeg = moduleAngleRevs * 2 * Math.PI; //convert revolutions to radians
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
        public double moduleAngle;
        public double moduleAngVelo;
        public double wheelAngle;
        public double wheelAngVelo;

        public ModuleState(double moduleAngle, double moduleAngVelo, double wheelAngle, double wheelAngVelo){
            this.moduleAngle = moduleAngle;
            this.moduleAngVelo = moduleAngVelo;
            this.wheelAngle = wheelAngVelo;
            this.wheelAngVelo = wheelAngVelo;
        }

        public ModuleState(){
            this.moduleAngle = 0;
            this.moduleAngVelo = 0;
            this.wheelAngle = 0;
            this.wheelAngVelo = 0;
        }

        public SimpleMatrix getState(){
            return new SimpleMatrix(new double[][]{
                {moduleAngle},
                {moduleAngVelo},
                {wheelAngle},
                {wheelAngVelo}
            });
        }

        public ModuleState copy(){
            return new ModuleState(moduleAngle, moduleAngVelo, wheelAngle, wheelAngVelo);
        }

    }
}