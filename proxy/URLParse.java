package proxy;

import java.io.*;
import java.util.*;

public class URLParse {

	private Map<String, String> postParameters = new HashMap<String, String>();	
	private String[] requestTriple = new String[3];
	private String URLParameters = "";

	URLParse(List<String> input){
		try{
			
			String[] lines = input.toArray(new String[input.size()]);
			//System.out.println("yo bro: "+lines[0]);
			requestTriple = lines[0].split(" ");

			int i = 1;
			while(i<input.size() && !lines[i].equals("")){
				System.out.println("yo bro: "+lines[1]);
				String[] line = lines[i].split(": ");
				postParameters.put(line[0], line[1]);
				i++;
			}
			System.out.println("Came out of while loop in URLParse");
			i++;
			if(this.getRequestType().equals("POST") && i<input.size()){
				System.out.println("Type of the request is; "+this.getRequestType());
				URLParameters = lines[i];
				System.out.println("Type2 of the request is; "+this.getRequestType());
			}
			System.out.println("url parameters are: "+URLParameters);
			System.out.println("Came out of without error");

		} catch (Exception e){
			System.out.println("URLParser exception: "+ e);
		}
	}

	public String getRequestType(){
		return requestTriple[0];
	}

	public void setRequestURL(String url){
		requestTriple[1] = url;
	}

	public String getRequestURL(){
		return requestTriple[1];
	}

	public String getRequestProtocol(){
		return requestTriple[2];
	}

	public String[] getRequestTriple(){
		return requestTriple;
	}
	
	public Map<String,String> getPostParameters(){
		return postParameters;
	}
	
	public String getURLParameters(){
		return URLParameters;
	}
}
