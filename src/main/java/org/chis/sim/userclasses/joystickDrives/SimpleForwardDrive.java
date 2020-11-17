package org.chis.sim.userclasses.joystickDrives;

public class SimpleForwardDrive extends Drive{
    
    @Override
    public DrivePowers calcPowers(double joystickX, double joystickY, double joystickZ, double leftDist, double rightDist, double leftVelo, double rightVelo){
        super.getConstants();
        double y = senscurve(-joystickY, SENSCURVE_EXP, 1);

        double lPower = y;
        double rPower = y;

        return new DrivePowers(lPower, rPower);
        
    }


}