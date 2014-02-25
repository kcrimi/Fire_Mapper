import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import oracle.jdbc.OracleTypeMetaData.Struct;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import java.awt.Insets;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JRadioButton;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JButton;

import com.sun.corba.se.impl.encoding.CodeSetConversion.BTCConverter;


public class FireMap extends JFrame implements MouseListener, MouseInputListener, ActionListener{

	DBConnect con;
	JLabel lblX, lblY;
	JTextArea queryLog;
	int queryIndex;
	private ImagePanel mapPanel;
	private JCheckBox chkbxBuildings, chkbxFireBuildings, chkbxHydrants;
	private JRadioButton rdbtnWholeRegion, rdbtnRange, rdbtnFindNeighborBuildings, rdbtnClosestHydrant;
	private JButton btnSubmitQuery;
	private boolean polyDraw, buildingSelect,polyOpen;
	
	public static void main(String[] args) {
		FireMap frame = new FireMap();	

	}

	public FireMap(){
		con = new DBConnect(); 
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Kevin Crimi 9872642000");
		
		Container contentPane = getContentPane();
		
		String path = getClass().getResource("bin/map.jpg").getFile();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {820, 240};
		gridBagLayout.rowHeights = new int[] {580, 100, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0};
		getContentPane().setLayout(gridBagLayout);
		mapPanel = new ImagePanel(path);
		
		GridBagConstraints gbc_mapPanel = new GridBagConstraints();
		gbc_mapPanel.insets = new Insets(0, 0, 5, 5);
		gbc_mapPanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_mapPanel.gridx = 0;
		gbc_mapPanel.gridy = 0;
		contentPane.add(mapPanel, gbc_mapPanel);
		
		//Control Panel
		JPanel controlPanel = new JPanel();
		controlPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Control Panel", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_controlPanel = new GridBagConstraints();
		gbc_controlPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_controlPanel.anchor = GridBagConstraints.NORTH;
		gbc_controlPanel.insets = new Insets(0, 0, 5, 0);
		gbc_controlPanel.gridx = 1;
		gbc_controlPanel.gridy = 0;
		getContentPane().add(controlPanel, gbc_controlPanel);
		GridBagLayout gbl_controlPanel = new GridBagLayout();
		gbl_controlPanel.columnWidths = new int[]{228, 0};
		gbl_controlPanel.rowHeights = new int[] {100, 150, 0, 0, 210};
		gbl_controlPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_controlPanel.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0};
		controlPanel.setLayout(gbl_controlPanel);
		
		//Feature Panel
		JPanel filterPanel = new JPanel();
		filterPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Feature Selection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_filterPanel = new GridBagConstraints();
		gbc_filterPanel.insets = new Insets(0, 0, 5, 0);
		gbc_filterPanel.gridx = 0;
		gbc_filterPanel.gridy = 0;
		controlPanel.add(filterPanel, gbc_filterPanel);
		GridBagLayout gbl_filterPanel = new GridBagLayout();
		gbl_filterPanel.columnWidths = new int[] {216, 0};
		gbl_filterPanel.rowHeights = new int[] {20, 0, 0};
		gbl_filterPanel.columnWeights = new double[]{0.0};
		gbl_filterPanel.rowWeights = new double[]{0.0, 0.0, 0.0};
		filterPanel.setLayout(gbl_filterPanel);
		
		chkbxBuildings = new JCheckBox("Buildings");
		GridBagConstraints gbc_chkbxBuildings = new GridBagConstraints();
		gbc_chkbxBuildings.fill = GridBagConstraints.BOTH;
		gbc_chkbxBuildings.insets = new Insets(0, 0, 5, 0);
		gbc_chkbxBuildings.gridx = 0;
		gbc_chkbxBuildings.gridy = 0;
		filterPanel.add(chkbxBuildings, gbc_chkbxBuildings);
		
		chkbxFireBuildings = new JCheckBox("Buildings on Fire");
		GridBagConstraints gbc_chkbxFireBuildings = new GridBagConstraints();
		gbc_chkbxFireBuildings.fill = GridBagConstraints.BOTH;
		gbc_chkbxFireBuildings.insets = new Insets(0, 0, 5, 0);
		gbc_chkbxFireBuildings.gridx = 0;
		gbc_chkbxFireBuildings.gridy = 1;
		filterPanel.add(chkbxFireBuildings, gbc_chkbxFireBuildings);
		
