package org.chis.sim;

//overarching physics simulation
public class Robot{

    //each gearbox contains 2 motors
    public Gearbox leftGearbox = new Gearbox(2);
    public Gearbox rightGearbox = new Gearbox(2);

    //applied by each gearbox
    double torqueL, torqueR;
    double forceL, forceR;

    //dynamics on the whole robot
    double forceNet;
    double torqueMotors, torqueNet;

    //robot state in meters, radians, and seconds
    double x, y, heading;
    double linVelo, angVelo;
    double linAccel, angAccel;
    double veloL, veloR;

    //storing previous velos and accels for midpoint riemann sum integration (trapezoids)
    double linVeloPrev, angVeloPrev;
    double linAccelPrev, angAccelPrev;

    //timekeeping for integration
    double dt;
    double lastTime;

    public void init(){
        x = 5; //initial position (meters)
        y = 6;
        heading = 0;
        linVelo = 0;
        angVelo = 0;

        dt = 0;
        lastTime = System.nanoTime();
        
        leftGearbox.setPower(0);
        rightGearbox.setPower(0);

        for(Motor motor : leftGearbox.motors){
            motor.resetEncoder();
        }
        for(Motor motor : rightGearbox.motors){
            motor.resetEncoder();
        }
    }

    public void update(){
        //calculate linear velocity of left and right sides given linear and angular velocity
        veloL = linVelo - Constants.HALF_DIST_BETWEEN_WHEELS * angVelo;
        veloR = linVelo + Constants.HALF_DIST_BETWEEN_WHEELS * angVelo;

        //convert linear velocity to angular velocity the wheels are spinning at, then feed into gearbox to get torque of each
        torqueL = leftGearbox.getOutputTorque(veloL / Constants.WHEEL_RADIUS.getDouble());
        torqueR = rightGearbox.getOutputTorque(veloR / Constants.WHEEL_RADIUS.getDouble());

        //convert torque of each gearbox into a force that moves the robot. Add them to get net force.
        forceL = torqueL / Constants.WHEEL_RADIUS.getDouble();
        forceR = torqueR / Constants.WHEEL_RADIUS.getDouble();
        forceNet = forceL + forceR; 

        //calculate the torque that the gearboxes create to spin the robot
        torqueMotors = (forceR - forceL) * Constants.HALF_DIST_BETWEEN_WHEELS;

        //wheels scrub, applying a frictional torque that slows turning
        torqueNet = Util.applyFrictions(torqueMotors, angVelo, Constants.SCRUB_STATIC, Constants.SCRUB_KINE, 0, Constants.ANGVELO_THRESHOLD.getDouble());

        //add a constant that simulates hardware error making robot curve
        torqueNet += Constants.TURN_ERROR.getDouble();
        
        //Newton's 2nd Law to find accelerations given forces and torques
        angAccel = torqueNet / Constants.ROBOT_ROT_INERTIA;
        linAccel = forceNet / Constants.ROBOT_MASS.getDouble();

        //get amount of time that passed since the last cycle (dt)
        dt = (System.nanoTime() - lastTime) * 1e-9; //convert nanoseconds to seconds
        lastTime = System.nanoTime(); //saving current time for same calculation next cycle

        //integrating acceleration into velocity, then saving current accels for next cycle
        linVelo += 0.5 * (linAccel + linAccelPrev) * dt;
        linAccelPrev = linAccel;
        angVelo += 0.5 * (angAccel + angAccelPrev) * dt;
        angAccelPrev = angAccel;

        //integrating velocity into heading and position (in relation to field), then saving velos
        heading += angVelo * dt;
        angVeloPrev = angVelo;
        x += 0.5 * (linVelo + linVeloPrev) * dt * Math.cos(heading);
        y += 0.5 * (linVelo + linVeloPrev) * dt * Math.sin(heading);
        linVeloPrev = linVelo;
    }


    //Functions to get encoder data. You can use these in your own drive program in UserCode.java.
    public double leftEncoderPosition(){
        double encoderSum = 0;
        for(Motor motor : leftGearbox.motors){
            encoderSum += motor.getEncoderPosition();
        }
        return Util.roundHundreths(encoderSum / (double)leftGearbox.motors.length);
    }

    public double leftEncoderVelocity(){
        double encoderSum = 0;
        for(Motor motor : leftGearbox.motors){
            encoderSum += motor.getEncoderVelocity();
        }
        return Util.roundHundreths(encoderSum / (double)leftGearbox.motors.length);
    }

    public double rightEncoderPosition(){
        double encoderSum = 0;
        for(Motor motor : rightGearbox.motors){
            encoderSum += motor.getEncoderPosition();
        }
        return Util.roundHundreths(encoderSum / (double)rightGearbox.motors.length);
    }

    public double rightEncoderVelocity(){
        double encoderSum = 0;
        for(Motor motor : rightGearbox.motors){
            encoderSum += motor.getEncoderVelocity();
        }
        return Util.roundHundreths(encoderSum / (double)rightGearbox.motors.length);
    }

    


}