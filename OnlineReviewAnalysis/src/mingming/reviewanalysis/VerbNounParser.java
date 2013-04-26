package mingming.reviewanalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class VerbNounParser {
	private BufferedReader in;
	private PrintWriter VerbNoun;
	
	
	public void Initialize(String infile,  String verbNounFile)
	{
		  try
		  {
			  in = new BufferedReader(new FileReader(infile));
			  
			  VerbNoun = new PrintWriter(new FileWriter(verbNounFile));
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
							   temp = sentences[1].trim();  //parent state of verb
							   temp2 = sentences[3].trim(); // whether it is "VB"
							   temp3 = sentences[6].trim(); // whether it is "ARG1"
							   temp4 = sentences[8].trim(); // parent state of noun
							   temp5 = sentences[9].trim(); // whether it is "NN"
                               
							   if(temp2.equals("VB") && (temp3.equals("ARG2")||temp3.equals("ARG3"))&& temp5.equals("NN"))
							   {
								   if(!isMeaninglessVerb(temp) && !isStopWords(temp4))
								   {
							    		 VerbNoun.println(temp + "\t" + temp4); 
								   }
							   }
						   }
					}
				  }
				  catch(Exception e)
				  {}
			  }
		  }

		  
		  if(VerbNoun != null)
		  {
			  VerbNoun.close();
			  VerbNoun = null;
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
	
	/**
	 * Verbs that do not have much meaning
	 * */
	public boolean isMeaninglessVerb(String s)
	{
		String[] stopwords = {"be","do","get","go","have","make","must","say","want","seem","will","UNKNOWN"};
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
}
