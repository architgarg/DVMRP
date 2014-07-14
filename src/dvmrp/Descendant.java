package dvmrp;

class Descendant
{
	int LanId,NonMemRepTime;
	boolean MemberStatus ;
	public Descendant(boolean MemberStatus,int NonMemRepTime,int LanId) 
	{
		this.MemberStatus=MemberStatus;
		this.NonMemRepTime=NonMemRepTime;
		this.LanId=LanId;
	}
}