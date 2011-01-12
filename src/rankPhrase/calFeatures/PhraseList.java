package rankPhrase.calFeatures;

import java.io.BufferedWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;


public class PhraseList {

	Vector <phraseProp> phrase_prop;
	Vector <String> lphrase;
	
	public PhraseList ()
	{
		phrase_prop= new Vector <phraseProp>();
		lphrase= new Vector <String>();
	}
	
	/*public void addPhrase(String phrase2, String phraseTag, String type,
			int tf1, float aidf1, float didf1, float cidf1) {
		// TODO Auto-generated method stub
		//System.out.println("phrase "+phrase2);
		phrase.add(phrase2);
		tfDoc tfDoc1= new tfDoc(type,tf1);
		POSTag postag= new POSTag(phraseTag,tf1);
		tags.add(postag);
		tfDoc.add(tfDoc1);
		aidf.add(aidf1);
		didf.add(didf1);
		cidf.add(cidf1);
		avidf.add((aidf1+didf1+cidf1)/3);
		
	}*/

	public void addPhrase(String phrase2, String phraseTag, String type) {
		// TODO Auto-generated method stub
		lphrase.add(phrase2);	
		phraseProp p = new phraseProp(phraseTag,type,1);
		phrase_prop.add(p);
	}
	
	public void removePhrase(String phrase2) {
		// TODO Auto-generated method stub
		try {
			int index = lphrase.indexOf(phrase2);
			//System.out.println("Removing "+lphrase.elementAt(index));
			phrase_prop.remove(index);			
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("couldnt remove "+phrase2);
		}
		
		
	}
	public void updatePhrase(String phrase2, String phraseTag, String type) {
		// TODO Auto-generated method stub
		
		int index = lphrase.indexOf(phrase2);
		if(index>-1 || index > phrase_prop.size())
		{
			phraseProp pp =phrase_prop.elementAt(index);
			pp.updatePhraseProp(phraseTag, type, 1);
		}
		else 
		System.out.println("did not find phrase "+phrase2);
		
	}
	
	public void updatePhrase(String phrase, float aidf1, float didf1, float cidf1) {
		// TODO Auto-generated method stub
		
		phraseProp pp =phrase_prop.elementAt(lphrase.indexOf(phrase));
		pp.aidf=aidf1;
		pp.didf=didf1;
		pp.cidf=cidf1;
		pp.avgidf=((aidf1+didf1+cidf1)/3);
		
	}

	/*public boolean containsPhrase(String phrase,String type) {
		// TODO Auto-generated method stub
		if(lphrase.contains(phrase) && phrase_prop.get(lphrase.indexOf(phrase)).tfDoc.nonZeroTF(type))
			return true; 
		
		return false;
	}*/

	public boolean containsPhrase(String phrase) {
		// TODO Auto-generated method stub
		if(lphrase.contains(phrase))
			return true; 
		
		return false;
	}
	public void printPhraseInformation(BufferedWriter bw) {
		// TODO Auto-generated method stub
		
		/*Iterator <Double> iscope  = queryScope.iterator();
		Iterator <Double> iictf   = avgICTF.iterator();
		Iterator <Double> imi	  = miDoc.iterator();
		Iterator <Float> iscs	  = scs.iterator();
		Iterator <POSTag> itags	  = tags.iterator();
		Iterator <Float> iaidf	  = aidf.iterator();
		Iterator <Float> ididf	  = didf.iterator();
		Iterator <Float> icidf	  = cidf.iterator();
		Iterator <Float> iavidf	  = avidf.iterator();
		Iterator <Float> imaxidf  = maxIDF.iterator();
		Iterator <Float> iavgidf  = avgIDF.iterator();
		Iterator <Float> iatfdoc  = avgDocTf.iterator();
		Iterator <Float> iatfcoll = avgCorpusTf.iterator();
		Iterator <Long> imaxtfdoc = maxDocTf.iterator();
		Iterator <Long> imaxtfcol = maxCorpusTf.iterator();
		Iterator <tfDoc> itfdoc	  = tfDoc.iterator();
		Iterator <tfDoc> itfcoll  = tfCorp.iterator();
		*/
		Iterator <String> iphrase = lphrase.iterator();
		Iterator <phraseProp> iphrase_prop = phrase_prop.iterator();
		phraseProp pp;
		DecimalFormat df = new DecimalFormat("0.0000");
		StringBuffer torite= new StringBuffer();
		String d =":";
		tfDoc tfd;
		tfDoc tfc;
		POSTag post;
		try{
			
			while(iphrase.hasNext())
			{
				torite.append("\n"+iphrase.next());
				pp=iphrase_prop.next();
				tfd=pp.tfDoc; //document level tf 
				tfc=pp.tfCorp; //corpus level tf
				torite.append(d+ tfd.tfA +d+ tfd.tfD +d+ tfd.tfC +d+ tfd.totalTF());
				torite.append(d+ tfc.tfA +d+ tfc.tfD +d+ tfc.tfC +d+ tfc.totalTF());
				torite.append(d+ df.format(pp.avgDocTf));
				torite.append(d+ df.format(pp.avgCorpusTf));
				torite.append(d+ df.format(pp.maxDocTf));
				torite.append(d+ df.format(pp.maxCorpusTf));
				
				torite.append(d+ df.format(pp.aidf));
				torite.append(d+ df.format(pp.didf));
				torite.append(d+ df.format(pp.cidf));
				torite.append(d+ df.format(pp.avgidf));
				torite.append(d+ df.format(pp.avgIDF));
				torite.append(d+ df.format(pp.maxIDF));
				post=pp.tags;
				torite.append(d+ post.tag.toString() +d+ post.count.toString());
				torite.append(d+ df.format(pp.scs));
				torite.append(d+ df.format(pp.queryScope));
				torite.append(d+ df.format(pp.avgICTF));
				torite.append(d+ df.format(pp.miDoc));
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
