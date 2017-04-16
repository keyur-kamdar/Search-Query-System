package csulb.edu.SearchEngine.Classes;

import java.util.regex.*;

public class PorterStemmer {

   // a single consonant
   private static final String c = "[^aeiou]";
   // a single vowel
   private static final String v = "[aeiouy]";

   // a sequence of consonants; the second/third/etc consonant cannot be 'y'
   private static final String C = c + "[^aeiouy]*";
   // a sequence of vowels; the second/third/etc cannot be 'y'
   private static final String V = v + "[aeiou]*";

   // this regex pattern tests if the token has measure > 0 [at least one VC].
   private static final Pattern mmGr0 = Pattern.compile("^(" + C + ")?" + V + C);
   private static final Pattern mGr0 = Pattern.compile("^(" + C + ")?" +"("+ V + C+")+"+"("+V+")?");

   // add more Pattern variables for the following patterns:
   // m equals 1: token has measure == 1
   private static final Pattern mEq1=Pattern.compile("^("+C+")?"+V+C+"("+V+")?$");
   
   // m greater than 1: token has measure > 1
   private static final Pattern mGr1=Pattern.compile("^("+C+")?"+"("+V+C+")+"+V+C+"("+V+")?");
   
   
   
   //private static final Pattern V_after_C=Pattern.compile("^("+C+")"+V+"[a-z]*");
   
   // double consonant: token ends in two consonants that are the same, 
   //unless they are L, S, or Z. (look up "backreferencing" to help with this)
   
   private static final Pattern double_C=Pattern.compile("^\\b\\w*(["+C+"&&[^lsz]])\\1\\b$");
   
   // m equals 1, Cvc: token is in Cvc form, where the last c is not w, x or y.
   private static final Pattern meq1_lastC=Pattern.compile("^("+c+")+"+v+"["+c+"&&[^wxy]]+$");
   
   //Regular expression for string ends with 's'
   private static final Pattern remove_suffix_s=Pattern.compile("^\\b\\w*(.[[a-z]&&[^s]])[s]\\b$");
   
   //Regular expression for string contains vowel end with "ed"
   private static final Pattern string_With_Vowel_EndWith_ed=Pattern.compile("^\\b\\w*"+ V +"\\w*"+"[e][d]\\b$");

   //Regular expression for string contains vowel end with "ing"
   private static final Pattern string_With_Vowel_EndWith_ing=Pattern.compile("^\\b\\w*"+ V +"\\w*"+"[i][n][g]\\b$");
   
