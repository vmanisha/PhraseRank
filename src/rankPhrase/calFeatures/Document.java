package rankPhrase.calFeatures;

import java.io.BufferedWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.lucene.analysis.SimpleAnalyzer;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.queryParser.MultiFieldQueryParser;

import org.apache.lucene.search.BooleanQuery;

import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Version;


public class Document {

	PhraseList phrases;
	WordList words;

	POSTag posDoc ; 

	float phraseCount=0; 
	//doesnt count stopwords;
	float wordCount =0;

	public Document()
	{
		phrases= new PhraseList();
		words= new WordList();
		posDoc= new POSTag();
	}

	public void addWord(String word, String tag, String type, float aidf, float didf, float cidf) {
		// TODO Auto-generated method stub

		words.addWord(word,tag,type,aidf,didf,cidf);
		wordCount++;
	}

	public void updateWord(String word, String type, String tag, int tf) {
		// TODO Auto-generated method stub
		words.updateWord(word,tag,type,tf);
		wordCount+=tf;
	}

	/*public void addPhrase(String phrase, String type, String phraseTag, int tf,
			float aidf,float didf, float cidf) {
		// TODO Auto-generated method stub
		phrases.addPhrase(phrase, phraseTag,type,tf,aidf,didf,cidf);
		phraseCount+=tf;
	}*/

	public void addPhrase(String phrase, String type, String phraseTag)//,
			//float aidf,float didf, float cidf) {
	{
		// TODO Auto-generated method stub
		phrases.addPhrase(phrase, phraseTag,type);//,aidf,didf,cidf);
		phraseCount+=1;
	}
	
	public void updatePhrase(String phrase, String type, String phraseTag
			) {
		// TODO Auto-generated method stub
		phrases.updatePhrase(phrase, phraseTag,type);
		phraseCount+=1;
	}

	/*public boolean containsWord(String word, String type) {
		// TODO Auto-generated method stub
		if(words.containsWord(word,type))
			return true;
		return false;
	}*/

	public boolean containsWord(String word) {
		// TODO Auto-generated method stub
		if(words.containsWord(word))
			return true;
		return false;
	}
	/*public boolean containsPhrase(String phrase, String type) {
		// TODO Auto-generated method stub
		//System.out.println("phrase " +phrase +" type "+type);
		
		if(phrases.containsPhrase(phrase,type))
			return true;

		return false;
	}*/
	public boolean containsPhrase(String phrase) {
		// TODO Auto-generated method stub
		//System.out.println("phrase " +phrase +" type "+type);
		
		if(phrases.containsPhrase(phrase))
			return true;

		return false;
	}

	/**
	 * For each word in the Document calculate the tf in the corpus
	 */
	public void calculateCorpusTF(IndexReader ir) {
		// TODO Auto-generated method stub

		words.calculateCorpusTF(ir);

	}

	/**
	 * For each word in the Document calculate the Df (no of documents that contains word) in the corpus
	 */
	public void calculateCorpusDF(IndexReader ir) {
		// TODO Auto-generated method stub
		words.calculateCorpusDF(ir);
	}

	public void calcutateAvICTF(findFeatures prv) {
		// TODO Auto-generated method stub

		Vector<String> phraseList= phrases.lphrase;
		Iterator <String> i = phraseList.iterator();
		int pno=0;
		String phrase;
		String split[];
		int index;
		long UWORDS=prv.UNIGRAMS;
		double multiply=0 ;
		double avPhrase;
		long tfcoll;

		while(i.hasNext())
		{
			phrase=i.next();
			split=phrase.split(" ");
			//System.out.println("the phrase "+phrase);
			for(int j=0;j<split.length;j++)
			{
				//each word in the phrase
				index=words.word.indexOf(split[j]);
				if(index!=-1)
				{
					tfcoll=words.word_prop.get(index).tfColl.totalTF();
					if(tfcoll>0)
						multiply+=Math.log(UWORDS/(double)tfcoll);
					else 
						System.out.println("avgICTF. tfColl=0 "+split[j]);
				}
				else 
					System.out.println("avgICTF. not der "+split[j]);
			}
			avPhrase=multiply/split.length;
			if (avPhrase==Double.POSITIVE_INFINITY)
				System.out.println("phrase "+phrase+ " multiply "+multiply);
			
			phrases.phrase_prop.elementAt(pno).avgICTF=avPhrase;
			multiply=0;
			pno++;
		}

	}

