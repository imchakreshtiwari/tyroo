package com;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;

public class Main extends GenericForwardComposer<Component>
{
	Button addrule;
	
	public void doAfterCompose(Component comp) throws Exception
	{
		super.doAfterCompose(comp);
		addrule.addEventListener("onClick", addrulev);
	}
	
	
	EventListener addrulev = new EventListener()
	{
		public void onEvent(Event evt) throws Exception
		{
			if((evt.getName().equalsIgnoreCase("onOK") || evt.getName().equalsIgnoreCase("onClick")) && evt.getTarget().getId().equalsIgnoreCase("addrule"))
			{
				Executions.sendRedirect("/addshowrule.zul");
			}
		}
		
	};
}
