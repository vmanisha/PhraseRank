package rankPhrase.calFeatures;

import java.util.Vector;

public class POSTag {

	Vector <String> tag;
	Vector <Integer> count;
	
	public POSTag(String tag2,int count2) {
		// TODO Auto-generated constructor stub
		tag= new Vector <String>();
		count = new Vector <Integer>();
		tag.add(tag2);
		count.add(count2);
	}

	public POSTag() {
		// TODO Auto-generated constructor stub
		tag= new Vector <String>();
		count = new Vector <Integer>();
	}

	public void updateTag(String tag2, int tf) {
		// TODO Auto-generated method stub
		if(tag.contains(tag2))
		{
			int index=tag.indexOf(tag2);
			count.setElementAt(count.get(index)+tf, index);
		}
		else
		{
			tag.add(tag2);
			count.add(tf);
		}
			
		
	}
}
