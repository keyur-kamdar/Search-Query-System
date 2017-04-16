package csulb.edu.SearchEngine.Classes;
import static java.lang.Math.log;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
class TermDocFreq implements Comparable {
	public int docId;
	
	public List<Integer> lst;
	
	public TermDocFreq(int did, List<Integer> l) {
		docId = did;
		lst=l;
	}
	
	public int compareTo(Object other) {
		TermDocFreq o = (TermDocFreq)other;
		
		if (lst.size() < o.lst.size())
			return 1;
		if (lst.size() > o.lst.size())
			return -1;
		return 0;
	}
}


/**
Writes an inverted indexing of a directory to disk.
*/
public class IndexWriter {

   private String mFolderPath;

   /**
   Constructs an IndexWriter object which is prepared to index the given folder.
   */
   public IndexWriter(String folderPath) {
      mFolderPath = folderPath;
   }

   /**
   Builds and writes an inverted index to disk. Creates three files: 
   vocab.bin, containing the vocabulary of the corpus; 
   postings.bin, containing the postings list of document IDs;
   vocabTable.bin, containing a table that maps vocab terms to postings locations
   docWeights.bin
 * @throws IOException 
   */
   public void buildIndex() throws IOException {
      buildIndexForDirectory(mFolderPath);
   }

   /**
   Builds the normal NaiveInvertedIndex for the folder.
 * @throws IOException 
   */
   private static void buildIndexForDirectory(String folder) throws IOException {
      //NaiveInvertedIndex index = new NaiveInvertedIndex();
	   
	   PositionalInvertedIndex objPos=new PositionalInvertedIndex();

	// at this point, "index" contains the in-memory inverted index 
      // now we save the index to disk, building three files: the postings index,
      // the vocabulary list, and the vocabulary table.

      // the array of terms
      String[] dictionary = objPos.getDictionary();
      // an array of positions in the vocabulary file
      long[] vocabPositions = new long[dictionary.length];

      buildVocabFile(folder, dictionary, vocabPositions);
      if(Properties.VariableByteEncode)
      {
    	  buildVariableByte_PostingsFile(folder,objPos ,dictionary, vocabPositions);
      }
      else if(Properties.Impactorder)
      {
    	  buildPostingsFile_ImpactOrdering(folder,objPos ,dictionary, vocabPositions);
      }
      else
      {
    	  buildPostingsFile(folder,objPos ,dictionary, vocabPositions);
      }
      
      
      buildDocWeightsFile(folder,dictionary);  
      
      
	}