		chkbxHydrants = new JCheckBox("Hydrants");
		GridBagConstraints gbc_chckbxHydrants = new GridBagConstraints();
		gbc_chckbxHydrants.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxHydrants.fill = GridBagConstraints.BOTH;
		gbc_chckbxHydrants.gridx = 0;
		gbc_chckbxHydrants.gridy = 2;
		filterPanel.add(chkbxHydrants, gbc_chckbxHydrants);
		
		//Query Panel
		JPanel queryPanel = new JPanel();
		queryPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Query Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_queryPanel = new GridBagConstraints();
		gbc_queryPanel.fill = GridBagConstraints.BOTH;
		gbc_queryPanel.insets = new Insets(0, 0, 5, 0);
		gbc_queryPanel.gridx = 0;
		gbc_queryPanel.gridy = 1;
		controlPanel.add(queryPanel, gbc_queryPanel);
		GridBagLayout gbl_queryPanel = new GridBagLayout();
		gbl_queryPanel.columnWidths = new int[]{216, 0};
		gbl_queryPanel.rowHeights = new int[] {20, 20, 20, 20};
		gbl_queryPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_queryPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		queryPanel.setLayout(gbl_queryPanel);
		
		//Radio Buttons
		rdbtnWholeRegion = new JRadioButton("Whole Region");
		GridBagConstraints gbc_rdbtnWholeRegion = new GridBagConstraints();
		gbc_rdbtnWholeRegion.fill = GridBagConstraints.BOTH;
		gbc_rdbtnWholeRegion.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnWholeRegion.gridx = 0;
		gbc_rdbtnWholeRegion.gridy = 0;
		queryPanel.add(rdbtnWholeRegion, gbc_rdbtnWholeRegion);
		
		rdbtnRange = new JRadioButton("Range");
		GridBagConstraints gbc_rdbtnRange = new GridBagConstraints();
		gbc_rdbtnRange.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnRange.fill = GridBagConstraints.BOTH;
		gbc_rdbtnRange.gridx = 0;
		gbc_rdbtnRange.gridy = 1;
		queryPanel.add(rdbtnRange, gbc_rdbtnRange);
		
		rdbtnFindNeighborBuildings = new JRadioButton("Find Neighbor Buildings");
		GridBagConstraints gbc_rdbtnFindNeighborBuildings = new GridBagConstraints();
		gbc_rdbtnFindNeighborBuildings.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnFindNeighborBuildings.fill = GridBagConstraints.BOTH;
		gbc_rdbtnFindNeighborBuildings.gridx = 0;
		gbc_rdbtnFindNeighborBuildings.gridy = 2;
		queryPanel.add(rdbtnFindNeighborBuildings, gbc_rdbtnFindNeighborBuildings);
		
		rdbtnClosestHydrant = new JRadioButton("Find Closest Fire Hydrants");
		GridBagConstraints gbc_rdbtnFindClosestFire = new GridBagConstraints();
		gbc_rdbtnFindClosestFire.fill = GridBagConstraints.BOTH;
		gbc_rdbtnFindClosestFire.gridx = 0;
		gbc_rdbtnFindClosestFire.gridy = 3;
		queryPanel.add(rdbtnClosestHydrant, gbc_rdbtnFindClosestFire);
		
		ButtonGroup queryRadios = new ButtonGroup();
		queryRadios.add(rdbtnWholeRegion);
		queryRadios.add(rdbtnRange);
		queryRadios.add(rdbtnFindNeighborBuildings);
		queryRadios.add(rdbtnClosestHydrant);
		
		//Coordinate Panel
		JPanel coordinatePanel = new JPanel();
		coordinatePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Coordinates", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_coordinatePanel = new GridBagConstraints();
		gbc_coordinatePanel.insets = new Insets(0, 0, 5, 0);
		gbc_coordinatePanel.fill = GridBagConstraints.BOTH;
		gbc_coordinatePanel.gridx = 0;
		gbc_coordinatePanel.gridy = 2;
		controlPanel.add(coordinatePanel, gbc_coordinatePanel);
		coordinatePanel.setLayout(new BoxLayout(coordinatePanel, BoxLayout.Y_AXIS));
		
		lblX = new JLabel("X: ");
		coordinatePanel.add(lblX);
		
		lblY = new JLabel("Y: ");
		coordinatePanel.add(lblY);
		
