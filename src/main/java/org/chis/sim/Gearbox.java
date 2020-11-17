package org.chis.sim;

public class Gearbox{

    public Motor[] motors;

    private double angVelo;


    public Gearbox(int numMotors){
        this.motors = new Motor[numMotors];
        for(int i = 0; i < numMotors; i++){
            motors[i] = new Motor();
        }
    }

    public double getOutputTorque(){
        double totalTorque = 0;
        for(Motor motor : motors){
            totalTorque += motor.getTorque();
        }
        totalTorque *= Constants.GEAR_RATIO.getDouble();
        return Util.applyFrictions(
            totalTorque,
            angVelo, 
            Constants.GEAR_STATIC_FRIC.getDouble(), 
            Constants.GEAR_KINE_FRIC.getDouble(), 
            Constants.GEAR_FRIC_THRESHOLD.getDouble());
    }

    public void update(double angVelo){
        this.angVelo = angVelo;
        double motorAngVelo = angVelo * Constants.GEAR_RATIO.getDouble();
        for(int i = 0; i < motors.length; i++){
            motors[i].update(motorAngVelo);
        }
    }

    public void setPower(double power){
        double voltage = Util.limit(power * 12, 12);
        for(int i = 0; i < motors.length; i++){
            motors[i].setVoltage(voltage);
        }
    }

}