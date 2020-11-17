package org.chis.sim;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Constants{

    enum Type{
        BOOLEAN, INT, DOUBLE, STRING;
    }

    JPanel panel = new JPanel();

    /** ////////////////////////////////////////////
     * REAL PHYSICAL CONSTANTS (meters, kilograms, seconds, Newtons, radians)
     * //////////////////////////////////////////// */ 
    public static Constant GEAR_RATIO = new Constant("GEAR_RATIO", 10, Type.DOUBLE);
    public static Constant STALL_TORQUE = new Constant("STALL_TORQUE", 3.36, Type.DOUBLE); //NEO
    public static Constant FREE_SPEED = new Constant("FREE_SPEED", 576, Type.DOUBLE); //NEO
    public static Constant TICKS_PER_REV = new Constant("TICKS_PER_REV", 10.72, Type.DOUBLE); //NEO on KOP drive

    public static Constant ROBOT_MASS = new Constant("ROBOT_MASS", 45, Type.DOUBLE);
    public static Constant ROBOT_WIDTH = new Constant("ROBOT_WIDTH", 0.6096, Type.DOUBLE);
    public static Constant DIST_BETWEEN_WHEELS = new Constant("DIST_BETWEEN_WHEELS", 0.508, Type.DOUBLE);
    public static Constant WHEEL_RADIUS = new Constant("WHEEL_RADIUS", 0.0635, Type.DOUBLE); //5 inch diameter
    public static Constant STATIC_FRIC_COEFF = new Constant("STATIC_FRIC_COEFF", 1.1, Type.DOUBLE); //between wheels and ground
    public static Constant KINE_FRIC_COEFF = new Constant("KINE_FRIC_COEFF", 0.7, Type.DOUBLE); //should be < static

    //Overall makes motors slower
    public static Constant GEAR_STATIC_FRIC = new Constant("GEAR_STATIC_FRIC", 0.5, Type.DOUBLE); //actual torque against gearbox when not moving, not the coefficient 
    public static Constant GEAR_KINE_FRIC = new Constant("GEAR_KINE_FRIC", 0.5, Type.DOUBLE); //actual torque against gearbox when moving, not the coefficient 
    public static Constant GEAR_FRIC_THRESHOLD = new Constant("GEAR_FRIC_THRESHOLD", 0.0001, Type.DOUBLE); //lowest motor speed in rad/sec considered as 'moving' to kine fric

    // Wheel scrub torque slows turning, coeff is a combo of fric coeff, drop center, robot length.
    public static Constant WHEEL_SCRUB_MULTIPLIER = new Constant("WHEEL_SCRUB_MULTIPLIER", 15, Type.DOUBLE); 
    public static Constant GRAV_ACCEL = new Constant("GRAV_ACCEL", 9.81, Type.DOUBLE);

    //Build problems
    public static Constant TURN_ERROR = new Constant("TURN_ERROR", 0.1, Type.DOUBLE); //torque that will be added/subtracted from left/right


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
     * CALCULATED FROM OTHERS
     * //////////////////////////////// */     
    public static double
        HALF_DIST_BETWEEN_WHEELS,
        STATIC_FRIC,
        KINE_FRIC,
        ROBOT_ROT_INERTIA
    ;

    public static void calcConstants(){
        HALF_DIST_BETWEEN_WHEELS = DIST_BETWEEN_WHEELS.getDouble() / 2.0;
        STATIC_FRIC = ROBOT_MASS.getDouble() * GRAV_ACCEL.getDouble() * STATIC_FRIC_COEFF.getDouble();
        KINE_FRIC = ROBOT_MASS.getDouble() * GRAV_ACCEL.getDouble() * KINE_FRIC_COEFF.getDouble();
        ROBOT_ROT_INERTIA = (1.0/6.0) * ROBOT_MASS.getDouble() * ROBOT_WIDTH.getDouble() * ROBOT_WIDTH.getDouble();
    }

    //constants that are editable by GraphicInput. Add them in as needed.
    public static Constant[] constants = {
        TURN_ERROR,
        STATIC_FRIC_COEFF,
        KINE_FRIC_COEFF,
        GEAR_STATIC_FRIC,
        GEAR_KINE_FRIC,
        GEAR_FRIC_THRESHOLD,
        WHEEL_SCRUB_MULTIPLIER,
        DISPLAY_SCALE,
    };

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