package rankPhrase.testTrain;

import java.io.BufferedReader;
import java.util.ArrayList;



/**@author mansi
 * The class reads each patents phrase list ex -- desc 34 "phrase"
 * and loads the decresing order Map arraged phrase list 
 * and forms a Lucene Query. 
 *
 */

public class prepareTopPhraseQuery {


	
	ArrayList <String> finalPhraseArray ; 
	ArrayList <String> phraseArray=new ArrayList <String> ();
	/**
	 * 
	 * @param patRead -- Reader for each patent file which has --( type #no "phrase" ) arrangement
	 * @param decMapRead -- Reader for corresponding sorted Map phrase no 
	 */

	public void loadPhrases(BufferedReader patRead,BufferedReader decMapRead)
	{
		String line ;
		String split [];
		finalPhraseArray = new ArrayList <String> (); 
		try{
			while((line=patRead.readLine())!=null)
			{
				split= line.split("\t");

				if(line.length()>1)
				{
						if(split[0].equals("abstract"))
							split[0]="abst";
						phraseArray.add(split[0]+":"+split[3]);
				}

			}
			int phraseNo;
			String phrase;
			while((line=decMapRead.readLine())!=null)
			{
				split= line.split("\\s+");
				//System.out.println("Phrase no  "+split[1]);
				try {
					if(split.length==3)
					phraseNo=Integer.parseInt(split[1]);
					else 
					phraseNo=Integer.parseInt(split[0]);	
					phrase=phraseArray.get(phraseNo);
					if(!finalPhraseArray.contains(phrase))
					finalPhraseArray.add(phrase);	
					
				}
				catch (Exception e) {
					// TODO: handle exception
				}
			}
			//decMapRead.close();

			phraseArray.clear();

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	
	public String combinePhrases(int k)
	{
		//Iterator<String> i = phraseList.iterator();
		int ctr=2;
		StringBuffer query= new StringBuffer();
		//StringBuffer abquery= new StringBuffer();
		//StringBuffer dequery= new StringBuffer();
		//StringBuffer clquery= new StringBuffer();
		//String temp="" ;
		//if(i.hasNext())
		//{
		//	temp=i.next();
		if(finalPhraseArray.size()>0)	
		query.append(finalPhraseArray.get(0));
		
		//}

		for(int i=2;i<finalPhraseArray.size() && ctr <=k;i++)
		{
			//temp=i.next();
			//System.out.println("temp "+temp);
			if(finalPhraseArray.get(i)!=null)
			query.append(" OR "+finalPhraseArray.get(i));
			ctr++;
		}

		/*
		String temp2;
		while(i.hasNext() && ctr<=k)
		{
			temp=i.next();
			temp2=temp.substring(temp.indexOf(":")+1);
			if(temp.startsWith("abst"))
			{
				if(abquery.length()>0)
				abquery.append(" OR "+temp2);
				else
				abquery.append(temp2);	
			}
			else if(temp.startsWith("desc"))
			{
				if(dequery.length()>0)
					dequery.append(" OR "+temp2);
					else
					dequery.append(temp2);
			}
			if(temp.startsWith("claim"))
			{
				if(clquery.length()>0)
					clquery.append(" OR "+temp2);
					else
					clquery.append(temp2);
			}
			ctr++;
		}
		StringBuffer Query = new StringBuffer();
		if (abquery.length()>3)
			Query.append("abst : "+abquery);
		if( dequery.length()>3)
			if(Query.length()>3)
				Query.append("\tdesc : "+dequery);
			else 		
				Query.append("desc : "+dequery);

		if(clquery.length()>3)
			if(Query.length()>3)
				Query.append("\tclaim : "+clquery);
			else 		
				Query.append("claim : "+clquery);
		 */
		//System.out.println("the returned query is "+query.toString());


		return query.toString();
	}

	public int getPhraseListSize()
	{
		return finalPhraseArray.size();
	}
	/*public void clearAll()
	{
		phraseList.clear();
	}*/
}
