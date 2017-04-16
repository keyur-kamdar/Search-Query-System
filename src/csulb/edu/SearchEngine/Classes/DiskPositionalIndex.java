package csulb.edu.SearchEngine.Classes;

import java.io.*;

import java.nio.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

public class DiskPositionalIndex {

   private String mPath;
   private RandomAccessFile mVocabList;
   private RandomAccessFile mPostings;
   private RandomAccessFile docWeight;
   private long[] mVocabTable;
   private List<String> mFileNames;

   public DiskPositionalIndex() {
      try {
         mPath = Properties.library_Directory_Path.toAbsolutePath().toString();
         mVocabList = new RandomAccessFile(new File(mPath, "vocab.bin"), "r");
         docWeight = new RandomAccessFile(new File(mPath, "docWeights.bin"), "r");
         
         
         if(Properties.VariableByteEncode)
         {
        	 mPostings = new RandomAccessFile(new File(mPath, "Variable_postings.bin"), "r");
        	 
        	 mVocabTable = Variable_readVocabTable(mPath);	 
        	 Properties.VariableLib=false;
         }
         else if(Properties.Impactorder)
         {
        	 mPostings = new RandomAccessFile(new File(mPath, "Impact_postings.bin"), "r");
        	 mVocabTable = Impact_readVocabTable(mPath);
        	 Properties.ImpactOrderLib=false;
         }
         else
         {
        	 mPostings = new RandomAccessFile(new File(mPath, "postings.bin"), "r");
        	 mVocabTable = readVocabTable(mPath);
        	 Properties.Lib=false;
         }
         
         mFileNames = readFileNames(mPath);
         
      }
      catch (FileNotFoundException ex) {
         System.out.println(ex.toString());
         Properties.Lib=true;
         Properties.VariableLib=true;
         Properties.ImpactOrderLib=true;
         
      }
   }
   /**
   Get Integer value from VariableByte Encode Bytes
 * @throws IOException 
   */
   private static int GetType(RandomAccessFile postings) throws IOException
   {
		

	   int n = 0;
	   byte[] buffer = new byte[1];
		while (postings.read(buffer, 0, buffer.length)>0) 
		{

			if ((buffer[0] & 0xff) < 128) 
			{
				n = 128 * n + buffer[0];
			} 
			else 
			{
				int num = (128 * n + ((buffer[0] - 128) & 0xff));
				n = 0;
				if(num==40495)
				{
					System.out.println();
				}
				return num;

			}

		}
		
		return n;
	   
	   
   }
   
   /**
   Get postings List if file encoded using Variable Byte encode
 * @throws IOException 
   */
   private static int[] Variable_readPostingsFromFile(RandomAccessFile postings, long postingsPosition) {
		      try {
		         // seek to the position in the file where the postings start.
		         postings.seek(postingsPosition);
		         
		         
		         
		         
		         
		         int documentFrequency = GetType(postings);
		         
		         // initialize the array that will hold the postings. 
		         int[] docIds = new int[documentFrequency];
		         
		         for(int i=0;i<documentFrequency;i++)
		         {
		        
		        	 docIds[i]=GetType(postings);
		        
		        /*	 int pos=GetType(postings);
		        	 for(int j=0;j<pos;j++)
		        	 {
		        		 int pos1=GetType(postings);
		        
		        	 }*/
		        	 
		        
		        	 
		         }
		         
		         
		         return docIds;
		      }
		      catch (IOException ex) {
		         System.out.println(ex.toString());
		      }
		      return null;
		   }
   
   

