package acn_dvmrp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Router
{
	public Router(int rid,ArrayList<Integer> lids) // rid- RouterId , lids - LanIDS
	{
		new Route(rid,lids);
	}
}


class Route
{
	int rid,i;
	ArrayList<Table> table;// RTable , RoutingTable 
	ArrayList<Lans> lans; // Clans connectedLans
	Timer timer;//counter
	ArrayList<Sources> sources; //Senders senders
	String line;//string1
	public Route(int rid,ArrayList<Integer> lids)
	{
		this.rid=rid;
		i=0;
		lans=new Lans[lids.length];
		table=new Table[10];
		for(int i=0;i<10;i++)
		{
			table[i]=new Table(10,10,10,new ArrayList<Childs>(),false,0);
			table.get(i).re=false;// re- Receiver
			table.get(i).re_time=0;// re_time - ReceiverTime
		}	
		for(int i=0;i<lids.length;i++)
		{
			table.get(lids.get(i)).hops=0; // hops=dist
			table.get(lids.get(i)).next_lan=lids.get(i); // NHopLan
			lans.get(i)=new Lans(lids.get(i),0,true);
		}
		
		sources=new ArrayList<Sources>();
		timer = new Timer();
		timer.schedule(new Operate(),0);	
	}
	
	class Operate extends TimerTask
	{
		public void run()
		{
				/*dv message sending*/
				String temp=null;
				if(i%5==0)   
				{
					temp=String.valueOf(rid);
					for(int i=0;i<table.length;i++)
					{
						if(table[i].next_router==10) // NHopRouter
							temp=temp.concat(" "+table[i].hops+" -");
						else
							temp=temp.concat(" "+table[i].hops+" "+table[i].next_router);
					}
					String dv=null; //DistVect
					for(int i=0;i<lans.length;i++)
					{
						try
						{
							dv="DV "+lans[i].value+" "+temp;// DistVect
							BufferedWriter writer= new BufferedWriter(new FileWriter("rout"+rid,true));
							writer.write(dv);
							writer.write("\n");
							writer.close();
						}
						catch(IOException e){System.out.println("execption occurred for DV");}
					}
				}
				/*dv message sending*/
				
				
				
				/*receiver message accepting in regular interval*/
				for(int i=0;i<lans.length;i++)
				{
					if(table[lans[i].value].re==true)
						table[lans[i].value].re_time++;
					if(table[lans[i].value].re_time==20)
					{
						table[lans[i].value].re=false;
						table[lans[i].value].re_time=0;
					}
				}
				/*receiver message accepting at regular interval*/
				
				
				/*nmr message sending at regular interval*/
				for(int i=0;i<sources.size();i++)
				{
					sources.get(i).nmr_total=true;//NonMemRepTime
					for(int j=0;j<table[sources.get(i).value].childs.size();j++) // childs- descendants
					{
						if(table[sources.get(i).value].childs.get(j).nmr_status==false) // nmr_status - MemberStatus
						{
							sources.get(i).nmr_total=false;
						}
						if(table[sources.get(i).value].childs.get(j).nmr_status==true)
						{
							table[sources.get(i).value].childs.get(j).nmr_time++;
							if(table[sources.get(i).value].childs.get(j).nmr_time==20)
							{
								table[sources.get(i).value].childs.get(j).nmr_time=0;
								table[sources.get(i).value].childs.get(j).nmr_status=false;//this will turn nmr off if no nmr received with 10 sec for that child of specific child
							}	
						}	
						else{table[sources.get(i).value].childs.get(j).nmr_time=0;}
							
					}	
					if(sources.get(i).nmr_total==true)
					{
						sources.get(i).nmr_total_time++; // nmr_total_time = MyStatusTime
						if(sources.get(i).nmr_total_time==10)
						{
							try
							{
								BufferedWriter writer= new BufferedWriter(new FileWriter("rout"+rid,true));
								writer.write("NMR "+table[sources.get(i).value].next_lan+" "+rid+" "+sources.get(i).value);
								writer.write("\n");
								writer.close();
								sources.get(i).nmr_total_time=0;
							}
							catch(IOException e){System.out.println("execption occurred for SOURCES NMR");}
							
						}
					}
					else{sources.get(i).nmr_total_time=0;}
				}
				/*nmr message sending at regular interval*/
				
				
				
				
				
				/*accepting messages from other routers*/
				int lan,host_lan,router_id; //HLan, Rid
				for(int i=0;i<lans.length;i++)
				{
					try
					{
					BufferedReader ReadFile = new BufferedReader(new FileReader("lan"+lans[i].value));
	                int new1=0;
	                while((line= ReadFile.readLine()) != null)
	                {
	                	++new1;
	                    if(new1 > lans[i].old)
	                    {
	                    	String[] token=line.split(" ");
	                    	switch(token[0])
	                    	{
	                    	case "data":
	                    		
	                    		lan=Integer.parseInt(token[1]);
	                    		host_lan=Integer.parseInt(token[2]);
	                    		//to check if source is present already
	                    		if(lan==table[host_lan].next_lan)
	                    		{
	                    			boolean contains=false;
	                    			for(int j=0;j<sources.size();j++)
	                    			{
	                    				if(sources.get(j).value==host_lan)
	                    				{
	                    					contains=true;
	                    					break;
	                    				}	
	                    			}
	                    			
	                    			if(sources.isEmpty() || contains==false)
        								sources.add(new Sources(host_lan,true,0));
	                    			
	                    			for(int j=0;j<table[host_lan].childs.size();j++)
	                    			{
	                    				//System.out.println(table[host_lan].childs.get(j).value);
	                    				if(table[host_lan].childs.get(j).nmr_status==false || (table[host_lan].childs.get(j).nmr_status==true && table[table[host_lan].childs.get(j).value].re==true))
	                    				{
	                    					try
	            							{
	                    						BufferedWriter WriteFile = new BufferedWriter(new FileWriter("rout"+rid,true));
	                    	                    WriteFile.write(token[0]+" "+table[host_lan].childs.get(j).value+" "+token[2]);
	                    	                    WriteFile.write("\n");
	                    	                    WriteFile.close();
	                    	                    System.out.println("data forwarded from rid: "+rid+" to lan: "+table[host_lan].childs.get(j).value);
	            							}
	            							catch(IOException e){System.out.println("execption occurred for DATA FORWARDING");}
	                    				}
	                    				
	                    			}
	                    			
	                    		}
	                    		break;
                			
	                    	
	                    	case "NMR":
                				
	                    		lan=Integer.parseInt(token[1]);
	                    		host_lan=Integer.parseInt(token[3]);
	                    		
	                    		for(int j=0;j<table[host_lan].childs.size();j++)
	                    		{
	                    			if(table[host_lan].childs.get(j).value==lan)
	                    			{
	                    				table[host_lan].childs.get(j).nmr_status=true;
	                    				table[host_lan].childs.get(j).nmr_time=0;
	                    				System.out.println("NMR of lan: "+table[host_lan].childs.get(j).value+" for rid: "+rid);
	                    				break;
	                    			}	
	                    		}
	                    		break;
	                    		
	                    		
	                    	case "receiver":
	                    		
	                    		lan=Integer.parseInt(token[1]);
	                    		table[lan].re=true;
	                    		table[lan].re_time=0;
	                    		break;
	                    		
	                    		
	                    	case "DV": 
	                    		
	                    		lan=Integer.parseInt(token[1]);
	                    		router_id=Integer.parseInt(token[2]);
	                    		if(router_id!=rid)
	                    		{
	                    			//leaf bitmap
	                    			for(int k=0;k<lans.length;k++)
	                    			{
	                    				if(lans[k].value==lan)
	                    				{
	                    					lans[k].leaf=false;
	                    					break;
	                    				}
	                    			}
	                    			//leaf bitmap
	                    			
	                    			
	                    			for(int x=3,y=3;x<=21;x+=2,y++) // x=a , y=b
                 				   {
	                    				//distance vector computation
	                    				int dist=Integer.parseInt(token[x]); // dist=dist1
                 						int rou=100;
                 						if(!token[x+1].equals("-"))
                 							rou=Integer.parseInt(token[x+1]);
                 						
                 						//see who gives less distance
                 						if((dist+1)<table[x-y].hops || ((dist+1)==table[x-y].hops && table[x-y].next_router>router_id))
                						{
                							table[x-y].hops=(dist+1);
                							table[x-y].next_lan=Integer.parseInt(token[1]);
                							table[x-y].next_router=Integer.parseInt(token[2]);        							
                						}
                 						//dv message computation
                 						
                 						
                 						//if i am the next hop router add child "if not present already"!!
                 						if(rou==rid && table[x-y].next_lan!=lan )
                						{
            								boolean isChildPresent=false;
                 							for(int j=0;j<table[x-y].childs.size();j++)
            								{
            									if(table[x-y].childs.get(j).value==lan)
            									{
            										isChildPresent=true;
            										break;
            									}	
            								}
                 							if(table[x-y].childs.isEmpty() ||isChildPresent==false)
                								table[x-y].childs.add(new Childs(lan,false,0));                							
                						}
                 						
                 						//fight between two router for a receiver as a child
                 						if(lan!=table[x-y].next_lan && table[lan].re==true)
                						{
                							if(dist>table[x-y].hops ||(dist==table[x-y].hops && router_id>rid))
                							{
                								boolean isChildPresent1=false;
                     							for(int j=0;j<table[x-y].childs.size();j++)
                								{
                									if(table[x-y].childs.get(j).value==lan)
                									{
                										isChildPresent1=true;
                										break;
                									}	
                								}
                     							if(table[x-y].childs.isEmpty() ||isChildPresent1==false)
                    								table[x-y].childs.add(new Childs(lan,false,0));
                							}
                						}
                 				   }
	                    		}
	                    		break;
	                    	}
	                    	
	                    }
	                    
	                }
					lans[i].old = new1;
		            ReadFile.close(); 
					}
					catch(IOException e){}//System.out.println("file reading exception");}
				}			
				/*accepting messages from other routers*/
				
				/*checking leafs and then adding receivers    */
				for(int i=0;i<lans.length;i++)
				{
					if(lans[i].leaf==true)
					{
						int leaf_lan=lans[i].value;
						if(table[leaf_lan].re==true)
						{
							for(int j=0;j<sources.size();j++)
							{
								boolean isChildPresent1=false;
     							for(int k=0;k<table[sources.get(j).value].childs.size();k++)
								{
									if(table[sources.get(j).value].childs.get(k).value==leaf_lan)
									{
										isChildPresent1=true;
										break;
									}	
								}
     							if(table[sources.get(j).value].childs.isEmpty() ||isChildPresent1==false)
    								table[sources.get(j).value].childs.add(new Childs(leaf_lan,false,0));
     						}
						}	
					}
				}
				
				/*checking leafs    */
				
			i++;
			if(i<100)l
				timer.schedule(new Operate(),1000);
			else 
				timer.cancel();
		}
		
	}

}


