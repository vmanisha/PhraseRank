package hadoop;



import java.io.IOException;

import java.io.File;
import java.util.TreeMap;
import java.util.Vector;





import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;


import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;

import org.apache.lucene.search.BooleanQuery;
//import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import similarity.checkOverlap;

public class executeQuery {

	public static class Map extends Mapper<Object, Text, Text, Text> {

		String filename;
		IndexReader reader;
		Searcher searcher;
		//SnowballAnalyzer sa;
		WhitespaceAnalyzer sa;
		QueryParser abst;
		QueryParser desc;
		QueryParser claim;
		QueryParser withoutField;
		MultiFieldQueryParser mfq;
		boolean foundIndex;
		//TreeMap<String, Vector<String>> relList;
		public void setup(Mapper.Context context) {

			try {
				System.out.println("In initialization");

				if(reader==null && searcher==null)
				{
					//System.out.println("In initialization of reader");
					//reader =IndexReader.open(FSDirectory.open(new File("/home/mansi/pindex")));
					File f =new File("/home/hadoop/patent_index/withoutFields/patIndex/");
					File f1 = new File("/home1/hadoopdata/patent_index/withoutFields/patIndex/");
					if(f.exists())
					{
						reader =IndexReader.open(FSDirectory.open(f));
						searcher = new IndexSearcher(reader);
						foundIndex=true;
					}
					else if (f1.exists())
					{
						reader =IndexReader.open(FSDirectory.open(f1));
						searcher = new IndexSearcher(reader);
						foundIndex=true;
					}
					else 
					{
						System.out.println("Index not there");
						foundIndex=false;
					}

				}
				//System.out.println("PATH IS "+job.get("map.input.file"));
				//if(sa==null && desc==null && claim==null && abst==null)
				//{
				System.out.println("In initialization of parsers");
				//sa = new SnowballAnalyzer(Version.LUCENE_CURRENT,"English");
				sa = new WhitespaceAnalyzer();
				abst=new QueryParser(Version.LUCENE_CURRENT,"abst", sa);
				desc=new QueryParser(Version.LUCENE_CURRENT,"desc", sa);
				claim=new QueryParser(Version.LUCENE_CURRENT,"claim", sa);
				String fields [] ={"abst","desc","claim"};
				mfq = new MultiFieldQueryParser(Version.LUCENE_CURRENT,fields, sa);
				withoutField = new QueryParser(Version.LUCENE_CURRENT,"content",sa);
				//		relList=checkOverlap.readRel(new File("/home/hdev/rels.b"));
				//System.out.println("list size "+relList.size());
				//}
				BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

			}
			catch (Exception e) {
				// TODO: handle exception
				//	System.out.println("PATH IS "+path);
				e.printStackTrace();
			}

		}

		public void map(Object key, Text value, Context context) 
		throws IOException, InterruptedException {

			String line=null;
			try {
				//System.out.println("In map");
				int count=0;
				String split [];
				line = value.toString();
				//System.out.println("line "+line);
				Query query=null;
				if (foundIndex)
				{
					if(line.indexOf(":")==-1 && line.length()>2)
					{
						//	System.out.println("Damned");
						split=line.split("\t");
						String title;
						
						query=withoutField.parse(split[split.length-1]);
						if(query!=null && searcher!=null)
						{
							TopDocs hits = searcher.search(query,1000); //Integer.MAX_VALUE);

							if(hits.totalHits>1000)
								count=1000;
							else
								count=hits.totalHits;
							//count=hits.totalHits;

							ScoreDoc sd [] = hits.scoreDocs; 

							for(int j=0;j<count;j++)
							{
								Document doc = searcher.doc(sd[j].doc);
								title=doc.get("title");
								context.write(new Text(split[0]+"_"+split[1]), new Text(split[0]+"\t"+1+"\t"+title+"\t"+j+"\t"+sd[j].score+"\tdemo"));
							}
							//	System.out.println("Written to the file");
						}

						/*if(split!=null && split.length==4)
						{
							if (line.startsWith("desc"))
								query=desc.parse(split[split.length-1]);
							else if(line.startsWith("claim"))
								query=claim.parse(split[split.length-1]);
							else if(line.startsWith("abstract"))
								query=abst.parse(split[split.length-1]);
							//System.out.println("Searching for "+split[2]);
							if(query!=null && searcher!=null)
							{
								TopDocs hits = searcher.search(query, 1000);

								if(hits.totalHits>1000)
									count=1000;
								else
									count=hits.totalHits;	

								ScoreDoc sd [] = hits.scoreDocs; 

								for(int j=0;j<count;j++)
								{
									Document doc = searcher.doc(sd[j].doc);
									//	System.out.println("For Query patent : "+split[2] + " matching documents "+hits.length());

									title=doc.get("title");
									//	riter.write("\n"+Qno+"\t"+round+"\t"+title+"\t"+j+"\t"+hits.score(j)+"\tdemo"+round);
									//Query no  Type PhraseNo
									context.write(new Text(split[0]+"_"+split[1]+"_"+split[2]), new Text(split[2]+"\t"+1+"\t"+title+"\t"+j+"\t"+sd[j].score+"\tdemo"));
								}
								//	System.out.println("Written to the file");
							}
						}*/
					}
					else if(line.indexOf(":")!=-1 && line.indexOf("\t")!=-1 && line.length()>2)
					{
						split=line.split("\t");
						query=mfq.parse(split[2]);
						//System.out.println("Query is"+query.toString());
						System.out.println("In map");
						String title;
						if(query!=null && searcher!=null)
						{
							TopDocs hits = searcher.search(query,1000); //Integer.MAX_VALUE);

							if(hits.totalHits>1000)
								count=1000;
							else
								count=hits.totalHits;
							//count=hits.totalHits;

							ScoreDoc sd [] = hits.scoreDocs; 

							for(int j=0;j<count;j++)
							{
								Document doc = searcher.doc(sd[j].doc);
								title=doc.get("title");
								context.write(new Text(split[0]+"_"+split[1]), new Text(split[0]+"\t"+1+"\t"+title+"\t"+j+"\t"+sd[j].score+"\tdemo"));
								/*if(relList.containsKey(split[0]))
								{
//									riter.write("\n"+Qno+"\t"+round+"\t"+title+"\t"+j+"\t"+hits.score(j)+"\tdemo"+round);
									if(relList.get(split[0]).contains(title.trim()))
									context.write(new Text(split[0]+"_"+split[1]), new Text(j+".\t"+split[0]+"\t"+title));
									//new Text(split[0]+"\t"+1+"\t"+title+"\t"+j+"\t"+sd[j].score+"\tdemo"));

								}*/
							}
							//	System.out.println("Written to the file");
						}
					}
				}

			} catch (Exception ex) {
				
				System.out.println("FOUND AN ERROR in map");
				System.out.println("line "+line);
				ex.printStackTrace();
			}
		}

	}

