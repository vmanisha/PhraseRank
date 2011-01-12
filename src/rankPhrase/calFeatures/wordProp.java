package rankPhrase.calFeatures;

import java.util.Vector;



public class wordProp {
	
	tfDoc wtfDoc;
	POSTag tags;
	float aidf;
	float didf;
	float cidf;
	tfDoc tfColl;
	int docFreq;
	 
	public wordProp()
	{
		aidf=didf=cidf=0.0f;
		wtfDoc=null;
		tags=null;
		tfColl=null;
		docFreq=0;
	}
	
}
