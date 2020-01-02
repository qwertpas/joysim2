package org.chis.sim.userclasses;

public class Calculate {

    // DRIVE
    public static double[] FOD(double x, double y, double gyroAngle, boolean inputSquare, boolean outputSquare) {
        // input the components of the vector (in circle or square) in which you want to
        // robot to go and the heading of the robot
        // outputs the relative x and y to the global coordinate plane (circle or
        // square)

        double localU;
        double localV;

        // if not a circle coordinate, convert to circle
        if (inputSquare) {
            double[] coord = squareToCircle(x, y);
            localU = coord[0];
            localV = coord[1];
        } else {
            localU = x;
            localV = y;
        }

        // does rotation in circled coordinate plane because rotation is a circle
        // uses the rotate point around origin formula
        double rotAngle = -Math.toRadians(gyroAngle);
        double globalX = 0.0001 * (Math.round((localU * Math.cos(rotAngle) - localV * Math.sin(rotAngle)) * 10000));
        double globalY = 0.0001 * (Math.round((localV * Math.cos(rotAngle) + localU * Math.sin(rotAngle)) * 10000));

        // output as requested
        if (outputSquare) {
            return circleToSquare(globalX, globalY);
        } else {
            return new double[] { globalX, globalY };
        }
    }

    // COORDINATE PLANE

    public static double sensCurve(double joystickVal, double exponent) {
        return Math.copySign(Math.pow(Math.abs(joystickVal), exponent), joystickVal);
    }

    public static double[] polarToCartesian(double magnitude, double angle, boolean angleInRadians) {
        // converts a power and angle to x and y coordinates
        // output: x, y
        double radians;
        if (angleInRadians) {
            radians = angle;
        } else {
            radians = Math.toRadians(angle);
        }
        return new double[] { magnitude * Math.cos(radians), magnitude * Math.sin(radians) };
    }

    public static double[] cartesianToPolar(double x, double y) {
        // converts x and y coordinates to power and angle
        // output: magnitude, direction
        double radians = Math.atan2(y, x); // get diretion (but it outputs in radians)
        double degrees = Math.toDegrees(radians); // convert to degrees
        double magnitude = Math.sqrt(x * x + y * y); // pythagorean theorem

        return new double[] { magnitude, degrees };
    }

    public static double[] circleToSquare(double u, double v) {
        // when you want to "stretch" points of a circular coordinate plane onto a
        // square one, ex (0.707, 0.707) maps to (1, 1)
        // visit http://squircular.blogspot.com/2015/09/mapping-circle-to-square.html
        // for more info (this is the inverse)

        double u2 = u * u;
        double v2 = v * v;
        double twosqrt2 = 2.0 * Math.sqrt(2.0);
        double subtermx = 2.0 + u2 - v2;
        double subtermy = 2.0 - u2 + v2;
        double termx1 = subtermx + u * twosqrt2;
        double termx2 = subtermx - u * twosqrt2;
        double termy1 = subtermy + v * twosqrt2;
        double termy2 = subtermy - v * twosqrt2;
        double x = (0.5 * Math.sqrt(termx1) - 0.5 * Math.sqrt(termx2));
        double y = (0.5 * Math.sqrt(termy1) - 0.5 * Math.sqrt(termy2));
        return new double[] { x, y };
    }

    public static double[] squareToCircle(double x, double y) {
        // when you want to "squeeze" points of a square coordinate plane onto a circle
        // one, ex (1, 1) maps to (0.707, 0.707)
        // visit http://squircular.blogspot.com/2015/09/mapping-circle-to-square.html
        // for more info

        double u = x * Math.sqrt(1 - y * y / 2);
        double v = y * Math.sqrt(1 - x * x / 2);
        return new double[] { u, v };
    }

    // CONTROL THEORY
    public static class PIDF {
        // constants
        double kP, kI, kD, kF;
        double tolerance;
        double velTolerance;

        private double currentValue, target, error, lastError, lastTime, vel;
        private double P, I, D, F, power;

        Boolean initialized = false;
        Boolean inTolerance = false;
        Boolean inVelTolerance = false;

        public PIDF(double kP, double kI, double kD, double kF, double tolerance, double velTolerance) {
            this.kP = kP;
            this.kI = kI;
            this.kD = kD;
            this.kF = kF;
            this.tolerance = tolerance;
            this.velTolerance = velTolerance;
        }

        public PIDF() { // empty constructor if you want to set constants later
        }

        public double loop(double currentValue, double target) {
            this.currentValue = currentValue;
            this.target = target;
            if (!initialized) {
                lastError = this.target;
                initialized = true;
            }
            error = this.target - this.currentValue;

            if (Math.abs(error) < tolerance) {
                I = 0;
                inTolerance = true;
            } else {
                inTolerance = false;
            }

            vel = (lastError - error) / (System.currentTimeMillis() - lastTime);
            if (Math.abs(vel) < velTolerance) {
                inVelTolerance = true;
            } else {
                inVelTolerance = false;
            }
            lastTime = System.currentTimeMillis();

            if (Math.signum(lastError) != Math.signum(error)) { // "bounces" back after reaching target, braking
                I = 0;
            }

            P = kP * error;
            I = I + (kI * error);
            D = kD * (lastError - error);
            F = Math.copySign(kF, error);

            if (Math.abs(P + I + F - D) < 0) {
                power = 0;
            } else {
                power = P + I - D + F;
            }

            lastError = error;
            return power;
        }

        public Boolean inTolerance() {
            return inTolerance;
        }

        public Boolean inVelTolerance() {
            return inVelTolerance;
        }

        public double getPower() {
            return power;
        }

        public double getVel() {
            return vel;
        }

        public double getError() {
            return error;
        }

        public void resetPID() {
            P = 0;
            I = 0;
            D = 0;
            F = 0;
        }

        public void setConstants(double kP, double kI, double kD, double kF, double tolerance, double velTolerance) {
            this.kP = kP;
            this.kI = kI;
            this.kD = kD;
            this.kF = kF;
            this.tolerance = tolerance;
            this.velTolerance = velTolerance;
        }

        public double[] getPID() {
            return new double[] { P, I, D, F };
        }
    }

    // RANDOM MATH
    public static double average(double a, double b) {
        return 0.5 * (a + b);
    }

    

}
