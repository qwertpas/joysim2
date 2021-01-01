
package org.chis.sim;

import java.text.DecimalFormat;

public class Util {

  public static double applyFrictions(double force, double velocity, double STATIC_FRIC, double KINE_FRIC, double VISCOUS_FRIC, double FRIC_THRESHOLD) {
    // if not moving and force is not enough to overcome static friction, net force is 0
    if (Math.abs(velocity) < FRIC_THRESHOLD && Math.abs(force) < STATIC_FRIC) {
      return 0;
    }
    // friction is opposite the direction of velocity
    return force - Math.copySign(KINE_FRIC, velocity) - VISCOUS_FRIC * velocity;
  }

  public static double rpmToRadSec(double rpm) { // Rotations per minute to Radians per second
    double rotationsPerSec = rpm / 60.0; // each minute is 60 seconds
    return rotationsPerSec * 2 * Math.PI; // each rotation is 2PI radians
  }

  public static double radSecToRPM(double radSec) {
    double rotationsPerSec = radSec / (2 * Math.PI);
    return rotationsPerSec * 60;
  }

  public static double metersToFeet(double meters) {
    return meters * 3.281;
  }

  public static double metersToInches(double meters) {
    return meters * 39.3701;
  }

  public static double inchesToMeters(double inches) {
    return inches / 39.3701;
  }

  public static double inchesToFeet(double inches) {
    return inches * 12.0;
  }

  public static double feetToInches(double feet) {
    return feet / 12.0;
  }

  public static double roundHundreths(double input) {
    return Double.parseDouble(new DecimalFormat("#.##").format(input));
  }

  public static double posModulo(double input, double modulo) { // modulo but it always returns a positive number, ideal for screen loopback
    while (input >= modulo)
      input -= modulo;
    while (input < 0)
      input += modulo;
    return input;
  }

  public static class MotionProfile {

    double vmax, amax, amin, target;
    public Boolean isTrapezoid;
    public double[] times;
    public Boolean done = false;

    public MotionProfile(double vmax_input, double amax_input, double amin_input, double target_input) {
      vmax = vmax_input;
      amax = amax_input;
      amin = Math.abs(amin_input);
      target = target_input;

      double time1 = vmax / amax;
      double time2 = (-vmax / (2.0 * amin)) + (target / vmax) + (time1 / 2.0);
      if (time1 < time2) {
        isTrapezoid = true;
        double time3 = (target / vmax) + (time1 / 2.0) + (vmax / (2.0 * amin));
        times = new double[] { 0.0, time1, time2, time3 }; // added a zero in the first index so times[1] is time1
      } else {
        isTrapezoid = false;
        time1 = Math.sqrt((2 * target) / (amax + ((amax) * (amax) / amin)));
        time2 = (amax * time1 / amin) + time1;
        times = new double[] { 0.0, time1, time2 };
      }
    }

    public MotionProfilePoint getPoint(double time) {
      double accel = 0;
      double velo = 0;
      double dist = 0;

      if (isTrapezoid) {
        if (time <= times[1]) {
          accel = amax;
          velo = amax * time;
          dist = 0.5 * amax * time * time;
        } else if (time <= times[2]) {
          accel = 0;
          velo = vmax;
          dist = (0.5 * amax * times[1] * times[1]) + (vmax * (time - times[1]));
        } else if (time <= times[3]) {
          accel = -amin;
          velo = vmax - (amin * (time - times[2]));
          dist = (0.5 * amax * times[1] * times[1]) + (vmax * (times[2] - times[1]))
              + (0.5 * (time - times[2]) * ((vmax) + (vmax - (amin * (time - times[2])))));
        } else if (time > times[3]) {
          done = true;
          accel = 0;
          velo = 0;
          dist = target;
        }
      } else {
        if (time < times[1]) {
          accel = amax;
          velo = amax * time;
          dist = 0.5 * amax * time * time;
        } else if (time < times[2]) {
          accel = amin;
          velo = (amax * times[1]) - (amax * (time - times[1]));
          dist = (0.5 * amax * times[1] * times[1])
              + (0.5 * (time - times[1]) * (amax * times[1] + amax * times[1] - (amin * (time - times[1]))));
        } else if (time > times[2]) {
          done = true;
          accel = 0;
          velo = 0;
          dist = target;
        }
      }
      return new MotionProfilePoint(accel, velo, dist);
    }

    public class MotionProfilePoint {
      double accel;
      public double velo;
      public double dist;

      public MotionProfilePoint(double accel_input, double velo_input, double dist_input) {
        accel = accel_input;
        velo = velo_input;
        dist = dist_input;
      }
    }

