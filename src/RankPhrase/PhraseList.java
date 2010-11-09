package RankPhrase;

import java.io.BufferedWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;

public class PhraseList {

	Vector <Double> queryScope;
	Vector <Double> avgICTF;
	Vector <Double> mi;
	Vector <Float> scs;
	Vector <POSTag> tags;
	Vector <Float> idf;
	Vector <tfDoc> tfDoc;
	Vector <String> phrase ;
 
	
	public PhraseList ()
	{
		queryScope= new Vector ();
		avgICTF= new Vector ();
		mi= new Vector ();
		scs= new Vector ();
		tfDoc = new Vector <tfDoc> ();
		idf= new Vector <Float>();
		phrase = new Vector <String>();
		tags= new Vector <POSTag> ();

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
		Iterator <Double> imi	  = mi.iterator();
		Iterator <Float> iscs	  = scs.iterator();
		Iterator <POSTag> itags	  = tags.iterator();
		Iterator <Float> iidf	  = idf.iterator();
		Iterator <tfDoc> itfdoc	  = tfDoc.iterator();
		Iterator <String> iphrase = phrase.iterator();
		DecimalFormat df = new DecimalFormat("0.00000");
		StringBuffer torite= new StringBuffer();
		String d =" : ";
		tfDoc tfd;
		POSTag post;
		try{
			
			while(iphrase.hasNext())
			{
				torite.append("\n"+iphrase.next());
				tfd=itfdoc.next();
				torite.append(d+ tfd.tfA +d+ tfd.tfD +d+ tfd.tfC +d+ tfd.totalTF());
				torite.append(d+ df.format(iidf.next()));
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
