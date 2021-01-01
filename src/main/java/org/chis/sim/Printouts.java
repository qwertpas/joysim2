package org.chis.sim;

import java.util.ArrayList;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Printouts extends JPanel{


    private static ArrayList<String> prints = new ArrayList<String>();
    private JFrame frame;
    private Dimension frameSize = new Dimension(300, 300);


    public Printouts(){
        frame = new JFrame("Printouts");
		frame.add(this);
        frame.setSize(frameSize);
        frame.setLocation((int) (GraphicSim.screenWidth - frameSize.getWidth()), 0);
		frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * Prints the number and its label on the GraphicDebug window. Run this every time you want to update the number.
     * @param text What the number means.
     * @param number The number you want to print
     */
    public static void put(String text, Object object){
        for(int i = 0; i < prints.size(); i++){
            if(prints.get(i).startsWith(text)){
                prints.set(i, text + ": " + object);
                return;
            }
        }
        prints.add(text + ": " + object);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int lineNumber = 1;
        for(String str : prints){
            g.drawString(str, 20, 20 * lineNumber);
            lineNumber++;
        }
    }



    private static final long serialVersionUID = 1L;

}
