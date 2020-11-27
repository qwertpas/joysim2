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
import org.chis.sim.Util.Vector2D.Type;

//draws the robot
public class GraphicSim extends JPanel {

	static JFrame frame;

	static BufferedImage robotImage;

	static int screenHeight;
	static int screenWidth;
	static GraphicSim sim;

	static int robotImgHeight;
	static int robotDisplayWidth;
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
		frame.setSize((int) screenWidth-200, (int) screenHeight);
		frame.setLocation(200, 0);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

    @Override
	public void paint(Graphics g) { //gets called iteratively by JFrame
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;

		// int x = (int) Util.posModulo(Main.robot.x * Constants.DISPLAY_SCALE.getDouble(), windowWidth); // robot position in pixels
		// int y = (int) Util.posModulo(Main.robot.y * Constants.DISPLAY_SCALE.getDouble(), windowHeight);

		//drawing the meter grid 
		g.setColor(Color.GRAY.brighter());
		for(int i = 0; i < screenWidth; i += Constants.DISPLAY_SCALE.getDouble()){
			g.drawLine(i, 0, i, screenHeight);
		}
		for(int i = 0; i < screenHeight; i += Constants.DISPLAY_SCALE.getDouble()){
			g.drawLine(0, i, screenWidth, i);
		}

		//scaling into robot transform
		int[] robotPixelPos = convertMeterToPixel(Main.robot.x, Main.robot.y, g.getClipBounds().getWidth(), g.getClipBounds().getHeight(), true);
        g2d.translate(robotPixelPos[0] + robotImage.getWidth()/2, robotPixelPos[1] + robotImage.getWidth()/2);
        g2d.scale(robotScale, robotScale);
		g2d.rotate(-Main.robot.heading);
		g.drawImage(robotImage, -robotImage.getWidth()/2, -robotImage.getHeight()/2, this);

		g.setColor(Color.RED);
		for(Vector2D pos : userPoints){
			g.fillOval((int) (pos.x * Constants.DISPLAY_SCALE.getDouble() / robotScale), (int) (pos.y * Constants.DISPLAY_SCALE.getDouble() / robotScale), 3, 3);
		}

	}

	public static void drawPoints(ArrayList<Vector2D> points){
		userPoints = points;
	}
	
    public int[] convertMeterToPixel(double xMeters, double yMeters, double windowWidth, double windowHeight, boolean alwaysOnscreen){
        xMeters += 0.5 * windowWidth / Constants.DISPLAY_SCALE.getDouble();
        yMeters += 0.5 * windowHeight / Constants.DISPLAY_SCALE.getDouble();
        int pixelX = (int) (xMeters * Constants.DISPLAY_SCALE.getDouble());
		int pixelY = (int) (-yMeters * Constants.DISPLAY_SCALE.getDouble()); //negative because down is positive in JFrame
		
		if(alwaysOnscreen){
			pixelX = (int) Util.posModulo(pixelX, windowWidth);
			pixelY = (int) Util.posModulo(pixelY, windowHeight);
		}
		
        return new int[] {pixelX, pixelY};
    }


	

	

	public static void rescaleRobot() {
		robotImgHeight = robotImage.getHeight();
		robotDisplayWidth = (int) (Constants.DISPLAY_SCALE.getDouble() * Constants.ROBOT_WIDTH.getDouble()); //width of robot in pixels
		robotScale = (double) robotDisplayWidth / robotImgHeight; //scaling robot image to fit display width.
	}


	//JPanel requires this, idk why
	private static final long serialVersionUID = -87884863222799400L;
}