    public static void main(String[] args) {
      double scale = 10;

      MotionProfile testProfile = new MotionProfile(3 * scale, 1 * scale, -1 * scale, 15 * scale);

      // MotionProfile testProfile = new MotionProfile(Util.metersToInches(3), //max
      // velocity
      // Util.metersToInches(1), //max acceleration
      // Util.metersToInches(-1), //min acceleration
      // Util.metersToInches(15) ); //target distance

      System.out.println("is trapezoid: " + testProfile.isTrapezoid);
      System.out.println("time1: " + testProfile.times[1]);
      System.out.println("time2: " + testProfile.times[2]);
      if (testProfile.isTrapezoid) {
        System.out.println("time3: " + testProfile.times[3]);
      }
      System.out.println("accel: " + testProfile.getPoint(0).accel);
      System.out.println("velo: " + testProfile.getPoint(0).velo);
      System.out.println("dist: " + testProfile.getPoint(0).dist);

      System.out.println("calc: " + (3 * 10) / (1 * 10));
    }

  }

  public static class PID {
    double kP;
    double kI, IworkingRange, ImaxValue;
    double kD, lastError;
    long lastTime;

    double P, I, D, power;

    Boolean initialized = false;
    public void loop(double currentValue, double target) {
      if (!initialized) {
        lastError = target;
        lastTime = System.nanoTime();
        initialized = true;
      }

      double error = target - currentValue;
      double dt = (System.nanoTime() - lastTime) * 1e-9;
      lastTime = System.nanoTime();

      P = kP * error;

      if(kI != 0 && Math.abs(I) < IworkingRange){
        I += kI * error * dt;
        I = limit(I, ImaxValue);
      }else{
        I = 0;
      }

      D = kD * (lastError - error) / dt;

      power = P + I + D;
    }

    public double getPower(){
      return power;
    }

    public void setkP(double newkP){
      kP = newkP;
    }
    public void setkI(double newkI, double newWorkingRange, double newMaxValue){
      kI = newkI;
      IworkingRange = newWorkingRange;
      ImaxValue = newMaxValue;
    }
    public void setkD(double newkD){
      kD = newkD;
    }

    public void copyConstants(PID other){
      this.kP = other.kP;
      this.kI = other.kD;
      this.IworkingRange = other.IworkingRange;
      this.ImaxValue = other.ImaxValue;
      this.kD = other.kD;
    }


  }

  private static final double kThrottleDeadband = 0.05;
  private static final double kWheelDeadband = 0.01;

  // These factor determine how fast the wheel traverses the "non linear" sine
  // curve.
  private static final double kHighWheelNonLinearity = 0.65;
  private static final double kLowWheelNonLinearity = 0.5;

  private static final double kHighNegInertiaScalar = 4.0;

  private static final double kLowNegInertiaThreshold = 0.65;
  private static final double kLowNegInertiaTurnScalar = 3.5;
  private static final double kLowNegInertiaCloseScalar = 4.0;
  private static final double kLowNegInertiaFarScalar = 5.0;

  private static final double kHighSensitivity = 0.65;
  private static final double kLowSensitiity = 0.65;

  private static final double kQuickStopDeadband = 0.5;
  private static final double kQuickStopWeight = 0.1;
  private static final double kQuickStopScalar = 5.0;

  private static double mOldWheel = 0.0;
  private static double mQuickStopAccumlator = 0.0;
  private static double mNegInertiaAccumlator = 0.0;

  public static double senscurve(double val, double exponent) {
    return Math.copySign(Math.pow(Math.abs(val), exponent), val);
  }

