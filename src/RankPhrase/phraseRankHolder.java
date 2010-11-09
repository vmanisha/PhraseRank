package RankPhrase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

public class phraseRankHolder {

	Vector <Integer> origRank ;
	int patNo;
	Vector <Integer> phraseNo ;
	Vector <Double> phraseScore ;

	public phraseRankHolder(int no, String fileName) {


		patNo=no;
		String line ;
		int phNo;
		origRank= new Vector <Integer>();

		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			while((line=br.readLine())!=null)
			if(line.indexOf("all")==-1)
				origRank.add(Integer.parseInt(line.substring(0,line.indexOf("\t")).trim()));
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void add(Vector<Integer> phraseNo2, Vector<Double> phraseScore2) {
		// TODO Auto-generated method stub
		phraseNo=phraseNo2;
		phraseScore=phraseScore2;
		
		//System.out.println("added "+phraseNo.size()+" phrase "+phraseScore.size());
	}

	public void sortPhraseByScore() {
		// TODO Auto-generated method stub

		double temp;
		int temp1;
		for(int i=0;i<phraseScore.size();i++)
			for(int j=0;j<phraseScore.size()-1;j++)
			{
				if(phraseScore.get(j)<phraseScore.get(j+1))
				{
					//System.out.println(" got in "+tf.get(j)+ " and "+tf.get(j-1) );
					temp=phraseScore.get(j);
					phraseScore.set(j, phraseScore.get(j+1));
					phraseScore.set(j+1, temp);

					temp1=phraseNo.get(j);
					phraseNo.set(j, phraseNo.get(j+1));
					phraseNo.set(j+1, temp1);
				}
			}
	}

	public float findTotalPercentageOverlap()
	{
		float count =0;
		float size =phraseNo.size();

		if(origRank.size()!=phraseNo.size())
			System.out.println("For Patent no "+patNo+" orig "+origRank.size()+" phraseInFeatures "+phraseNo.size());
		if(origRank.size()<phraseNo.size())
			size=origRank.size();
		
		for(int i=0;i<size;i++)
		{
			if(phraseNo.get(i)==origRank.get(i))
				count++;
		}
		//System.out.println("count "+count +" size "+size);
		System.out.println("For Patent no "+patNo+" Percentage overlap "+((count/size)*100));
		return ((count/size)*100);
	}
	
	public int positionOfBestPhrase()
	{
		System.out.println("For Patent no "+patNo+" best phrase is at  "+phraseNo.indexOf(origRank.get(0)));
		System.out.println("For Patent no "+patNo+" obest phrase is at "+origRank.indexOf(phraseNo.elementAt(0)));
		return phraseNo.indexOf(origRank.get(0));
	}
	
	public int positionOfWorstPhrase()
	{
		System.out.println("For Patent no "+patNo+" worst phrase is at "+phraseNo.indexOf(origRank.lastElement()));
		int index= phraseNo.indexOf(origRank.lastElement());
		if(index==-1)
			return phraseNo.size();
		return index;
	}
	
	
	public float findTop50overlap()
	{
		float top50=0;
		int phrase;
		int ctr=0;
		if(phraseNo.size()>100)
		ctr=50;	
		else 
		ctr=phraseNo.size()/2;
		
		for(int i=0;i<ctr;i++)
		{
			phrase=phraseNo.elementAt(i);
			if(origRank.indexOf(phrase)<=100 && origRank.indexOf(phrase)>0)
				top50++;
		}
		System.out.println("For Patent no "+patNo+" the overlap in top 50 phrases is "+top50 +" "+ctr);
		return top50/(float)ctr;
	}
	
	public float findBot50overlap()
	{
		float bot50=0;
		int phrase;
		int ctr=0,tot=0;
		if(phraseNo.size()>100)
		{
			ctr=50;
			tot=100;
		}
		else 
		{
			ctr=phraseNo.size()/2;
			tot=phraseNo.size();
		}
		
		for(int i=ctr;i<tot;i++)
		{
			phrase=phraseNo.elementAt(i);
			if(origRank.indexOf(phrase)<=100 && origRank.indexOf(phrase)>0)
				bot50++;
		}
		System.out.println("For Patent no "+patNo+" the overlap in bot 50 phrases is "+bot50+" "+(tot-ctr));
		return bot50/(float)(tot-ctr);
	}
	
	public int findBest100Overlap()
	{
		int count=0;
		
		int bottom50=0;
		int phrase;
		
		for(int i=0;i<phraseNo.size();i++)
		{
			phrase=phraseNo.elementAt(i);
			if(origRank.indexOf(phrase)<=100 && origRank.indexOf(phrase)>0)
				count++;
			if(i==101)
				break;
		}
		System.out.println("For Patent no "+patNo+" the overlap in top 100 phrases is "+count);
		return count;
	}

	public void writeTopK(int k,BufferedWriter bw)
	{
		if(k>phraseNo.size())
			k=phraseNo.size();
		try{
			for(int i=0;i<k;i++)
				bw.write("\n"+phraseNo.elementAt(i));
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

}
