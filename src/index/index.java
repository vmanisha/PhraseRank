package index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class index {
	private index() {
	}

	static final File INDEX_DIR = new File("patIndex");
	static read pr;
	/** Index all text files under a directory. */
	public static void main(String[] args) {
		String usage = "java index.index <root_directory> <stop word file> <Query No file>";
		if (args.length == 0) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		if (INDEX_DIR.exists()) {
			System.out.println("Cannot save index to '" + INDEX_DIR
					+ "' directory, please delete it first");
			System.exit(1);
		}

		final File docDir = new File(args[0]);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out
					.println("Document directory '"
							+ docDir.getAbsolutePath()
							+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			IndexWriter writer = new IndexWriter(FSDirectory.open(INDEX_DIR), new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.UNLIMITED);
			writer.setMaxFieldLength(Integer.MAX_VALUE);
			String stop=args[1];
			String QFile=args[2];
			System.out.println("Indexing to directory '" + INDEX_DIR + "'...");
			pr = new read(writer);
			pr.loadstop(new File(stop));
			pr.loadQuery(new File(QFile));
			indexDocs(writer, docDir);
			System.out.println("Optimizing...");
			writer.optimize();
			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime()
					+ " total milliseconds");
			pr.writeQDocList("QueryDocNo");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
	}

	static void indexDocs(IndexWriter writer, File file) throws IOException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				Arrays.sort(files);
				// an IO error could occur
				if (files != null) {
					
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				System.out.println("adding " + file);
				try {
					if(pr==null)
						System.out.println("IS null");
					pr.parse(file);
				}
				// at least on windows, some temporary files raise this
				// exception with an "access denied" message
				// checking if the file can be read doesn't help
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
