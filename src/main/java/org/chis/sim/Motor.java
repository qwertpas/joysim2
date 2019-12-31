package org.chis.sim;

class Motor{

    double voltage = 0; //ranges from 0-12V
    double angVelo = 0; //angular velocity in radians per second
    double torque = 0; //newton*meters

    double position = 0;

    void setVoltage(double voltage_input){
        voltage = voltage_input;
    }

    long lastTime = System.nanoTime();
    double dt;
    void setAngSpeed(double radPerSec_input){
        dt = (System.nanoTime() - lastTime) * 1e-9; //change in time (seconds) used for integrating
        lastTime = System.nanoTime();
        angVelo = radPerSec_input;

        position += angVelo;
    }

    double getTorque(){ //input is radians per second
        torque = Constants.STALL_TORQUE.getDouble() * ((voltage/12.0) - (angVelo / Constants.FREE_SPEED.getDouble()));
        return torque;
    }

    public static void main(String[] args) {
        Motor mot = new Motor();
        mot.setAngSpeed(-142);
        mot.setVoltage(-12);
        System.out.println(mot.getTorque());
    }


}