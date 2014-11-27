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
		// TODO Auto-generated method stub
	    /**
	     * Reads the all lines from a file and places it a String array. In each 
	     * record in the String array we store a training example text.
	     * 
	     * @param url
	     * @return
	     * @throws IOException 
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
	     * 
	     * @param args the command line arguments
	     * @throws java.io.IOException
	     */
	    public static NaiveBayes trainData() throws IOException {
	 
	    	
	    	Map<String, String[]> trainingExamples = new HashMap<>();
	 	    	
	    	
	    	trainingExamples.put("Porn", data.Porn);
	    	trainingExamples.put("Pure", data.Pure);

	    	nb = new NaiveBayes();
	        nb.setChisquareCriticalValue(6.63); //0.01 pvalue
	        nb.train(trainingExamples);
	        
	        //get trained classifier knowledgeBase
	        NaiveBayesKnowledgeBase knowledgeBase = nb.getKnowledgeBase();
	        
	        
	        //Use classifier
	        nb  = new NaiveBayes(knowledgeBase);
	        return  nb;
	      
	        
	        //String outputEn = nb.predict(exampleEn);
	        //System.out.format("The sentense \"%s\" was classified as \"%s\".%n", "output", outputEn);
	        /*
	        String exampleFr = "Je suis FranÃ§ais";
	        String outputFr = nb.predict(exampleFr);
	        System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleFr, outputFr);
	        
	        String exampleDe = "Ich bin Deutsch";
	        String outputDe = nb.predict(exampleDe);
	        System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleDe, outputDe);
	        */

	    }
	}

