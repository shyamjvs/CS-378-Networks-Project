package proxy;

import java.io.IOException;
import java.util.HashMap;

public class data {

	
	static int len = 1;
	//HashMap<String,>
	
	static NaiveBayes nb_link ;
	static NaiveBayes nb_text ;
	
	static String [] explicitContentLink = new String[len];
	static String [] acceptableLink = new String[len];

	static String [] explicitContentText = new String[len];
	static String [] acceptableText = new String[len];

	
	static HashMap<String,String[]> Exceptions =  new HashMap<String,String[]>();
	
	
	
	data()
	{
		explicitContentLink[0] = "porn sex cunt";
		explicitContentText[0] = "porn sex cunt";
		acceptableLink[0] = "Acads ";
		acceptableText[0] = "Acads ";
        try 
        {
			nb_link = Run.trainData("link");
			nb_text = Run.trainData("text");	
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
	}	
}