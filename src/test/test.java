package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jtextpro.JTextPro;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.CharStream;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

import extraction.phraseList;

import util.util;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String line = "I have to go am going sales Steven<tab>Chaney + PATENT-US-GRT-2001-06189043  aaaabbb cc atcg atcgff <DOCNO>PATENT-US-GRT-2001-06189043</DOCNO>  The first router then computes an optimum assignment of a replica copy of the information, for storage in at least one newly assigned server in the network";//; No. 60/023,904 filed Aug. 14, 1996. TECHNICAL FIELD The present invention relates to television (TV) cable/antenna systems, and in particular, to a TV graphical user interface (GUI)";
		util util = new util();
		String line2 = "<DOCNO>PATENT-US-GRT-2001-06189043</DOCNO>";
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
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
			line="" ;
			String split [];
		//	while(line=br.readLine()!=null)
				
			
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
	
	public static void Stringmaker (String make){
		make="mansi";
	}

}
