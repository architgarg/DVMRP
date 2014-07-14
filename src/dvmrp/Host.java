package acn_dvmrp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Host 
{
	void Sender(int hid,int lid,int tts,int gap) // HostId , LanId , TimeToStart , period
	{
		new Sender("hout"+hid, "data "+lid+" "+lid,tts,gap);
	}
	void Receiver(int hid,int lid)
	{
		new Receiver("hout"+hid,"hin"+hid,"lan"+lid,"receiver "+lid);
	}
}



class Sender
{
	Timer timer;// counter
	String outfile,data;//OutputFile
	int tts,gap;
	int i;
	public Sender(String outfile,String data,int tts,int gap) //OutputFile
	{
		this.outfile=outfile;
		this.data=data;
		this.tts=tts;
		this.gap=gap;
		i=tts;
		timer=new Timer();
		timer.schedule(new Operate(), tts*1000);
	}

	class Operate extends TimerTask
	{
		public void run()
		{
			try 
   		 	{
        		BufferedWriter writer= new BufferedWriter(new FileWriter(outfile,true));
        		writer.write(data);
        		writer.write("\n");
        		writer.close();
   		 	} 
			catch(IOException e){}
			i+=gap;
			if(i<=100)
				timer.schedule(new Operate(),gap*1000);
			else
				timer.cancel();
		}	
	}
}


class Receiver
{
	Timer timer;
	String outfile,infile,readfrom,advertise,line;// InputFile , FileRead1 , adv ,string1
	int i,old; //marker
	public Receiver(String outfile,String infile,String readfrom,String advertise)
	{
		this.outfile=outfile;
		this.infile=infile;
		this.readfrom=readfrom;
		this.advertise=advertise;
		timer=new Timer();
		i=0;old=0;
		timer.schedule(new Operate(),0);
	}
	
	class Operate extends TimerTask
	{
		public void run() 
		{
			try 
   		 	{
				if(i%10==0)
				{
					BufferedWriter writer= new BufferedWriter(new FileWriter(outfile,true));
	        		writer.write(advertise);
	        		writer.write("\n");
	        		writer.close();
				}
				BufferedReader ReadFile = new BufferedReader(new FileReader(readfrom));
                int new1=0; //n1
                while((line= ReadFile.readLine()) != null)
                {
                	++new1;
                	String[] token=line.split(" "); //menu 
                    if(new1 > old && token[0].equals("data"))
                    {
                        BufferedWriter WriteFile = new BufferedWriter(new FileWriter(infile,true));
                        WriteFile.write(line);
                        WriteFile.write("\n");
                        WriteFile.close();                       
                    }
                }
               old = new1;
               ReadFile.close(); 
   		 	} 
			catch(IOException e){}
			i++;
			if(i<=100)
				timer.schedule(new Operate(),1000);
			else 
				timer.cancel();
		}
		
	}
	
}
