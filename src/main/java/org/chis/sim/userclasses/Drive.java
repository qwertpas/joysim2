package org.chis.sim.userclasses;

import org.chis.sim.Constants;

public abstract class Drive {
    protected double
        MAX_SPEED,
        MAX_SPIN,
        SENSCURVE_EXP,
        JOYSTICK_DEADBAND,
        SPIN_DEADBAND,
        DELTA_CORRECTION,
        VELO_CORRECTION,
        OPP_VELO_CORRECTION,
        FRICTION_RATIO;

    public double targetLinVelo, targetAngVelo;
    public double targetDelta, targetLVelo, targetRVelo;
    public double errorInDelta, errorInLVelo, errorInRVelo;
    public boolean isGoingStraight = true;

    public abstract DrivePowers calcPowers(double joystickX, double joystickY, double leftDist, double rightDist, double leftVelo, double rightVelo);

    protected void getConstants(){
        MAX_SPEED = Constants.MAX_SPEED.getDouble();
        MAX_SPIN = Constants.MAX_SPIN.getDouble();
        SENSCURVE_EXP = Constants.SENSCURVE_EXP.getDouble();
        SPIN_DEADBAND = Constants.SPIN_DEADBAND.getDouble();
        DELTA_CORRECTION = Constants.DELTA_CORRECTION.getDouble();
        VELO_CORRECTION = Constants.VELO_CORRECTION.getDouble();
        OPP_VELO_CORRECTION = Constants.OPP_VELO_CORRECTION.getDouble();
        FRICTION_RATIO = Constants.FRICTION_RATIO.getDouble();
    }

    public static class DrivePowers{
        public double lPower, rPower;
        public DrivePowers(double lPower, double rPower){
            this.lPower = lPower;
            this.rPower = rPower;
        }
        public DrivePowers scale(double factor){
            return new DrivePowers(lPower * factor, rPower * factor);
        }
    }

    public static double senscurve(double input, double exponent, double maxValue){
        return Math.pow(Math.abs(input), exponent) * Math.copySign(maxValue, input);
    }
}