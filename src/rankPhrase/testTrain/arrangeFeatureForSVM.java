package rankPhrase.testTrain;

//35 25
//40 60
//70 30
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.util;

public class arrangeFeatureForSVM {


	/** 
	 * @param args[0] -- Each phrase List 
	 * @param args[1] -- List of phrases ordered by map
	 * @param args[2] -- Features file
	 * @param args[3] -- No of features - POSTag feature (its not a number)
	 * @param args[4] -- Output folder
	 * @param args[5] -- 'rank' --> svm rank format , 'lnknet' --> in classification lnknet format
	 * 
	 *  Format of SVM is -- score , qid:PatentNo , featureNo:featureValue ..
	 *  Please specify the index of feature with POSTag count  20:[NN, NNS]   21:[23,45] index=21 modify at line #366
	 * @throws IOException 
	 */
	static Pattern p=Pattern.compile("[A-Za-z.:]");
	static Pattern removeTag=Pattern.compile("( :)|((: ){1,}:)|(:,)|( : )|(: )|(:[A-Za-z]{1,})");
	static Pattern removeEmptyTag=Pattern.compile("(\\[:\\])");
	static Matcher m;
	static String deLim =":";
	static int formatType;
	public static void main (String args []) throws IOException
	{
		String line ="";
		BufferedWriter writeFeatures=null;
		try{
			ArrayList <File> eachPhraseList = util.makefilelist(new File(args[0]));
			ArrayList <File> orderedByMapList = util.makefilelist(new File(args[1]));
			ArrayList <File> featurePatentList = util.makefilelist(new File(args[2]));

			Collections.sort(eachPhraseList);
			Collections.sort(orderedByMapList);
			Collections.sort(featurePatentList);

			int noFeature= Integer.parseInt(args[3]);
			Vector <String> posTagList= new Vector <String>();
			Double [] minimum = new Double [noFeature]; //20 are the no of features
			Double [] maximum = new Double [noFeature]; //20 are the no of features
			if(args[5].equals("rank") )
				formatType=1;
			else if (args[5].equals("lnknet"))
				formatType=0;
			else 
			{	
				System.err.println("Type isnt corrent, enter \'rank\' or \'lnknet\'");
				System.exit(0);
			}

			for(int k=0;k<noFeature;k++)
			{
				minimum[k]=Double.MAX_VALUE;
				maximum[k]=Double.MIN_VALUE;
			}

			//get the POS tags present in the features and the min and max of each feature
			posTagList=getPOSAndRest(featurePatentList,minimum,maximum);
			//System.out.println("the pos tag list "+posTagList.toString());

			File output = new File(args[4]);
			if(output.exists())
			{
				System.err.println("the output folder exists ");
				System.exit(0);
			}

			printMinMax(minimum,maximum);

			File f;

			BufferedReader readEach ;
			BufferedReader readFeature ;
			BufferedReader readOrderedList ;
			writeFeatures= new BufferedWriter(new FileWriter(output));

			String split [];

			int qId;

			Vector <String> phraseWritten = new Vector <String> ();

			Vector <String >  featureList = new Vector<String> ();
			Vector <String > fphraseList= new Vector <String> ();

			Vector <Integer > phraseNoList = new Vector <Integer>();
			Vector <String> originalPhraseList = new Vector <String>();

			int index=0,j=0,phraseNo;
			String map;
			String phrase;

			for (j=0;j<eachPhraseList.size();j++)
			{
				fphraseList.clear();
				phraseNoList.clear();
				phraseWritten.clear();
				featureList.clear();
				originalPhraseList.clear();

				//pos=0;

				//Read the features for the phrases that have been queried
				f=featurePatentList.get(j);
				qId= Integer.parseInt(f.getName().substring(1));
				readFeature = new BufferedReader(new FileReader(f));
				while((line= readFeature.readLine())!=null)
				{
					if(line.length()>1)
					{
						//split[0] is the phrase
						fphraseList.add(line.substring(0, line.indexOf(deLim)).trim());
						//System.out.println("adding phrase "+ line.substring(0, line.indexOf(" :")).trim());
						featureList.add(getNewFeatureVector(line,minimum,maximum,qId,posTagList));
					}
				}
				readFeature.close();

				//read the phrase list
				f=eachPhraseList.get(j);
				readEach = new BufferedReader(new FileReader(f));
				while((line = readEach.readLine())!=null)
				{
					if(line.length()>1)
					{
						split= line.split("\t");
						phrase= split[3].substring(1,split[3].length()-1).trim();
						phrase=makeNewPhrase(phrase); //remove a single letter or alphabet

						//System.out.println("phrase "+phrase +" " + split[3]);
						//put the phrase and phrase no in the phraseList , phraseNoList and typeList
						index= fphraseList.indexOf(phrase);
						if(index!=-1)
						{
							originalPhraseList.add(phrase);
							phraseNoList.add(Integer.parseInt(split[1]));
						}
						else 
							System.out.println("no feat "+split[3]);
					}
				}
				readEach.close();


				f=orderedByMapList.get(j);
				readOrderedList= new BufferedReader(new FileReader(f));

				System.out.println("** "+f.getName()+" **");
				while((line= readOrderedList.readLine())!=null)
				{
					if(line.length()>0)
					{
						//System.out.println("line is "+line.trim() +"number "+line.trim().substring(0,line.indexOf("\t")-1)) ;
						try{
							phraseNo = Integer.parseInt(line.substring(0,line.indexOf("\t")).trim());
							//System.out.println("line "+line +" phrase "+phraseNo);
							index= phraseNoList.indexOf(phraseNo);
							map=line.substring(line.indexOf("\t")+1);

							if(index!=-1)
							{
								phrase=originalPhraseList.elementAt(index);
								if(!phraseWritten.contains(phrase))
								{
									//write the feature to the file
									//writeFeatures.write("\n"+map+" "+featureList.get(fphraseList.indexOf(phrase))+" #"+phraseNo);
									//writeFeatures.write("\n1 "+featureList.get(fphraseList.indexOf(phrase))+" #"+phraseNo+" #"+
									//		Integer.parseInt(f.getName())+"#");

									//FOR SVM RANK
									if(formatType==1)
										writeFeatures.write("\n"+map+" "+featureList.get(fphraseList.indexOf(phrase))+" #"+phraseNo+"#"+phrase);
									else {	
										//FOR SVM CLASSIFIER
										if(Float.parseFloat(map) > 0)
											writeFeatures.write("\n"+1+" "+featureList.get(fphraseList.indexOf(phrase))+" #"+f.getName()+" #"+phraseNo+"#"+phrase);	
										else 
											writeFeatures.write("\n"+0+" "+featureList.get(fphraseList.indexOf(phrase))+" #"+f.getName()+" #"+phraseNo+"#"+phrase);
									}	
									phraseWritten.add(phrase);
								}
							}
							//else
							//	System.out.println("phrase not present "+phraseNo);
						}
						catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				}
				readOrderedList.close();
			}
			writeFeatures.close();

		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("error. "+line);
			writeFeatures.flush();
		}


	}

	private static String makeNewPhrase(String phrase) {
		// TODO Auto-generated method stub
		String [] split=phrase.split(" ");
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<split.length;i++)
			if(split[i].length()>1)
				sb.append(" "+split[i]);


		return sb.toString().trim();
	}

