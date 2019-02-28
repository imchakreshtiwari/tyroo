package com;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;
import java.text.*;


public class addshow extends GenericForwardComposer<Component>
{

	Div svrdatadiv;
	Textbox rulename;
	Textbox scheduletb;
	Combobox  campaigncb;
	Combobox conditioncb;
	Combobox actionncb;
	Combobox statuscb;
	Button savebtn;
	Button fetchservices;
	Listbox servicelistbox;
	Listitem servicesitem;
	Timebox tb3;
	int pagenumber = 0;

	public void doAfterCompose(Component comp) throws Exception 
	{
		super.doAfterCompose(comp);
		loadcombovaluesstatus();
		loadcombovaluescampaign();
		loadcombovaluescondition();
		loadcombovaluesaction();
		servicelistbox.addEventListener("onPaging", getpagenumber);
		pagenumber = 0;
		
		savebtn.addEventListener("onClick", savelist);
		fetchservices.addEventListener("onClick", showstatus);
		show_grid_tasks(); 
	}
	
	public void loadcombovaluescampaign()
	{
		campaigncb.appendItem("swigy");
		campaigncb.appendItem("zomato");
		campaigncb.appendItem("netflix");
		campaigncb.appendItem("ola");
	}

	public void loadcombovaluescondition()
	{
		conditioncb.appendItem("`eCPM` >= $5.00 AND `Impressions` >= 1000000");
		conditioncb.appendItem("`Spend` >= $1000.00 AND `eCPC` <=  $0.20");
		conditioncb.appendItem("`Clicks` >= 50000 AND `Installs` <= 100");
		conditioncb.appendItem("`eCPI` >= $2.00 AND `Installs` >= 100");
	}
	
	public void loadcombovaluesaction()
	{
		actionncb.appendItem("Pause");
		actionncb.appendItem("Start");
		actionncb.appendItem("Notify");
		
	}
	public void loadcombovaluesstatus()
	{
		statuscb.appendItem("A");
		statuscb.appendItem("D");
		
	}

