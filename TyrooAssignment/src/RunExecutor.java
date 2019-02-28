

import java.io.IOException;
import java.security.InvalidKeyException;
import java.sql.*;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class RunExecutor 
{

	
	public float eCPM=0;
	public float Spend=0;
	public float eCPC=0;
	public float eCPI=0;
	public int Clicks=0;
	public int Installs=0;
	public int Impressions=0;
	public Boolean servicerun = true;
	public Boolean notify = false;
	String rule_cond ;
	public String rulename="";
	public String starttime="";
	public String schedule;
	public int schedule_gap=0;
	
	public RunExecutor(String i , String time) {
		this.rulename = i;
		this.starttime = time;
	}

	public void fetch(String schedule)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{

			conn = Globals.getdbcon();
			
			String sql ="select install, clicke, spends,impression from tbl_cond_data where rule_name='"+ rulename +"' and format(schedule , 'hh:mm') = '"+schedule+"'";
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			
			System.out.println(rulename + "   = " + schedule + "  sql : "+sql); 
			while(rs.next())
			{
				Installs = rs.getInt("install");
				Clicks = rs.getInt("clicke");
				Impressions=rs.getInt("impression");
				Spend = rs.getInt("spends");
			}
			eCPM = Spend*1000 /Impressions;
			eCPC = Spend / Clicks;
			eCPI = Spend / Installs;
			
			System.out.println( "-------------------");
			System.out.println( "Installs " + Installs);
			System.out.println( "Clicks " + Clicks);
			System.out.println( "Impressions " + Impressions);
			System.out.println( "Spend " + Spend);
			System.out.println( "eCPM " + Installs);
			System.out.println( "eCPC " + eCPC);
			System.out.println( "eCPI " + eCPI);
			System.out.println( "-------------------");

			conn.close();
			rs.close();
		
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
	
	 String rulecondition() 
	 {
		 	Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
		 try
			{
			 
			 	conn = Globals.getdbcon();
			 	
				//DataSource ds = (DataSource) new InitialContext(Globals.getICProps()).lookup(Globals.getDSlookup());
				//conn = ds.getConnection();
			 	
				String sql ="select condition,schedule_gap from tbl_cond_data where rule_name='"+ rulename +"'";
				pstmt = conn.prepareStatement(sql);
				System.out.println(sql);
				rs = pstmt.executeQuery();
				while(rs.next())
				{
					rule_cond = rs.getString("condition");
					schedule_gap = rs.getInt("schedule_gap");
				}
				//rulename = "`Clicks` >= 50000 AND `Installs` <= 100";
				System.out.println("runnung for condititon  :  "+rule_cond);
				System.out.println("schedule_gap  :  "+schedule_gap);
				rs.close();
			}
		 catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try{conn.close();if(pstmt!=null)pstmt.close();}catch (Exception fe){fe.printStackTrace();}	
			}
		return rule_cond;
	}
	void dbconnect() 
	{
		
		rule_cond = rulecondition();
		System.out.println("rule_cond : "+rule_cond);
		System.out.println("schedule_gap  :  "+schedule_gap);
		while(servicerun)
		{
			Calendar c =Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("hh:mm");
			java.util.Date d = c.getTime();
			String date = df.format(d);
			System.out.println(date  + " starttime : "+starttime);
			
			if(date.equals(starttime))
			{
				servicerun = false;
				System.out.println("servicerun  :" +servicerun);
				schedule = starttime;
				while(!notify)
				{
					fetch(schedule);
					System.out.println("schedule : " +schedule);
					notify = runservice(rule_cond);
					try 
					{
						Calendar cal = Calendar.getInstance();
						Date d1 = new SimpleDateFormat("hh:mm").parse(schedule);
						cal.setTime(d1);	
						System.out.println(d1);
						cal.add(Calendar.MINUTE, 2);
						System.out.println(cal.getTime());
						schedule =  cal.getTime().toString();
					}
					catch (ParseException et) 
					{
						et.printStackTrace();
					}
					System.out.println("thread sleep..  schedule ." + schedule );
					try
					{
						Thread.sleep(3000);
						//Thread.sleep(schedule_gap *60 *1000);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				System.out.println("Service Stopped....!!!");
			}
			else
			{
				servicerun=true;
				System.out.println("else part ....!!!");
				try 
				{
					Thread.sleep(30* 1000);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		
		try 
		{
			Connection conn = Globals.getdbcon();
			String sql = "update tbl_cond set status= 'D' where rule_name ='"+rulename+"'";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			int k = pstmt.executeUpdate();
			if(k >0) System.out.println("done for  : "+rulename);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		
	}
	

	private Boolean runservice(String rule_cond) {
		Boolean ret = false;
		switch(rule_cond)
		{
		case "`eCPM` >= $5.00 AND `Impressions` >= 1000000":
		{
			if(eCPM >= 5 && Impressions >= 1000000)
			{
				ret = sendtomail();
			}
			break;
		}
		case "`Spend` >= $1000.00 AND `eCPC` <=  $0.20":
		{
			if(Spend >= 1000 && eCPC <=  0.20)
			{
				ret = sendtomail();
			}
			break;
		}
		case "`Clicks` >= 50000 AND `Installs` <= 100":
		{
			if(Clicks >= 50000 && Installs <= 100)
			{
				ret = sendtomail();
			}
			break;
		}
		case "`eCPI` >= $2.00 AND `Installs` >= 100":
		{
			if(eCPI >= 2.00 && Installs >= 100)
			{
				ret = sendtomail();
			}
			break;
		}
		default :
		{
			System.out.println("Not any Condition fulfilled Service must be going on....!!");
		}
		}
		return ret;
	}
	private Boolean sendtomail() 
	{
		
		Boolean ret = true;
		
		  String host="smtp.gmail.com";
		  final String user="chakresh0108@gmail.com";//change accordingly
		  final String password="*****";//change accordingly
		  
		  String to="xxxxxxxx@gmail.com";//change accordingly
		 
		   //Get the session object
		   Properties props = new Properties();
		   props.put("mail.smtp.starttls.enable", "true");
		   props.setProperty("mail.transport.protocol", "smtp"); 
		   props.put("mail.smtp.host",host);
		   props.put("mail.smtp.auth", "true");
		   props.put("mail.smtp.port", "587");  
		 //  Session session = Session.getInstance(props, new GMailAuthenticator(user, password));
		   Session session = Session.getDefaultInstance(props,
		    new javax.mail.Authenticator() {
			   
		      protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user,password);
		      }
		    });

		   //Compose the message
		    try 
		    {
		     MimeMessage message = new MimeMessage(session);
		     message.setFrom(new InternetAddress(user));
		     message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
		     message.setSubject("Campaign Stopprd");
		     message.setText("This message is to inform you that your campaign is stopped.");
		     
		    //send the message
		     Transport.send(message);
		     
		     System.out.println("message sent successfully...");
		     
		     } 
		    catch (MessagingException e) 
		    {
		    	ret = false;
		    	e.printStackTrace();
		    	
		    } 
		
		return ret;
	}

	
}
