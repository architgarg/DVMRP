package acn_dvmrp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Controller//cntlr
{
	Timer timer;//counter
	ArrayList<Host1> host;//sender H
	ArrayList<Router1> router;// R
	int i;
	String line;//string1
	public Controller(ArrayList<Integer> host, ArrayList<Integer> router,ArrayList<Integer> lan)// H R L
	{
		this.host=new Host1[host.length];
	    for(int i=0;i<host.length;i++)
	    	this.host[i]=new Host1(host[i],0);
	    this.router=new Router1[router.length];
	    for(int i=0;i<router.length;i++)
	    	this.router[i]=new Router1(router[i],0);
	    timer = new Timer();
		timer.schedule(new Operate(),0);// operate - handle
	}
		
	class Operate extends TimerTask
    {
        public void run() 
        {
        	try 
   		 	{
        		
        		
        		
        		for(int i=0;i<router.size();i++)
        		{
        			BufferedReader ReadFile = new BufferedReader(new FileReader("rout"+router.get(i).value));// value - LanId
        			int new1=0;//n1
                    while((line = ReadFile.readLine()) != null)
                    {
                    	++new1;
                        if(new1 > router.get(i).old)// old- marker
                        {
                        	String[] token=line.split(" ");
                            BufferedWriter WriteFile = new BufferedWriter(new FileWriter("lan"+token[1],true));
                            WriteFile.write(line);
                            WriteFile.write("\n");
                            WriteFile.close();                       
                        }
                    }
                    ReadFile.close();
                   router.get(i).old = new1;
        		}
        		
        		for(int i=0;i<host.size();i++)
        		{
        			BufferedReader ReadFile = new BufferedReader(new FileReader("hout"+host.get(i).value));
        			int new1=0;
                    while((line = ReadFile.readLine()) != null)
                    {
                    	++new1;
                        if(new1 > host.get(i).old)
                        {
                        	String[] token=line.split(" ");
                            BufferedWriter WriteFile = new BufferedWriter(new FileWriter("lan"+token[1],true));
                            WriteFile.write(line);
                            WriteFile.write("\n");
                            WriteFile.close();                       
                        }
                    }
                    ReadFile.close();
                   host.get(i).old = new1;
        		}
        		
        		
   		 	} 
   		 	catch (IOException e) {}
        	i++;
            if(i<100)
            	timer.schedule(new Operate(),1000);
            else
            	 timer.cancel();
        }
    }
}

class Host1
{
	int value,old;
	public Host1(int value,int old)
	{
		this.old=old;
		this.value=value;
	}
}

class Router1
{	
	int value,old;
	public Router1(int value,int old)
	{
		this.old=old;
		this.value=value;
	}
}
