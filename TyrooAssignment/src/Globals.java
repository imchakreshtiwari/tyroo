import java.io.IOException;
import java.security.InvalidKeyException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Globals 
{
	/*static String appServerDSlookup = "java:jboss/ExpressTXR_DIB";

	public static String getDSlookup() 
	{
		return appServerDSlookup;
	}

	public static java.util.Properties getICProps()
	{
		return null; 
		
	}*/

	protected static String dbURL;
    protected static String dbDriver;
    protected static Connection dbCon;
    protected static String  dbUser = "expapp_vat";
    protected static String  dbPassword = "Uday754Uaa";

    public static Connection getdbcon() throws SQLException, ClassNotFoundException, InvalidKeyException,IOException
    {
    	
		
		dbURL = "jdbc:sqlserver://"+"192.168.2.104\\EXPRESS_VAT_DIB:1433";
		dbDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        Class.forName(dbDriver);
        dbCon = DriverManager.getConnection(dbURL, dbUser, dbPassword); 
        return dbCon;
    }
    public void dbcon_close() throws SQLException
    {
        dbCon.close();
    }

}