		//Submit Button
		btnSubmitQuery = new JButton("Submit Query");
		GridBagConstraints gbc_btnSubmitQuery = new GridBagConstraints();
		gbc_btnSubmitQuery.gridx = 0;
		gbc_btnSubmitQuery.gridy = 3;
		controlPanel.add(btnSubmitQuery, gbc_btnSubmitQuery);
		
		//Query Text Area
		queryLog = new JTextArea();
		queryLog.setBackground(Color.BLACK);
		queryLog.setForeground(Color.GREEN);
		
		JScrollPane scrollPane = new JScrollPane(queryLog);
		GridBagConstraints gbc_queryLog = new GridBagConstraints();
		gbc_queryLog.gridwidth = 2;
		gbc_queryLog.insets = new Insets(0, 0, 5, 5);
		gbc_queryLog.fill = GridBagConstraints.BOTH;
		gbc_queryLog.gridx = 0;
		gbc_queryLog.gridy = 1;
		getContentPane().add(scrollPane, gbc_queryLog);
		
		btnSubmitQuery.addActionListener(this);
		rdbtnClosestHydrant.addActionListener(this);
		rdbtnFindNeighborBuildings.addActionListener(this);
		rdbtnRange.addActionListener(this);
		rdbtnWholeRegion.addActionListener(this);
		mapPanel.addMouseMotionListener(this);
		mapPanel.addMouseListener(this);
		pack();
		setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//Draw Polygon Mode
		if (polyDraw && polyOpen)
		{
			if (e.isMetaDown())
			{
				if (mapPanel.userPolyX.length> 2)
				{
					mapPanel.closeUserPoly();
					btnSubmitQuery.setEnabled(true);
					polyOpen = false;
				}
								
			}
			else
			{
				mapPanel.addUserPoint(e.getX(), e.getY());
			}
			repaint();
		}
		
