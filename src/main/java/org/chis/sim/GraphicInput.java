package org.chis.sim;

import javax.swing.*;

import org.chis.sim.Constants.Constant;

import java.awt.*;
import java.awt.event.*;


public class GraphicInput extends JFrame implements ActionListener {
    private static final long serialVersionUID = 3664593486389802170L;

    static JPanel panel = new JPanel();
    JScrollPane scrollPane = new JScrollPane(panel);
    static JButton buttonSave = new JButton("Save");
    static JButton buttonPause = new JButton("Pause");

    
    public GraphicInput() {
        super("Input");
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setSize(200, GraphicSim.screenHeight);
    }

    private void initComponents() {
        add(scrollPane);

        panel.add(buttonSave);
        panel.add(buttonPause);
        buttonSave.addActionListener(this);
        buttonPause.addActionListener(this);

        for(Constant constant : Constants.constants){
            panel.add(constant.label);
            constant.field.setMaximumSize(new Dimension(200, constant.field.getPreferredScrollableViewportSize().height));
            panel.add(constant.field);
        }
        
        pause();
    }
    
    public void actionPerformed(ActionEvent event) {
        System.out.println("Button pressed...");

        if(event.getSource() == buttonSave){
            for(Constant constant : Constants.constants){
                Object obj = constant.field.getText();
                constant.setValue(obj);
            }
            if(Constants.checkTypes()){ //if all the constants inputted are the correct type
                Constants.calcConstants();
                GraphicSim.rescale();
                System.out.println("Saved");
            }else{
                Main.paused = true;
                buttonPause.setText("Resume");
                System.out.println("Paused: Input type error");
            }
        }

        if(event.getSource() == buttonPause){
            if(Main.paused){
                if(Constants.checkTypes()){
                    resume();
                }
            }else{
                pause();
            }
        }
    }

    public static void pause(){
        Main.paused = true;
        buttonPause.setText("Resume");
        buttonSave.setEnabled(true);
        System.out.println("Paused");
    }

    public static void resume(){
        Main.paused = false;
        Robot.dt = 0;
        Robot.lastTime = System.nanoTime();
        Main.pausedTime = (System.nanoTime() * 1e-9) - Main.elaspedTime - Main.startTime;
        buttonPause.setText("Pause");
        buttonSave.setEnabled(false);
        System.out.println("Resume");
    }
    

    public static void main(String[] args) { //for testing solely the GraphicInput class. not run during actual simulation
        new GraphicInput().setVisible(true);
    }
}