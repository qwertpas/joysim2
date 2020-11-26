package org.chis.sim;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Constants{

    /** ////////////////////////////////////////////
     * REAL PHYSICAL CONSTANTS (meters, kilograms, seconds, Newtons, radians)
     * //////////////////////////////////////////// */ 
    public static Constant MAX_VOLTAGE = new Constant("MAX_VOLTAGE", 12, Type.DOUBLE);
    public static Constant GEAR_RATIO = new Constant("GEAR_RATIO", 10, Type.DOUBLE);
    public static Constant STALL_TORQUE = new Constant("STALL_TORQUE", 2.7, Type.DOUBLE); //NEO: https://www.revrobotics.com/content/docs/REV-21-1650-DS.pdf
    public static Constant FREE_SPEED = new Constant("FREE_SPEED", 594, Type.DOUBLE); //NEO
    public static Constant TICKS_PER_REV = new Constant("TICKS_PER_REV", 10.72, Type.DOUBLE); //NEO on KOP drive

    public static Constant ROBOT_MASS = new Constant("ROBOT_MASS", 45, Type.DOUBLE); //about 100 lbs
    public static Constant ROBOT_WIDTH = new Constant("ROBOT_WIDTH", Util.inchesToMeters(24), Type.DOUBLE);
    public static Constant DIST_BETWEEN_WHEELS = new Constant("DIST_BETWEEN_WHEELS", Util.inchesToMeters(20), Type.DOUBLE);
    public static Constant WHEEL_RADIUS = new Constant("WHEEL_RADIUS", Util.inchesToMeters(2.5), Type.DOUBLE);

    //Slows robot rotation
    public static Constant STATIC_FRIC_COEFF = new Constant("STATIC_FRIC_COEFF", 1.1, Type.DOUBLE); //between wheels and ground
    public static Constant KINE_FRIC_COEFF = new Constant("KINE_FRIC_COEFF", 0.9, Type.DOUBLE); //should be < static
    public static Constant SCRUB_COEFF = new Constant("SCRUB_COEFF", 0.2, Type.DOUBLE); //proportion of weight that rests on scrubbing wheels (depends on dropcenter)

    //Slows gearboxes
    public static Constant GEAR_STATIC_FRIC = new Constant("GEAR_STATIC_FRIC", 2, Type.DOUBLE); //actual torque against gearbox when not moving, not the coefficient 
    public static Constant GEAR_KINE_FRIC = new Constant("GEAR_KINE_FRIC", 1.5, Type.DOUBLE); //actual torque against gearbox when moving, not the coefficient
    public static Constant GEAR_VISCOUS_FRIC = new Constant("GEAR_VISCOUS_FRIC", 0.1, Type.DOUBLE); //coefficient that will be multiplied to gearbox angvelo to get friction

    //Thresholds that decide whether something is moving, so the sim can choose kinetic or static friction
    public static Constant ANGVELO_THRESHOLD = new Constant("ANGVELO_THRESHOLD", 0.0001, Type.DOUBLE); //lowest moving rad/sec
    public static Constant LINVELO_THRESHOLD = new Constant("LINVELO_THRESHOLD", 0.0001, Type.DOUBLE); //lowest moving m/sec

    public static Constant GRAV_ACCEL = new Constant("GRAV_ACCEL", 9.81, Type.DOUBLE);

    //Build problems
    public static Constant TURN_ERROR = new Constant("TURN_ERROR", 0.0, Type.DOUBLE); //torque that will be added/subtracted from left/right


    /** ////////////////////////////////
     * SIMULATOR CONFIG
     * //////////////////////////////// */  
    public static Constant CONTROLLER_INDEX = new Constant("Controller_INDEX", 0, Type.INT); //which joystick?
    public static Constant DISPLAY_SCALE = new Constant("DISPLAY_SCALE", 75, Type.DOUBLE); //in pixels per meter


    /** ////////////////////////////////
     * USERCODE
     * //////////////////////////////// */  
    public static Constant DRIVE_OPTION = new Constant("DRIVE_OPTION", 0, Type.INT);
    public static Constant MAX_SPEED = new Constant("MAX_SPEED", 3.5, Type.DOUBLE);
    public static Constant MAX_SPIN = new Constant("MAX_SPIN", 5, Type.DOUBLE);
    public static Constant SENSCURVE_EXP = new Constant("SENSCURVE_EXP", 1.5, Type.DOUBLE);
    public static Constant JOYSTICK_DEADBAND = new Constant("JOYSTICK_DEADBAND", 0.1, Type.DOUBLE);
    public static Constant SPIN_DEADBAND = new Constant("SPIN_DEADBAND", 0.1, Type.DOUBLE);
    public static Constant DELTA_CORRECTION = new Constant("DELTA_CORRECTION", 1, Type.DOUBLE);
    public static Constant VELO_CORRECTION = new Constant("VELO_CORRECTION", 1.2, Type.DOUBLE);
    public static Constant OPP_VELO_CORRECTION = new Constant("OPP_VELO_CORRECTION", 0.1, Type.DOUBLE);
    public static Constant FRICTION_RATIO = new Constant("FRICTION_RATIO", 0.1, Type.DOUBLE);


    /** ////////////////////////////////
     * ADD CONSTANTS TO THIS LIST TO BE EDITABLE
     * //////////////////////////////// */  
    public static Constant[] constants = {
        TURN_ERROR,
        GEAR_RATIO,
        STATIC_FRIC_COEFF,
        KINE_FRIC_COEFF,
        GEAR_STATIC_FRIC,
        GEAR_KINE_FRIC,
        GEAR_VISCOUS_FRIC,
        DISPLAY_SCALE,
    };


    
    /** ////////////////////////////////
     * CALCULATED FROM OTHERS
     * //////////////////////////////// */     
    public static double
        HALF_DIST_BETWEEN_WHEELS,
        SCRUB_STATIC,
        SCRUB_KINE,
        ROBOT_ROT_INERTIA
    ;

    public static void calcConstants(){
        HALF_DIST_BETWEEN_WHEELS = 0.5 * DIST_BETWEEN_WHEELS.getDouble();
        SCRUB_STATIC = SCRUB_COEFF.getDouble() * ROBOT_MASS.getDouble() * GRAV_ACCEL.getDouble() * STATIC_FRIC_COEFF.getDouble() * HALF_DIST_BETWEEN_WHEELS * Math.sqrt(2);
        SCRUB_KINE = SCRUB_COEFF.getDouble() * ROBOT_MASS.getDouble() * GRAV_ACCEL.getDouble() * KINE_FRIC_COEFF.getDouble() * HALF_DIST_BETWEEN_WHEELS * Math.sqrt(2);
        ROBOT_ROT_INERTIA = (1.0/6.0) * ROBOT_MASS.getDouble() * ROBOT_WIDTH.getDouble() * ROBOT_WIDTH.getDouble();
    }


    enum Type{
        BOOLEAN, INT, DOUBLE, STRING;
    }

    JPanel panel = new JPanel();

    public static Boolean checkTypes(){
        Boolean good = true;
        for(Constant constant : constants){
            try{
                if(constant.type.equals(Type.DOUBLE)) constant.getDouble();
                if(constant.type.equals(Type.INT)) constant.getInt();
                if(constant.type.equals(Type.STRING)) constant.getString();
            }catch(IllegalArgumentException e){
                JOptionPane.showMessageDialog(GraphicInput.panel, constant.name + " must be of type " + constant.type.name());
                good = false;
            }
        }
        return good;
    }

    public static void setAllToDefault(){
        for(Constant constant : constants){
            constant.setValue(constant.defaultValue);
        }
    }


    public static class Constant{
        private String name;
        private Object value;
        private String defaultValue;
        
        Type type;
        
        JLabel label;
        JTextField field;


        Constant(String name_input, Object value_input, Type type_input){
            name = name_input;
            value = value_input;
            defaultValue = String.valueOf(value_input);
            type = type_input;
            
            label = new JLabel(name);
            field = new JTextField(String.valueOf(value));
        }
        
        public String getName(){
            return name;
        }

        public double getDouble() {
            return Double.valueOf(getString());
        }

        public int getInt() {
            return Integer.valueOf(getString());
        }

        public String getString() {
            return String.valueOf(value);
        }

        public String getDefaultString() {
            return String.valueOf(defaultValue);
        }

        public Object getObject(){
            return value;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public void setValue(Object value_input) {
            this.value = value_input;
        }

    }

}