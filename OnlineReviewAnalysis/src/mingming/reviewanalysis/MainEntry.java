/**
 * 
mingming
Apr 7, 2013
OnlineReviewAnalysis
MainEntry.java
 */
package mingming.reviewanalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author mingming
 *
 */
public class MainEntry {

    ParseHTML et;
	String basicURL;
	String basicOutputdir;
	String outputfile;
	String startStr;
	
	int startID;
	int urlPerPage;  // this is when retrieval URLs of biz 
	int numPages;    // how many pages of Biz we want to retrieve
	int reviewPerPage;  // number of reviews per page for a Biz 
	HashMap<String, Boolean> linksMap; 
	
	
	/***
	 * Initialization 
	 */
	public MainEntry()
	{
		linksMap = new HashMap<String,Boolean>();
		et = new ParseHTML(linksMap);
		basicURL = "http://www.yelp.com/search?find_desc=spa&find_loc=Irvine%2C+CA&start=";  // remember to remove "&ns=..." part, which will cause problems
		basicOutputdir = "D:\\Research\\ReviewSpotlight\\2013_April_Research\\data\\data\\";
		outputfile =  basicOutputdir + "spa_irvine_url_list.csv";
		startStr = "/biz";
		startID = 0;
		urlPerPage = 10;
		numPages = 4;
		reviewPerPage = 40;
	}
	
	/**
	 * 		// grab all biz in one place from yelp: 
		// two pieces of info are grabbed: actual URL to that biz, and number of reviews
	 */
	public void Phrase1()
	{
		et.addTitle(outputfile);
		et.grabSpecificBiz(basicURL, startID, urlPerPage, numPages, startStr, outputfile);
	}
	
	/**
	 * for each line of Phrase 1 output file, grab reviews and store them in separate files
	 */
	public void Phrase2()
	{
		
		et.getReivews4AllBiz(outputfile, basicOutputdir,startID,reviewPerPage);

	}
	
	/**
	 * analyze sentences from text reviews
	 */
	public void Phrase3()
	{		
		et.sentenceAnalysis(basicOutputdir,urlPerPage*numPages);
	}
	
