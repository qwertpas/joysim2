package org.chis.sim;

public class Gearbox{

    public Motor[] motors;

    private double angVelo;
    private double power;


    public Gearbox(int numMotors){
        this.motors = new Motor[numMotors];
        for(int i = 0; i < numMotors; i++){
            motors[i] = new Motor();
        }
    }

    public double getOutputTorque(double angVelo_input){

        angVelo = angVelo_input;
        double motorAngVelo = angVelo * Constants.GEAR_RATIO.getDouble();
        for(int i = 0; i < motors.length; i++){
            motors[i].update(motorAngVelo);
        }

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
            Constants.GEAR_VISCOUS_FRIC.getDouble(), 
            Constants.ANGVELO_THRESHOLD.getDouble());
    }

    public void setPower(double power_input){
        power = Util.limit(power_input, 1);
        for(int i = 0; i < motors.length; i++){
            motors[i].setPower(power);
        }
    }

    public double getPower(){
        return power;
    }

}