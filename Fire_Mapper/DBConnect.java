import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class DBConnect {
	private Connection mainCon;
	
	public DBConnect()
	{
		connectDB();
	}
	
	public void connectDB()
	{
		try {
			// loading Oracle Driver
    		System.out.print("Looking for Oracle's jdbc-odbc driver ... ");
	    	DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
	    	System.out.print("Loaded.");

			//url = "jdbc:oracle:thin:@localhost:1521:SWAPNIL";
			String url = "jdbc:oracle:thin:@localhost:1521:ORCL";
	    	String userId = "homework1";
	    	String password = "homework1";

	    	System.out.print("Connecting to DB...");
	    	mainCon = DriverManager.getConnection(url, userId, password);
	    	System.out.println("connected !!");

   		} catch (Exception e) {
     		System.out.println( "Error while connecting to DB: "+ e.toString() );
     		e.printStackTrace();
     		System.exit(-1);
   		}
	}
	
	public void command(String sql)
	{
		try
		{
			Statement s = this.mainCon.createStatement();
			s.executeQuery(sql);
			this.mainCon.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public ResultSet retrieve(String sql) throws SQLException
	{
		Statement s = this.mainCon.createStatement();
		ResultSet rs = s.executeQuery(sql);
		return rs;
	}
	
	public void close()
	{
		try {
			mainCon.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}