	/**
	 * Part of Speech analysis
	 */
	public void Phrase4()
	{
		// invoke Enju to process the output file to get part-of-speech		
		try {
		
			int id = 1;
			while(id <= urlPerPage*numPages)
			{
				System.out.println("Processing folder " + id + " start...");
				String param1 = "<" + basicOutputdir + id + "\\sentence1.txt>";
				String param2 = basicOutputdir + id + "\\partofspeech.txt";
				//System.out.println("param1: " + param1);
				//System.out.println("param2: " + param2);
				ProcessBuilder pb = new ProcessBuilder("cmd","/c","D:\\Research\\ReviewSpotlight\\NLP-Parser\\enju-win\\mogura.bat",param1,param2);
				pb.start();
				System.out.println("Start Enju..");
				TimeUnit.MINUTES.sleep(3);
				System.out.println("Processing folder " + id + " end...");
				id++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( InterruptedException ex ){
		    //Validate the case the process is being stopped by some external situation     
		}
	}
	
	/**
	 * extract (adj,noun) word pairs and (verb,noun) word pairs
	 */
	public void Phrase5()
	{
		int id = 1;
		while(id <= urlPerPage*numPages)
		//while(id <= 1)
		{
			String inputfile3 = basicOutputdir + id + "\\partofspeech.txt";
			String adjNounfile = basicOutputdir + id +"\\adjectiveNoun.txt";
			//String verbNounfile = basicOutputdir + id +"\\verbNoun.txt";
			String verbNounfile = basicOutputdir +"\\"+id+"verbNoun.txt";
			AdjNounParser adjN = new AdjNounParser();
			adjN.Initialize(inputfile3,adjNounfile);
			adjN.Parse();
			System.out.println("Extracting (adj,noun) pair " + id + " ends...!");
			
			VerbNounParser verbN = new VerbNounParser();
			verbN.Initialize(inputfile3,verbNounfile);
			verbN.Parse();
			System.out.println("Extracting (verb,noun) pair " + id + " ends...!");
			
			id++;
		}
	}
	
	
	
	
	/**
	 *  extract top rated noun and adj
	 * @param _TopKNoun
	 * @param _TopKAdj
	 */
	public void Phrase6(int _TopKNoun, int _TopKAdj)
	{
		int id = 1;
		while(id <= urlPerPage*numPages)
		//while(id <= 1)
		{
			String inputfile3 = basicOutputdir + id + "\\adjectiveNoun.txt";
			String adjNounfile = basicOutputdir + id +"\\topratedadjnoun.txt";
			TopRateAdjNounParser TopRated = new TopRateAdjNounParser();
			TopRated.Initialize(inputfile3,adjNounfile);
			TopRated.Parse(_TopKNoun,_TopKAdj);
			System.out.println("Extracting adjs and nouns" + id + " ends...!");
			id++;
		}
	}
	
	/**
	 * sentimental analysis
	 */
	public void Phrase7()
	{
		SentimentalAnalysis.instance();
		int id = 1;
		while(id <= urlPerPage*numPages)
	    //while(id <= 1)
		{
			String inputfile = basicOutputdir + id + "\\topratedadjnoun.txt";
			String outputfile = basicOutputdir + id +"\\sentimentalAnalysisResult.txt";
			SentimentalAnalysis.swn.Initialize(inputfile, outputfile);
			SentimentalAnalysis.swn.process();
			System.out.println("Done " + id+ " round...");
			id++;
		}
        System.out.println("Done all sentimental analysis...");
	}
	
	
	HashMap<String, Integer> AdjNounPairMap = new HashMap<String,Integer>();
	
	/***
     * get all top rated (adj,noun) features from all biz
	 * 
	 */
	public void Phrase8()
	{
		int id = 1;
		//HashMap<String, Integer> AdjNounPairMap = new HashMap<String,Integer>();
		String outputfile = basicOutputdir + "\\AllTopRatedAdjNounPairs.txt";
		PrintWriter out = null; 
		try {
			out = new PrintWriter(new FileWriter(outputfile,true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int count = 1;
		while(id <= urlPerPage*numPages)
		{
			System.out.println("Processing " + id + " file");
			String inputfile = basicOutputdir + id + "\\topratedadjnoun.txt";		
			//TODO: 
			//1. read adj noun pair into the map:  make "adj,noun" as the key 
			//2. if not in the map, then add into the map and also write into the output file.
			 BufferedReader in = null;
			 
			 try
			  {
				  in = new BufferedReader(new FileReader(inputfile));				  	  
			  }
			  catch(Exception e)
			  {
				  System.out.println("Error in create file: "+e.toString());
			  }
			  
			  if(in != null)
			  {
				  String oneline = " ";
				  while( oneline != null)
				  {
					  try {
						
						oneline = in.readLine();
						
						if(oneline != null)
						{
						   String[] data = oneline.split("\t");
						   if(data.length == 4)
						   {
							   String key = data[0] + ","+data[2];
							   if(!AdjNounPairMap.containsKey(key))
							   {
								   AdjNounPairMap.put(key, count);
								   count++;
								   out.println(key);
							   }
						   }
						}
					  }catch(Exception e)
					  {
						  
					  }
				  }
				  try {
						in.close();
						in = null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			     }
			 			  
			  id++;
		}
		
		  if(out != null)
		  {
			  out.close();
			  out = null;
		  }
		  
		/**
		 generate the feature matrix
		 * */
	   GenerateFeatureMatrixs();
	}
	
	
	/**
	 * Create the feature matrix
	 * */
	
	public void GenerateFeatureMatrixs()
	{
		String outputfile = basicOutputdir + "\\FeatureMatrix.txt";
		String outputfile2 = basicOutputdir + "\\FeatureMatrix01.arff";
		PrintWriter out = null; 
		PrintWriter out2 = null;
		try {
			out = new PrintWriter(new FileWriter(outputfile,true));
			out2 = new PrintWriter(new FileWriter(outputfile2,true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		out2.println("@relation yelp_review_features");
		for(int i = 1; i <= AdjNounPairMap.size(); i++)
		{
			out2.println("@attribute \tWP"+i+"\tNUMERIC");
		}
		
		out2.println("@data");
		
		int id = 1;
		while(id <= urlPerPage*numPages)
		{
			String inputfile = basicOutputdir + id + "\\topratedadjnoun.txt";		
			BufferedReader in = null;			 
			 try
			  {
				  in = new BufferedReader(new FileReader(inputfile));				  	  
			  }
			  catch(Exception e)
			  {
				  System.out.println("Error in create file: "+e.toString());
			  }
			  
			  String result = "";
			  int[] dataresult = new int[AdjNounPairMap.size()];
			  
			  if(in != null)
			  {
				  String oneline = " ";
				  while( oneline != null)
				  {
					  try {
						
						oneline = in.readLine();
						
						if(oneline != null)
						{
						   String[] data = oneline.split("\t");
						   if(data.length == 4)
						   {
							   String key = data[0] + ","+data[2];
							   if(AdjNounPairMap.containsKey(key))
							   {
								   result += AdjNounPairMap.get(key);
								   result += ",";
								   dataresult[AdjNounPairMap.get(key)] = Integer.parseInt(data[1]);  // adj appearing times for that noun							  
							   }
						   }
						}
					  }catch(Exception e)
					  {
						  
					  }
				  }
				  try {
						in.close();
						in = null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			     }  
			  
			out.println(result); 
			out2.print(dataresult[0]);
			for(int i = 1; i < dataresult.length;i++)
			{
				out2.print("," + dataresult[i]);
			}
			out2.println();
			id++;
		}
		
		  if(out != null)
		  {
			  out.close();
			  out = null;
		  }
		  
		  if(out2 != null)
		  {
			  out2.close();
			  out2 = null;
		  }
	}
	
	
	public static void main(String[] args) throws Exception{

         MainEntry ME = new MainEntry();
         
       //  ME.Phrase1();
       //  ME.Phrase2();
       //  ME.Phrase3();
       //  ME.Phrase4();
           ME.Phrase5();
       //  ME.Phrase6(40, 5); // top 40 nouns and top 5 adj
       //  ME.Phrase7();
       //  ME.Phrase8();
        
	}
}