   /**
   Get Documents Weights
 * @throws IOException 
   */
   public float readDocWeightsFile(Integer docKey) {
		
		
		try {
			docWeight.seek(docKey*4);
			
			 byte[] buffer = new byte[4];
	         docWeight.read(buffer, 0, buffer.length);
	         
	         float documentWeight = ByteBuffer.wrap(buffer).getFloat();
	         
	         return documentWeight;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (Float)null;
  }
   
   /**
   Get Documents List
 * @throws IOException 
   */
   
   private static int[] readPostingsFromFile(RandomAccessFile postings, 
    long postingsPosition) {
      try {
         // seek to the position in the file where the postings start.
         postings.seek(postingsPosition);
         
         // read the 4 bytes for the document frequency
         byte[] buffer = new byte[4];
         postings.read(buffer, 0, buffer.length);

         // use ByteBuffer to convert the 4 bytes into an int.
         int documentFrequency = ByteBuffer.wrap(buffer).getInt();
         
         // initialize the array that will hold the postings. 
         int[] docIds = new int[documentFrequency];

         
         //System.out.print(buffer);
        
         
         for(int i=0;i<documentFrequency;i++)
         {
        	 postings.read(buffer, 0, buffer.length);
        	 docIds[i]=ByteBuffer.wrap(buffer).getInt();
        	 postings.read(buffer, 0, buffer.length);
        	 int pos=ByteBuffer.wrap(buffer).getInt();
        	 postings.skipBytes(pos*4);
        	 
         }
         
         
         
        
         
         
         return docIds;
      }
      catch (IOException ex) {
         System.out.println(ex.toString());
      }
      return null;
   }

   /**
   Get Documents list with positions
 * @throws IOException 
   */
   private static DictionaryPosition[] Variable_readPostingsAndPositionFromFile(RandomAccessFile postings, long postingsPosition) 
	{
		try 
		{
			DictionaryPosition[] lstDp;
			int count=0;
			// seek to the position in the file where the postings start.
			postings.seek(postingsPosition);

			// read the 4 bytes for the document frequency
			byte[] buffer = new byte[1];
			

			// use ByteBuffer to convert the 4 bytes into an int.
			int documentFrequency = GetType(postings);

			// initialize the array that will hold the postings.
			
			lstDp=new DictionaryPosition[documentFrequency];
			
			System.out.print(buffer);

			for (int i = 0; i < documentFrequency; i++) 
			{
				DictionaryPosition dp=new DictionaryPosition();
				
				dp.documentID = GetType(postings);
				
				int pos = GetType(postings);
				int[] lst=new int[pos];
				int lastPosID=0;
				for(int j=0;j<pos;j++)
				{
					
					lst[j]=GetType(postings)+lastPosID;
					lastPosID=lst[j];
				}
				dp.lst=lst;
				lstDp[count]=dp;
				count++;
				

			}

			return lstDp;
		} catch (IOException ex) {
			System.out.println(ex.toString());
		}
		return null;
	}
   
   /**
   Get Documents list with position for Variable Byte encoding 
 * @throws IOException 
   */
	private static DictionaryPosition[] readPostingsAndPositionFromFile(RandomAccessFile postings, long postingsPosition) 
	{
		try 
		{
			DictionaryPosition[] lstDp;
			int count=0;
			// seek to the position in the file where the postings start.
			postings.seek(postingsPosition);

			// read the 4 bytes for the document frequency
			byte[] buffer = new byte[4];
			postings.read(buffer, 0, buffer.length);

			// use ByteBuffer to convert the 4 bytes into an int.
			int documentFrequency = ByteBuffer.wrap(buffer).getInt();

			// initialize the array that will hold the postings.
			
			lstDp=new DictionaryPosition[documentFrequency];
			
			

			for (int i = 0; i < documentFrequency; i++) 
			{
				DictionaryPosition dp=new DictionaryPosition();
				postings.read(buffer, 0, buffer.length);
				dp.documentID = ByteBuffer.wrap(buffer).getInt();
				postings.read(buffer, 0, buffer.length);
				int pos = ByteBuffer.wrap(buffer).getInt();
				int[] lst=new int[pos];
				int lastPosID=0;
				for(int j=0;j<pos;j++)
				{
					postings.read(buffer, 0, buffer.length);
					lst[j]=ByteBuffer.wrap(buffer).getInt()+lastPosID;
					lastPosID=lst[j];
				}
				dp.lst=lst;
				lstDp[count]=dp;
				count++;
				

			}

			return lstDp;
		} catch (IOException ex) {
			System.out.println(ex.toString());
		}
		return null;
	}   
	
	/**
	   Get Documents List
	 * @throws IOException 
	   */
   public int[] GetPostings(String term) 
   {
	   long postingsPosition;
	   postingsPosition = BinarySearchVocabulary(term);
	   
	         
	   
	   
      
      if (postingsPosition >= 0) 
      {
    	  if(Properties.VariableByteEncode)
    	  {
    		  return Variable_readPostingsFromFile(mPostings, postingsPosition);
    	  }
    	  
    	  else
    	  {
    		  return readPostingsFromFile(mPostings, postingsPosition);
    	  }
         
      }
      return null;
   }
   
   
   /**
   Get Documents list with Positions 
 * @throws IOException 
   */
   public DictionaryPosition[] GetPostingsWithPosition(String term) 
   {
	      long postingsPosition;
	      postingsPosition = BinarySearchVocabulary(term);
	      
	   
	   
	      if (postingsPosition >= 0) 
	      {
	    	  if(Properties.VariableByteEncode)
	    	  {
	    		  
	    		  return Variable_readPostingsAndPositionFromFile(mPostings, postingsPosition);
	    	  }
	    	  else
	    	  {
	    		  
	    		  return readPostingsAndPositionFromFile(mPostings, postingsPosition);	  
	    	  }
	         
	      }
	      return null;
	   }

   
   /**
   Binary Search for the word
 * @throws IOException 
   */
   private long BinarySearchVocabulary(String term) {
      // do a binary search over the vocabulary, using the vocabTable and the file vocabList.
	  
      int i = 0, j = mVocabTable.length / 2 - 1;
      while (i <= j) 
      {
         try 
         {
            int m = (i + j) / 2;
            long vListPosition = mVocabTable[m * 2];
            int termLength =0;
            if((m+1)*2>=mVocabTable.length)
            {
            	termLength=(int) (mVocabList.length()-mVocabTable[m*2]);
            }
            else
            {
            	termLength = (int) (mVocabTable[(m + 1) * 2] - vListPosition);
            }
      
            mVocabList.seek(vListPosition);
            
            byte[] buffer = new byte[termLength];
            mVocabList.read(buffer, 0, termLength);
            String fileTerm = new String(buffer, "ASCII");
            
            int compareValue = term.compareTo(fileTerm);
            if (compareValue == 0) 
            {
               // found it!
               return mVocabTable[m * 2 + 1];
            }
            else if (compareValue < 0) {
               j = m - 1;
            }
            else {
               i = m + 1;
            }
         }
         catch (IOException ex) {
            System.out.println(ex.toString());
         }
      }
      return -1;
   }


   /**
   Read files 
 * @throws IOException 
   */

   private List<String> readFileNames(String indexName) {
      try {
         final List<String> names = new ArrayList<String>();
         final Path currentWorkingPath = Paths.get(indexName).toAbsolutePath();

         Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
            

            public FileVisitResult preVisitDirectory(Path dir,
             BasicFileAttributes attrs) {
               // make sure we only process the current working directory
               if (currentWorkingPath.equals(dir)) {
                  return FileVisitResult.CONTINUE;
               }
               return FileVisitResult.SKIP_SUBTREE;
            }

            public FileVisitResult visitFile(Path file,
             BasicFileAttributes attrs) {
               // only process .txt files
               if (file.toString().endsWith(".txt")) {
                  names.add(file.toFile().getName());
               }
               return FileVisitResult.CONTINUE;
            }

            // don't throw exceptions if files are locked/other errors occur
            public FileVisitResult visitFileFailed(Path file,
             IOException e) {

               return FileVisitResult.CONTINUE;
            }

         });
         return names;
      }
      catch (IOException ex) {
         System.out.println(ex.toString());
      }
      return null;
   }
   /**
   Read VocabTable for Variable Byte Code
 * @throws IOException 
   */
   private static long[] Variable_readVocabTable(String indexName) {
	      try {
	         long[] vocabTable;
	         
	         RandomAccessFile tableFile = new RandomAccessFile(
	          new File(indexName, "Variable_vocabTable.bin"),
	          "r");
	         
	         
	         int len=GetType(tableFile);
	         
	         vocabTable = new long[len*2];
	         
	         
	         
	         for(int i=0;i<len*2;i++)
	         {
	        	 vocabTable[i] = GetType(tableFile);
		            
	         }
	         
	        
	         tableFile.close();
	         return vocabTable;
	      }
	      catch (FileNotFoundException ex) {
	         System.out.println(ex.toString());
	      }
	      catch (IOException ex) {
	         System.out.println(ex.toString());
	      }
	      return null;
	   }
   
   
   
   private static long[] Impact_readVocabTable(String indexName) {
	      try {
	         long[] vocabTable;
	         
	         RandomAccessFile tableFile = new RandomAccessFile(
	          new File(indexName, "Impact_vocabTable.bin"),
	          "r");
	         
	         byte[] byteBuffer = new byte[4];
	         tableFile.read(byteBuffer, 0, byteBuffer.length);
	        
	         int tableIndex = 0;
	         vocabTable = new long[ByteBuffer.wrap(byteBuffer).getInt() * 2];
	         byteBuffer = new byte[8];
	         
	         while (tableFile.read(byteBuffer, 0, byteBuffer.length) > 0) { // while we keep reading 4 bytes
	            vocabTable[tableIndex] = ByteBuffer.wrap(byteBuffer).getLong();
	            tableIndex++;
	         }
	         tableFile.close();
	         return vocabTable;
	      }
	      catch (FileNotFoundException ex) {
	         System.out.println(ex.toString());
	      }
	      catch (IOException ex) {
	         System.out.println(ex.toString());
	      }
	      return null;
	   }
   
   /**
   Get VocabTable 
 * @throws IOException 
   */
   private static long[] readVocabTable(String indexName) {
      try {
         long[] vocabTable;
         
         RandomAccessFile tableFile = new RandomAccessFile(
          new File(indexName, "vocabTable.bin"),
          "r");
         
         byte[] byteBuffer = new byte[4];
         tableFile.read(byteBuffer, 0, byteBuffer.length);
        
         int tableIndex = 0;
         vocabTable = new long[ByteBuffer.wrap(byteBuffer).getInt() * 2];
         byteBuffer = new byte[8];
         
         while (tableFile.read(byteBuffer, 0, byteBuffer.length) > 0) { // while we keep reading 4 bytes
            vocabTable[tableIndex] = ByteBuffer.wrap(byteBuffer).getLong();
            tableIndex++;
         }
         tableFile.close();
         return vocabTable;
      }
      catch (FileNotFoundException ex) {
         System.out.println(ex.toString());
      }
      catch (IOException ex) {
         System.out.println(ex.toString());
      }
      return null;
   }

   public List<String> getFileNames() {
      return mFileNames;
   }
   
   public int getTermCount() {
      return mVocabTable.length / 2;
   }
}
