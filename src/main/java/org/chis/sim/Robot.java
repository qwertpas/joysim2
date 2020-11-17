package org.chis.sim;

public class Robot{

    double x = 5;
    double y = 6;
    double heading = 0;

    public double linVelo = 0;
    public double angVelo = 0;

    double linAccel = 0.1;
    double angAccel = 0;

    double veloL = 0;
    double veloR = 0;

    double distL = 0;
    double distR = 0;

    double torqueL;
    double torqueR;
    double torqueNet;
    double forceNet;
    Boolean slipping = false;

    static double dt;
    static double lastTime;

    public Gearbox leftGearbox = new Gearbox(2);
    public Gearbox rightGearbox = new Gearbox(2);

    public void init(){
        lastTime = System.nanoTime();
        leftGearbox.setPower(0);
        rightGearbox.setPower(0);
    }

    public void update(){
        dt = (System.nanoTime() - lastTime) / 1e+9; //change in time (seconds) used for integrating
        lastTime = System.nanoTime();

        leftGearbox.update(veloL / Constants.WHEEL_RADIUS.getDouble());
        rightGearbox.update(veloR / Constants.WHEEL_RADIUS.getDouble());

        torqueL = leftGearbox.getOutputTorque() - Constants.TURN_ERROR.getDouble();
        torqueR = rightGearbox.getOutputTorque() + Constants.TURN_ERROR.getDouble();

        double forceL = calcWheelForce(torqueL);
        double forceR = calcWheelForce(torqueR);

        torqueNet = calcTorqueNet(forceL, forceR); //newton*meters
        forceNet = forceL + forceR; //newtons

        angAccel = torqueNet / Constants.ROBOT_ROT_INERTIA; //rad per sec per sec
        linAccel = forceNet / Constants.ROBOT_MASS.getDouble(); //meters per sec per sec

        angVelo = angVelo + angAccel * dt;
        linVelo = linVelo + linAccel * dt;
        veloL = linVelo - Constants.HALF_DIST_BETWEEN_WHEELS * angVelo;
        veloR = linVelo + Constants.HALF_DIST_BETWEEN_WHEELS * angVelo;

        heading = heading + angVelo * dt; //integrating angVelo using physics equation
        distL = distL + veloL * dt; //acting as encoder since integrateVelocity() inside motor isn't working
        distR = distR + veloR * dt;

        x = x + linVelo * dt * Math.cos(heading); //for display purposes
        y = y + linVelo * dt * Math.sin(heading);
    }


    private double calcWheelForce(double torque){
        double force = torque / Constants.WHEEL_RADIUS.getDouble();
        if(force > Constants.STATIC_FRIC){
            force = Constants.KINE_FRIC;
            slipping = true;
        } else slipping = false;
        return force;
    }

    private double calcTorqueNet(double forceL, double forceR){
        double torqueMotors = (forceR - forceL) * Constants.HALF_DIST_BETWEEN_WHEELS; //torque around center of robot

        torqueNet = torqueMotors - Constants.WHEEL_SCRUB_MULTIPLIER.getDouble() * angVelo;
        return torqueNet;
    }

    public double leftEncoderPosition(){
        double encoderDistSum = 0;
        for(Motor motor : leftGearbox.motors){
            encoderDistSum += motor.getEncoderPosition();
        }
        return Util.roundHundreths(encoderDistSum / (double)leftGearbox.motors.length);
    }

    public double leftEncoderVelocity(){
        double encoderDistSum = 0;
        for(Motor motor : leftGearbox.motors){
            encoderDistSum += motor.getEncoderVelocity();
        }
        return Util.roundHundreths(encoderDistSum / (double)leftGearbox.motors.length);
    }

    public double rightEncoderPosition(){
        double encoderDistSum = 0;
        for(Motor motor : rightGearbox.motors){
            encoderDistSum += motor.getEncoderPosition();
        }
        return Util.roundHundreths(encoderDistSum / (double)rightGearbox.motors.length);
    }

    public double rightEncoderVelocity(){
        double encoderDistSum = 0;
        for(Motor motor : rightGearbox.motors){
            encoderDistSum += motor.getEncoderVelocity();
        }
        return Util.roundHundreths(encoderDistSum / (double)rightGearbox.motors.length);
    }

    public void reset(){
        x = 5;
        y = 6;
        heading = 0;
        linVelo = 0;
        angVelo = 0;

        for(Motor motor : leftGearbox.motors){
            motor.resetEncoder();
        }
        for(Motor motor : rightGearbox.motors){
            motor.resetEncoder();
        }
    }


}