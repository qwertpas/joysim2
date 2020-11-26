package org.chis.sim;

public class Motor{

    final double maxVoltage = 12; //volts

    public double voltage = 0; //ranges from 0-12V
    public double angVelo = 0; //angular velocity in radians per second
    public double angVeloPrev = 0; //store previous angular velocity for midpoint riemann sum (trapezoid)
    public double torque = 0; //newton*meters

    public double position = 0; //motor shaft in radians

    public void setPower(double power){
        voltage = power * maxVoltage; //assume that robot system voltage is always at max (12ish)
    }

    public long lastTime = System.nanoTime();
    public double dt;
    public void update(double radPerSec_input){
        dt = (System.nanoTime() - lastTime) * 1e-9; //change in time (seconds) used for integrating
        lastTime = System.nanoTime();

        angVelo = radPerSec_input;
        position += 0.5 * (angVelo + angVeloPrev) * dt; //midpoint riemann sum integration
        angVeloPrev = angVelo;
    }

    public double getTorque(){
        //calculates torque based on motor torque-angvelo graph: https://www.desmos.com/calculator/nmge6gksgj 
        torque = Constants.STALL_TORQUE.getDouble() * ((voltage / maxVoltage) - (angVelo / Constants.FREE_SPEED.getDouble()));
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

    public void resetEncoder(){
        position = 0;
    }
}