class Lans // CLans
{
	int value,old; // Val , marker
	boolean leaf; // LeafLan
	public Lans(int value,int old,boolean leaf)
	{
		this.value=value;
		this.old=old;
		this.leaf=leaf;
	}
}

class Childs // Descendant
{
	int value,nmr_time; // value- val , NonMemRepTime
	boolean nmr_status; // MemberStatus
	public Childs(int value,boolean nmr_status,int nmr_time) 
	{
		this.value=value;
		this.nmr_status=nmr_status;
		this.nmr_time=nmr_time;
	}
}


class Table // RoutingTable
{
	int hops,next_lan,next_router,re_time; // dist , NHopLan , NHopRouter , ReceiverTime
	ArrayList<Childs> childs; // Descendant descendants
	boolean re; // receiver
	public Table(int hops,int next_lan,int next_router,ArrayList<Childs> childs,boolean re,int re_time)
	{
		this.hops=hops;
		this.next_lan=next_lan;
		this.next_router=next_router;
		this.childs=childs;
		this.re=re;
		this.re_time=re_time;
	}
	
}

class Sources
{
	boolean nmr_total; // NonMemRepTime
	int nmr_total_time,value; //MyStatusTime, val
	public Sources(int value,boolean nmr_total,int nmr_total_time) 
	{
		this.nmr_total=nmr_total;
		this.value=value;
		this.nmr_total_time=nmr_total_time;
	}
	

}
//initilize childs with nmr =false;
//dont read if sent by you only...diffreentiate using router_id
//add one more field to router ...receiver parent


