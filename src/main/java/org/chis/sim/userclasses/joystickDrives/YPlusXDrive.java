package org.chis.sim.userclasses.joystickDrives;

import java.lang.Math;

public class YPlusXDrive extends Drive{
    
    @Override
    public DrivePowers calcPowers(double joystickX, double joystickY, double joystickZ, double leftDist, double rightDist, double leftVelo, double rightVelo){
        super.getConstants();
        double x = senscurve(joystickX, SENSCURVE_EXP, 1);
        double y = senscurve(-joystickY, SENSCURVE_EXP, 1);

        double lPower = y + x;
        double rPower = y - x;

        if(Math.abs(lPower) > 1 || Math.abs(rPower) > 1){
            double biggerValue = Math.max(Math.abs(lPower), Math.abs(rPower));
            lPower = lPower / biggerValue;
            rPower = rPower / biggerValue;
        }

        return new DrivePowers(lPower, rPower);
        
    }


}