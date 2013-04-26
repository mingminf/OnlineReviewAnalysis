/**
 * 
mingming
Apr 7, 2013
OnlineReviewAnalysis
SentenceParse.java
 */
package mingming.reviewanalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
/*
 * 
 * Process the adj-Noun pair document to get
 * the most frequent appearing nouns and the most frequent appearing adj along with them
 * */

public class TopRateAdjNounParser {
	
		private BufferedReader in;
		private PrintWriter out;

		
		public HashMap<String,Integer> NounFreq; // store the appearing times of each noun
		public HashMap<String,HashMap<String,Integer>> AdjFreq; // store the appearing times of adj of a specific noun
		
		int TopKNouns = 30;
		int TopKAdj = 5;
		
		public void Initialize(String infile, String outfile)
		{
			  try
			  {
				  in = new BufferedReader(new FileReader(infile));
				  out = new PrintWriter(new FileWriter(outfile));		  
			  }
			  catch(Exception e)
			  {
				  System.out.println("Error in create file: "+e.toString());
			  }
			  
			  NounFreq = new HashMap<String,Integer>();  // store the appearing times of each noun
			  AdjFreq = new HashMap<String,HashMap<String,Integer>>(); // store the appearing times of adj of a specific noun
		}
		
		public void Parse(int _TopKNouns, int _TopKAdj)
		{
			  if(in != null)
			  {
				  TopKNouns = _TopKNouns;
				  TopKAdj = _TopKAdj;
				  String oneline = " ";
				  while( oneline != null)
				  {
					  try {
						
						oneline = in.readLine();
						
						if(oneline != null)
						{
							   String[] sentences = oneline.split("\t");
							  // System.out.println("sentences size: " + sentences.length);
							   
							   if(sentences.length == 2) // each line should only have two words: adj + noun
							   {
								   String key = sentences[1].trim();
								   String adjKey = sentences[0].trim();
								   if(NounFreq.containsKey(key)) // already store this noun; add the number of appearing
								   {
									  NounFreq.put(key, NounFreq.get(key)+1);
									  HashMap<String, Integer> data = AdjFreq.get(key);
									  
									  if(data.containsKey(adjKey))
									  {
										  data.put(adjKey, data.get(adjKey)+1);
									  }
									  else
									  {										  										  
										  data.put(adjKey, 1);
										  AdjFreq.put(key, data);
									  }
									  
								   }
								   else  // the first time seeing this noun
								   {
									   NounFreq.put(key,1);
									   HashMap<String,Integer> data = new HashMap<String,Integer>();
									   data.put(adjKey, 1);
									   AdjFreq.put(key, data );
								   }
							   }
						}
					  }
					  catch(Exception e)
					  {}
				  }
			  }
			
			  // sort nouns by their appearing times
			ArrayList<Integer> NounFrequency = new ArrayList<Integer>();  
			ArrayList<String> keys = sortHashMapByValues(NounFreq, TopKNouns,NounFrequency);
			
						
			for(int i = 0; i < keys.size(); i++)
			{
				String key = keys.get(i);
				Integer freq = NounFrequency.get(i);
				//sort adjs by their appearing times
				ArrayList<Integer> adjFrequency = new ArrayList<Integer>();
				ArrayList<String> adjList = sortHashMapByValues( AdjFreq.get(key),TopKAdj, adjFrequency);
				for(int j = 0; j < adjList.size();j++)
				{
					out.println(adjList.get(j)+"\t"+adjFrequency.get(j)+"\t"+key+"\t"+freq);
				}
			}
			
			
			  if(out != null)
			  {
				  out.close();
				  out = null;
			  }

		}
		

	public ArrayList<String> sortHashMapByValues(HashMap<String, Integer> data, int TopKNouns, ArrayList<Integer> frequency)
	{
		ArrayList<String>  keys = new ArrayList<String>();
		ArrayList<Integer> values = new ArrayList<Integer>();
		values.addAll(data.values());
		Collections.sort(values,Collections.reverseOrder()); // sort nouns according to the frequency of their appearance
		
		for(int i = 0; i < Math.min(values.size(), TopKNouns);i++)
		{
			Integer v = values.get(i);
			for(String s: data.keySet())
			{
				if(data.get(s) == v && !keys.contains(s))
				{
					keys.add(s);
					frequency.add(v);
					System.out.println(s + ":" + v);
				}
			}
		}
		return keys;
	}
	
	public static void main(String[] args) throws Exception {
		String inputfile = "E:\\ReviewSpotlight\\data\\result\\adjNoun1.txt";
		String outputfile = "E:\\ReviewSpotlight\\data\\result\\topWordsAdjs.txt";
		
		TopRateAdjNounParser wa = new TopRateAdjNounParser();
		wa.Initialize(inputfile, outputfile);
		wa.Parse(40,5);
	//	System.out.println("Noun size: " + wa.NounFreq.size());
		
		System.out.println("Done!");
	}
}
