package org.chis.sim.userclasses;

import org.chis.sim.Constants;
import java.lang.Math;

public class DeltaVeloDrive{

    private double
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

    DrivePowers calcDrivePowers(double joystickX, double joystickY, double leftDist, double rightDist, double leftVelo, double rightVelo){
        MAX_SPEED = Constants.MAX_SPEED.getDouble();
        MAX_SPIN = Constants.MAX_SPIN.getDouble();
        SENSCURVE_EXP = Constants.SENSCURVE_EXP.getDouble();
        SPIN_DEADBAND = Constants.SPIN_DEADBAND.getDouble();
        DELTA_CORRECTION = Constants.DELTA_CORRECTION.getDouble();
        VELO_CORRECTION = Constants.VELO_CORRECTION.getDouble();
        OPP_VELO_CORRECTION = Constants.OPP_VELO_CORRECTION.getDouble();
        FRICTION_RATIO = Constants.FRICTION_RATIO.getDouble();

        if(Math.abs(joystickX) < JOYSTICK_DEADBAND && Math.abs(joystickY) < JOYSTICK_DEADBAND){
            return new DrivePowers(0, 0);
        }else{
            targetLinVelo = senscurve(-joystickY, SENSCURVE_EXP, MAX_SPEED);
            targetAngVelo = senscurve(joystickX, SENSCURVE_EXP, MAX_SPIN);

            if(Math.abs(targetAngVelo) < SPIN_DEADBAND){
                isGoingStraight = true;
            }else{
                isGoingStraight = false;
                targetDelta = (rightDist - leftDist);
            }
            targetLVelo = targetLinVelo - targetAngVelo * Constants.HALF_DIST_BETWEEN_WHEELS;
            targetRVelo = targetLinVelo + targetAngVelo * Constants.HALF_DIST_BETWEEN_WHEELS;
            
            errorInDelta = targetDelta - (rightDist - leftDist);
            errorInLVelo = targetLVelo - leftVelo;
            errorInRVelo = targetRVelo - rightVelo;

            double lPower = 
                errorInDelta * -DELTA_CORRECTION + 
                errorInLVelo * VELO_CORRECTION +
                errorInRVelo * -OPP_VELO_CORRECTION +
                Math.copySign(FRICTION_RATIO, leftVelo) + leftVelo / MAX_SPEED;
            double rPower = 
                errorInDelta * DELTA_CORRECTION + 
                errorInLVelo * -OPP_VELO_CORRECTION +
                errorInRVelo * VELO_CORRECTION + 
                Math.copySign(FRICTION_RATIO, rightVelo) + rightVelo / MAX_SPEED;

            if(Math.abs(lPower) > 1 || Math.abs(rPower) > 1){
                double biggerValue = Math.max(Math.abs(lPower), Math.abs(rPower));
                lPower = lPower / biggerValue;
                rPower = rPower / biggerValue;
            }
            return new DrivePowers(lPower, rPower);
        }
    }

    public class DrivePowers{
        public double lPower, rPower;
        public DrivePowers(double lPower, double rPower){
            this.lPower = lPower;
            this.rPower = rPower;
        }
        public DrivePowers scale(double factor){
            return new DrivePowers(lPower * factor, rPower * factor);
        }
    }

    public double senscurve(double input, double exponent, double maxValue){
        return Math.pow(Math.abs(input), exponent) * Math.copySign(maxValue, input);
    }
}