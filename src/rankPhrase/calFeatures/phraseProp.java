package rankPhrase.calFeatures;


public class phraseProp {


	double queryScope;
	double avgICTF=0;
	double miDoc;
	//Double miCorp;
	float scs;
	POSTag tags;

	float aidf;
	float didf;
	float cidf;
	float avgidf;

	tfDoc tfDoc;
	tfDoc tfCorp;

	float avgDocTf; //avg tf in doc of all the words in the phrase
	long maxDocTf;  //max doc tf out of words in a phrase
	float avgCorpusTf; //avg tf in corpus of all the words in the phrase
	long maxCorpusTf;  //max corpus tf out of words in a phrase
	float avgIDF; //avg idf of all the words in the phrase
	float maxIDF; //max idf out of words in a phrase

	public phraseProp(String phraseTag, String type, int tf1) {
		// TODO Auto-generated constructor stub
		tags=new POSTag(phraseTag,tf1);
		tfDoc=new tfDoc(type,tf1);
	}

	public void updatePhraseProp(String phraseTag, String type, int tf) {
		// TODO Auto-generated method stub
		tags.updateTag(phraseTag,tf);
		tfDoc.addTF(type,tf);
	}


}
