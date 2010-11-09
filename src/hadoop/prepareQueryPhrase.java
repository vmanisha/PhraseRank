package hadoop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

public class prepareQueryPhrase {

	
	Vector <String> phraseqList; //to send for query
	Vector <String> modPhList; //modified removing a single char
	Vector <String> phraseSVM; //read from svm feature file
	Vector <Float> MI;
	Vector <Float> tfidf;
//	Vector <Float> scs;
	Vector <Float> avictf;
	
 	public void loadPhrases(BufferedReader br)
 	{
 		try {
 			String line, word, type;
 			phraseqList= new Vector <String>();
 			modPhList= new Vector <String>();
 			while((line=br.readLine())!=null)
 			{
 				if(line.length()>1)
 				{
 					word=line.substring(line.lastIndexOf("\t")).trim();
 					if(word==null)
 						modPhList.add(" ");
 					else
 					modPhList.add(modifyPhrase(word));
 					type=line.substring(0,line.indexOf("\t"));
 					if(type.equals("abstract"))
 						type="abst";
 					phraseqList.add(type+":"+word);
 				}
 			}
 			
 		}catch (Exception e) {
			// TODO: handle exception
 			e.printStackTrace();
		}
 	}
	public String modifyPhrase(String phrase)
	{
		phrase=phrase.substring(1,phrase.length()-1);
		String split [] = phrase.split(" ");
		StringBuffer sb = new StringBuffer();
		for (int i =0;i<split.length;i++)
			if(split[i].length()>1)
				sb.append(" "+split[i]);
		return sb.toString().trim();
				
	}
 	public void loadFeatures(BufferedReader br)
 	{
 		try {
 			String line,split[];
 			MI=new Vector <Float>();
 			tfidf = new Vector <Float>();
 			//scs=new Vector <Float>();
 			avictf= new Vector <Float>();
 			phraseSVM= new Vector <String>();
 			
 			float tf,idf,scs1,av,mi;
 			while((line=br.readLine())!=null)
 			{
 				if(line.length()>1)
 				{
 					split=line.split(" : ");
 					tf=Float.parseFloat(split[4]); //tf in document
	 				idf=Float.parseFloat(split[5]);
	 				tfidf.add(tf*idf);
	 				phraseSVM.add(split[0]);
	 				try{
 						//scs1=Float.parseFloat(split[8]);
 	 					av=Float.parseFloat(split[10]);
 	 				
 	 				//	scs.add(scs1);
 	 					avictf.add(av);
 					}
 					catch(NumberFormatException ex)
 					{
 						System.out.println("found infinity "+split[0]);
 						avictf.add((float)0);
 					}
 					try{
 						mi=Float.parseFloat(split[11]);
 						MI.add(mi);
 					}
 					catch (Exception e) {
						// TODO: handle exception
 						System.out.println("found infinity "+split[0]);
 						MI.add((float)0);
					}
 					
 	 				
 				}
 			}
 			
 		}catch (Exception e) {
			// TODO: handle exception
 			e.printStackTrace();
		}
 	}
 	
 	
 	/**
	 * @param args[0] = svm features folder
	 * @param args[1] = folder with phrase list 
	 * @param args[2] = test file
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			BufferedReader br = new BufferedReader (new FileReader(new File(args[2])));
			String line ;
			prepareQueryPhrase pqp = new prepareQueryPhrase();
			
			File out = new File("tf-idf");
			File out1 = new File("mi");
		//	File out2 = new File("scs");
			File out3 = new File("avictf");
			
			if(out.isDirectory())
			{
				System.out.println("Dir exists");
				System.exit(0);
			}
			else
			{
				out.mkdir();
				out1.mkdir();
			//	out2.mkdir();
				out3.mkdir();
			}
			
			BufferedReader br2;
			Vector sortedPhrases;
			while((line=br.readLine())!=null)
			{
				System.out.println("****"+line+"*****");
				//load phrases
				br2=new BufferedReader (new FileReader (new File(args[1]+"/"+line)));
				pqp.loadPhrases(br2);
				br2.close();
				//load feature values
				br2=new BufferedReader (new FileReader (new File(args[0]+"/p"+line)));
				pqp.loadFeatures(br2);
				br2.close();
				
				sortedPhrases=pqp.sortBy(pqp.tfidf);
				writeToFile(out.getAbsolutePath()+"/"+line, line, sortedPhrases);
				
				sortedPhrases=pqp.sortBy(pqp.MI);
				writeToFile(out1.getAbsolutePath()+"/"+line, line, sortedPhrases);

				//sortedPhrases=pqp.sortBy(pqp.scs);
				//writeToFile(out2.getAbsolutePath()+"/"+line, line, sortedPhrases);

				sortedPhrases=pqp.sortBy(pqp.avictf);
				writeToFile(out3.getAbsolutePath()+"/"+line, line, sortedPhrases);

			}
			br.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}
	}

	public Vector sortBy(Vector v)
	{
		Vector v1=new Vector (v);
		Vector v2= new Vector(phraseSVM);
		Vector toReturn = new Vector ();
		String stemp;
		
		//System.out.println("old num vect "+v1.toString());
		//System.out.println("old string vect "+v2.toString());
		
		float ftemp,no1,no2;
		for(int i=0;i<v1.size();i++)
		for(int j=0;j<v1.size()-1;j++)
		{
			no1=(Float)v1.elementAt(j);
			no2=(Float)v1.elementAt(j+1);
			if(no1<no2)
			{
				ftemp=no1;
				stemp=(String)v2.elementAt(j);
				v1.set(j,no2);
				v1.set(j+1, ftemp);
				
				v2.set(j,v2.elementAt(j+1));
				v2.set(j+1, stemp);
			}
		}
		//System.out.println("sorted num vect "+v1.toString());
		//System.out.println("sorted string vect "+v2.toString());
		
		//make a list
		Iterator i = v2.iterator();
		int index;
		String word;
		while(i.hasNext())
		{
			word=(String)i.next();
			index= modPhList.indexOf(word);
			if(index==-1)
			{
				System.out.println("word is "+ word);
				//System.out.println(modPhList.toString());
			}
			else
			toReturn.add(phraseqList.elementAt(index));
		}
		return toReturn;
	}
	
	public static void writeToFile(String file,String qNo,Vector v)
	{
		try{
			BufferedWriter bw = new BufferedWriter (new FileWriter(new File(file),true));
			Iterator i = v.iterator();
			int j=0;
			while(i.hasNext() && j<200)
			{
				bw.write("\n"+qNo+"\t"+j+"\t"+i.next());
				j++;
			}
			bw.close();

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
