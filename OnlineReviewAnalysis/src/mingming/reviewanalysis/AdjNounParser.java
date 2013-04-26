/**
 * 
mingming
Apr 9, 2013
OnlineReviewAnalysis
AdjNounParser.java
 */
package mingming.reviewanalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;



/**
 * @author mingming
 *
 */
public class AdjNounParser {
	
	
	private BufferedReader in;
	private PrintWriter adjNout;
	
	
	public void Initialize(String infile,  String adjNFile)
	{
		  try
		  {
			  in = new BufferedReader(new FileReader(infile));
			  
			  adjNout = new PrintWriter(new FileWriter(adjNFile));
		  }
		  catch(Exception e)
		  {
			  System.out.println("Error in create AdjNounParser files: "+e.toString());
		  }
	}
	
	public void Parse()
	{
		  if(in != null)
		  {
			  String oneline = " ";
			  String temp,temp2,temp3,temp4,temp5;
			  while( oneline != null)
			  {
				  try {
					
					oneline = in.readLine();
					//System.out.println(oneline);
					if(oneline != null)
					{
						
						   String[] sentences = oneline.split("\t");
						  // System.out.println(sentences.length);
						   if(sentences.length >= 10)
						   {
							   temp = sentences[1].trim();  //parent state of adj
							   temp2 = sentences[3].trim(); // whether it is "JJ"
							   temp3 = sentences[6].trim(); // whether it is "ARG1"
							   temp4 = sentences[8].trim(); // parent state of noun
							   //temp5 = sentences[9].trim(); // whether it is "NN"
                               
							   if(temp2.equals("JJ") && temp3.equals("ARG1")) // && temp5.equals("NN"))
							   {
								   if(! isStopWords(temp4))
								   {
							    		 adjNout.println(temp + "\t" + temp4); 
								   }
							   }
						   }
						   
						   /*
						     for (int i=0;i<sentences.length;i++){
						    	 String temp = sentences[i].trim();
						    	 
						    	// System.out.print(temp.length()+"\t");
						    	 String temp2 = sentences[i+4].trim();
						    	 String temp3 = sentences[i+6].trim();
						    	 if(temp.equals("JJ") && temp2.equals("ARG1") && !isStopWords(temp3))
						    	 {
						    		 adj = sentences[i-1].trim();
						    		 noun = sentences[i+6].trim();
						    		 adjNout.println(adj + "\t" + noun);
						    	 }						    	 
						     }
						     */
					}
				  }
				  catch(Exception e)
				  {}
			  }
		  }

		  
		  if(adjNout != null)
		  {
			  adjNout.close();
			  adjNout = null;
		  }
	}
	
	
	public boolean isStopWords(String s)
	{
		String[] stopwords = {"a","able","about","across","after","all","almost","also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","however","i","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their","them","then","there","these","they","thing","this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your","UNKNOWN"};
		boolean result = false;
		for(int i = 0 ; i < stopwords.length; i++ )
		{
			if(s.equals(stopwords[i]))
			{
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	
	public static void main(String[] args) throws Exception {
		String inputfile = "E:\\ReviewSpotlight\\data\\result\\outputpairs1.txt";
		String adjNounfile = "E:\\ReviewSpotlight\\data\\result\\adjNoun1.txt";
		AdjNounParser adjN = new AdjNounParser();
		adjN.Initialize(inputfile,adjNounfile);
		adjN.Parse();
		System.out.println("Done!");
	}
}
