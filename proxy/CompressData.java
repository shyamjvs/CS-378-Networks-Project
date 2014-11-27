package proxy;

import java.io.*;
import java.util.zip.*;

public class CompressData{

	public static String compress(String str){
		if (str == null || str.length() == 0){
	        	return str;
		}

		String compressed = "";	
		try{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes());
			gzip.close();
			compressed = out.toString("ISO-8859-1");
		} catch(IOException e){
			System.out.println("Exception in CompressData: "+e);
		}
		return compressed;
	}

	/*
	public static void main(String[] args) throws IOException{
		String string = "aaafnfklfnnfnfnfkclcdndndndddcvlcllcmvcmvc'sdmv;nsdfvndfvnl;djfvnkdjfvn.djfvn";
		System.out.println("after compress:");
		System.out.println(CompressData.compress(string));
	}
	*/
}
