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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.csvreader.CsvReader;
/**
 * @author mingming
 *
 */
public class ParseHTML {
	
	HashMap<String, Boolean> linksMap;
	
	public ParseHTML(HashMap<String, Boolean> _linksMap)
	{
		linksMap = _linksMap;
	}
	

	/**
	 * get URLs for business
	 * */
	public void getBizURLs(String html, String startStr, String outputfile){
		Document doc;
		try{
			System.out.println("Begin parsing!");
			doc = Jsoup.connect(html).get();
			
			PrintWriter out;
			File file = new File(outputfile);
			file.getParentFile().mkdir();
			out = new PrintWriter(new FileWriter(file));  //true; append   ;  false: overwrite
			
			Elements links = doc.select("a");
			String relHref, absHref;
			int len = startStr.length();
			//System.out.println(len);
			HashMap<String, Boolean> linksMap = new HashMap<String,Boolean>();
			for(int i = 0; i < links.size();i++)
			{
				relHref = links.get(i).attr("href");
				absHref = links.get(i).attr("abs:href");
				System.out.println(Math.min(len, relHref.length()));
				//out.println(relHref.substring(0, Math.min(len, relHref.length())) + "," + startStr);
				if(relHref.substring(0, Math.min(len, relHref.length())).equals(startStr)) // search for matched URLs
				{
					if(!linksMap.containsKey(absHref)) // guarantee there is no duplication
					{
						linksMap.put(absHref, new Boolean(true));
						out.println(absHref);
					}
				}
				//out.println(relHref);
			}
			
			if(out != null)
			{
				out.close();
				out = null;
			}
			
			System.out.println("Finish parsing!");
		}
		catch(Exception e)
		{
			
		}
	}
	
	
	/**
	 * get biz URLs and reviews numbers
	 * */
	public void getBizURLsReviewsNumber(String html, String startStr, String outputfile)
	{
		Document doc = null;
		try{
			System.out.println("Begin URL and Reviews Number parsing!");
			System.out.println("html: " + html);
			
			//doc = Jsoup.connect(html).get();
			doc = Jsoup.connect(html).userAgent("Mozilla").get();
			
			
			PrintWriter out;
			File file = new File(outputfile);
			file.getParentFile().mkdir();
			out = new PrintWriter(new FileWriter(file, true));  //true; append   ;  false: overwrite
			
			//System.out.println("sucessfully open file to write!");			
			
			Elements links = doc.select("a");
			
			Elements reviews = doc.getElementsByClass("reviews");
			
			System.out.println("number of reviews: " + reviews.size());
			
			int counter = 0;
			String numReview,num;
			String relHref, absHref;
			int len = startStr.length();
		
		//	HashMap<String, Boolean> linksMap = new HashMap<String,Boolean>();
			for(int i = 0; i < links.size();i++)
			{
				relHref = links.get(i).attr("href");
				absHref = links.get(i).attr("abs:href");
				//System.out.println(Math.min(len, relHref.length()));
				//out.println(relHref.substring(0, Math.min(len, relHref.length())) + "," + startStr);
				if(relHref.substring(0, Math.min(len, relHref.length())).equals(startStr)) // search for matched URLs
				{
					if(!linksMap.containsKey(absHref)) // guarantee there is no duplication
					{
						linksMap.put(absHref, new Boolean(true));
						numReview = reviews.get(counter).text();
						num = numReview.split(" ")[0];
						out.println(num + ","+absHref);
						counter++;
					}
				}
				//out.println(relHref);
			}
			
			if(out != null)
			{
				out.close();
				out = null;
			}
			
			System.out.println("Finish URL and Reviews Number Parsing!");
		}
		catch(Exception e)
		{
			
		}
	}
	
	
	/**
	 * grab specific biz by given how many number of pages 
	 * */
	public void grabSpecificBiz(String basicURL,  int startID, int urlPerPage, int numPages, String startStr, String outputfile)
	{
		for(int i = startID; i < urlPerPage * numPages; i+=urlPerPage)
		{
			getBizURLsReviewsNumber(basicURL+i,  startStr,  outputfile);
		}
	}
	
