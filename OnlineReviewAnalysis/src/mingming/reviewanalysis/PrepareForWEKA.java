package mingming.reviewanalysis;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PrepareForWEKA {

	public void Process(String BasicDir, String inputfilename, String outputfilename)
	{
		String outputfile = BasicDir + outputfilename;

		PrintWriter out = null; 

		try {
			out = new PrintWriter(new FileWriter(outputfile,true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
}
