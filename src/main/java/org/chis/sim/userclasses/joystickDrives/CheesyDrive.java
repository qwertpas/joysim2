package org.chis.sim.userclasses.joystickDrives;

import org.chis.sim.Util;

public class CheesyDrive extends Drive{
    
    @Override
    public DrivePowers calcPowers(double joystickX, double joystickY, double joystickZ, double leftDist, double rightDist, double leftVelo, double rightVelo){
        
        double[] powers = Util.cheesyDrive(-joystickY, -joystickX, false, false);

        

        return new DrivePowers(powers[0], powers[1]);
        
    }


}