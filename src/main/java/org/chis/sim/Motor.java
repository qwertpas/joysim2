package org.chis.sim;

public class Motor{

    double voltage = 0;
    double angVelo = 0; //angular velocity in radians per second
    double angVeloPrev = 0; //store previous angular velocity for midpoint riemann sum (trapezoid)
    double torque = 0; //newton*meters

    double position = 0; //motor shaft in radians

    public void setPower(double power){
        voltage = power * Constants.MAX_VOLTAGE.getDouble(); //assumes that robot system voltage is always at max (no brownout)
    }

    public double getTorque(double radPerSec_input){
        angVelo = radPerSec_input;
        integrateEncoder();

        //calculates torque based on motor torque-angvelo graph: https://www.desmos.com/calculator/nmge6gksgj 
        torque = Constants.STALL_TORQUE.getDouble() * ((voltage / Constants.MAX_VOLTAGE.getDouble()) - (angVelo / Constants.FREE_SPEED.getDouble()));
        return torque;
    }

    //integrates angular velocity to get angular position 
    public long lastTime = System.nanoTime();
    public double dt;
    public void integrateEncoder(){
        dt = (System.nanoTime() - lastTime) * 1e-9; //change in time (seconds) used for integrating
        lastTime = System.nanoTime();

        position += 0.5 * (angVelo + angVeloPrev) * dt;
        angVeloPrev = angVelo;
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