package org.chis.sim;

public class Motor{

    public double voltage = 0; //ranges from 0-12V
    public double angVelo = 0; //angular velocity in radians per second
    public double torque = 0; //newton*meters

    public double position = 0;

    public void setVoltage(double voltage_input){
        voltage = voltage_input;
    }

    public long lastTime = System.nanoTime();
    public double dt;
    public void update(double radPerSec_input){
        dt = (System.nanoTime() - lastTime) * 1e-9; //change in time (seconds) used for integrating
        lastTime = System.nanoTime();
        angVelo = radPerSec_input;

        position = position + angVelo * dt;
    }

    public double getTorque(){ //input is radians per second
        torque = Constants.STALL_TORQUE.getDouble() * ((voltage/12.0) - (angVelo / Constants.FREE_SPEED.getDouble()));
        return torque;
    }

    public double getEncoderPosition(){
        double revolutions = position / (2 * Math.PI); //convert the position in radians to position in revolutions
        double encoderTicks = revolutions * Constants.TICKS_PER_REV.getDouble(); //convert revolutions to encoder ticks
        return encoderTicks;
    }

    public double getEncoderVelocity(){
        double rps = angVelo / (2 * Math.PI);
        double encoderTicksPerSec = rps * Constants.TICKS_PER_REV.getDouble();
        return encoderTicksPerSec;
    }
}