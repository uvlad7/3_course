import java.sql.*;
import java.io.*;
public class DB
{
 public static void main(String[] args)
     {
	//MyDB должно быть настроено на JavaTest.mdb
	String url = "jdbc:odbc:MyDB";
	Connection conn;

	try
	 {
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	 }
	catch(ClassNotFoundException e)
	 {
		System.out.println("Exception"+e.getMessage());
		return;
	 }	

	try
	 {
		conn = DriverManager.getConnection(url,"Admin","Admin");
		java.sql.Statement stmt = conn.createStatement();
		ResultSet r = stmt.executeQuery("SELECT * FROM Students");
		while (r.next())
		{
			System.out.println("Name = " +  r.getString("name")+" Address = " +  r.getString("address")+" Telephone = " +  r.getString("telephone"));
		}
				
		stmt.close();
		conn.close();
	 }
	 catch(SQLException ex)
	 {
		System.err.println("SQLException: " + ex.getMessage());
	 }

}

}
