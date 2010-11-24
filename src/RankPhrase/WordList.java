package RankPhrase;


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
	Vector <tfDoc> tfDoc;
	Vector <POSTag> tags;
	Vector <Float> idf;
	Vector <tfDoc> tfColl;
	Vector <Integer> docFreq;
	//Vector  <Vector <Integer>>  docNo;
	
	public WordList()
	{
		word= new Vector<String>();
		docFreq= new Vector<Integer>();
		tfColl= new Vector<tfDoc>();
		tfDoc= new Vector<tfDoc>();
		tags= new Vector<POSTag>();
		idf= new Vector<Float>();
		//docNo= new Vector  <Vector <Integer>>();
	}
	
	public void addWord(String word2, String tag, String type, int tf1, float idf1) {
		// TODO Auto-generated method stub
		
		//System.out.println("word in add "+word2);
		word.add(word2);
		tfDoc tfDoc1= new tfDoc(type,tf1);
		POSTag postag= new POSTag(tag,tf1);
		tags.add(postag);
		tfDoc.add(tfDoc1);
		idf.add(idf1);
		
	}
	public boolean containsWord(String word2) {
		// TODO Auto-generated method stub
		if(word.contains(word2))
			return true; 
		
		
		return false;
	}
	public void updateWord(String word2, String wordTag, String type, int tf) {
		// TODO Auto-generated method stub
		int index =  word.indexOf(word2);
		
		POSTag posTag = tags.get(index);
		posTag.updateTag(wordTag,tf);
		
		tfDoc tfd = tfDoc.elementAt(index);
		tfd.setTF(type,tf);
	}
	public void calculateCorpusTF(IndexReader reader) {
		// TODO Auto-generated method stub
		Iterator <String>it = word.iterator();
		tfColl = new Vector <tfDoc> ();
		//docNo  = new Vector <Vector <Integer>> ();
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
					System.out.println(" "+tfcoll.tfA+" "+tfcoll.tfD+" "+tfcoll.tfC);
					System.out.println("Text "+text+ " Freq "+freq+" "+ta.text());
				}
				
				tfColl.add(tfcoll);
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
			
			String text;
			Term ta = null;
			Term td = null;
			Term tc = null;
			docFreq= new Vector <Integer> ();
			while(it.hasNext())
			{	
				text = it.next();
				ta= new Term("abst",text);
				td= new Term("desc",text);
				tc= new Term("claim",text);
				docFreq.add(ir.docFreq(ta)+ir.docFreq(tc)+ir.docFreq(td));
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
		Iterator <Integer> idocfreq  = docFreq.iterator();
		Iterator <tfDoc> itfcoll  = tfColl.iterator();
		Iterator <POSTag> itags	  = tags.iterator();
		Iterator <Float> iidf	  = idf.iterator();
		Iterator <tfDoc> itfdoc	  = tfDoc.iterator();
		Iterator <String> iword = word.iterator();
		DecimalFormat df = new DecimalFormat("0.00000");
		StringBuffer torite= new StringBuffer();
		String d =" : ";
		tfDoc tfd;
		POSTag post;
		try{
			while(iword.hasNext())
			{
				torite.append("\n"+iword.next());
				
				//doc freq -- abst , desc , claim
				tfd=itfdoc.next();
				torite.append(d+ tfd.tfA +d+ tfd.tfD +d+ tfd.tfC +d+ tfd.totalTF());
				//corpus freq -- abst , desc, claim
				tfd=itfcoll.next();
				torite.append(d+ tfd.tfA +d+ tfd.tfD +d+ tfd.tfC +d+ tfd.totalTF());
				//idf of word
				torite.append(d+ df.format(iidf.next()));
				//pos tags
				post=itags.next();
				torite.append(d+ post.tag.toString() +d+ post.count.toString());
				//doc frquency
				torite.append(d+ df.format(idocfreq.next()));
				
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
