package org.chis.sim;

public class Gearbox{

    public Motor[] motors;

    double angVelo;
    double outputTorque;
    double power;


    public Gearbox(int numMotors){
        this.motors = new Motor[numMotors];
        for(int i = 0; i < numMotors; i++){
            motors[i] = new Motor();
        }
    }

    public double getOutputTorque(double angVelo_input){
        angVelo = angVelo_input;

        //convert gearbox shaft speed to motor shaft speed
        double motorAngVelo = angVelo * Constants.GEAR_RATIO.getDouble();

        //feed each motor the motor shaft speed and add up all the torques
        outputTorque = 0;
        for(Motor motor : motors){
            outputTorque += motor.getTorque(motorAngVelo);
        }

        //torque from motors is scaled by gear ratio
        outputTorque *= Constants.GEAR_RATIO.getDouble();

        //friction in gearboxes depend on how fast it is spinning, in what direction, and a bunch of constants
        outputTorque =  Util.applyFrictions(
            outputTorque,
            angVelo, 
            Constants.GEAR_STATIC_FRIC.getDouble(), 
            Constants.GEAR_KINE_FRIC.getDouble(),
            Constants.GEAR_VISCOUS_FRIC.getDouble(), 
            Constants.ANGVELO_THRESHOLD.getDouble())
        ;

        return outputTorque;
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