	/**
	 * for a specific biz: grab reivews
	 * */
	public void getReivews4OneBiz(String html, String htmlElement,  String outputfile)
	{
		Document doc;
		try {			
			doc = Jsoup.connect(html).get();

			//get review part		
		    Elements myin = doc.getElementsByClass(htmlElement);
				
		   // System.out.println("Num of review: " + myin.size());
			PrintWriter out;
			File file = new File(outputfile);
			file.getParentFile().mkdir();
			out = new PrintWriter(new FileWriter(file,true));
			 
			int i = 1;
			String linkText;
			for (Element link : myin) {
			  linkText = link.text();
			  out.println(linkText);
			  //System.out.println("text " + i + " :" + linkText);
			  i++;
			}
				
			if(out != null)
			{
				out.close();
				out = null;
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	static int fileID = 1;
	public void getReivews4AllBiz(String inputfile, String basicOutputdir, int _startId, int _ReviewPerPage)
	{
		try {
			CsvReader hotelsdata = new CsvReader(inputfile);
			hotelsdata.readHeaders();
			//int ccc = 1;
			//while(ccc <= 3)
			while(hotelsdata.readRecord())
			{
				//ccc++;
				//hotelsdata.readRecord();
				String ReviewNumber = hotelsdata.get("ReviewNumber");
				String URL = hotelsdata.get("URL");
				
				//System.out.println(ReviewNumber + "\t" + URL);
				String html = URL + "?start="; 
				String htmlElement = "review_comment";
				String outputfile1 = basicOutputdir + fileID + "\\reviewfile1.txt";
				
				int startId = _startId; //start review page ID
				int inc = _ReviewPerPage; // number of reviews per page
				int pages = (int)(Integer.parseInt(ReviewNumber)/inc) + 1; // how many pages 
				
				for(int i = startId; i < inc*pages; i+= inc)
				{
					getReivews4OneBiz(html+i,htmlElement,outputfile1);
				}
				
				fileID++;
				
			}
			
			PrintWriter out2;
			File file = new File(basicOutputdir+"\\fileID.txt");
			file.getParentFile().mkdir();
			out2 = new PrintWriter(new FileWriter(file));
			out2.println(fileID);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * add title to the URL file
	 * */
	public void addTitle(String outputfile)
	{
		try
		{
			PrintWriter out;
			File file = new File(outputfile);
			file.getParentFile().mkdir();
			out = new PrintWriter(new FileWriter(file));
			
			out.println("ReviewNumber"+","+"URL");
			
			if(out != null)
			{
				out.close();
				out = null;
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void sentenceAnalysis(String basicOutputdir, int num)
	{
		SentenceParse sp = new SentenceParse();
		String inputfile,outputfile;
		
		for(int i= 1; i <= num; i++)
		{
			 inputfile = basicOutputdir + i + "\\reviewfile1.txt";
			 outputfile = basicOutputdir + i + "\\sentence1.txt";
			 sp.Initialize(inputfile, outputfile);
			 sp.process();
		}

	}
	
	
	public void sentenceAnalysis(String basicOutputdir)
	{
		System.out.println("Begin Sentence Analysis...");
		int AllFileNUM = 1; 
		//read file number
		try{
			BufferedReader in = new BufferedReader(new FileReader(basicOutputdir+"fileID.txt"));
			
			if(in != null)
			{
				try
				{
					String oneline = in.readLine();
					if(oneline != null)
					{
						AllFileNUM = Integer.parseInt(oneline);
						System.out.println("AllFileNUM: "+AllFileNUM);
					}
				}catch(Exception e)
				{}
			}
			else
			{
				System.out.println("buffered reader is null...");
			}
			
			if(in != null)
			{
				in.close();
				in = null;
			}
		}
		catch(Exception e)
		{
			
		}
		/*
		SentenceParse sp = new SentenceParse();
		String inputfile,outputfile;
		AllFileNUM = 30;
		for(int i= 1; i <= AllFileNUM; i++)
		{
			 inputfile = basicOutputdir + i + "\\reviewfile1.txt";
			 outputfile = basicOutputdir + i + "\\sentence1.txt";
			 sp.Initialize(inputfile, outputfile);
			 sp.process();
		}
		*/
		System.out.println("After Sentence Analysis...");
	}
	
	
	public static void main(String[] args) throws Exception {
		/*
		// test one: parse one page of specific biz in yelp
		String html = "http://www.yelp.com/search?find_desc=spa&find_loc=Irvine%2C+CA&ns=1#!";   // starting html format
		String outputfile = "D:\\Research\\ReviewSpotlight\\2013_April_Research\\data\\spa_irvine_url_list.csv";
		String startStr = "/biz";
		ParseHTML et = new ParseHTML();
        
		//et.getBizURLs(html,startStr, outputfile);
		et.getBizURLsReviewsNumber(html, startStr, outputfile);
		*/
		
		//test two: parse several pages of specific biz in yelp: get URL of that biz and num of reviews
		String basicURL = "http://www.yelp.com/search?find_desc=&find_loc=Los+Angeles%2C+CA&ns=1&ls=ac78b67876639b43#find_desc=spa&show_filters=1&start=";//"http://www.yelp.com/search?find_desc=spa&find_loc=Irvine%2C+CA&ns=1#rpp=40&start=";
		String basicOutputdir = "D:\\Research\\ReviewSpotlight\\2013_April_Research\\data\\data\\";
		String outputfile =  basicOutputdir + "spa_irvine_url_list.csv";
        String startStr = "/biz";
        
		HashMap<String, Boolean> linksMap = new HashMap<String,Boolean>();
		ParseHTML et = new ParseHTML(linksMap);
		
		int startID = 0;
		int urlPerPage = 40;
		int numPages = 2;
		
		System.out.println("Parse HTML class testing");
		
		
		et.addTitle(outputfile);
		et.grabSpecificBiz(basicURL, startID, urlPerPage, numPages, startStr, outputfile);
		
		//for each line of above output file, grab reviews and store in separate files
		//et.getReivews4AllBiz(outputfile, basicOutputdir,startID,urlPerPage);
		
		//analyze sentences from text
		//et.sentenceAnalysis(basicOutputdir);
	}
}
