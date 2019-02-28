package com;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

public class Loginpage extends GenericForwardComposer<Component>
{

	Textbox emailid;
	Textbox psw;
	Button login;
	String email;
	String password;
	
	@SuppressWarnings("unchecked")
	public void doAfterCompose(Component comp) throws Exception
	{
		super.doAfterCompose(comp);
		login.addEventListener("onClick", loginevent);
	}
	
	

	@SuppressWarnings("rawtypes")
	EventListener loginevent = new EventListener()
	{
		public void onEvent(Event evt) throws Exception
		{
			if((evt.getName().equalsIgnoreCase("onOK") || evt.getName().equalsIgnoreCase("onClick")) && evt.getTarget().getId().equalsIgnoreCase("Login"))
			{
				Connection conn = null;
				PreparedStatement pstmt = null;
				ResultSet rs = null;
				String em=null,p=null;
				email = emailid.getValue().toString();
				password = psw.getValue().toString();
				try
				{
					DataSource ds = (DataSource) new InitialContext(Globals.getICProps()).lookup(Globals.getDSlookup());
					conn = ds.getConnection();
					String sql = "select email ,passwor from userinfo where email = '"+email+"' and passwor = '"+password+"'";
					pstmt = conn.prepareStatement(sql);
					rs = pstmt.executeQuery();
					System.out.println(sql);
					while(rs.next())
					{
						em = rs.getString("email");
						p = rs.getString("passwor");
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				System.out.println(email +"   "+ password);
				if(em!=null  && p!=null && email.equalsIgnoreCase(em) && password.equals(p))
				{
					Executions.sendRedirect("/main.zul");
				}
				else
				{
					Executions.sendRedirect("/index1.zul");
				}
			}
		}
	};
}
