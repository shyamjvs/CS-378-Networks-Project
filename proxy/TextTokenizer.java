/* 
 * Copyright (C) 2014 Vasilis Vryniotis <bbriniotis at datumbox.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package proxy;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * TextTokenizer class used to tokenize the texts and store them as Document
 * objects.
 */
public class TextTokenizer {
    
    /**
     * Preprocess the text by removing punctuation, duplicate spaces and 
     * lowercasing it. 
     */
    public static String preprocess(String text) {
        return text.replaceAll("\\p{P}", " ").replaceAll("\\s+", " ").toLowerCase(Locale.getDefault());
    }
    
    /**
     * A simple method to extract the keywords from the text. For real world 
     * applications it is necessary to extract also keyword combinations.
     */
    public static String[] extractKeywords(String text) {
        return text.split(" ");
    }
    
    /**
     * Counts the number of occurrences of the keywords inside the text.
     */
    public static Map<String, Integer> getKeywordCounts(String[] keywordArray) {
        Map<String, Integer> counts = new HashMap<>();
        
        Integer counter;
        for(int i=0;i<keywordArray.length;++i) {
            counter = counts.get(keywordArray[i]);
            if(counter==null) {
                counter=0;
            }
            counts.put(keywordArray[i], ++counter); //increase counter for the keyword
        }
        
        return counts;
    }
    
    /**
     * Tokenizes the document and returns a Document Object.
     */
    public static Document tokenize(String text) {
        String preprocessedText = preprocess(text);
        String[] keywordArray = extractKeywords(preprocessedText);
        
        Document doc = new Document();
        doc.tokens = getKeywordCounts(keywordArray);
        return doc;
    }
}
