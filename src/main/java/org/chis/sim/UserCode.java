package org.chis.sim;

import org.chis.sim.GraphicDebug.Serie;
import org.chis.sim.userclasses.ModuleController;
import org.chis.sim.userclasses.ModuleController.ModulePowers;
import org.chis.sim.userclasses.ModuleController.ModuleState;
import org.ejml.simple.SimpleMatrix;

import java.awt.Color;

public class UserCode{

    static ModuleController leftController = new ModuleController(new ModuleState());
    static ModuleController rightController = new ModuleController(new ModuleState());

    static Motor leftTopMotor;
    static Motor leftBottomMotor;
    static Motor rightTopMotor;
    static Motor rightBottomMotor;

    public static void initialize(){ //this function is run once when the robot starts
        GraphicDebug.turnOnAll(); //displaying the graphs

        leftTopMotor = Main.robot.leftModule.topMotor;
        leftBottomMotor = Main.robot.leftModule.bottomMotor;
        rightTopMotor = Main.robot.rightModule.topMotor;
        rightBottomMotor = Main.robot.rightModule.bottomMotor;
        
    }

    public static void execute(){ //this function is run 50 times a second (every 0.02 second)

        leftController.updateState(leftTopMotor.getEncoderPosition(), 
                                   leftBottomMotor.getEncoderPosition(), 
                                   leftTopMotor.getEncoderVelocity(), 
                                   leftBottomMotor.getEncoderVelocity());

        rightController.updateState(rightTopMotor.getEncoderPosition(),
                                    rightBottomMotor.getEncoderPosition(), 
                                    rightTopMotor.getEncoderVelocity(),
                                    rightBottomMotor.getEncoderVelocity());

        
        double moduleAngle = Math.toDegrees(Math.atan2(-Controls.rawY, Controls.rawX));
        
        ModulePowers leftPowers = leftController.rotateModule(moduleAngle);
        ModulePowers rightPowers = rightController.rotateModule(moduleAngle);

        Main.robot.setDrivePowers(leftPowers.topPower,
                                  leftPowers.bottomPower, 
                                  rightPowers.topPower, 
                                  rightPowers.bottomPower);

                        
        // double forward = -Controls.rawY;
        // double forward = 0;
        // double moduleRot = Controls.rawX;

        // SimpleMatrix wheelMatrix = new SimpleMatrix(new double[][] { { forward }, { moduleRot } });
        // SimpleMatrix diffMatrix = new SimpleMatrix(new double[][] { { 0.5 , -0.5 }, { 0.5, 0.5 } });

        // SimpleMatrix ringsMatrix = diffMatrix.solve(wheelMatrix);

        // double top = ringsMatrix.get(0, 0);
        // double bottom = ringsMatrix.get(1, 0);

        //power ranges from -1 to 1s
        // Main.robot.setDrivePowers(top, bottom, top, bottom); 
        // Main.robot.setDrivePowers(1, -1, 1, -1);
        // Main.robot.setDrivePowers(forward+moduleRot, -(forward+moduleRot), forward-moduleRot, -(forward-moduleRot)); //tank drive

        graph(); //updating the graphs
    }




    // Motion graphs
    static Serie w1s1 = new Serie(Color.BLUE, 3);
    static Serie w1s2 = new Serie(Color.RED, 3);
    static GraphicDebug w1 = new GraphicDebug("linear velocity", new Serie[]{w1s1, w1s2}, 100);

    static Serie w2s1 = new Serie(Color.BLUE, 3);
    static Serie w2s2 = new Serie(Color.RED, 3);
    static GraphicDebug w2 = new GraphicDebug("angular velocity", new Serie[]{w2s1, w2s2}, 200);
    
    private static void graph(){
        w1s1.addPoint(Main.robot.linVelo);
        // w1s1.addPoint(Main.robot.leftModule.topRingSpeed, Main.robot.leftModule.bottomRingSpeed);


        // w1s1.addPoint(Main.elaspedTime, Main.robot.leftModule.force.x);
        // w1s2.addPoint(Main.elaspedTime, Main.robot.rightModule.force.x);
        // w1s2.addPoint(Main.elaspedTime, Main.robot.leftModule.force.x);

        // w2s1.addPoint(Main.robot.leftModule.topRingTorque, Main.robot.leftModule.bottomRingTorque);
        // w2s1.addPoint(Main.robot.forceNet.x, Main.robot.forceNet.y);
        w2s1.addPoint(Main.elaspedTime, Main.robot.leftModule.topMotor.position);
        w2s2.addPoint(Main.elaspedTime, Main.robot.leftModule.bottomMotor.position);

        GraphicDebug.paintAll();
    }




}
