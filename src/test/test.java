package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jtextpro.JTextPro;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.CharStream;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

import rankPhrase.testTrain.chunkPatent;


import extraction.phraseList;

import util.util;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Pattern p=Pattern.compile("[A-Z]");
		Matcher m=p.matcher("2240 NN NN NNS CD");
			if(m.find())
			System.out.println("MILGAYA");
				
		String line = "I have to go am going sales Steven<tab>Chaney + PATENT-US-GRT-2001-06189043  aaaabbb cc atcg atcgff <DOCNO>PATENT-US-GRT-2001-06189043</DOCNO>  The first router then computes an optimum assignment of a replica copy of the information, for storage in at least one newly assigned server in the network";//; No. 60/023,904 filed Aug. 14, 1996. TECHNICAL FIELD The present invention relates to television (TV) cable/antenna systems, and in particular, to a TV graphical user interface (GUI)";
		util util = new util();
		String line2 = "<DOCNO>PATENT-US-GRT-2001-06189043</DOCNO>";
		long n1=23;
		long n2= 46;
		System.out.println("div "+n1/n2+" div(float) "+((float)n1)/n2+ " div both float "+ ((float)n1)/((float)n2));
		System.out.println(util.process(line));
		System.out.println("ye raha "+line2.substring(line2.indexOf(">")+1,line2.lastIndexOf("<")));
		
		System.out.println("Minimum is "+Double.MIN_VALUE +" another "+Double.MIN_NORMAL);
		
		/*int len=str.length();
		int i=10;
		int y=0;
		while(str.indexOf(" ",i)<len)
		{
			System.out.println(str.substring(y,str.indexOf(" ",i)));
			y=str.indexOf(" ",i);
			i+=10;
		}
	*/
	/*	temp=util.process(temp);
		System.out.println("TMEP "+temp);
		temp=temp.replaceAll("-", "");
		System.out.print("\n\n"+temp);
		
		System.out.println("---QUERY--");
		StandardAnalyzer sa = new StandardAnalyzer();
		sa.setMaxTokenLength(Integer.MAX_VALUE);
		QueryParser abst=null;
		abst=new QueryParser("abst", sa);
		try{
			Query query=abst.parse(temp);
			System.out.println(query.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		*/
/*		int window , nxt;
		String descPart;
		window = (line.lastIndexOf("<")+1)/3; //dividing the string into 3 parts
		System.out.println("window "+window);
		for (int i=0;i<line.lastIndexOf("<");i++)
		{
			nxt=line.indexOf(" ", i+window);
			System.out.println("Next "+nxt +" i "+i);
			if(nxt==-1)
				nxt=line.lastIndexOf("<");
			descPart =line.substring(i,nxt);
			System.out.println("the string "+descPart);
			i=nxt;	
		}*/
		
/*		ArrayList al = new ArrayList ();
		al.add(2);
		al.add(4);
		al.add(1);
		al.add(3);
		
		ArrayList al = new ArrayList ();
		al.add("c");
		al.add("k");
		al.add("d");
		al.add("s");
		al.add("y");
		al.add("a");
		al.add("s");
		al.add("b");
		al.add("y");
		al.add("a");
		al.add("k");
		al.add("d");
		al.add("s");
		al.add("m");
		al.add("m");
		al.add("m");
		al.add("m");
		al.add("m");

		phraseList ph = new phraseList(al);
		List m =ph.returnTopK(3);
		System.out.println(m);
		
		
		String text="A TV graphical user interface provided on a TV screen "
			+"includes a graphical channel changer for enabling a user to select a " +
					"required TV channel among about 1000 channels carried by a satellite " +
					"TV system. The graphical channel changer contains a vertical channel bar " +
					"composed of channel boxes that display numbers and logos of selected TV " +
					"channels. To switch the TV set to a required TV channel, the user directs " +
					"the pointing device at the graphical channel box that indicates the " +
					"required channel. Up and down scroll bars, that allow the user to scan through the " +
					"entire list of TV channels, extend beyond a safe area on the screen provided to " +
					"accomodate a picture to overscan conditions. A direct access bar is arranged so as " +
					"to cause the channel boxes to display numbers and logos of a selected group of channels " +
					"when the user directs the pointing device at the area of the direct access bar that " +
					"represents the selected group of channels. ";
		JTextPro jtp = new JTextPro();
		jtp.setPhraseChunkerModelDir("/home/mansi/Documents/JTextPro/models/CRFChunker");
		jtp.setSenSegmenterModelDir("/home/mansi/Documents/JTextPro/models/SenSegmenter");
		jtp.setPosTaggerModelDir("/home/mansi/Documents/JTextPro/models/CRFTagger");
		jtp.initPosTagger();
		jtp.initSenSegmenter();
		jtp.initPhraseChunker();
		List sentence =jtp.doSenSegmentation(text);
		List tags ;
		Iterator i=sentence.iterator() ;
		Iterator i2 ;
		while(i.hasNext())
		{
			tags=jtp.doPhraseChunking((String)i.next());
			i2=tags.iterator();
			while(i2.hasNext())
			{
				System.out.println("tag or text "+i2.next());
			}
			
		}
		
		String reg="\\[\\[.*?\\]\\]|\\[.*?\\]";
		Pattern p=Pattern.compile(reg,Pattern.DOTALL|Pattern.MULTILINE);
		Matcher m=p.matcher("[there is sth] [more to it]");
		*/
		/*String make="" ;
	test.Stringmaker(make);
	System.out.println("make is "+make);*/
	/*	try{
			BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
			line="" ;
			String split [];
		//	while(line=br.readLine()!=null)
				
			
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		*/
		
		String line1 = "distribut, 1, 8, 10, 19, 49413, 1681530, 288407, 2019350, 39.0000, 2019350.0000, 39.0000, 2019350.0000, 15.0897, 15.0897, 15.0897, [NN], [19], 12.9294, 1.3652, 2.0747, -0.3525";
