package mingming.reviewanalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class SentimentalAnalysis {

	public static SentimentalAnalysis swn = null;
	private String location = "./lib/SentiWordNet_3.0.0_20120510.txt";
	private HashMap<String, Double> dict;

	private SentimentalAnalysis() {

		dict = new HashMap<String, Double>();
		HashMap<String, Vector<Double>> _temp = new HashMap<String, Vector<Double>>();
		try {
			BufferedReader csv = new BufferedReader(new FileReader(location));
			String line = "";

			while ((line = csv.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				String[] data = line.split("\t");
				
				if(data.length < 5 || data[2].isEmpty() || data[3].isEmpty())
					continue;
				
				Double score = 0.;

				score = Double.parseDouble(data[2])
						- Double.parseDouble(data[3]);

				String[] words = data[4].split(" ");
				for (String w : words) {
					String[] w_n = w.split("#");
					w_n[0] += "#" + data[0];
					//System.out.println(w_n[0]);
					int index = Integer.parseInt(w_n[1]) - 1;
					if (_temp.containsKey(w_n[0])) {
						Vector<Double> v = _temp.get(w_n[0]);
						if (index > v.size())
							for (int i = v.size(); i < index; i++)
								v.add(0.0);
						v.add(index, score);
						_temp.put(w_n[0], v);
					} else {
						Vector<Double> v = new Vector<Double>();
						for (int i = 0; i < index; i++)
							v.add(0.0);
						v.add(index, score);
						_temp.put(w_n[0], v);
					}
				}
			}
			Set<String> temp = _temp.keySet();
			for (Iterator<String> iterator = temp.iterator(); iterator
					.hasNext();) {
				String word = (String) iterator.next();
				Vector<Double> v = _temp.get(word);
				double score = 0.0;
				double sum = 0.0;
				for (int i = 0; i < v.size(); i++)
					score += ((double) 1 / (double) (i + 1)) * v.get(i);
				for (int i = 1; i <= v.size(); i++)
					sum += (double) 1 / (double) i;
				score /= sum;

				dict.put(word, score);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public double sentiScore(String word, String posTag) {
		Double score = dict.get(word+"#"+posTag);
		if (score == null) score = 0.;
		return score;
	}

	public static SentimentalAnalysis instance() {
		if (swn == null) {
			swn = new SentimentalAnalysis();
			System.out.println("size: "+swn.dict.size());
			return swn;
		} else
			return swn;
	}
	
	private BufferedReader in;
	private PrintWriter out;

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
					   String[] sentences = oneline.split("\t"); //use these as separators to separate sentences 
					   if(sentences.length > 1)
					   {
						  // System.out.println(sentences[0]);
						   double score = swn.sentiScore(sentences[0], "a");
				    	   out.println(oneline + "\t" + score);  
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
		  
		  if(in != null)
		  {
			  try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			in = null;
		  }
	}
	
	
	public void statisticalAnalysis(String infile, String outfile)
	{
		
	}
	
	
	public static void main(String[] args) throws Exception {
	//	String infile = "E:\\ReviewSpotlight\\data\\hotels\\result0\\topWordsAdjs.txt";
	//	String outfile = "E:\\ReviewSpotlight\\data\\hotels\\result0\\SentiAnalysis.txt";
	//	String infile = "E:\\ReviewSpotlight\\data\\hotels\\result1\\topWordsAdjs.txt";
	//	String outfile = "E:\\ReviewSpotlight\\data\\hotels\\result1\\SentiAnalysis.txt";
	//	String infile = "E:\\ReviewSpotlight\\data\\hotels\\result2\\topWordsAdjs.txt";
	//	String outfile = "E:\\ReviewSpotlight\\data\\hotels\\result2\\SentiAnalysis.txt";
		
	//	String infile = "E:\\ReviewSpotlight\\data\\hotels\\result3\\topWordsAdjs.txt";
	//	String outfile = "E:\\ReviewSpotlight\\data\\hotels\\result3\\SentiAnalysis.txt";
		
	//	String infile = "E:\\ReviewSpotlight\\data\\hotels\\result4\\topWordsAdjs.txt";
	//	String outfile = "E:\\ReviewSpotlight\\data\\hotels\\result4\\SentiAnalysis.txt";
		
	//	String infile = "E:\\ReviewSpotlight\\data\\hotels\\result7\\topWordsAdjs.txt";
	//	String outfile = "E:\\ReviewSpotlight\\data\\hotels\\result7\\SentiAnalysis.txt";
		
		String root = "D:\\Research\\ReviewSpotlight\\data\\hotels\\";
		
		String infile = root + "result7\\topWordsAdjs.txt";
		String outfile = root + "result7\\SentiAnalysis.txt";
		
		SentimentalAnalysis.instance();
		SentimentalAnalysis.swn.Initialize(infile, outfile);
		SentimentalAnalysis.swn.process();
		
		System.out.println("Done!");
	}
}