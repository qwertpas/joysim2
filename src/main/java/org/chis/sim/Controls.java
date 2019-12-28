package org.chis.sim;

import java.awt.MouseInfo;
import java.util.ArrayList;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Component.Identifier;

public class Controls {

    public static double rawX, rawY = 0;
    public static ArrayList<Boolean> buttons = new ArrayList<Boolean>();
    public static Boolean usingMouse = false;

    static ArrayList<Controller> foundControllers;

    public static void searchForControllers() {
        foundControllers = new ArrayList<>();
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

		for (int i = 0; i < controllers.length; i++) {
			Controller controller = controllers[i];
			if (controller.getType() == Controller.Type.STICK || controller.getType() == Controller.Type.GAMEPAD
					|| controller.getType() == Controller.Type.WHEEL
					|| controller.getType() == Controller.Type.FINGERSTICK) {
				// Add new controller to the list of all controllers.
                foundControllers.add(controller);
			}
        }

        if(foundControllers.size() == 0){
            System.out.println("No Controller found, using mouse coords");
            usingMouse = true;
        }else{
            System.out.println("Found controllers : " + foundControllers);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        init();
        // for testing joystick
        while(true){
            updateControls();
            Thread.sleep(100);
        }
    }

    static void updateControls() {
		if (usingMouse) {
			getMouseData();
		} else {
            getControllerData(foundControllers.get(Constants.CONTROLLER_INDEX.getInt()));
        }
        // System.out.println(buttons);
    }
    
    private static void getMouseData() {
		rawX = (MouseInfo.getPointerInfo().getLocation().getX() - (GraphicSim.screenWidth / 2)) / (GraphicSim.screenWidth / 2);
		rawY = -(MouseInfo.getPointerInfo().getLocation().getY() - (GraphicSim.screenHeight / 2)) / -(GraphicSim.screenHeight / 2);
    }
    
    private static void getControllerData(Controller controller){
        if(!controller.poll()){ //polls controller, if it returns false then polling has failed
            System.out.println("Controller disconnected, using mouse");
            usingMouse = true;
            return;
        }
        Component[] components = controller.getComponents();
        for (int i = 0; i < components.length; i++) {
            Component component = components[i];
            Identifier componentIdentifier = component.getIdentifier();
            float value = component.getPollData();

            if(componentIdentifier == Component.Identifier.Axis.X){
                rawX = value;
            }else if(componentIdentifier == Component.Identifier.Axis.Y){
                rawY = value;
            }

            //commented out the other buttons to improve performance
            if(componentIdentifier == Component.Identifier.Button._0){
                buttons.set(0, floatToBoolean(value));
            }else
            if(componentIdentifier == Component.Identifier.Button._1){
                buttons.set(1, floatToBoolean(value));
            }//else
            // if(componentIdentifier == Component.Identifier.Button._2){
            //     buttons.set(2, floatToBoolean(value));
            // }else
            // if(componentIdentifier == Component.Identifier.Button._3){
            //     buttons.set(3, floatToBoolean(value));
            // }else
            // if(componentIdentifier == Component.Identifier.Button._4){
            //     buttons.set(4, floatToBoolean(value));
            // }else
            // if(componentIdentifier == Component.Identifier.Button._5){
            //     buttons.set(5, floatToBoolean(value));
            // }else
            // if(componentIdentifier == Component.Identifier.Button._6){
            //     buttons.set(6, floatToBoolean(value));
            // }else
            // if(componentIdentifier == Component.Identifier.Button._7){
            //     buttons.set(7, floatToBoolean(value));
            // }else
            // if(componentIdentifier == Component.Identifier.Button._8){
            //     buttons.set(8, floatToBoolean(value));
            // }else
            // if(componentIdentifier == Component.Identifier.Button._9){
            //     buttons.set(9, floatToBoolean(value));
            // }else
            // if(componentIdentifier == Component.Identifier.Button._10){
            //     buttons.set(10, floatToBoolean(value));
            // }else
            // if(componentIdentifier == Component.Identifier.Button._11){
            //     buttons.set(11, floatToBoolean(value));
            // }else
            // if(componentIdentifier == Component.Identifier.Button._12){
            //     buttons.set(12, floatToBoolean(value));
            // }
                
            
        }
    }

    public static Boolean floatToBoolean(float floatValue){
        if(floatValue == 0.0f){
            return false;
        }else if(floatValue == 1.0f){
            return true;
        }else{
            throw new IllegalArgumentException("float is not 0 or 1");
        }
    }

    public static void init(){
        for(int i = 0; i < 13; i++){
            buttons.add(false);
        }
        searchForControllers();
    }


}