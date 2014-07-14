package dvmrp;

class CLans
{
	int LanId,Marker;
	boolean LeafLan;
	public CLans(boolean LeafLan,int LanId,int Marker)
	{
		this.LeafLan=LeafLan;
		this.LanId=LanId;
		this.Marker=Marker;
	}
}
