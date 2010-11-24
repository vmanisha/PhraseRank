package RankPhrase;

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

	int phraseCount=0; 
	//doesnt count stopwords;
	int wordCount =0;

	public Document()
	{
		phrases= new PhraseList();
		words= new WordList();
		posDoc= new POSTag();
	}

	public void addWord(String word, String tag, String type, int tf, float idf) {
		// TODO Auto-generated method stub

		words.addWord(word,tag,type,tf,idf);
		wordCount+=tf;
	}

	public void updateWord(String word, String type, String tag, int tf) {
		// TODO Auto-generated method stub
		words.updateWord(word,tag,type,tf);
		wordCount+=tf;
	}

	public void addPhrase(String phrase, String type, String phraseTag, int tf,
			float idf) {
		// TODO Auto-generated method stub
		phrases.addPhrase(phrase, phraseTag,type,tf,idf);
		phraseCount+=tf;
	}

	public void updatePhrase(String phrase, String type, String phraseTag,
			int tf) {
		// TODO Auto-generated method stub
		phrases.updatePhrase(phrase, phraseTag,type,tf);
		phraseCount+=tf;
	}

	public boolean containsWord(String word, String type) {
		// TODO Auto-generated method stub
		if(words.containsWord(word))
			return true;
		return false;
	}

	public boolean containsPhrase(String phrase, String type) {
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

		Vector<String> phraseList= phrases.phrase;
		Iterator <String> i = phraseList.iterator();
		String phrase;
		String split[];
		int index;
		long UWORDS=prv.UNIGRAMS;
		float multiply=1 ;
		double avPhrase;

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
					if(words.tfColl.get(index).totalTF()>0)
						multiply*=(UWORDS/(float)(words.tfColl.get(index).totalTF()));
				}
				else 
					System.out.println("not present "+split[j]);
			}
			avPhrase=Math.log(multiply);
			phrases.avgICTF.add(avPhrase);
			multiply=1;
		}

	}

	/** Calculate Query scope for each phrase  
	 *	@param findFeatures class object
	 */
	public void calculateQueryScope() {
		// TODO Auto-generated method stub
		Vector<String> phraseList= phrases.phrase;
		Iterator <String> i = phraseList.iterator();
		String phrase;
		int totCommonDoc;

		Enumeration<Integer> en ;
		double qScopePhrase=0;

		Integer temp;
		while(i.hasNext())
		{
			phrase=i.next();
			totCommonDoc=findDocList(phrase);
			if(totCommonDoc==0)
			{
				System.out.println("phrase not present "+phrase);
				totCommonDoc=1;
			}
			qScopePhrase= -1*Math.log((float)totCommonDoc/findFeatures.NODOC);
			phrases.queryScope.add(qScopePhrase);

		}	
	}

	public void calculateMIDoc() {
		// TODO Auto-generated method stub
		Vector<String> phraseList= phrases.phrase;
		Iterator <String> i = phraseList.iterator();
		String phrase;
		String split[];
		int index;

		//summation of IDF of each word

		float sumIDFWord=0;
		long tfPhrase=0,tfWord=0;
		//int tfPOSPhrase=0,tfPOSWord=0;
		float wordMultiply =1;
		int ctr =0;
		double PMIPhrase=0;
		//double totalMI=0;
		double probC=0;

		while(i.hasNext())
		{
			phrase=i.next();

			//totalMI=0;
			wordMultiply=1;
			sumIDFWord=0;

			tfPhrase= phrases.tfDoc.get(ctr).totalTF();
			//tfPOSPhrase=posDoc.count.get(posDoc.tag.indexOf(phrases.tags.get(ctr).tag.get(0)));

			split=phrase.split(" ");
			for(int j=0;j<split.length;j++)
			{
				index=words.word.indexOf(split[j]);
				if(index>-1)
				{
					tfWord=words.tfDoc.get(index).totalTF();

					//tfPOSWord=posDoc.count.get(posDoc.tag.indexOf(words.tags.get(index).tag.get(0)));
					wordMultiply*=((float)tfWord/wordCount);///tfPOSWord;
					sumIDFWord+=words.idf.get(index);
				}
				else 
					System.out.println("Not present "+split[j]);
			}
			probC=((float)tfPhrase/phraseCount);
			PMIPhrase=Math.log(probC/wordMultiply);
			//totalMI+= (probC)*(sumIDFWord + Math.log(probC)+PMIPhrase);
			//System.out.println("probc "+probC + " word "+wordMultiply+" PMIPhrase "+PMIPhrase+" sum "+sumIDFWord);
			phrases.miDoc.add(PMIPhrase);
			ctr++;
		}

	}

	public void calculateSCS() {
		// TODO Auto-generated method stub
		Vector<String> phraseList= phrases.phrase;
		Iterator <String> i = phraseList.iterator();
		String phrase;
		String split[];
		//int ctr=0;

		float scsPhrase=0;
		int qLen=0;
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
					freqdoc=words.tfDoc.get(words.word.indexOf(split[j])).totalTF();
					scsPhrase+=(tfinP/qLen)*(float)Math.log((tfinP/qLen)*(findFeatures.UNIGRAMS/freqdoc));
					//System.out.println("word "+split[j] +" tf "+tfinP +" freqDoc " +freqdoc);
					//System.out.println("1. "+ (tfinP/qLen) + " 2. "+Math.log((tfinP/qLen)*(findFeatures.UNIGRAMS/freqdoc)) +" 3. "+(tfinP/qLen)*(findFeatures.UNIGRAMS/freqdoc));
				}
				else 
					System.out.println("word not present "+split[j]);
				//System.out.println("split j "+split[j]+" tf is "+tfinP +"freq doc "+freqdoc +" scs "+ scsPhrase) ;
			}
			phrases.scs.add(scsPhrase);
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

	}

	public void calculateTfCorpus() {
		// TODO Auto-generated method stub
	
		Vector<String> phraseList= phrases.phrase;
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
					System.out.println("Not present "+split[j]);
			}
			
			//abst count
			tfaPhrase=getCorpCount(abstList);
			//desc count 
			tfdPhrase=getCorpCount(descList);
			//claim count
			tfcPhrase=getCorpCount(claimList);
			tfcorp.setTF(tfaPhrase, tfdPhrase, tfcPhrase);
			phrases.tfCorp.add(tfcorp);
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
		Vector<String> phraseList= phrases.phrase;
		Iterator <String> i = phraseList.iterator();
		String phrase;
		String split[];
		int index=0;
		
		float avgtfdoc=0;
		float avgtfcoll=0;
		float avgidf=0;
		
		long maxtfdoc=0;
		long maxtfcoll=0;
		float maxidf=0;
		
		//to store temp values 
		float ival=0;
		long value;
		
		while(i.hasNext())
		{
			phrase=i.next();
		
			avgtfdoc=0;
			avgtfcoll=0;
			avgidf=0;
			maxtfdoc=0;
			maxtfcoll=0;
			maxidf=0;
			//ctr=0;
			value=0;
			ival=0;
			
			split=phrase.split(" ");	
			for(int j=0;j<split.length;j++)
			{
				index=words.word.indexOf(split[j]);
				if(index>-1)
				{
					value=words.tfDoc.get(index).totalTF();
					avgtfdoc+=value;
					if(maxtfdoc < value)
					maxtfdoc=value;
					
					value=words.tfColl.get(index).totalTF();
					avgtfcoll+=value;
					if(maxtfcoll < value)
					maxtfcoll=value;
					
					ival=words.idf.get(index);
					avgidf+=ival;
					if(maxidf < ival)
					maxidf=ival;
				}
				else
				System.out.println("Not present "+split[j]);
			}	
			avgtfdoc/=split.length;
			avgtfcoll/=split.length;
			avgidf/=split.length;
			
			phrases.avgDocTf.add(avgtfdoc);
			phrases.avgCorpusTf.add(avgtfcoll);
			phrases.avgIDF.add(avgidf);
			
			phrases.maxDocTf.add(maxtfdoc);
			phrases.maxCorpusTf.add(maxtfcoll);
			phrases.maxIDF.add(maxidf);
		}
		
	}
	
}