package dvmrp;

public class Senders 
{
	boolean MyStatus;
	int MyStatusTime,LanId;
	public Senders(int LanId,boolean MyStatus,int MyStatusTime)
	{
		this.MyStatus=MyStatus;
		this.LanId=LanId;
		this.MyStatusTime=MyStatusTime;
	}
}
