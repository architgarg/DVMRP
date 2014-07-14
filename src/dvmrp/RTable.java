package dvmrp;

import java.util.ArrayList;

	class RTable
	{
		int dist,NHopLan,NHopRouter,ReceiverTime;
		ArrayList<Descendant> descendant;
		boolean receiver;
		public RTable(int dist,int NHopRouter,int NHopLan,ArrayList<Descendant> descendant,int ReceiverTime,boolean receiver)
		{
			this.dist=dist;
			this.NHopRouter=NHopRouter;
			this.NHopLan=NHopLan;
			this.descendant=descendant;
			this.ReceiverTime=ReceiverTime;
			this.receiver=receiver;
			
		}
		
	}