		//Closest Hydrant Mode
		if (rdbtnClosestHydrant.isSelected())
		{
			//Find Clicked Building
			String bldID;
			String sql = "SELECT * FROM Buildings B WHERE sdo_geom.relate(B.shape, 'anyinteract', SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE("+
								e.getX()+","+
								e.getY()+","+
								"NULL),"+
								"NULL, NULL), 0.005)='TRUE'";
			try {
				queryLog.append("Query #"+ queryIndex + ": "+sql+"\n");
				queryIndex++;
				ResultSet rs = con.retrieve(sql);
				while (rs.next())
				{
					STRUCT st = (STRUCT) rs.getObject("shape");
					bldID = rs.getString("bldIndex");
					JGeometry geo = JGeometry.load(st);
					mapPanel.fireBuildings.add(new Building(geo.getOrdinatesArray()));
				
					sql = "SELECT H.point FROM Hydrants H, Buildings B WHERE B.bldIndex= '"+
							bldID+
							"' AND SDO_NN(H.point, B.shape, 'sdo_num_res=1')='TRUE'";
					ResultSet rs2 = con.retrieve(sql);
					while(rs2.next())
					{
						STRUCT st2 = (STRUCT) rs2.getObject("point");
						JGeometry geo2 = JGeometry.load(st2);
						mapPanel.hydrants.add((int)geo2.getPoint()[0]);
						mapPanel.hydrants.add((int)geo2.getPoint()[1]);
					}
				}
					
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		lblX.setText("X: "+e.getX());
		lblY.setText("Y: "+e.getY());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnSubmitQuery)
		{
		// Make sure there are features to display
		if ((rdbtnWholeRegion.isSelected()||rdbtnRange.isSelected())&&!chkbxBuildings.isSelected()&&!chkbxFireBuildings.isSelected()&&!chkbxHydrants.isSelected()){
			queryLog.append("No features selected to display\n");
		}
		
		//create range WHERE clause
		String shapeWhereClause = "";
		String pointWhereClause = "";
		String andWhereClause = "";
		if(rdbtnRange.isSelected())
		{
			shapeWhereClause = " sdo_geom.relate(B.shape, 'anyinteract', SDO_Geometry(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY("+
					mapPanel.getUserPoly()+")), 0.005)='TRUE'";
			andWhereClause = " AND"+ shapeWhereClause;
			shapeWhereClause = " WHERE"+ shapeWhereClause;
			pointWhereClause = " WHERE sdo_geom.relate(H.point, 'ANYINTERACT', SDO_Geometry (2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY("+
					mapPanel.getUserPoly()+")), 0.01)='TRUE'";
			mapPanel.clearUserPoly();
		}
		
			String sql;
			if(chkbxBuildings.isSelected())
			{
				
				sql = "SELECT shape FROM Buildings B"+shapeWhereClause;
				try {
					queryLog.append("Query #"+ queryIndex + ": "+sql+"\n");
					queryIndex++;
					ResultSet rs = con.retrieve(sql);
					while (rs.next())
					{
						STRUCT st = (STRUCT) rs.getObject("shape");
						JGeometry geo = JGeometry.load(st);
						mapPanel.buildings.add(new Building(geo.getOrdinatesArray()));
					}
						
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(chkbxFireBuildings.isSelected())
			{
				
				sql = "SELECT shape FROM Buildings B WHERE fire = '1'"+andWhereClause;
				try {
					queryLog.append("Query #"+ queryIndex + ": "+sql+"\n");
					queryIndex++;
					ResultSet rs = con.retrieve(sql);
					while (rs.next())
					{
						STRUCT st = (STRUCT) rs.getObject("shape");
						JGeometry geo = JGeometry.load(st);
						mapPanel.fireBuildings.add(new Building(geo.getOrdinatesArray()));
					}
						
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			if(chkbxHydrants.isSelected())
			{
				
				sql = "SELECT point FROM Hydrants H"+pointWhereClause;
				try {
					queryLog.append("Query #"+ queryIndex + ": "+sql+"\n");
					queryIndex++;
					ResultSet rs = con.retrieve(sql);
					while (rs.next())
					{
						STRUCT st = (STRUCT) rs.getObject("point");
						JGeometry geo = JGeometry.load(st);
						mapPanel.hydrants.add((int)geo.getPoint()[0]);
						mapPanel.hydrants.add((int)geo.getPoint()[1]);
					}
						
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			repaint();
			polyOpen = true;
		}		
					
		//Neighbor Buildings mode
		if(e.getSource() == rdbtnFindNeighborBuildings)
		{
			mapPanel.clearShapes();
			String sql = "SELECT * FROM Buildings B1, (SELECT * FROM Buildings B WHERE B.fire = '1') fireB WHERE sdo_geom.sdo_distance(B1.shape, fireB.shape, 0.005) <= 100";
			try {
				queryLog.append("Query #"+ queryIndex + ": "+sql+"\n");
				queryIndex++;
				ResultSet rs = con.retrieve(sql);
				while (rs.next())
				{
					STRUCT st = (STRUCT) rs.getObject("shape");
					JGeometry geo = JGeometry.load(st);
					if (rs.getBoolean("fire"))
					{
						mapPanel.fireBuildings.add(new Building(geo.getOrdinatesArray()));
					}
					else
					{
						mapPanel.buildings.add(new Building(geo.getOrdinatesArray()));
					}
				}
					
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			repaint();
		}
		
		//Disable/enable buttons and settings
		if(e.getSource() == rdbtnWholeRegion || e.getSource() ==rdbtnRange)
		{
			mapPanel.clearShapes();
			mapPanel.clearSaved = true;
			buildingSelect = false;
			chkbxBuildings.setEnabled(true);
			chkbxFireBuildings.setEnabled(true);
			chkbxHydrants.setEnabled(true);
			btnSubmitQuery.setEnabled(true);
			if (e.getSource()==rdbtnWholeRegion)
			{
				mapPanel.clearUserPoly();
				polyDraw = false;
			}
			else
			{
				polyDraw = true;
				polyOpen = true;
				btnSubmitQuery.setEnabled(false);
			}
		}
		if(e.getSource() == rdbtnClosestHydrant || e.getSource() ==rdbtnFindNeighborBuildings)
		{
			mapPanel.clearUserPoly();
			chkbxBuildings.setEnabled(false);
			chkbxFireBuildings.setEnabled(false);
			chkbxHydrants.setEnabled(false);
			btnSubmitQuery.setEnabled(false);
			if (e.getSource() == rdbtnClosestHydrant)
			{
				mapPanel.clearSaved = false;
				buildingSelect = true;
				polyDraw = false;
			}
			else
			{
				mapPanel.clearSaved = true;
				buildingSelect = false;
				polyDraw = true;
			}
		}
	}
}
