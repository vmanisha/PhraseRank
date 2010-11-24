package RankPhrase;

import java.io.BufferedWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;

public class PhraseList {

	Vector <Double> queryScope;
	Vector <Double> avgICTF;
	Vector <Double> miDoc;
	//Vector <Double> miCorp;
	Vector <Float> scs;
	Vector <POSTag> tags;
	Vector <Float> idf;
	Vector <tfDoc> tfDoc;
	Vector <tfDoc> tfCorp;
	
	Vector <Float> avgDocTf; //avg tf in doc of all the words in the phrase
	Vector <Long> maxDocTf;  //avg tf in doc of all the words in the phrase
	Vector <Float> avgCorpusTf; //avg tf in corpus of all the words in the phrase
	Vector <Long> maxCorpusTf;  //avg tf in corpus of all the words in the phrase
	Vector <Float> avgIDF; //avg idf of all the words in the phrase
	Vector <Float> maxIDF; //avg idf of all the words in the phrase
	
	Vector <String> phrase ;
 
	public PhraseList ()
	{
		queryScope= new Vector <Double>  ();
		avgICTF= new Vector <Double> ();
		miDoc= new Vector <Double> ();
		//miCorp= new Vector ();
		scs= new Vector <Float>();
		tfDoc = new Vector <tfDoc> ();
		tfCorp = new Vector <tfDoc> ();
		idf= new Vector <Float>();
		phrase = new Vector <String>();
		tags= new Vector <POSTag> ();
		
		avgDocTf= new Vector <Float>();
		avgCorpusTf= new Vector <Float>();
		avgIDF= new Vector <Float>();
		maxDocTf= new Vector<Long>();
		maxCorpusTf= new Vector<Long>();
		maxIDF= new Vector<Float>();
		
	}
	
	public void addPhrase(String phrase2, String phraseTag, String type,
			int tf1, float idf1) {
		// TODO Auto-generated method stub
		//System.out.println("phrase "+phrase2);
		phrase.add(phrase2);
		tfDoc tfDoc1= new tfDoc(type,tf1);
		POSTag postag= new POSTag(phraseTag,tf1);
		tags.add(postag);
		tfDoc.add(tfDoc1);
		idf.add(idf1);
		
	}


	public void updatePhrase(String phrase2, String phraseTag, String type,
			int tf) {
		// TODO Auto-generated method stub
		
		int index = phrase.indexOf(phrase2);
		POSTag posTag = tags.get(index);
		posTag.updateTag(phraseTag,tf);
		tfDoc tfd = tfDoc.elementAt(index);
		tfd.setTF(type,tf);
		
	}

	public boolean containsPhrase(String phrase2) {
		// TODO Auto-generated method stub
		
		if(phrase.contains(phrase2))
			return true; 
		
		return false;
		
	}

	public void printPhraseInformation(BufferedWriter bw) {
		// TODO Auto-generated method stub
		Iterator <Double> iscope  = queryScope.iterator();
		Iterator <Double> iictf   = avgICTF.iterator();
		Iterator <Double> imi	  = miDoc.iterator();
		Iterator <Float> iscs	  = scs.iterator();
		Iterator <POSTag> itags	  = tags.iterator();
		Iterator <Float> iidf	  = idf.iterator();
		Iterator <Float> imaxidf  = maxIDF.iterator();
		Iterator <Float> iavgidf  = avgIDF.iterator();
		Iterator <Float> iatfdoc  = avgDocTf.iterator();
		Iterator <Float> iatfcoll = avgCorpusTf.iterator();
		Iterator <Long> imaxtfdoc = maxDocTf.iterator();
		Iterator <Long> imaxtfcol = maxCorpusTf.iterator();
		Iterator <tfDoc> itfdoc	  = tfDoc.iterator();
		Iterator <tfDoc> itfcoll  = tfCorp.iterator();
		
		Iterator <String> iphrase = phrase.iterator();
		
		DecimalFormat df = new DecimalFormat("0.0000");
		StringBuffer torite= new StringBuffer();
		String d =", ";
		tfDoc tfd;
		tfDoc tfc;
		POSTag post;
		try{
			
			while(iphrase.hasNext())
			{
				torite.append("\n"+iphrase.next());
				tfd=itfdoc.next(); //document level tf 
				tfc=itfcoll.next(); //corpus level tf
				torite.append(d+ tfd.tfA +d+ tfd.tfD +d+ tfd.tfC +d+ tfd.totalTF());
				torite.append(d+ tfc.tfA +d+ tfc.tfD +d+ tfc.tfC +d+ tfc.totalTF());
				torite.append(d+ df.format(iatfdoc.next()));
				torite.append(d+ df.format(iatfcoll.next()));
				torite.append(d+ df.format(imaxtfdoc.next()));
				torite.append(d+ df.format(imaxtfcol.next()));
				torite.append(d+ df.format(iidf.next()));
				torite.append(d+ df.format(iavgidf.next()));
				torite.append(d+ df.format(imaxidf.next()));
				post=itags.next();
				torite.append(d+ post.tag.toString() +d+ post.count.toString());
				torite.append(d+ df.format(iscs.next()));
				torite.append(d+ df.format(iscope.next()));
				torite.append(d+ df.format(iictf.next()));
				torite.append(d+ df.format(imi.next()));
				bw.write(torite.toString());
				torite.replace(0, torite.length(), "");
			}
			bw.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
