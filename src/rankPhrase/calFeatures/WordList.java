package rankPhrase.calFeatures;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Vector;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;


public class WordList {

	Vector <String> word ;
	Vector  <wordProp>  word_prop;
	
	public WordList()
	{
		word= new Vector<String>();
		word_prop= new Vector<wordProp>();
		/*docFreq= new Vector<Integer>();
		tfColl= new Vector<tfDoc>();
		tfDoc= new Vector<tfDoc>();
		tags= new Vector<POSTag>();
		aidf= new Vector<Float>();
		cidf= new Vector<Float>();
		didf= new Vector<Float>();
		*/
		
		//docNo= new Vector  <Vector <Integer>>();
	}
	
	public void addWord(String word2, String tag, String type, float aidf1, float didf1, float cidf1) {
		// TODO Auto-generated method stub
		
		//System.out.println("word in add "+word2);
		word.add(word2);
		wordProp wp = new wordProp();
		
		wp.tags= new POSTag(tag,1);
		wp.wtfDoc=new tfDoc(type,1);
		
			wp.aidf=aidf1;
			wp.didf=didf1;
			wp.cidf=cidf1;
		/*aidf.add(aidf1);
		cidf.add(cidf1);
		didf.add(didf1);*/
		
		word_prop.add(wp);
	}
	/*public boolean containsWord(String word2,String type) {
		// TODO Auto-generated method stub
		if(word.contains(word2) && word_prop.get(word.indexOf(word2)).wtfDoc.nonZeroTF(type))
			return true; 
		
		return false;
	}*/
	
	public boolean containsWord(String word2) {
		// TODO Auto-generated method stub
		if(word.contains(word2))
			return true; 
		
		return false;
	}
	public void updateWord(String word2, String wordTag, String type, int tf) {
		// TODO Auto-generated method stub
		int index =  word.indexOf(word2);
		
		POSTag posTag = word_prop.get(index).tags;
		posTag.updateTag(wordTag,tf);
		
		tfDoc tfd = word_prop.get(index).wtfDoc;
		tfd.addTF(type,tf);
	}
	
	public void calculateCorpusTF(IndexReader reader) {
		// TODO Auto-generated method stub
		Iterator <String>it = word.iterator();
		
		Iterator <wordProp> iwp  = word_prop.iterator();
		wordProp wp;
		Term ta = null;
		Term td = null;
		Term tc = null;
		TermDocs tds;
		String text;
		tfDoc tfcoll;  
		long freq=0;
		try {
			
			while(it.hasNext())
			{	
				text=it.next();
				wp=iwp.next();
				
				//System.out.println("Word is "+text);
				ta= new Term("abst",text);
				td= new Term("desc",text);
				tc= new Term("claim",text);
				tfcoll = new tfDoc();
				freq=0;
			
				tds=reader.termDocs(ta);
				if(tds!=null)
				{
					while(tds.next())
					 {
						freq+=tds.freq();
					 }
				}
				tfcoll.tfA=freq;
			//	if(text.equals("monitor"))
			//		System.out.println("the monitor "+freq+" "+tfcoll.tfA);
				
				freq=0;
				tds=reader.termDocs(tc);
				if(tds!=null)
				{
					while(tds.next())
					 {
						freq+=tds.freq();
					 }
				}
				tfcoll.tfC=freq;
				
			//	if(text.equals("monitor"))
			//		System.out.println("the monitor "+freq+" "+tfcoll.tfC);
				
				freq=0;
				tds=reader.termDocs(td);
				if(tds!=null)
				{
					while(tds.next())
					 {
						freq+=tds.freq();
					 }
				}
				tfcoll.tfD=freq;
				
			//	if(text.equals("monitor"))
			//	System.out.println("the monitor "+freq+" "+tfcoll.tfD + tfcoll.totalTF());
				
				if(tfcoll.totalTF()==0)
				{
					//System.out.println(" "+tfcoll.tfA+" "+tfcoll.tfD+" "+tfcoll.tfC);
					System.out.println("Text "+text+ " Freq "+freq);
				}
				
				wp.tfColl=tfcoll;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	public void calculateCorpusDF(IndexReader ir) {
		// TODO Auto-generated method stub
		try {
			Iterator <String>it = word.iterator();
			Iterator <wordProp> iwp  = word_prop.iterator();
			wordProp wp;
			String text;
			Term ta = null;
			Term td = null;
			Term tc = null;
			//docFreq= new Vector <Integer> ();
			while(it.hasNext())
			{	
				text = it.next();
				wp=iwp.next();
				ta= new Term("abst",text);
				td= new Term("desc",text);
				tc= new Term("claim",text);
				wp.docFreq=ir.docFreq(ta)+ir.docFreq(tc)+ir.docFreq(td);
				//System.out.println("docfreq  "+text+" "+ir.docFreq(ta)+ir.docFreq(tc)+ir.docFreq(td));
			}	
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void printWordInformation(BufferedWriter bw) {
		// TODO Auto-generated method stub
		/*Iterator <Integer> idocfreq  = docFreq.iterator();
		Iterator <tfDoc> itfcoll  = tfColl.iterator();
		Iterator <POSTag> itags	  = tags.iterator();
		Iterator <Float> iaidf	  = aidf.iterator();
		Iterator <Float> icidf	  = cidf.iterator();
		Iterator <Float> ididf	  = didf.iterator();
		Iterator <tfDoc> itfdoc	  = tfDoc.iterator();
		*/
		
		Iterator <String> iword = word.iterator();
		Iterator <wordProp> iwp  = word_prop.iterator();
		wordProp wp;
	
		DecimalFormat df = new DecimalFormat("0.0000");
		StringBuffer torite= new StringBuffer();
		String d =":";
		tfDoc tfd;
		POSTag post;
		try{
			while(iword.hasNext())
			{
				torite.append("\n"+iword.next());
				wp=iwp.next();
				//doc freq -- abst , desc , claim
				tfd=wp.wtfDoc;
				torite.append(d+ tfd.tfA +d+ tfd.tfD +d+ tfd.tfC +d+ tfd.totalTF());
				//corpus freq -- abst , desc, claim
				tfd=wp.tfColl;
				torite.append(d+ tfd.tfA +d+ tfd.tfD +d+ tfd.tfC +d+ tfd.totalTF());
				//idf of word abstract , description , claim
				torite.append(d+ df.format(wp.aidf));
				torite.append(d+ df.format(wp.didf));
				torite.append(d+ df.format(wp.cidf));
				//pos tags
				post=wp.tags;
				torite.append(d+ post.tag.toString() +d+ post.count.toString());
				//doc frquency
				torite.append(d+ df.format(wp.docFreq));
				
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