   private static void buildPostingsFile_ImpactOrdering(String folder,PositionalInvertedIndex objPos,
		    String[] dictionary, long[] vocabPositions) {
			   
		      FileOutputStream postingsFile = null;
		      try {
		         postingsFile = new FileOutputStream(new File(folder, "Impact_postings.bin"));

		         // simultaneously build the vocabulary table on disk, mapping a term index to a
		         // file location in the postings file.
		         FileOutputStream vocabTable = new FileOutputStream(new File(folder, "Impact_vocabTable.bin"));

		         // the first thing we must write to the vocabTable file is the number of vocab terms.
		         byte[] tSize = ByteBuffer.allocate(4).putInt(dictionary.length).array();
		         vocabTable.write(tSize, 0, tSize.length);
		         int vocabI = 0;
		         for (String s : dictionary) {
		            // for each String in dictionary, retrieve its postings.
		        	LinkedHashMap<Integer,List<Integer>> postings = objPos.getPostings(s);

		        	PriorityQueue<TermDocFreq> impactOrderQueue = new PriorityQueue<TermDocFreq>();
		            for(Integer key : postings.keySet()){
		            	impactOrderQueue.add(new TermDocFreq(key, postings.get(key)));
		    		}
		            
		            // write the vocab table entry for this term: the byte location of the term in the vocab list file,
		            // and the byte location of the postings for the term in the postings file.
		            byte[] vPositionBytes = ByteBuffer.allocate(8).putLong(vocabPositions[vocabI]).array();
		            vocabTable.write(vPositionBytes, 0, vPositionBytes.length);

		            byte[] pPositionBytes = ByteBuffer.allocate(8).putLong(postingsFile.getChannel().position()).array();
		            vocabTable.write(pPositionBytes, 0, pPositionBytes.length);

		            // write the postings file for this term. first, the document frequency for the term, then
		            // the document IDs, encoded as gaps.
		            byte[] docFreqBytes = ByteBuffer.allocate(4).putInt(postings.size()).array();
		            postingsFile.write(docFreqBytes, 0, docFreqBytes.length);
		            if(s.contains("angel"))
		            {
		            	System.out.println();
		            }
		            
		            
		            System.out.println(s);
		            TermDocFreq objTermDocFreq=null;
		            int len=impactOrderQueue.size();
		            for(int i=0;i<len;i++)
		            {
		            	objTermDocFreq = impactOrderQueue.remove();
		            	byte[] docIdBytes = ByteBuffer.allocate(4).putInt(objTermDocFreq.docId).array(); 
		            	postingsFile.write(docIdBytes, 0, docIdBytes.length);
		            	
		            	byte[] PosFreqBytes = ByteBuffer.allocate(4).putInt(objTermDocFreq.lst.size()).array();
		                postingsFile.write(PosFreqBytes, 0, PosFreqBytes.length);
		                
		                int lastPosId = 0;
		                for(int j=0;j<objTermDocFreq.lst.size();j++)
		                {
		                	byte[] PosIdBytes = ByteBuffer.allocate(4).putInt(objTermDocFreq.lst.get(j) - lastPosId).array();
		                	postingsFile.write(PosIdBytes, 0, PosIdBytes.length);
		                    lastPosId = objTermDocFreq.lst.get(j);
		                }

		                
		                
		            }
		            

		            vocabI++;
		         }
		         vocabTable.close();
		         postingsFile.close();
		      }
		      catch (FileNotFoundException ex) {
		      }
		      catch (IOException ex) {
		      }
		      finally {
		         try {
		            postingsFile.close();
		         }
		         catch (IOException ex) {
		         }
		      }
		   }
   
   
	private static void buildDocWeightsFile(String folder,String[] dictionary) throws IOException {
		byte[] docWeight;
		float[] tempDocWeight=new float[SimpleEngine.docWeight.size()];
		
		FileOutputStream docWeights = null;
		
		for(int i=0;i<SimpleEngine.docWeight.size();i++)
		{
			tempDocWeight[i]=SimpleEngine.docWeight.get(i);
		}
		
		try {
			docWeights = new FileOutputStream(new File(folder, "docWeights.bin"));
			
				for(int i=0;i<tempDocWeight.length;i++)
				{
					docWeight = ByteBuffer.allocate(4).putFloat(tempDocWeight[i]).array();
					docWeights.write(docWeight, 0, docWeight.length);
				}
			 
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
   

   /**
   Builds the postings.bin file for the indexed directory, using the given
   NaiveInvertedIndex of that directory.
   */
   private static void buildPostingsFile(String folder,PositionalInvertedIndex objPos,
    String[] dictionary, long[] vocabPositions) {
	   
      FileOutputStream postingsFile = null;
      try {
         postingsFile = new FileOutputStream(new File(folder, "postings.bin"));

         // simultaneously build the vocabulary table on disk, mapping a term index to a
         // file location in the postings file.
         FileOutputStream vocabTable = new FileOutputStream(new File(folder, "vocabTable.bin"));

         // the first thing we must write to the vocabTable file is the number of vocab terms.
         byte[] tSize = ByteBuffer.allocate(4).putInt(dictionary.length).array();
         vocabTable.write(tSize, 0, tSize.length);
         int vocabI = 0;
         for (String s : dictionary) {
            // for each String in dictionary, retrieve its postings.
        	 LinkedHashMap<Integer,List<Integer>> postings = objPos.getPostings(s);

            // write the vocab table entry for this term: the byte location of the term in the vocab list file,
            // and the byte location of the postings for the term in the postings file.
            byte[] vPositionBytes = ByteBuffer.allocate(8).putLong(vocabPositions[vocabI]).array();
            vocabTable.write(vPositionBytes, 0, vPositionBytes.length);

            byte[] pPositionBytes = ByteBuffer.allocate(8).putLong(postingsFile.getChannel().position()).array();
            vocabTable.write(pPositionBytes, 0, pPositionBytes.length);

            // write the postings file for this term. first, the document frequency for the term, then
            // the document IDs, encoded as gaps.
            byte[] docFreqBytes = ByteBuffer.allocate(4).putInt(postings.size()).array();
            postingsFile.write(docFreqBytes, 0, docFreqBytes.length);
            
            System.out.println(s);
            
            for(Integer doc: postings.keySet())
            {
            	
            	//d=postings.get(i);
            	byte[] docIdBytes = ByteBuffer.allocate(4).putInt(doc).array(); 
            	postingsFile.write(docIdBytes, 0, docIdBytes.length);
            	
            	byte[] PosFreqBytes = ByteBuffer.allocate(4).putInt(postings.get(doc).size()).array();
                postingsFile.write(PosFreqBytes, 0, PosFreqBytes.length);
            	
            	int lastPosId = 0;
            	for(int j=0;j<postings.get(doc).size();j++)
            	{
            		byte[] PosIdBytes = ByteBuffer.allocate(4).putInt(postings.get(doc).get(j) - lastPosId).array(); 

                    postingsFile.write(PosIdBytes, 0, PosIdBytes.length);
                    lastPosId = postings.get(doc).get(j);
            	}
            }
           
           

            vocabI++;
         }
         vocabTable.close();
         postingsFile.close();
      }
      catch (FileNotFoundException ex) {
      }
      catch (IOException ex) {
      }
      finally {
         try {
            postingsFile.close();
         }
         catch (IOException ex) {
         }
      }
   }
   /**
   Builds the Variable_postings.bin file for the indexed directory, using the given
   NaiveInvertedIndex of that directory.
   */
   
   private static byte[] encodeNumber(float n) 
   {
	   int i;
       if (n == 0) 
       {
           //return new byte[]{0};
    	   i = (int) (log(1) / log(128)) + 1;
       }
       else 
       {
    	   i = (int) (log(n) / log(128)) + 1;
       }
       
 
       
       byte[] rv = new byte[i];
       
       int j = i - 1;
       do 
       {
           rv[j--] = (byte) (n % 128);
           n /= 128;
       } while (j >= 0);
       
       rv[i - 1] += 128;
       
       return rv;
   }

   
   
   private static void buildVariableByte_PostingsFile(String folder,PositionalInvertedIndex objPos,
		    String[] dictionary, long[] vocabPositions) {
			   
		      FileOutputStream postingsFile = null;
		      try {
		         postingsFile = new FileOutputStream(new File(folder, "Variable_postings.bin"));

		         // simultaneously build the vocabulary table on disk, mapping a term index to a
		         // file location in the postings file.
		         FileOutputStream vocabTable = new FileOutputStream(new File(folder, "Variable_vocabTable.bin"));

		         // the first thing we must write to the vocabTable file is the number of vocab terms.
		    
		         
		         
		         byte[] tSize = encodeNumber(dictionary.length);
		         
		         vocabTable.write(tSize, 0, tSize.length);
		         int vocabI = 0;
		         for (String s : dictionary) 
		         {
		        	
		            // for each String in dictionary, retrieve its postings.
		        	 LinkedHashMap<Integer,List<Integer>> postings = objPos.getPostings(s);

		            // write the vocab table entry for this term: the byte location of the term in the vocab list file,
		            // and the byte location of the postings for the term in the postings file.
		            

		        	
		        	byte[] vPositionBytes = encodeNumber((int)vocabPositions[vocabI]);
		            vocabTable.write(vPositionBytes, 0, vPositionBytes.length);

		            
		            byte[] pPositionBytes = encodeNumber((int)postingsFile.getChannel().position());
		            vocabTable.write(pPositionBytes, 0, pPositionBytes.length);

		            // write the postings file for this term. first, the document frequency for the term, then
		            // the document IDs, encoded as gaps.

		            byte[] docFreqBytes = encodeNumber(postings.size());
		            postingsFile.write(docFreqBytes, 0, docFreqBytes.length);
		            
		            

		            for(Integer doc: postings.keySet())
		            {

		            	
		            
		            	byte[] docIdBytes = encodeNumber(doc);
		            	postingsFile.write(docIdBytes, 0, docIdBytes.length);
		            	

		            	byte[] PosFreqBytes = encodeNumber(postings.get(doc).size());
		                postingsFile.write(PosFreqBytes, 0, PosFreqBytes.length);
		            	
		            	int lastPosId = 0;
		            	for(int j=0;j<postings.get(doc).size();j++)
		            	{
		            
		            		byte[] PosIdBytes = encodeNumber(postings.get(doc).get(j) - lastPosId);
		            		
		                    postingsFile.write(PosIdBytes, 0, PosIdBytes.length);
		                    lastPosId = postings.get(doc).get(j);
		            	}
		            	
		            }
		           

		            vocabI++;
		         }
		         vocabTable.close();
		         postingsFile.close();
		      }
		      catch (FileNotFoundException ex) {
		      }
		      catch (IOException ex) {
		      }
		      finally {
		         try {
		            postingsFile.close();
		         }
		         catch (IOException ex) {
		         }
		      }
		   }

   private static void buildVocabFile(String folder, String[] dictionary,
    long[] vocabPositions) {
      OutputStreamWriter vocabList = null;
      try {
         // first build the vocabulary list: a file of each vocab word concatenated together.
         // also build an array associating each term with its byte location in this file.
         int vocabI = 0;
         vocabList = new OutputStreamWriter(
          new FileOutputStream(new File(folder, "vocab.bin")), "ASCII"
         );
         
         int vocabPos = 0;
         for (String vocabWord : dictionary) {
            // for each String in dictionary, save the byte position where that term will start in the vocab file.
            vocabPositions[vocabI] = vocabPos;
            vocabList.write(vocabWord); // then write the String
            vocabI++;
            vocabPos += vocabWord.length();
         }
      }
      catch (FileNotFoundException ex) {
         System.out.println(ex.toString());
      }
      catch (UnsupportedEncodingException ex) {
         System.out.println(ex.toString());
      }
      catch (IOException ex) {
         System.out.println(ex.toString());
      }
      finally {
         try {
            vocabList.close();
         }
         catch (IOException ex) {
            System.out.println(ex.toString());
         }
      }
   }



}