	/** Calculate Query scope for each phrase  
	 *	@param findFeatures class object
	 */
	public void calculateQueryScope() {
		// TODO Auto-generated method stub
		Vector<String> phraseList= phrases.lphrase;
		Iterator <String> i = phraseList.iterator();
		String phrase;
		int totCommonDoc;
		int pno=0;
		//Enumeration<Integer> en ;
		double qScopePhrase=0;

		//Integer temp;
		while(i.hasNext())
		{
			phrase=i.next();
			totCommonDoc=findDocList(phrase);
			if(totCommonDoc==0)
			{
				System.out.println("QScope. not der "+phrase);
				totCommonDoc=1;
			}
			qScopePhrase= -1*Math.log(((float)totCommonDoc)/findFeatures.NODOC);
			phrases.phrase_prop.elementAt(pno).queryScope=(qScopePhrase);
			pno++;
		}	
	}

	public void calculateMIDoc() {
		// TODO Auto-generated method stub
		Vector<String> phraseList= phrases.lphrase;
		Iterator <String> i = phraseList.iterator();
		String phrase;
		String split[];
		int index;
		int pno=0;
		//summation of IDF of each word

		//float sumIDFWord=0;
		long tfPhrase=0,tfWord=0;
		//int tfPOSPhrase=0,tfPOSWord=0;
		double wordMultiply =0;
		double PMIPhrase=0;
		//double totalMI=0;
		double probC=0;
		phraseProp pp;
		while(i.hasNext())
		{
			phrase=i.next();
			wordMultiply=0;
			tfWord=0;
			pp=phrases.phrase_prop.elementAt(pno);
			tfPhrase= pp.tfDoc.totalTF();
			
			split=phrase.split(" ");
			for(int j=0;j<split.length;j++)
			{
				index=words.word.indexOf(split[j]);
				if(index>-1)
				{
					tfWord=words.word_prop.get(index).wtfDoc.totalTF();
					if(tfWord>0)
						wordMultiply+=Math.log((double)tfWord/wordCount);///tfPOSWord;
					else 
						System.out.println("MI. tf =0"+split[j]);
				}
				else 
					System.out.println("MI. not der "+split[j]);
			}
			probC=((double)tfPhrase)/phraseCount;
			PMIPhrase=(Math.log(probC)-wordMultiply)/split.length;
			if (PMIPhrase==Double.POSITIVE_INFINITY)
				System.out.println("phrase "+phrase+"probc "+probC + " word "+wordMultiply+" PMIPhrase "+PMIPhrase +" phrasecount "+phraseCount);
			pp.miDoc=PMIPhrase;
			pno++;
		}

	}

	public void calculateSCS() {
		// TODO Auto-generated method stub
		Vector<String> phraseList= phrases.lphrase;
		Iterator <String> i = phraseList.iterator();
		String phrase;
		String split[];
		//int ctr=0;

		float scsPhrase=0;
		int qLen=0;
		int pno=0;
		float tfinP=0;
		long freqdoc=0;
		Pattern p;
		Matcher m;
		while(i.hasNext())
		{
			phrase=i.next();
			scsPhrase=0;
			split=phrase.split(" ");
			qLen=split.length;
			//System.out.println("\nSCS Phrase "+phrase +" length "+qLen);
			for(int j=0;j<split.length;j++)
			{	
				if(words.word.indexOf(split[j])>-1)
				{
					tfinP=0;
					p=Pattern.compile(split[j]);
					m=p.matcher(phrase);
					while(m.find())
						tfinP++;
					freqdoc=words.word_prop.get(words.word.indexOf(split[j])).wtfDoc.totalTF();
					scsPhrase+=(tfinP/qLen)*(float)Math.log((tfinP/qLen)*(findFeatures.UNIGRAMS/freqdoc));
					//System.out.println("word "+split[j] +" tf "+tfinP +" freqDoc " +freqdoc);
					//System.out.println("1. "+ (tfinP/qLen) + " 2. "+Math.log((tfinP/qLen)*(findFeatures.UNIGRAMS/freqdoc)) +" 3. "+(tfinP/qLen)*(findFeatures.UNIGRAMS/freqdoc));
				}
				else 
					System.out.println("scs. not der "+split[j]);
				//System.out.println("split j "+split[j]+" tf is "+tfinP +"freq doc "+freqdoc +" scs "+ scsPhrase) ;
			}
			phrases.phrase_prop.elementAt(pno).scs=scsPhrase;
			pno++;
		}

	}

