package org.chis.sim;

import org.chis.sim.GraphicDebug.Serie;
import org.chis.sim.Util.Vector2D;
import org.chis.sim.Util.Vector2D.Type;
import org.chis.sim.userclasses.RobotController;
import org.chis.sim.userclasses.RobotController.RobotState;
import org.ejml.simple.SimpleMatrix;

import java.awt.Color;

public class UserCode{

    static RobotController controller = new RobotController(new RobotState());
    static RobotState targetRobotState;

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

        Vector2D joystick = new Vector2D(Controls.rawX, -Controls.rawY, Type.CARTESIAN);

        controller.updateState(
            Main.robot.heading,
            leftTopMotor.getEncoderPosition(), 
            leftBottomMotor.getEncoderPosition(), 
            leftTopMotor.getEncoderVelocity(), 
            leftBottomMotor.getEncoderVelocity(),
            rightTopMotor.getEncoderPosition(),
            rightBottomMotor.getEncoderPosition(), 
            rightTopMotor.getEncoderVelocity(),
            rightBottomMotor.getEncoderVelocity()
        );


        joystick = joystick.scalarMult(8).rotate(-Main.robot.heading);
        
        targetRobotState = new RobotState(new Vector2D(joystick.x, joystick.y, Type.CARTESIAN), -Controls.slider * Math.PI);

        controller.move(targetRobotState);

        setDrivePowersAndFeed(
            controller.leftController.modulePowers.topPower,
            controller.leftController.modulePowers.bottomPower,
            controller.rightController.modulePowers.topPower,
            controller.rightController.modulePowers.bottomPower,
            0.0
        );


        // Main.robot.setDrivePowers(
        //     joystick.getMagnitude(),
        //     joystick.getMagnitude(),
        //     joystick.getMagnitude(),
        //     joystick.getMagnitude());

                        
        // double forward = -Controls.rawY;
        // double forward = 0;
        // double moduleRot = Controls.rawX;

        // SimpleMatrix wheelMatrix = new SimpleMatrix(new double[][] { { forward }, { moduleRot } });
        // SimpleMatrix diffMatrix = new SimpleMatrix(new double[][] { { 0.5 , -0.5 }, { 0.5, 0.5 } });

        // SimpleMatrix ringsMatrix = diffMatrix.solve(wheelMatrix);

        // double top = ringsMatrix.get(0, 0);
        // double bottom = ringsMatrix.get(1, 0);

        // power ranges from -1 to 1s
        // Main.robot.setDrivePowers(top, bottom, top, bottom); 
        // Main.robot.setDrivePowers(1, -1, 1, -1);
        // Main.robot.setDrivePowers(forward+moduleRot, -(forward+moduleRot), forward-moduleRot, -(forward-moduleRot)); //tank drive

        graph(); //updating the graphs
    }

    private static void setDrivePowersAndFeed(double LT, double LB, double RT, double RB, double feedforward){
        Main.robot.setDrivePowers(
            LT + Math.copySign(feedforward, LT),
            LB + Math.copySign(feedforward, LB), 
            RT + Math.copySign(feedforward, RT), 
            RB + Math.copySign(feedforward, RB)
        );
    }




    // Motion graphs
    static Serie w1s1 = new Serie(Color.BLUE, 3);
    static Serie w1s2 = new Serie(Color.RED, 3);
    static GraphicDebug w1 = new GraphicDebug("robot position", new Serie[]{w1s1, w1s2}, 100);

    static Serie w2s1 = new Serie(Color.BLUE, 3);
    static Serie w2s2 = new Serie(Color.RED, 3);
    static GraphicDebug w2 = new GraphicDebug("robot heading", new Serie[]{w2s1, w2s2}, 200);
    
    private static void graph(){
        w1s1.addPoint(Main.elaspedTime, Main.robot.leftModule.topMotor.voltage);
        w1s2.addPoint(Main.elaspedTime, Main.robot.leftModule.bottomMotor.voltage);

        w2s1.addPoint(Main.elaspedTime, controller.robotState.heading);
        w2s2.addPoint(Main.elaspedTime, targetRobotState.heading);
        // w1s1.addPoint(Main.robot.leftModule.topRingSpeed, Main.robot.leftModule.bottomRingSpeed);


        // w1s1.addPoint(Main.elaspedTime, Main.robot.leftModule.force.x);
        // w1s2.addPoint(Main.elaspedTime, Main.robot.rightModule.force.x);
        // w1s2.addPoint(Main.elaspedTime, Main.robot.leftModule.force.x);

        // w2s1.addPoint(Main.robot.leftModule.topRingTorque, Main.robot.leftModule.bottomRingTorque);
        // w2s1.addPoint(Main.robot.forceNet.x, Main.robot.forceNet.y);
        // w2s1.addPoint(Main.elaspedTime, Main.robot.leftModule.topMotor.position);
        // w2s2.addPoint(Main.elaspedTime, Main.robot.leftModule.bottomMotor.position);

        GraphicDebug.paintAll();
    }




}
