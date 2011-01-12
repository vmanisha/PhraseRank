package rankPhrase.calFeatures;

public class tfDoc {

	long tfA=0;
	long tfD=0;
	long tfC=0;
	
	public tfDoc()
	{
		tfA=0;
		tfD=0;
		tfC=0;
	}
	public tfDoc(String type, int tf1) {
		// TODO Auto-generated constructor stub
		
		if(type.indexOf("abst")!=-1)
			tfA=tf1;
		else if (type.indexOf("desc")!=-1)
			tfD=tf1;
		else if(type.indexOf("claim")!=-1)
			tfC=tf1;
	}


	public long totalTF()
	{
		
		return tfA+tfC+tfD;
	}


	public void addTF(String type, int tf1) {
		// TODO Auto-generated method stub
		if(type.indexOf("abst")!=-1)
			tfA+=tf1;
		else if (type.indexOf("desc")!=-1)
			tfD+=tf1;
		else if(type.indexOf("claim")!=-1)
			tfC+=tf1;
	}

	public void addTF(long tfa,long tfd,long tfc) {
		// TODO Auto-generated method stub
			tfA+=tfa;
			tfD+=tfd;
			tfC+=tfc;
	}
	
	public void setTFA(int tfa) {
		// TODO Auto-generated method stub
			tfA+=tfa;
	}
	public void setTFC(int tfc) {
		// TODO Auto-generated method stub
			tfC+=tfc;
	}
	public void setTFD(int tfd) {
		// TODO Auto-generated method stub
			tfD+=tfd;
	}
	public boolean nonZeroTF(String type) {
		// TODO Auto-generated method stub
		
		if(type.startsWith("abst") && tfA>0)
				return true;
		else if(type.startsWith("desc")&& tfD>0)
				return true;
		else if(type.startsWith("claim") && tfC>0)
			return true;
		
		return false;
	}
	

	
}