	public void addPOSTag(String phraseTag,int count) {
		// TODO Auto-generated method stub
		int index = posDoc.tag.indexOf(phraseTag);
		if(index!=-1)
			posDoc.count.set(index, posDoc.count.get(index)+count);
		else 
		{
			posDoc.tag.add(phraseTag);
			posDoc.count.add(count);
		}
	}

	public void printAll(BufferedWriter bw, BufferedWriter wbw) {
		// TODO Auto-generated method stub
		try {
			phrases.printPhraseInformation(bw);

			words.printWordInformation(wbw);

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public int findDocList(String phrase)
	{
		String split[]=phrase.split(" ");
		Term ta = null;
		Term td = null;
		Term tc = null;
		TermDocs tds;
		int numTotalHits =0;
		Vector <Integer> temp= new Vector <Integer>(); 
		try{
			BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
			SimpleAnalyzer sa = new SimpleAnalyzer();
			StringBuffer query = new StringBuffer();
			int field=3;
			String fieldName [] = {"abst","desc","claim"};
			query.append(fieldName[0]+":"+split[0]);
			for(int i=0;i<3;i++)
				for(int j=0;j<split.length;j++)
				{
					if(i==0 &&j==0)
						continue;
					else 
						query.append(" OR "+fieldName[i]+":"+split[j]);
				}


			MultiFieldQueryParser qp = new MultiFieldQueryParser(Version.LUCENE_CURRENT,fieldName, sa);
			//System.out.println("original text "+phrase+" query "+qp.parse(query.toString()).toString());
			TopScoreDocCollector collector = TopScoreDocCollector.create(0, false);

			if(query!=null && findFeatures.searcher!=null)
			{
				findFeatures.searcher.search(qp.parse(query.toString()), collector);

				numTotalHits = collector.getTotalHits();
			}	


			//System.out.println("Query becomes "+query.toString());
			/*for(int j=0;j<split.length;j++)
			{
				ta= new Term("abst",split[j]);
				td= new Term("desc",split[j]);
				tc= new Term("claim",split[j]);
				tds=reader.termDocs(ta);
				if(tds!=null)
				{
					while(tds.next())
					 {
						if(!temp.contains(tds.doc()))
						temp.add(tds.doc());
					 }
				}

				tds=reader.termDocs(tc);
				if(tds!=null)
				{
					while(tds.next())
						temp.add(tds.doc());
				}
				tds=reader.termDocs(td);
				if(tds!=null)
				{
					while(tds.next())
						temp.add(tds.doc());
				}
				if(temp.size()==0)
				{
					System.out.println("Text has zero common document "+split[j]);
				}


			}*/		
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return numTotalHits;
	}

	public void calculateIPCOverlap() {
		// TODO Auto-generated method stub
		//Vector<String> phraseList= phrases.phrase;
		//Iterator <String> i = phraseList.iterator();
		

	}

	public void calculateTfCorpus() {
		// TODO Auto-generated method stub
	
		Vector<String> phraseList= phrases.lphrase;
		Iterator <String> i = phraseList.iterator();
		String phrase;
		String split[];
		int index=0;
	
		long tfaPhrase=0; //corpus count of phrase in abstract 
		long tfdPhrase=0; //corpus count of phrase in description
		long tfcPhrase=0; //corpus count of phrase in claim
	
		Vector <SpanTermQuery> abstList = new Vector <SpanTermQuery> (); 
		Vector <SpanTermQuery> descList = new Vector <SpanTermQuery> ();
		Vector <SpanTermQuery> claimList = new Vector <SpanTermQuery> ();
		
		tfDoc tfcorp;
		int pno=0;
		while(i.hasNext())
		{
			phrase=i.next();
			split=phrase.split(" ");
			//find the tf of the phrase in the corpus 
			tfaPhrase=0;
			tfdPhrase=0;
			tfcPhrase=0;
			abstList.clear();
			descList.clear();
			claimList.clear();
			tfcorp= new tfDoc();
			for(int j=0;j<split.length;j++)
			{
				index=words.word.indexOf(split[j]);
				if(index>-1)
				{
					abstList.add(new SpanTermQuery(new Term("abst", split[j])));
					descList.add(new SpanTermQuery(new Term("desc", split[j])));
					claimList.add(new SpanTermQuery(new Term("claim", split[j])));
				}
				else 
					System.out.println("not in word_list "+split[j]);
			}
			
			//abst count
			tfaPhrase=getCorpCount(abstList);
			//desc count 
			tfdPhrase=getCorpCount(descList);
			//claim count
			tfcPhrase=getCorpCount(claimList);
			tfcorp.addTF(tfaPhrase, tfdPhrase, tfcPhrase);
			phrases.phrase_prop.elementAt(pno).tfCorp=(tfcorp);
			pno++;
		}
	}
	
	public long getCorpCount(Vector <SpanTermQuery> clauses)
	{
		long count=0;
		try {
			SpanQuery [] clause= new SpanQuery[clauses.size()]; 
			clause= clauses.toArray(clause);
			SpanNearQuery sq = new SpanNearQuery(clause,1,true);
			Spans spans=sq.getSpans(findFeatures.reader);
			
			while(spans.next())
			{
				count++;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return count ;
	}

	public void calculateAvgMax() {
		// TODO Auto-generated method stub
		Vector<String> phraseList= phrases.lphrase;
		Iterator <String> i = phraseList.iterator();
		String phrase;
		String split[];
		int index=0;
		
		float avgtfdoc=0;
		float avgtfcoll=0;
		float avgidf=0;//avgaidf=0,avgdidf=0,avgcidf=0;
		
		long maxtfdoc=0;
		long maxtfcoll=0;
		float maxidf=0;//maxaidf=0,maxdidf=0,maxcidf=0;
		int pno=0;
		//to store temp values 
		float ival=0;//iaval=0,idval=0,icval=0;
		long value;
		
		phraseProp pp;
		wordProp wp;
		
		while(i.hasNext())
		{
			phrase=i.next();
		
			avgtfdoc=0;
			avgtfcoll=0;
			//avgaidf=0;
			//avgdidf=0;
			//avgcidf=0;
			//maxaidf=0;
			//maxdidf=0;
			//maxcidf=0;
			//ctr=0;
			avgidf=0;
			maxtfdoc=0;
			maxtfcoll=0;
			maxidf=0;
			value=0;
			ival=0;
			
			split=phrase.split(" ");	
			for(int j=0;j<split.length;j++)
			{
				index=words.word.indexOf(split[j]);
				if(index>-1)
				{
					wp=words.word_prop.get(index);
					if(wp==null)
						System.out.println("Word property is null "+ split[j]);
						
					value=wp.wtfDoc.totalTF();
					avgtfdoc+=value;
					if(maxtfdoc < value)
					maxtfdoc=value;
					
					value=wp.tfColl.totalTF();
					avgtfcoll+=value;
					if(maxtfcoll < value)
					maxtfcoll=value;
					
					ival=(wp.aidf+wp.didf+wp.cidf)/3;
					avgidf+=ival;
					if(maxidf < ival)
					maxidf=ival;
					
					/*idval=words.didf.get(index);
					avgdidf+=idval;
					if(maxdidf < idval)
					maxdidf=idval;
					
					icval=words.cidf.get(index);
					avgcidf+=icval;
					if(maxcidf < icval)
					maxcidf=icval;
					*/
				}
				else
				System.out.println("max,avg. not der "+split[j]);
			}	
			pp=phrases.phrase_prop.elementAt(pno);
			avgtfdoc/=split.length;
			avgtfcoll/=split.length;
			avgidf/=split.length;
			//System.out.println("avg idf "+avgidf+" max IDF "+maxidf);
					
			pp.avgDocTf=(avgtfdoc);
			pp.avgCorpusTf=(avgtfcoll);
			pp.avgIDF=(avgidf);
			
			pp.maxDocTf=(maxtfdoc);
			pp.maxCorpusTf=(maxtfcoll);
			pp.maxIDF=(maxidf);
			pno++;
		}
		
	}
	
	   
	
}