	EventListener savelist = new EventListener()
	{
		@Override
		public void onEvent(Event ev) throws Exception 
		{
			if(ev.getName().equalsIgnoreCase("onOK") || ev.getName().equalsIgnoreCase("onClick"))
			{
				Connection conn=null;
				PreparedStatement pstmt =null;
				PreparedStatement pstmt1 =null;

				try		{
					DataSource ds = (DataSource) new InitialContext(Globals.getICProps()).lookup(Globals.getDSlookup());
					conn = ds.getConnection();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					System.out.println("---"+ tb3.getValue());
					pstmt = conn.prepareStatement("insert into tbl_cond (rule_name,schedule_gap,campaign,status,condition,action,start_time) values (?,?,?,?,?,?,?)");
					pstmt.setString(1, rulename.getValue());
					pstmt.setString(2, scheduletb.getValue());
					pstmt.setString(3, campaigncb.getValue());
					pstmt.setString(4, statuscb.getValue());
					pstmt.setString(5, conditioncb.getValue());
					pstmt.setString(6, actionncb.getValue());
					pstmt.setString(7, tb3.getValue().toString());
					pstmt.executeUpdate();
					pstmt2 = conn.prepareStatement("insert into tbl_cond_data (rule_name,schedule_gap,campaign,status,condition,action,start_time,install, clicke, spends,impression) values (?,?,?,?,?,?,?,?,?,?,?)");
					pstmt2.setString(1, rulename.getValue());
					pstmt2.setString(2, scheduletb.getValue());
					pstmt2.setString(3, campaigncb.getValue());
					pstmt2.setString(4, statuscb.getValue());
					pstmt2.setString(5, conditioncb.getValue());
					pstmt2.setString(6, actionncb.getValue());
					pstmt2.setString(7, tb3.getValue().toString());
					pstmt2.setInt(8,0);
					pstmt2.setInt(9,0);
					pstmt2.setInt(10,0);
					pstmt2.setInt(11,0);
					pstmt2.executeUpdate();
					pstmt2.close();
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
	};
	EventListener getpagenumber = new EventListener()
	{
		public void onEvent(Event ev) throws Exception
		{
			if(ev.getName().equalsIgnoreCase("onPaging"))
			{
				pagenumber = servicelistbox.getActivePage();
			}
		}
	};
	EventListener showstatus = new EventListener()
	{
		public void onEvent(Event ev) throws Exception
		{
			if(ev.getName().equalsIgnoreCase("onOK") || ev.getName().equalsIgnoreCase("onClick"))
			{
					show_grid_tasks();
			}
		}
	};
	
	public void show_grid_tasks()
	{
		PreparedStatement pstmt1 = null;
		Connection conn = null;
		try
		{
			servicelistbox.getItems().clear();
			DataSource ds = (DataSource) new InitialContext(Globals.getICProps()).lookup(Globals.getDSlookup());
			conn = ds.getConnection();
			String sql="select rule_name,schedule,campaign,status from tbl_cond order by rule_name";
			pstmt1=conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			System.out.println(sql);
			ResultSet rs = pstmt1.executeQuery();
			if(rs.next())
			{
				svrdatadiv.setVisible(true);
				rs.beforeFirst();
				while(rs.next())
				{
					servicesitem = new Listitem();
					//servicesitem.setId("R"+rs.getString("SNO"));

					Listcell node = new Listcell();
					node.setLabel(rs.getString("rule_name"));
					node.setParent(servicesitem);

					Listcell service = new Listcell();
					service.setStyle("text-align: left");
					service.setLabel(rs.getString("campaign"));
					service.setParent(servicesitem);

					Listcell instancename = new Listcell();
					instancename.setLabel(rs.getString("schedule"));
					instancename.setParent(servicesitem);

					Listcell current_stat = new Listcell();
					current_stat.setLabel(rs.getString("status"));
					current_stat.setParent(servicesitem);

					

					Listcell btn = new Listcell();
					Button editbtn = new Button();
					editbtn.setWidth("100%");
					editbtn.setId(rs.getString("rule_name"));
					
					editbtn.setLabel("EDIT");
					
					editbtn.setParent(btn);
					editbtn.setMold("trendy");
					editbtn.setClass("btn btn-primary");
					btn.setParent(servicesitem);

					servicesitem.setParent(servicelistbox);

					editbtn.addEventListener("onClick", proceesdevent);
					editbtn.addEventListener("onOK", proceesdevent);
				}
				servicelistbox.setActivePage(pagenumber);
			}
			else
			{
				System.out.println("no data...!!!");
				}
			rs.close(); rs = null;
		}
		catch (Exception e){e.printStackTrace();}
		finally{try{conn.close();if(pstmt1!=null)pstmt1.close();}catch (Exception fe){fe.printStackTrace();}}
	}	
	
	EventListener proceesdevent = new EventListener()
	{
		@SuppressWarnings("null")
		public void onEvent(Event ev) throws Exception
		{
			if(ev.getName().equalsIgnoreCase("onClick"))
			{
					Connection conn = null;
					PreparedStatement pstmt6 = null;
					ResultSet rs=null;
					String sevrid = ev.getTarget().getId();
					try
					{
						DataSource ds = (DataSource) new InitialContext(Globals.getICProps()).lookup(Globals.getDSlookup());
						conn = ds.getConnection();
						
						String q6="select rule_name, campaign, schedule , status ,start_time from tbl_cond where rule_name = '"+sevrid+"'";
						pstmt6 = conn.prepareStatement(q6,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
						rs = pstmt6.executeQuery();
						System.out.println(q6);
						if(rs.next())
						{
							if(rs.getString("rule_name")!=null)
							{
								rulename.setValue(rs.getString("rule_name"));
							}
							if(rs.getString("start_time")!=null)
							{
								tb3.setRawValue(rs.getString("start_time"));
							}
							if(rs.getString("schedule")!=null)
							{
								scheduletb.setValue(rs.getString("schedule"));
							}
							if(rs.getString("Campaign")!=null)
							{
								campaigncb.setValue(rs.getString("Campaign"));
							}
							/*if(rs.getString("BRANCH_ADDRESS")!=null)
							{
								actionncb.setValue(rs.getString("BRANCH_ADDRESS"));
							}*/
							if(rs.getString("status")!=null)
							{
								statuscb.setValue(rs.getString("status"));
							}
							
						}
						savebtn.setVisible(false);
						rs.close();rs=null;
						pstmt6.close();pstmt6=null;
					}
					catch (Exception e){e.printStackTrace();}
					finally
					{
						try
						{
							if(pstmt6!=null)
							{pstmt6.close();}
							if(rs!=null){rs.close();}
							conn.close();}
						catch (Exception fe){fe.printStackTrace();}
						}
				
		}
}
};
}
