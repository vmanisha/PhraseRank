package hadoop;

import hadoop.executeQuery.Map;
import hadoop.executeQuery.Reduce;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import similarity.checkOverlap;

public class queryDocument {

	public static class Map extends Mapper<Object, Text, Text, IntWritable> {
		String filename;
		IndexReader reader;
		Searcher searcher;
		//SnowballAnalyzer sa;
		QueryParser qp;
		WhitespaceAnalyzer sa;
		TreeMap<String, Vector<String>> relList;
		public void setup(Mapper.Context context) {

			try {
				System.out.println("In initialization");

				if(reader==null && searcher==null)
				{
					reader =IndexReader.open(FSDirectory.open(new File("/home/hdev/patent_index/withoutFields/patIndex")));
					searcher = new IndexSearcher(reader);
				}
				System.out.println("In initialization of parsers");
				//sa = new SnowballAnalyzer(Version.LUCENE_CURRENT,"English");
				sa = new WhitespaceAnalyzer();
				qp=new QueryParser(Version.LUCENE_CURRENT,"content", sa);
				relList=checkOverlap.readRel(new File("/home/hdev/rels.b"));
				System.out.println("list size "+relList.size());
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

			try {
				int count=0;
				String split [];
				String line = value.toString();
				System.out.println("map");
				Query query=null;
				
					split=line.split("\t");
					//System.out.println("split length "+split.length);
					String title;
					if(split.length==2)
					{
						query=qp.parse(split[1]);
						TopDocs hits = searcher.search(query, Integer.MAX_VALUE);
						count=hits.totalHits;
						ScoreDoc sd [] = hits.scoreDocs; 
						for(int j=0;j<count;j++)
						{
							Document doc = searcher.doc(sd[j].doc);
							title=doc.get("path");
							//System.out.println("title "+title);
							//System.out.println("split[0]"+split[0]);
							if(relList.containsKey(split[0]))
							{
								//	riter.write("\n"+Qno+"\t"+round+"\t"+title+"\t"+j+"\t"+hits.score(j)+"\tdemo"+round);
								if(relList.get(split[0]).contains(title.trim()))
								context.write(new Text(split[0]+" "+title), new IntWritable(1));
								//new Text(split[0]+"\t"+1+"\t"+title+"\t"+j+"\t"+sd[j].score+"\tdemo"));
							}
						}
					}
					
			}catch (Exception ex) {
				//System.out.println("FOUND AN ERROR in map");
				ex.printStackTrace();
			}
		}

	}
	public static class Reduce extends Reducer<Text,IntWritable,Text,Text> {

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context
		) throws IOException, InterruptedException {
			try {
				//System.out.println("In reduce ");
				int count=0;
				for (IntWritable val : values)
					count++;
				context.write(key,new Text(""+count));
				// System.out.println(" value class : "+wl.getClass());

			} catch (Exception ex) {
				ex.printStackTrace();
				//System.out.println("IN ERROR");

			}
		}

	}
	
	public static void main(String[] args) throws Exception {

		/*int res = ToolRunner.run(new Configuration(), new executeQuery(), args);
		System.exit(res);*/

		Configuration conf = new Configuration();
		conf.set("mapred.child.java.opts","-Xmx2000m");
		conf.set("mapred.task.timeout", "1200000");
		conf.set( "lucene.index.path", "/home/hdev/pindex" );
		Job job = new Job(conf, "QueryExecution");

		job.setJarByClass(executeQuery.class);
		job.setMapperClass(Map.class);
		//job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
