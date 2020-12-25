package org.chis.sim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.chis.sim.Util.Vector2D;

//draws the robot
public class GraphicSim extends JPanel {

	static JFrame frame;

	static BufferedImage robotImage;

	static int screenHeight, screenWidth;
	static int windowWidth, windowHeight;
	static GraphicSim sim;

	static double robotScale;

	public static String imagesDirectory = "./src/images/";

	public static ArrayList<Vector2D> userPoints = new ArrayList<Vector2D>();

	public static void init(){
		screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		try {
			robotImage = ImageIO.read(new File(imagesDirectory, "robot.png"));
			rescaleRobot();
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame = new JFrame("Robot Sim");
		sim = new GraphicSim();
		frame.add(sim);
		frame.setSize(screenWidth - 200, screenHeight);
		frame.setLocation(200, 0);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

    @Override
	public void paint(Graphics g) { //gets called iteratively by JFrame
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;

		windowWidth = (int) g.getClipBounds().getWidth();
		windowHeight = (int) g.getClipBounds().getHeight();

		//center the grid and flip y so it is up (default y is down)
		g2d.translate(windowWidth/2, windowHeight/2);
		g2d.scale(1, -1);

		//draw vertical lines
		for(int ix = 0; ix < windowWidth/2; ix += Constants.DISPLAY_SCALE.getInt()){
			if(ix == 0){
				g.setColor(Color.GRAY.darker());
			}else{
				g.setColor(Color.GRAY.brighter());
			}
			g.drawLine(ix, -windowHeight/2, ix, windowHeight/2);
			g.drawLine(-ix, -windowHeight/2, -ix, windowHeight/2);
		}

		//draw horizontal lines
		for(int iy = 0; iy < windowHeight/2; iy += Constants.DISPLAY_SCALE.getInt()){
			if(iy == 0){
				g.setColor(Color.GRAY.darker());
			}else{
				g.setColor(Color.GRAY.brighter());
			}
			g.drawLine(-windowWidth/2, iy, windowWidth/2, iy);
			g.drawLine(-windowWidth/2, -iy, windowWidth/2, -iy);
		}

		//robot transform in pixels
		int[] robotPixelPos = meterToPixel(Main.robot.x, Main.robot.y);
		g2d.translate(robotPixelPos[0], robotPixelPos[1]);
		g2d.rotate(Main.robot.heading);

		//scaling down to draw robot and then scaling back up
        g2d.scale(robotScale, robotScale);
		g.drawImage(robotImage, -robotImage.getWidth()/2, -robotImage.getHeight()/2, this);
		g2d.scale(1/robotScale, 1/robotScale);

		//draw points at pixel scale
		g.setColor(Color.RED);
		for(Vector2D pos : userPoints){
			int[] scaledPos = meterToPixel(pos.x, pos.y);
			g.fillOval(scaledPos[0], scaledPos[1], 3, 3);
		}

	}

	
	
    public int[] meterToPixel(double xMeters, double yMeters){
        int pixelX = (int) (xMeters * Constants.DISPLAY_SCALE.getDouble());
		int pixelY = (int) (yMeters * Constants.DISPLAY_SCALE.getDouble());
        return new int[] {pixelX, pixelY};
    }


	public static void drawPoints(ArrayList<Vector2D> points){
		userPoints = points;
	}

	public static void rescaleRobot() {
		double robotDisplayWidth = Constants.DISPLAY_SCALE.getDouble() * Constants.ROBOT_WIDTH.getDouble(); //width of robot in pixels
		robotScale = (double) robotDisplayWidth / robotImage.getWidth(); //scaling robot image to fit display width
	}


	//JPanel requires this, idk why
	private static final long serialVersionUID = -87884863222799400L;
}