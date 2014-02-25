import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Populate {

	DBConnect con;
	
	public static void main(String args[]) throws FileNotFoundException
	{
		Populate p = new Populate(args);
		System.exit(0);
	}
	
	private Populate(String[] args) throws FileNotFoundException
	{
		con = new DBConnect();
		
		//empty tables
		emptyTables();
		
		//take in the building.xy file 
		String path = getClass().getResource(args[0]).getFile();
		populateBuildings(path);
		
		//take in the hydrant.xy file 
		path = getClass().getResource(args[1]).getFile();
		populateHydrants(path);
		
		//take in the firebuilding.txt file 
		path = getClass().getResource(args[2]).getFile();
		populateFires(path);
		
		//close connection
		con.close();
		
		System.out.println("Database Successfully Populated");
	}
	
	private void emptyTables()
	{
		String sql = "TRUNCATE TABLE Hydrants";
		con.command(sql);
		sql = "TRUNCATE TABLE Buildings";
		con.command(sql);
	}
	
	private void populateBuildings(String path)
	{
		try {
			Scanner read = new Scanner(new FileInputStream(path));
			String insertSQL;
			while (read.hasNext())
			{
				//Parse Buildings
				
				String[] items = read.nextLine().split(", ");
				insertSQL = 
						"INSERT INTO Buildings VALUES ('"+
								items[0]+"','"+
								items[1]+"','0',"+
								Integer.parseInt(items[2])+
								",SDO_GEOMETRY(2003,NULL,NULL, SDO_ELEM_INFO_ARRAY(1, 1003, 1),	SDO_ORDINATE_ARRAY(";
				for (int i =3; i< items.length; i++)
				{
					insertSQL= insertSQL + Integer.parseInt(items[i])+",";
				}
				insertSQL = insertSQL + Integer.parseInt(items[3]) + "," + Integer.parseInt(items[4]) +")))";
				
				con.command(insertSQL);
			}
			
			
			read.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Building.xy not found");
		}
	}
	
	private void populateHydrants(String path)
	{
		try {
			Scanner read = new Scanner(new FileInputStream(path));
			while (read.hasNext())
			{
				//Parse Hydrants
				
				String[] items = read.nextLine().split(", ");
				String insertSQL = 
						"INSERT INTO Hydrants VALUES ('"+
								items[0]+
								"',SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE("+
								items[1]+","+
								items[2]+","+
								"NULL),"+
								"NULL, NULL))";	
				con.command(insertSQL);
				
			}
			read.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Building.xy not found");
		}
	}
	
	private void populateFires(String path)
	{
		try {
			Scanner read = new Scanner(new FileInputStream(path));
			while (read.hasNext())
			{
				//Parse Fires
				String modSQL = "UPDATE Buildings SET fire = '1' WHERE bldCode = '"+ read.nextLine()+"'";
				con.command(modSQL);
			}
			read.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Building.xy not found");
		}
	}

}
