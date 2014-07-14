package dvmrp;

import java.util.ArrayList;

public class AA {

	public static void main(String[] args)
	{
		Host h=new Host();
		h.Sender(0, 0, 20, 10);
		
		ArrayList<Integer> a=new ArrayList<Integer>();
		a.add(1);
		a.add(2);
		new Router(a,1);
		
		ArrayList<Integer> b=new ArrayList<Integer>();
		b.add(0);
		b.add(1);
		new Router(b,0);
		////
		ArrayList<Integer> hh=new ArrayList<Integer>();
		hh.add(0);
		hh.add(1);
		
		ArrayList<Integer> rr=new ArrayList<Integer>();
		rr.add(0);
		rr.add(1);
		
		ArrayList<Integer> ll=new ArrayList<Integer>();
		ll.add(0);
		ll.add(1);
		ll.add(2);
		
		new Controller(rr,hh,ll);
		
		try {
			Thread.sleep(50*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		h.Receiver(1, 2);
	}

}
