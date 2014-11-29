package proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Run {
		
	
		static NaiveBayes nb;
	    /**
	     * Reads the all lines from a file and places it a String array. In each 
	     * record in the String array we store a training example text.
	     */
	    public static String[] readLines(URL url) throws IOException {

	        Reader fileReader = new InputStreamReader(url.openStream(), Charset.forName("UTF-8"));
	        List<String> lines;
	        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
	            lines = new ArrayList<>();
	            String line;
	            while ((line = bufferedReader.readLine()) != null) {
	                lines.add(line);
	            }
	        }
	        return lines.toArray(new String[lines.size()]);
	    }
	    
	    /**
	     * Main method
	     */
	    public static NaiveBayes trainData(String type) throws IOException {
	 
	    	
	    	Map<String, String[]> trainingExamples = new HashMap<>();
	 	    	
	    	if(type.equals("link"))
	    	{	
		    	trainingExamples.put("explicitContent", data.explicitContentLink);
		    	trainingExamples.put("acceptable", data.acceptableLink);
	    	}

	    	if(type.equals("text"))
	    	{	
		    	trainingExamples.put("explicitContent", data.explicitContentText);
		    	trainingExamples.put("acceptable", data.acceptableText);
	    	}
	    	
	    	nb = new NaiveBayes();
	        nb.setChisquareCriticalValue(6.63); //0.01 pvalue
	        nb.train(trainingExamples);
	        
	        //get trained classifier knowledgeBase
	        NaiveBayesKnowledgeBase knowledgeBase = nb.getKnowledgeBase();
	        
	        
	        //Use classifier
	        nb  = new NaiveBayes(knowledgeBase);
	        return  nb;

	    }
	}