//		String split []= line1.split("[.*]");
		//System.out.println(line1.substring(0,line1.indexOf("[")-2)+ line1.substring(line1.lastIndexOf("]")+1));
		System.out.println(line1.substring(line1.indexOf("["),line1.lastIndexOf("]")+1));
		TreeMap<String, Integer> list1=new TreeMap<String,Integer>();
		list1.put("mansi", 3);
		list1.put("mai", 13);
		list1.put("ai", 43);
		list1.put("mi", 23);
		list1.put("m", 30);
		list1.put("i",93);
		list1.put("si", 38);
		list1.put("xi", 31);
		Iterator <Entry<String, Integer>> i =sortByValues(list1).entrySet().iterator();
		System.out.println("Integer maximum" +Integer.MAX_VALUE);
		Entry<String, Integer> en;
		while(i.hasNext())
		{
			en=i.next();
			System.out.println("key "+en.getKey()+" value "+en.getValue());
		}
		try{
			System.out.println(getNGrams("minimum weight spanning tree"));
			System.out.println(getNGrams("JJ NN VBG NN"));
		//	chunkPatent chP = new chunkPatent(args);
		//	String content =chP.chunkPatentFile(new File (args[7]));
			//System.out.println("content is "+content);
		//	BufferedWriter bw = new BufferedWriter(new FileWriter(new File("ghatiya")));
		//	bw.write(content);
		//	bw.close();
			
			Pattern removeTag=Pattern.compile("((: ){1,}:)|(:,)|( : )|( :)|(: )|(:[A-Za-z]{1,})");
			//Matcher m;
			m=removeTag.matcher(":[CD JJ : CD]:[1]:10.3321:[NN, :]:[1:IPCALL NN NN NN NN NN]:[1]:[: : :]:[1]:14.4640:[: : : :]:[1]:14.4640:4:[NNP NNP, JJ NN, : :]");
			line =m.replaceAll(" ");
			System.out.println("line "+line);
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
	}
	
	public static void Stringmaker (String make){
		make="mansi";
	}
	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
		Comparator<K> valueComparator =  new Comparator<K>() {
		    public int compare(K k1, K k2) {
		        int compare = map.get(k2).compareTo(map.get(k1));
		        if (compare == 0) return 1;
		        else return compare;
		    }
		};
		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}
	
 public static Vector<String> getNGrams(String phrase) {
		// TODO Auto-generated method stub
	Vector <String> list = new Vector<String> ();
	String split[]= phrase.split(" ");
	StringBuffer sb = new StringBuffer();
	
	for(int j=0;j<split.length-1;j++)
	{
		sb.append(split[j]+" ");
		for(int i=1;i<3;i++) //trigrams
		{
			if(j+i<split.length)
			{
				sb.append(split[j+i]+" ");
				list.add(sb.toString());
			}
			
		}
		sb.replace(0, sb.length(), "");
	}
	
	return list;
	}

}
