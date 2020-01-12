package org.chis.sim.userclasses;

import org.ejml.simple.SimpleMatrix;

public class DeltaVeloDrive{

    SimpleMatrix K = new SimpleMatrix(new double[][]{
        {  -1.5811, 4.0014, -3.5872 },
        {  1.5811, -3.5872, 4.0014 }
    });

    SimpleMatrix state = new SimpleMatrix(new double[][]{
        {  -1.5811, 4.0014, -3.5872 },
        {  1.5811, -3.5872, 4.0014 }
    });

    

    


}