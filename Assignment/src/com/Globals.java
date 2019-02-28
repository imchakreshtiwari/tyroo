package com;

public class Globals 
{
	static String appServerDSlookup = "java:jboss/ExpressTXR_DIB";

	public static String getDSlookup() 
	{
		return appServerDSlookup;
	}

	public static java.util.Properties getICProps()
	{
		return null; 
	}
/*
	public static void loadLocalAttributeMapCache()
	{
		Connection expconn = null;
		try
		{
			//Fetch the record from tbl_settings
			DataSource ds = (DataSource) new InitialContext(Globals.getICProps()).lookup(Globals.getDSlookup());
			//DataSource ds = (DataSource) new InitialContext(Globals.getICProps()).lookup(Globals.getDSlookup());
			expconn = ds.getConnection();
			//Context initContext = new InitialContext();
			//Context envContext  = (Context)initContext.lookup("java:/comp/env");
			//DataSource ds = (DataSource)envContext.lookup("jdbc/myoracle");
			//expconn = ds.getConnection();
			
			Statement stmt1 = expconn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String sql = "select attribute, value from tbl_settings ";
			ResultSet rs=stmt1.executeQuery(sql);
			while (rs.next())
			{
				localAttributeMapCache.put(rs.getString("attribute"), rs.getString("value"));
			}
			rs.close();
		}
		catch (Exception e){e.printStackTrace();}finally{try{if(expconn!=null){expconn.close();expconn = null; }}catch (Exception fe){fe.printStackTrace();}}
	}
	*/	
	
}
