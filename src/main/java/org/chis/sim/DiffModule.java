package org.chis.sim;

import org.ejml.simple.SimpleMatrix;
import org.chis.sim.Util.*;

class DiffModule {

    Motor topMotor;
    Motor bottomMotor;

    Vector2D moduleTranslation = new Vector2D(); // translational velocity of the module
    double wheelTanVelo; //velocity of the surface wheel that is driving the ground
    double wheelScrubVelo; //velocity of the surface wheel that is scrubbing the ground
    double wheelAngVelo; // angular velocity of the wheel
    double moduleAngVelo; // angular velocity of the module
    double moduleAngle = 0;

    double topRingSpeed, bottomRingSpeed;
    double topRingTorque, bottomRingTorque;

    double wheelTorque;
    double driveForce;
    double scrubForce;

    Vector2D force = new Vector2D();

    double dt;
    double lastTime;

    DiffModule() {
        topMotor = new Motor();
        bottomMotor = new Motor();
        lastTime = System.nanoTime();
    }

    void update() {
        wheelTanVelo = (new Vector2D(moduleAngle)).dotProduct(moduleTranslation); // component of the movement in the direction of the wheel
        wheelScrubVelo = (new Vector2D(moduleAngle)).rotate(Math.PI/2.0).dotProduct(moduleTranslation); // component of the movement perperdicular to the wheel
        // wheelTanVelo = moduleTranslation.x; // tangential velocity of the wheel

        wheelAngVelo = wheelTanVelo / Constants.WHEEL_RADIUS.getDouble(); // tangential velocity = radius * angular velocity

        updateMotorSpeeds();
        updateModuleAngle();

        topRingTorque = topMotor.getTorque() * Constants.RINGS_GEAR_RATIO.getDouble();
        bottomRingTorque = bottomMotor.getTorque() * Constants.RINGS_GEAR_RATIO.getDouble();

        wheelTorque = (topRingTorque - bottomRingTorque) * Constants.WHEEL_GEAR_RATIO.getDouble();
        // wheelTorque = (topRingTorque - bottomRingTorque) * 1;


        wheelTorque = Util.applyFrictions(wheelTorque, wheelAngVelo, Constants.GEAR_STATIC_FRIC.getDouble(), Constants.GEAR_KINE_FRIC.getDouble(), Constants.GEAR_FRIC_THRESHOLD.getDouble());

        driveForce = wheelTorque / Constants.WHEEL_RADIUS.getDouble(); // F=ma
        scrubForce = Util.applyFrictions(0, wheelScrubVelo, Constants.WHEEL_STATIC_FRIC, Constants.WHEEL_KINE_FRIC, Constants.WHEEL_FRIC_THRESHOLD.getDouble());
        force = new Vector2D(driveForce, scrubForce, Vector2D.Type.CARTESIAN).rotate(moduleAngle);
    }

    void setTranslation(Vector2D moduleTranslation_input) {
        this.moduleTranslation = moduleTranslation_input;
    }

    void updateModuleAngle() {
        dt = (System.nanoTime() - lastTime) / 1e+9; // change in time (seconds) used for integrating
        lastTime = System.nanoTime();

        double moduleTorque = topMotor.torque + bottomMotor.torque;
        moduleTorque = Util.applyFrictions(moduleTorque, moduleAngVelo, 0.1, 0.1, 0.0001);
        // moduleTorque = Util.applyFrictions(moduleTorque, moduleAngVelo, 0, 0, 0.001);
        double moduleAngAccel = moduleTorque / Constants.MODULE_ROT_INERTIA.getDouble();
        moduleAngVelo = moduleAngAccel * dt + moduleAngVelo; // integration
        moduleAngle = moduleAngVelo * dt + moduleAngle; // second integration
    }

    void updateMotorSpeeds() {
        double GR_R = Constants.RINGS_GEAR_RATIO.getDouble();
        double GR_W = Constants.WHEEL_GEAR_RATIO.getDouble();

        SimpleMatrix wheelMatrix = new SimpleMatrix(new double[][] { { wheelAngVelo }, { moduleAngVelo } });
        SimpleMatrix diffMatrix = new SimpleMatrix(new double[][] { { 0.5 / GR_W, -0.5 / GR_W }, { 0.5, 0.5 } });
        SimpleMatrix ringsMatrix = diffMatrix.solve(wheelMatrix);

        topRingSpeed = ringsMatrix.get(0, 0);
        bottomRingSpeed = ringsMatrix.get(1, 0);

        topMotor.setAngSpeed(topRingSpeed * GR_R);
        bottomMotor.setAngSpeed(bottomRingSpeed * GR_R);
    }


} 