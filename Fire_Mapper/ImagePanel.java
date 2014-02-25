import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.events.MouseEvent;


public class ImagePanel extends JPanel{

	private BufferedImage bkgrnd;
	ArrayList<Integer> hydrants = new ArrayList<Integer>();
	ArrayList<Building>buildings = new ArrayList<Building>();
	ArrayList<Building> fireBuildings = new ArrayList<Building>();
	int[] userPolyX = new int[0];
	int[] userPolyY = new int[0];
	boolean dispBuildings, dispFires, clearSaved;
	
	
	public ImagePanel(String path){
		try{
			bkgrnd = ImageIO.read(new File(path));
		} catch (IOException ex){
			System.out.println("error");
		}
		Dimension size = new Dimension(bkgrnd.getWidth(null), bkgrnd.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
		
	}
	
	public void addUserPoint(int x, int y)
	{
		int[] tempX = new int[userPolyX.length+1];
		int[] tempY = new int[userPolyY.length+1];
		for (int i = 0; i< userPolyX.length; i++)
		{
			tempX[i] = userPolyX[i];
			tempY[i] = userPolyY[i];
		}
		tempX[tempX.length-1] = x;
		tempY[tempY.length-1] = y;
		userPolyX = tempX;
		userPolyY = tempY;
	}
	
	public void closeUserPoly()
	{
		addUserPoint(userPolyX[0],userPolyY[0]);
	}
	
	public String getUserPoly()
	{
		String points = userPolyX[0]+","+userPolyY[0];
		for (int i = 1; i< userPolyX.length;i++)
		{
			points = points +", " + userPolyX[i]+ ","+ userPolyY[i];
		}
		return points;
	}
	
	public void clearUserPoly()
	{
		userPolyX = new int[0];
		userPolyY = new int[0];
	}
	
	public void clearShapes()
	{
		hydrants.clear();
		buildings.clear();
		fireBuildings.clear();
	}
	
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(bkgrnd, 0, 0, null);
		Graphics2D g2d = (Graphics2D) g;
		
		//paint hydrants
		for (int i = 0; i < (hydrants.size()/2); i++)
		{
			g2d.setColor(Color.green);
			g2d.fillRect(hydrants.get(i*2)-7, hydrants.get((i*2)+1)-7, 15, 15);
		}	
		
		//paint buildings
		for (int i = 0; i < buildings.size(); i++)
		{
			g2d.setColor(Color.yellow);
			g2d.drawPolygon(buildings.get(i).xVerts, buildings.get(i).yVerts, buildings.get(i).xVerts.length);
		}
		for (int i = 0; i< fireBuildings.size(); i++)
		{
			g2d.setColor(Color.red);
			g2d.drawPolygon(fireBuildings.get(i).xVerts, fireBuildings.get(i).yVerts, fireBuildings.get(i).xVerts.length);
		}
		
		//paint userpolygon
		if (userPolyX.length>0)
		{
			g2d.setColor(Color.red);
			g2d.drawPolyline(userPolyX, userPolyY, userPolyX.length);
		}
		
		if(clearSaved)
		{
			clearShapes();
		}
	}
}