	private static void printMinMax(Double[] minimum, Double[] maximum) {
		// TODO Auto-generated method stub

		for(int i=1;i<minimum.length;i++)
		{
			System.out.println("Feature no "+i);
			System.out.println("Minimum is "+minimum[i]);
			System.out.println("Maximum is "+maximum[i]);
		}

	}

	private static Vector<String> getPOSAndRest(
			ArrayList<File> featurePatentList, Double[] minimum,
			Double[] maximum) {
		// TODO Auto-generated method stub

		Iterator<File> i = featurePatentList.iterator();
		BufferedReader br ;
		String line =null;

		String split[],split2[];
		String posTag;
		Vector <String> posTags = new Vector <String>();
		Double value;

		int j=0;
		try{
			while(i.hasNext())
			{
				br = new BufferedReader (new FileReader(i.next()));
				while((line= br.readLine())!=null)
				{
					if(line.length()>2)
					{
						//method:4.1269:1.9266:3.5396:3.1977:3.1977:3.1977:[NN]:[13]:13.7597:0.9139:1.0342:0.0983
						m=removeTag.matcher(line);
						line =m.replaceAll(" ");
						m=removeEmptyTag.matcher(line);
						line =m.replaceAll("[ ]");

						split=line.split(deLim);
						//System.out.println("split lenth "+split.length);
						//System.out.println("split lengt "+split.length);
						for(j=1;j<split.length;j++)
						{
							if(split[j].startsWith("["))
							{
								if(split[j].indexOf("N")!=-1)
								{
									split2=split[j].substring(1,split[j].length()-1).split(",");
									for(int k=0;k<split2.length;k++)
									{
										if(!posTags.contains(split2[k].trim()))
											posTags.add(split2[k].trim());
									}
								}

							}
							else 
							{
								try{
									value=Math.log(Double.parseDouble(split[j])+1);
									if(minimum[j]>value)
									{
										if(value==0)
											System.out.println("val 0. "+j );
										//System.out.println(" min of "+j+" is "+value +"for line "+line);
										minimum[j]=value;
									}
									if (maximum[j]<value)
									{
										//System.out.println(" max of "+j+" is "+value +"for line "+line);
										maximum[j]=value;
									}

								}catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();
									System.out.println("line "+line);
								}
							}
						}

					}
				}

			}
		}
		catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();
		}

		return posTags;
	}



	private static String getNewFeatureVector(String line, Double[] minimum, Double[] maximum,
			int qId, Vector<String> posTagList) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("0.0000");
		StringBuffer newFeature= new StringBuffer();
		String split[],split2[],split3[];
		double maxCount=0;
		int maxIndex=0;
		double index;

		m=removeTag.matcher(line);
		line =m.replaceAll(" ");
		m=removeEmptyTag.matcher(line);
		line =m.replaceAll("[ ]");
		split= line.split(deLim);

		double value;

		//append the patentName
		if(formatType==1)
		newFeature.append("qid:"+qId);

		for(int i=1;i<split.length;i++)
		{
			if(split[i].indexOf("[")==-1)
			{
				try{
					//if(i==4)
					//System.out.println("the old value "+split[i] +" min "+minimum[i]+" max "+maximum[i]);
					value=Math.log(Double.parseDouble(split[i])+1);
					value=(float)(value-minimum[i])/(float)(maximum[i]-minimum[i]);
					//if(i==4)
					//System.out.println("new value "+value);
				}catch (Exception e) {
					// TODO: handle exception
					System.out.println("PROBLEM. " +split[i]);
					value=0;
				}

				newFeature.append(" "+i+":"+df.format(value));

				//for the classifier
				/*if(i==1)
				newFeature.append(df.format(value));
				else
				newFeature.append(" "+df.format(value));*/

			}
			else if(i==20)   //!notNumber(split[i].substring(1,split[i].length()-1)))  No of times the POS tag appears
			{
				split2=split[i-1].substring(1,split[i-1].length()-1).split(",");
				split3=split[i].substring(1,split[i].length()-1).split(",");
				try{
					maxCount=Double.parseDouble(split3[0]);
					maxIndex=0;
					for(int k=1;k<split3.length;k++)
					{
						if(maxCount<Double.parseDouble(split3[k]))
						{
							maxCount=Double.parseDouble(split3[k]);
							maxIndex=k;
						}
					}
					if(split2.length<maxIndex)
						index=0;
					else
						index=posTagList.indexOf(split2[maxIndex]);
					//normalize the value of the index
					index=index/(float)posTagList.size();
					newFeature.append(" "+(i-1)+":"+df.format(index)+" "+i+":"+maxCount);

				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					/*System.out.println("split 2 "+split[i-1].substring(1,split[i-1].length()-1));
					System.out.println("Not a number "+split[i].substring(1,split[i].length()-1));*/
					newFeature.append(" "+(i-1)+":"+df.format(0)+" "+i+":"+maxCount);

				}
				//for classification
				//newFeature.append(" "+df.format(index)+" "+maxCount);
			}

		}
		//	newFeature.append(" 25:1.0");
		return newFeature.toString();
	}
	public static boolean notNumber(String text)
	{
		m=p.matcher(text);
		if(m.find())
			return true;
		return false;
	}
}
