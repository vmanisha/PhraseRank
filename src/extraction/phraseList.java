package extraction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/** Class for storing phrases and followin
 * 	TF
 * 	IDF
 *  TF-IDF 
 */
public class phraseList {

	Vector <String> phrase ;
	

	Vector  <Integer> tf ;
	Vector  <Float> idf ;
	Vector <Float> tfIDF;
	

	public phraseList()
	{
		phrase = new Vector ();
	

		tf= new Vector ();
		idf= new Vector ();
		tfIDF = new Vector ();
	
	}

	public void clearPhrases()
	{
		phrase.clear();
		tf.clear();
		idf.clear();
	}
	
	public void add_phrase(String words)
	{
		int index;
		if(phrase.contains(words))
		{
			index=phrase.indexOf(words);
			tf.set(index,(Integer)tf.get(index)+1);
		}
		else
		{
			phrase.add(words);
			tf.add(1);
		}
	}

	

	
	public void add_phrase(String words,float idfval,int countval)
	{
		int index;
		if(phrase.contains(words))
		{
			index=phrase.indexOf(words);
			tf.set(index,(Integer)tf.get(index)+countval);
			idf.set(index,idfval);
		}
		else
		{
			phrase.add(words);
			tf.add(countval);
			idf.add(idfval);
		}
	}


	public phraseList (List l)
	{
		Iterator i = l.iterator();
		String words ;
		int index;
		phrase = new Vector ();
		tf= new Vector ();
		while(i.hasNext())
		{
			words =(String)i.next();

			if(phrase.contains(words))
			{
				index=phrase.indexOf(words);
				tf.set(index,(Integer)tf.get(index)+1);
			}
			else
			{
				phrase.add(words);
				tf.add(1);
			}
		}
		System.out.println("Unique phrases in the field "+phrase.size());
	}



	//descending order sort
	public void sortByFrequency()
	{
		int temp;
		String temp1;
		for(int i=0;i<phrase.size();i++)
			for(int j=0;j<phrase.size()-1;j++)
			{

				//System.out.println(" wcomparing "+phrase.get(j) +" "+tf.get(j)+ " and "+phrase.get(j+1)+" "+count.get(j+1) );
				//System.out.println(" comparing "+count.get(j)+ " and "+count.get(j-1) );
				//System.out.println(" icomparing "+j+ " and "+(j+1) );

				if(tf.get(j)<tf.get(j+1))
				{
					//System.out.println(" got in "+tf.get(j)+ " and "+tf.get(j-1) );
					temp=tf.get(j);
					tf.set(j, tf.get(j+1));
					tf.set(j+1, temp);

					temp1=(String)phrase.get(j);
					phrase.set(j, phrase.get(j+1));
					phrase.set(j+1, temp1);


				}
			}

		//for(int i =0 ;i<phrase.size();i++)
		//{
		//	System.out.println("Phrase "+phrase.get(i)+" freq : "+count.get(i));
		//}
	}

	//descending order sort
	public void sortByIdf()
	{
		float temp;
		int temp2;
		String temp1;
		for(int i=0;i<phrase.size();i++)
			for(int j=0;j<phrase.size()-1;j++)
			{

				//System.out.println(" wcomparing "+phrase.get(j) +" "+count.get(j)+ " and "+phrase.get(j+1)+" "+count.get(j+1) );
				//System.out.println(" comparing "+count.get(j)+ " and "+count.get(j-1) );
				//System.out.println(" icomparing "+j+ " and "+(j+1) );

				if(idf.get(j)<idf.get(j+1))
				{
					//System.out.println(" got in "+count.get(j)+ " and "+count.get(j-1) );
					temp=idf.get(j);
					idf.set(j, idf.get(j+1));
					idf.set(j+1, temp);

					temp2=tf.get(j);
					tf.set(j, tf.get(j+1));
					tf.set(j+1, temp2);

					temp1=(String)phrase.get(j);
					phrase.set(j, phrase.get(j+1));
					phrase.set(j+1, temp1);
				}
			}

		//for(int i =0 ;i<phrase.size();i++)
		//{
		//	System.out.println("Phrase "+phrase.get(i)+" freq : "+count.get(i));
		//}
	}

	//descending order sort
	public void sortByTfIdf()
	{
		float temp;
		int temp2;
		String temp1;
		for(int i=0;i<phrase.size();i++)
			for(int j=0;j<phrase.size()-1;j++)
			{

				//System.out.println(" wcomparing "+phrase.get(j) +" "+count.get(j)+ " and "+phrase.get(j+1)+" "+count.get(j+1) );
				//System.out.println(" comparing "+count.get(j)+ " and "+count.get(j-1) );
				//System.out.println(" icomparing "+j+ " and "+(j+1) );

				if(tfIDF.get(j)<tfIDF.get(j+1))
				{
					//System.out.println(" got in "+count.get(j)+ " and "+count.get(j-1) );
					temp=tfIDF.get(j);
					tfIDF.set(j, tfIDF.get(j+1));
					tfIDF.set(j+1, temp);

					temp=idf.get(j);
					idf.set(j, idf.get(j+1));
					idf.set(j+1, temp);

					temp2=tf.get(j);
					tf.set(j, tf.get(j+1));
					tf.set(j+1, temp2);

					temp1=(String)phrase.get(j);
					phrase.set(j, phrase.get(j+1));
					phrase.set(j+1, temp1);
				}
			}

		//for(int i =0 ;i<phrase.size();i++)
		//{
		//	System.out.println("Phrase "+phrase.get(i)+" freq : "+count.get(i));
		//}
	}

	public List returnTopK(int k)
	{
		ArrayList list = new ArrayList ();
		this.sortByFrequency();
		//int ctr=0;
		int current=tf.get(0);
		if(k>phrase.size())
			k=phrase.size();
		for(int i=0;i<k ;i++)
		{
			/*	if(current!=count.get(i))
			{
				current=count.get(i);
				ctr++;
			}*/
			list.add(phrase.get(i));
			//i++;
		}
		System.out.println("Sending "+list.size()+" phrases");
		return list;
	}

	public void writeToFile(String file,String fieldName)
	{
		try{
			BufferedWriter bw = new BufferedWriter (new FileWriter(new File(file),true));
			for(int i=0;i<phrase.size();i++)
				bw.write("\n"+fieldName +"\t"+phrase.get(i)+"\t"+tf.get(i));
			bw.close();

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void writeToFile(String file,String type,String outType)
	{
		try{
			BufferedWriter bw = new BufferedWriter (new FileWriter(new File(file),true));
			if(outType.equals("IDF"))
			{
				for(int i=0;i<phrase.size();i++)
					bw.write("\n"+type +"\t"+phrase.get(i)+"\t"+idf.get(i));
			}
			else if(outType.equals("tfIDF"))
			{
				tfIDF = new Vector();
				for(int i=0;i<phrase.size();i++)
					tfIDF.add(tf.get(i)*idf.get(i));
				sortByTfIdf();
				for(int i=0;i<phrase.size();i++)
					bw.write("\n"+type +"\t"+phrase.get(i)+"\t"+tfIDF.get(i));

			}
			bw.close();

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
