/**
 * 
mingming
Apr 7, 2013
OnlineReviewAnalysis
SentenceParse.java
 */
package mingming.reviewanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;



/**
 * @author mingming
 * process the paragraph into sentences :  one sentence per line
 */
public class SentenceParse {
	private BufferedReader in;
	private PrintWriter out;
	
	public void Initialize(String infile, String outfile)
	{
		  try
		  {
			 in = new BufferedReader(new FileReader(infile));
			  
			 File file = new File(outfile);
			 file.getParentFile().mkdir();
			 out = new PrintWriter(new FileWriter(file));
		  }
		  catch(Exception e)
		  {
			  System.out.println("Error in create file: "+e.toString());
		  }
	}
	
	public void process()
	{
		  if(in != null)
		  {
			  String oneline = " ";
			  while( oneline != null)
			  {
				  try {
					
					oneline = in.readLine();

					if(oneline != null)
					{
						   String[] sentences = oneline.split("[\\.\\!\\?]"); //use these as separators to separate sentences 
						     for (int i=0;i<sentences.length;i++){
						    	 String temp = sentences[i].trim();
						    	 if(temp.length() > 0)
						    	 out.println(temp+".");  
						     }
					}
				  }
				  catch(Exception e)
				  {}
			  }
		  }
		  
		  if(out != null)
		  {
			  out.close();
			  out = null;
		  }
	}
	
	public static void main(String[] args) throws Exception {
		String inputfile = "D:\\Research\\ReviewSpotlight\\2013_April_Research\\data\\1\\reviewfile1.txt";
		String outputfile = "D:\\Research\\ReviewSpotlight\\2013_April_Research\\data\\1\\sentences1.txt";
		SentenceParse sp = new SentenceParse();
		sp.Initialize(inputfile, outputfile);
		sp.process();
		System.out.println("Done!");
	}
}
