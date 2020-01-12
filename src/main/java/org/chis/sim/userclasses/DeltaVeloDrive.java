package org.chis.sim.userclasses;

import org.ejml.simple.SimpleMatrix;

public class DeltaVeloDrive{

    private final SimpleMatrix K = new SimpleMatrix(new double[][]{
        {  -0.22361, 1.2014, -1.1526 },
     {  0.22361, -1.1526, 1.2014 }
    });
    

    public DrivePowers calcDrivePowers(DeltaVeloState currentState, DeltaVeloState targetState){
        SimpleMatrix error = currentState.get().minus(targetState.get());
        SimpleMatrix powers = K.mult(error).negative();
        return new DrivePowers(powers.get(0), powers.get(1));
    }

    public static class DeltaVeloState{
        public double deltaPos;
        public double lVelo;
        public double rVelo;

        public DeltaVeloState(double deltaPos, double lVelo, double rVelo){
            this.deltaPos = deltaPos;
            this.lVelo = lVelo;
            this.rVelo = rVelo;
        }
        
        public SimpleMatrix get(){
            return new SimpleMatrix(new double[][]{
                { deltaPos },
                { lVelo },
                { rVelo }
            });
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

    

    


}