	public static class Reduce extends Reducer<Text,Text,Text,Text> {

		public void reduce(Text key, Iterable<Text> values,
				Context context
		) throws IOException, InterruptedException {
			try {
				//System.out.println("In reduce ");
				for (Text val : values)
					context.write(key, val);
				// System.out.println(" value class : "+wl.getClass());

			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("IN ERROR");

			}
		}

	}

	public static void main(String[] args) throws Exception {

		/*int res = ToolRunner.run(new Configuration(), new executeQuery(), args);
		System.exit(res);*/

		Configuration conf = new Configuration();
		conf.set("mapred.child.java.opts","-Xmx2000m");
		//conf.set("mapred.task.timeout", "2400000");
		conf.set( "lucene.index.path", "/home/hdev/pindex" );
		Job job = new Job(conf, "QueryExecution");

		job.setJarByClass(executeQuery.class);
		job.setMapperClass(Map.class);
		job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}

/*public int run(String[] args) throws Exception {


/*JobConf conf = new JobConf(getConf(), executeQuery.class);
conf.setJobName("QueryExecution");

conf.setOutputKeyClass(Text.class);
conf.setOutputValueClass(Text.class);
conf.setMapOutputKeyClass(Text.class);
conf.setMapOutputValueClass(Text.class);

conf.setMapperClass(Map.class);
//conf.setCombinerClass(Reduce.class);
conf.setReducerClass(Reduce.class);

conf.setInputFormat(TextInputFormat.class);
conf.setOutputFormat(TextOutputFormat.class);

//int i = 0;
/*List<String> other_args = new ArrayList<String>();

other_args.add(args[0]);
other_args.add(args[1]);
System.out.println("File : "+args[0]);
System.out.println("Output File : "+args[1]);

FileInputFormat.addInputPath(conf, new Path(args[0]));
FileOutputFormat.setOutputPath(conf, new Path(args[1]));

JobClient.runJob(conf);

return 0;
}*/


/*	public void configure(JobConf job) {

	String path =job.get("mapred.local.dir")+"/pindex";
	try {
	System.out.println("In initialization");

		if(reader==null && searcher==null)
		{
			//System.out.println("In initialization of reader");
			//reader =IndexReader.open(FSDirectory.open(new File("/home/mansi/pindex")));
			reader =IndexReader.open(FSDirectory.open(new File("/home/hdev/pindex")));
			searcher = new IndexSearcher(reader);
		}
		//System.out.println("PATH IS "+job.get("map.input.file"));
		if(sa==null && abst==null & desc==null && claim==null)
		{
			//System.out.println("In initialization of parsers");
			sa = new SnowballAnalyzer("English");	
			abst=new QueryParser("abst", sa);
			desc=new QueryParser("desc", sa);
			claim=new QueryParser("claim", sa);

		}
		BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

	}
	catch (Exception e) {
		// TODO: handle exception
		System.out.println("PATH IS "+path);
		e.printStackTrace();
	}
}*/
