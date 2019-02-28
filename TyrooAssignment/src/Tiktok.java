import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class mythread implements Runnable
{
	String rule_name="";
	String time= "";

	mythread(String rule_name, String time )
	{
		this.rule_name = rule_name;
		this.time = time;
	
	}
	@Override
	public void run()
	{
		System.out.println("rule_name : "+ rule_name +" time : "+ time +"\n  Thread Name : " + Thread.currentThread().getName() + " time :  " + System.currentTimeMillis());
		RunExecutor rex = new RunExecutor(rule_name,time);
		rex.dbconnect();
	}
	
}
public class Tiktok 
{
	static final int MAX_T = 3;
	public static void main(String[] args) 
	{
		ExecutorService pool = Executors.newFixedThreadPool(MAX_T); 
		Connection conn = null;
		PreparedStatement pstmt =null;
		ResultSet rs = null;
		try
		{
			conn = Globals.getdbcon();
			String sql ="select rule_name, start_time from tbl_cond where status ='A' order by start_time";
			pstmt = conn.prepareStatement(sql);
			System.out.println(sql);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				System.out.println("\n rule name : "+rs.getString("rule_name"));
				String rulename = rs.getString("start_time");
				rulename = rulename.substring(11, 16);
				System.out.println("rule time2 : "+rulename);
				pool.execute(new mythread(rs.getString("rule_name"), rulename ));
				/*Thread  t1 = new Thread(new mythread(rs.getString("rule_name")));
				t1.start();*/
			}
		}
		 catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{conn.close();if(pstmt!=null)pstmt.close();}catch (Exception fe){fe.printStackTrace();}	
		}
		
	}
}