   //Regular expression for string contains vowel end with "ing"
   private static final Pattern string_With_Vowel_EndWith_y=Pattern.compile("^\\b\\w*"+ V +"\\w*"+"[y]\\b$");
   private static final Pattern hyphenated_String=Pattern.compile("^\\w*[-]\\w*$");
   
   
   public static String processToken(String token) {
      if (token.length() < 3) {
         return token; // token must be at least 3 chars
      }
      
      // program the other steps in 1a. 
      // note that Step 1a.3 implies that there is only a single 's' as the 
      //	suffix; ss does not count. you may need a regex pattern here for 
      // "not s followed by s".

      // step 1a.1
      if (token.endsWith("sses")) {
         token = token.substring(0, token.length() - 2);
      }
      //step 1a.2
      else if(token.endsWith("ies"))
      {
    	  token = token.substring(0, token.length() - 2);
    	  
      }
      //step 1a.3
      else if(remove_suffix_s.matcher(token).matches())
      {
    	  token=token.substring(0,token.length()-1);
      }
      
      // step 1b
      boolean doStep1bb = false;
      //		step 1b.1
      if (token.endsWith("eed")) { // 1b.1
         // token.substring(0, token.length() - 3) is the stem prior to "eed".
         // if that has m>0, then remove the "d".
         String stem = token.substring(0, token.length() - 3);
         if (mmGr0.matcher(stem).matches()) { // if the pattern matches the stem
            token = stem + "ee";
         }
      }
      //step 1b.2
      
      else if(string_With_Vowel_EndWith_ed.matcher(token).matches())
      {
    	  token=token.substring(0,token.length()-2);
    	  doStep1bb=true;
      }
      // step 1b.3
      else if(string_With_Vowel_EndWith_ing.matcher(token).matches())
      {
    	  token=token.substring(0,token.length()-3);
    	  doStep1bb=true;
      }
            
      // program the rest of 1b. set the boolean doStep1bb to true if Step 1b* 
      // should be performed.

      // step 1b*, only if the 1b.2 or 1b.3 were performed.
      if (doStep1bb) {
    	  //step 1b*.1,step 1b*.2, step 1b*.3
         if (token.endsWith("at") || token.endsWith("bl")
          || token.endsWith("iz")) {

            token = token + "e";
         }
         // use the regex patterns you wrote for 1b*.4 and 1b*.5
         //step 1b*.4
         else if(double_C.matcher(token).matches())
         {
        	 token=token.substring(0,token.length()-1);
        	 
         }
         //step 1b*.5
         
         else if(meq1_lastC.matcher(token).matches())
         {
        	         	 
        	 token=token+'e';
        	 
         }
         
      }

      // step 1c
      // program this step. test the suffix of 'y' first, then test the 
      // condition *v*.
      
      if(string_With_Vowel_EndWith_y.matcher(token).matches())
      {
    	  token=token.substring(0,token.length()-1);
    	  token=token+'i';
    	  
      }
      
      

      // step 2
      // program this step. for each suffix, see if the token ends in the 
      // suffix. 
      //		* if it does, extract the stem, and do NOT test any other suffix.
      //    * take the stem and make sure it has m > 0.
      //			* if it does, complete the step. if it does not, do not 
      //				attempt any other suffix.
      // you may want to write a helper method for this. a matrix of 
      // "suffix"/"replacement" pairs might be helpful. It could look like
      // string[][] step2pairs = {  new string[] {"ational", "ate"}, 
      //										new string[] {"tional", "tion"}, ....
      
    	  String[][] step2pairs={new String[]{"actional","ate"},new String[]{"tional","tion"},new String[]{"enci","ence"},new String[]{"anci","ance"},new String[]{"izer","ize"},new String[]{"bli","ble"},new String[]{"alli","al"},new String[]{"entli","ent"},new String[]{"eli","e"},new String[]{"ousli","ous"},new String[]{"ization","ize"},new String[]{"ation","ate"},new String[]{"ator","ate"},new String[]{"alism","al"},new String[]{"iveness","ive"},new String[]{"fulness","ful"},new String[]{"ousness","ous"},new String[]{"aliti","al"},new String[]{"aviti","ive"},new String[]{"biliti","ble"}};
          token=Remove_Suffix(token,step2pairs,mGr0);  
      
      
       
      

      // step 3
      // program this step. the rules are identical to step 2 and you can use
      // the same helper method. you may also want a matrix here.
      
    	  String[][] step3pairs={new String[]{"icate","ic"},new String[]{"ative",""},new String[]{"alize","al"},new String[]{"iciti","ic"},new String[]{"ical","ic"},new String[]{"ful",""},new String[]{"ness",""}};
          token=Remove_Suffix(token,step3pairs,mGr0);  
      
      

      // step 4
      // program this step similar to step 2/3, except now the stem must have
      // measure > 1.
      // note that ION should only be removed if the suffix is SION or TION, 
      // which would leave the S or T.
      // as before, if one suffix matches, do not try any others even if the 
      // stem does not have measure > 1.
      
    	  
      String[][] step4pairs={new String[]{"al",""},new String[]{"ance",""},new String[]{"ence",""},new String[]{"er",""},new String[]{"ic",""},new String[]{"able",""},new String[]{"ible",""},new String[]{"ant",""},new String[]{"ement",""},new String[]{"ment",""},new String[]{"ent",""},new String[]{"sion","s"},new String[]{"tion","t"},new String[]{"ou",""},new String[]{"ism",""},new String[]{"ate",""},new String[]{"iti",""},new String[]{"ous",""},new String[]{"ive",""},new String[]{"ize",""}};
      token=Remove_Suffix(token,step4pairs,mGr1);  
      
      

      // step 5
      // program this step. you have a regex for m=1 and for "Cvc", which
      // you can use to see if m=1 and NOT Cvc.
      // all your code should change the variable token, which represents
      // the stemmed term for the token.
      String stem="";
      if(token.endsWith("e"))
      {
    	  stem=token.substring(0, token.length()-1);
    	  if(mGr1.matcher(stem).matches())
    	  {
    		  
    		  token=stem;
    	  }
      }
      else if(token.endsWith("e") )
      {
    	  stem=token.substring(0, token.length()-1);
    	  if(mEq1.matcher(stem).matches() && !meq1_lastC.matcher(stem).matches())
    	  {
    		  token=stem;
    		  
    	  }
    	  
      }
      else if(token.endsWith("ll") )
      {
    	  stem=token.substring(0, token.length()-1);
    	  if(mGr1.matcher(stem).matches())
    	  {
    		  
    		  token=stem;
    	  }
      }
      
      
      
      return token;
   }
   
   private static String Remove_Suffix(String token,String[][] arr_Str,Pattern ptrn)
   {
	   
	   String stem="";
	   for(int i=0;i<arr_Str.length;i++)
	   {
		   
		   if(token.endsWith(arr_Str[i][0]))
		   {
			   stem=token.substring(0, token.length()-arr_Str[i][0].length());
			   
			   if(ptrn.matcher(stem).matches())
			   {
				   stem=stem+arr_Str[i][1];
				   token=stem;
				   
			   }
			   break;
			  
			   
		   }
	   }
	   return token;
   }
   
}
