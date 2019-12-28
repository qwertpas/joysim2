package org.chis.sim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GraphicSim extends JPanel implements MouseListener {
	private static final long serialVersionUID = -87884863222799400L;

	static JFrame frame;

	AffineTransform defaultTransform = new AffineTransform(); //to reset the g2d position and rotation

	static File robotFile;
	static File moduleFile;

	static BufferedImage robotImage;
	static BufferedImage moduleImage;

	static int screenHeight;
	static int screenWidth;
	static GraphicSim sim;

	static int robotImgHeight;
	static int robotDisplayWidth;
	static double robotScale;
	static int moduleDisplayWidth;

	static ArrayList<Point> points = new ArrayList<Point>();

  	GraphicSim() {
		addMouseListener(this);
	}

    @Override
	public void paint(Graphics g) { //gets called iteratively by JFrame
		double windowWidth = frame.getContentPane().getSize().getWidth();
		double windowHeight = frame.getContentPane().getSize().getHeight();
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int x = (int) Util.posModulo(Main.robot.position.x * Constants.DISPLAY_SCALE.getDouble(), windowWidth); // robot position in pixels
		int y = (int) (windowHeight - Util.posModulo(Main.robot.position.y * Constants.DISPLAY_SCALE.getDouble(), windowHeight));

		// g.drawString("torque net " + Util.roundHundreths(Main.robot.torqueNet), 500, 575);
		g.drawString("heading " + Util.roundHundreths(Main.robot.heading), 500, 600);
		g.drawString("linvelo x " + Util.roundHundreths(Main.robot.linVelo.x), 500, 625);
		g.drawString("linvelo y " + Util.roundHundreths(Main.robot.linVelo.y), 500, 650);
		g.drawString("leftModTrans "+ Main.robot.leftModule.moduleTranslation, 500, 675);
		g.drawString("rightModTrans "+ Main.robot.rightModule.moduleTranslation, 500, 700);
		g.drawString("L module angle " + Util.roundHundreths(Main.robot.leftModule.moduleAngle), 500, 725);
		g.drawString("R module angle " + Util.roundHundreths(Main.robot.rightModule.moduleAngle), 500, 750);
		// g.drawString("LT motor speed " + Util.roundHundreths(Main.robot.leftModule.topMotor.angVelo), 500, 775);
		// g.drawString("LB motor speed " + Util.roundHundreths(Main.robot.leftModule.bottomMotor.angVelo), 500, 800);
		// g.drawString("LT motor torque " + Util.roundHundreths(Main.robot.leftModule.topMotor.torque), 700, 775);
		// g.drawString("LB motor torque " + Util.roundHundreths(Main.robot.leftModule.bottomMotor.torque), 700, 800);


		//drawing the grid
		g.setColor(Color.GRAY.brighter());
		for(int i = 0; i < screenWidth; i += Constants.DISPLAY_SCALE.getDouble() / Util.metersToFeet(1)){
			g.drawLine(i, 0, i, screenHeight);
		}
		for(int i = 0; i < screenHeight; i += Constants.DISPLAY_SCALE.getDouble() / Util.metersToFeet(1)){
			g.drawLine(0, i, screenWidth, i);
		}
		

		int robotCenterX = x + robotDisplayWidth/2;
		int robotCenterY = y + robotDisplayWidth/2;

		g2d.rotate(-Main.robot.heading, robotCenterX, robotCenterY);

		g2d.scale(robotScale, robotScale);
		g.translate((int) (x / robotScale), (int) (y / robotScale));
		g.drawImage(robotImage, 0, 0, this);


		drawFromCenter(g, moduleImage, 0, robotDisplayWidth/2, Main.robot.leftModule.moduleAngle, this);

		drawFromCenter(g, moduleImage, robotDisplayWidth, robotDisplayWidth/2, Main.robot.rightModule.moduleAngle, this);




		// g2d.setTransform(defaultTransform);
		// g2d.scale(robotScale, robotScale);

		// g.drawImage(targetImage, (int) (1600 / robotScale), (int) (200 / robotScale), this);

		
    }
    
	public static void init(){
		screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		try {
			robotFile = new File("./src/images/robot.png");
			moduleFile = new File("./src/images/module.png");

			robotImage = ImageIO.read(robotFile);
			moduleImage = ImageIO.read(moduleFile);

			setDisplayScales(robotFile);

		} catch (IOException e) {
			e.printStackTrace();
		}
		frame = new JFrame("Robot Sim");
		sim = new GraphicSim();
		frame.add(sim);
		frame.setSize((int) screenWidth, (int) screenHeight);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private static void setDisplayScales(File file) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(file);
		robotImgHeight = bufferedImage.getHeight();
		robotDisplayWidth = (int) (Constants.DISPLAY_SCALE.getDouble() * Constants.ROBOT_WIDTH.getDouble()); //width of robot in pixels
		robotScale = (double) robotDisplayWidth / robotImgHeight; //scaling robot image to fit display width.
	}

	public static void rescale(){
		try {
			setDisplayScales(robotFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void drawFromCenter(Graphics g, BufferedImage image, int x, int y, double rotation, ImageObserver imageObserver){
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform initialTransform = g2d.getTransform();
		g.translate(y + image.getWidth()/2, x + image.getWidth()/2);
		g2d.rotate(rotation);
		g.drawImage(image, -image.getWidth()/2, -image.getWidth()/2, imageObserver);
		g2d.setTransform(initialTransform);
	}

	

    public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		System.out.println("mouseClicked");
	}


}