  public static double[] cheesyDrive(double throttle, double wheel, boolean isQuickTurn, boolean isHighGear) {
    wheel = -handleDeadband(wheel, kWheelDeadband);
    throttle = handleDeadband(throttle, kThrottleDeadband);

    double negInertia = wheel - mOldWheel;
    mOldWheel = wheel;

    double wheelNonLinearity;
    if (isHighGear) {
      wheelNonLinearity = kHighWheelNonLinearity;
      final double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);
      // Apply a sin function that's scaled to make it feel better.
      wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
      wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
    } else {
      wheelNonLinearity = kLowWheelNonLinearity;
      final double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);
      // Apply a sin function that's scaled to make it feel better.
      wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
      wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
      wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
    }

    double leftPwm, rightPwm, overPower;
    double sensitivity;

    double angularPower;
    double linearPower;

    // Negative inertia!
    double negInertiaScalar;
    if (isHighGear) {
      negInertiaScalar = kHighNegInertiaScalar;
      sensitivity = kHighSensitivity;
    } else {
      if (wheel * negInertia > 0) {
        // If we are moving away from 0.0, aka, trying to get more wheel.
        negInertiaScalar = kLowNegInertiaTurnScalar;
      } else {
        // Otherwise, we are attempting to go back to 0.0.
        if (Math.abs(wheel) > kLowNegInertiaThreshold) {
          negInertiaScalar = kLowNegInertiaFarScalar;
        } else {
          negInertiaScalar = kLowNegInertiaCloseScalar;
        }
      }
      sensitivity = kLowSensitiity;
    }
    double negInertiaPower = negInertia * negInertiaScalar;
    mNegInertiaAccumlator += negInertiaPower;

    wheel = wheel + mNegInertiaAccumlator;
    if (mNegInertiaAccumlator > 1) {
      mNegInertiaAccumlator -= 1;
    } else if (mNegInertiaAccumlator < -1) {
      mNegInertiaAccumlator += 1;
    } else {
      mNegInertiaAccumlator = 0;
    }
    linearPower = throttle;

    // Quickturn!
    if (isQuickTurn) {
      if (Math.abs(linearPower) < kQuickStopDeadband) {
        double alpha = kQuickStopWeight;
        mQuickStopAccumlator = (1 - alpha) * mQuickStopAccumlator + alpha * limit(wheel, 1.0) * kQuickStopScalar;
      }
      overPower = 1.0;
      angularPower = wheel;
    } else {
      overPower = 0.0;
      angularPower = Math.abs(throttle) * wheel * sensitivity - mQuickStopAccumlator;
      if (mQuickStopAccumlator > 1) {
        mQuickStopAccumlator -= 1;
      } else if (mQuickStopAccumlator < -1) {
        mQuickStopAccumlator += 1;
      } else {
        mQuickStopAccumlator = 0.0;
      }
    }

    rightPwm = leftPwm = linearPower;
    leftPwm += angularPower;
    rightPwm -= angularPower;

    if (leftPwm > 1.0) {
      rightPwm -= overPower * (leftPwm - 1.0);
      leftPwm = 1.0;
    } else if (rightPwm > 1.0) {
      leftPwm -= overPower * (rightPwm - 1.0);
      rightPwm = 1.0;
    } else if (leftPwm < -1.0) {
      rightPwm += overPower * (-1.0 - leftPwm);
      leftPwm = -1.0;
    } else if (rightPwm < -1.0) {
      leftPwm += overPower * (-1.0 - rightPwm);
      rightPwm = -1.0;
    }
    return new double[] { leftPwm, rightPwm };
  }

  public static double handleDeadband(double val, double deadband) {
    return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
  }

  public static double limit(double val, double limit) {
    if (val > limit) {
      return limit;
    } else if (val < -limit) {
      return -limit;
    } else {
      return val;
    }
  }

  public static class Vector2D {
    public double x;
    public double y;

    public enum Type {
      CARTESIAN, POLAR
    }

    public Vector2D(double magnitudeOrX, double directionOrY, Type vectorType) {
      if (vectorType == Type.CARTESIAN) {
        x = magnitudeOrX;
        y = directionOrY;
      } else {
        x = magnitudeOrX * Math.cos(directionOrY);
        y = magnitudeOrX * Math.sin(directionOrY);
      }
    }

    // unit vector
    public Vector2D(double direction) {
      x = Math.cos(direction);
      y = Math.sin(direction);
    }

    // zero vector
    public Vector2D() {
      x = 0;
      y = 0;
    }

    public Vector2D add(Vector2D valueToAdd) {
      return new Vector2D(this.x + valueToAdd.x, this.y + valueToAdd.y, Type.CARTESIAN);
    }

    public Vector2D subtract(Vector2D valueToSubtract) {
      return new Vector2D(this.x - valueToSubtract.x, this.y - valueToSubtract.y, Type.CARTESIAN);
    }

    public double dotProduct(Vector2D f) {
      return this.x * f.x + this.y * f.y;
    }

    public Vector2D scalarAdd(double scalar) {
      return new Vector2D(this.x + scalar, this.y + scalar, Type.CARTESIAN);
    }

    public Vector2D scalarMult(double scalar) {
      return new Vector2D(this.x * scalar, this.y * scalar, Type.CARTESIAN);
    }

    public Vector2D scalarDiv(double scalar) {
      return new Vector2D(this.x / (double) scalar, this.y / (double) scalar, Type.CARTESIAN);
    }

    public Vector2D rotate(double radiansToRotate) {
      double sin = Math.sin(radiansToRotate);
      double cos = Math.cos(radiansToRotate);
      return new Vector2D(this.x * cos - this.y * sin, this.x * sin + this.y * cos, Type.CARTESIAN);
    }

    public double getMagnitude() {
      return Math.sqrt(x * x + y * y);
    }

    public double dist(Vector2D otherVec){
      return this.subtract(otherVec).getMagnitude();
    }

    public double getAngle() {
      return Math.atan2(y, x);
    }

    public boolean equals(Vector2D comparison){
      double epsilon = 1e-10; //close enough to 10 decimal places
      return Math.abs(this.x - comparison.x) < epsilon && Math.abs(this.y - comparison.y) < epsilon;
    }

    public boolean equals(Vector2D comparison, double epsilon){
      return Math.abs(this.x - comparison.x) < epsilon && Math.abs(this.y - comparison.y) < epsilon;
    }

    public String toString() {
      return "(" + Util.roundHundreths(x) + ", " + Util.roundHundreths(y) + ")";
    }

  }

}