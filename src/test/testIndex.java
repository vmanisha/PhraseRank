package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import util.util;

public class testIndex {

	 static void main (String args[])
	 {
			util util= new util();
			try{
				IndexReader reader =IndexReader.open(FSDirectory.open(new File(args[0])));
				Searcher searcher = new IndexSearcher(reader);
				Directory d=reader.directory();
			//	reader.
			}
			catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		
	 }
}
