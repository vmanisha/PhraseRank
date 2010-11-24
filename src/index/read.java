package index;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Vector;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import util.util;

public class read {

	static SnowballAnalyzer en;
	Vector <String> qList; //List of query files
	Document pat = null;
	IndexWriter writer;
	Vector <String> QDocNo; 
	public read(IndexWriter w)
	{
		try{
	
			writer = w;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void loadstop(File stop)
	{
		try{
			en=util.LoadStopWords(new BufferedReader(new FileReader(stop)));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
	}
	
	public void loadQuery(File query)
	{
		try{
			qList=util.returnQueryList(new BufferedReader(new FileReader(query)));
			QDocNo = new Vector ();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
	}
	public void parse(File dir)
	{
		//Iterator <File> files = util.makefilelist(dir).iterator();
		BufferedReader br;
		String line;
		String line2;
	//	StringBuffer content=new StringBuffer();
	//	Field f;
		java.util.List l;
		String docNo;
		try{
		//	while(files.hasNext())
		//	{
				br = new BufferedReader(new FileReader (dir));
				pat = new Document();
				while((line=br.readLine())!=null)
				{
					line2=util.processForIndex(line.toLowerCase());
					//System.out.println(line2);
					if(line.startsWith("<CITATION>"))
					{
						pat.add(new Field("cite",line2,Field.Store.YES,Field.Index.NO));
					}
					else if(line.startsWith("<PRI-IPC>"))
					{
						pat.add(new Field("ipc",line2.replaceAll("\\/", ""),Field.Store.YES,Field.Index.NOT_ANALYZED));
					}
					else if (line.startsWith("<DOCNO>"))
					{
						docNo=line.substring(line.indexOf(">")+1,line.lastIndexOf("<"));
						if(qList.contains(docNo))
							QDocNo.add(docNo+":"+writer.numDocs());
						pat.add(new Field("title",docNo,Field.Store.YES,Field.Index.NOT_ANALYZED));
						pat.add(new Field("path",docNo,Field.Store.YES,Field.Index.NO));
					}
					else if(line.startsWith("<TITLE>"))
					{
						pat.add(new Field("tit",line2,Field.Store.NO,Field.Index.ANALYZED,Field.TermVector.YES));
					}
					else if(line.startsWith("<ABST>"))
					{
						//System.out.println("Abst "+line2);
						pat.add(new Field("abst",line2,Field.Store.NO,Field.Index.ANALYZED,Field.TermVector.YES));
					}
					else if(line.startsWith("<SPEC>"))
					{
						//System.out.println("SPEC "+line2);
						pat.add(new Field("desc",line2,Field.Store.NO,Field.Index.ANALYZED,Field.TermVector.YES));
					}
					else if(line.startsWith("<CLAIM>"))
					{
						pat.add(new Field("claim",line2,Field.Store.NO,Field.Index.ANALYZED,Field.TermVector.YES));
					}
					
				}
				//pat.add(new Field("content",content.toString(),Field.Store.NO,Field.Index.NOT_ANALYZED));
				//System.out.println("got here");
				//content.replace(0,content.length(), "");
				br.close();
				
				l=pat.getFields();
				//System.out.println("fields are "+l.size());
				writer.addDocument(pat, en);
		
			//}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	public void writeQDocList(String filename)
	{
		util.writeText(QDocNo, filename);
	}

}
