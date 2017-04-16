package csulb.edu.SearchEngine.Classes;




import java.io.*;
import java.nio.file.*;

import java.util.*;


public class SimpleEngine {

	
	
	
	final Path currentWorkingPath = Properties.library_Directory_Path.toAbsolutePath();
	
	public static File[] listOfFiles;
	public static ArrayList<Float> docWeight = new ArrayList<Float>();
	
	public SimpleEngine()
	{
		
		PositionalInvertedIndex.mIndex=new LinkedHashMap<String, LinkedHashMap<Integer,List<Integer>>>();
		FilenameFilter textFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".txt")) {
					return true;
				} else {
					return false;
				}
			}
		};
		
		listOfFiles=currentWorkingPath.toFile().listFiles(textFilter);
	}
	
	private void GenerateHashMap() throws IOException
	{
		HashMap<String, Integer> termFreqHashmap = new HashMap<String, Integer>();
		//listOfFiles = currentWorkingPath.toFile().listFiles();
		long filePointer = 0;
		SimpleTokenStream objSimpleToken;
		
		
		for (int i = 0; i < listOfFiles.length; i++) {
			
			double[] WDT;
			System.out.println(i);
			if(!listOfFiles[i].getAbsoluteFile().toString().endsWith(".txt"))
			{
				continue;
			}
			
			
			objSimpleToken = new SimpleTokenStream(listOfFiles[i].getAbsoluteFile());

			int word_position=0;
			int termCount = 0;
			while (filePointer < objSimpleToken.GetLength()) {
		

				objSimpleToken.ReadNextLine();
				
				while (objSimpleToken.hasNextToken()) {

					String str = objSimpleToken.nextToken();
		
					if (str == null) {
						continue;
					}
					
					
					
				
					
				
					String[] strHyphen=null;
					
					strHyphen=str.split("-");
					
					
					
					if (strHyphen.length > 1) {

						
		
						
						PositionalInvertedIndex.addTerm(strHyphen[0] + strHyphen[1], i,word_position);
						
						PositionalInvertedIndex.addTerm(strHyphen[0], i, word_position);
						PositionalInvertedIndex.addTerm(strHyphen[1], i, word_position);
						word_position++;

					} else {
						
						
						PositionalInvertedIndex.addTerm(str, i, word_position);
						word_position++;
						
						if(termFreqHashmap.containsKey(str)){
							termCount = termFreqHashmap.get(str);
							termCount++;
							termFreqHashmap.put(str, termCount);
							termCount=0;
						}else{
							termCount++;
							termFreqHashmap.put(str, termCount);
							termCount = 0;
						}
					}

				}
				filePointer = objSimpleToken.GetLinePointer();

			}
			
		
			WDT = calculateWDT(termFreqHashmap);
			calculateDocWeight(WDT);
			System.out.println("FileName:"+listOfFiles[i].getAbsoluteFile().toString()+"DocWeight:"+docWeight.get(i));
			termFreqHashmap.clear();
			objSimpleToken.CloseFile();
			filePointer = 0;
			word_position=0;

		}		
	}
	
	public void GenerateLibrary() throws IOException {

		
		GenerateHashMap();
		IndexWriter writer = new IndexWriter(Properties.library_Directory_Path.toAbsolutePath().toString());
		writer.buildIndex();
		
		
        


	}
	
	private void calculateDocWeight(double[] wDT) {
		float tempWDT = 0;
		for(int i = 0; i<wDT.length; i++){
			wDT[i] = Math.pow(wDT[i], 2);
		}
		for(int j = 0; j<wDT.length; j++){
			tempWDT = (float) (tempWDT + wDT[j]);
		}
		tempWDT = (float) Math.sqrt(tempWDT);
		docWeight.add(tempWDT);
		
	}

	private double[] calculateWDT(HashMap<String, Integer> termFreqHashmap) {
		double[] WDT = new double[termFreqHashmap.keySet().size()];
		int i = 0;
		double tempWDT = 0;
		
		for(String key : termFreqHashmap.keySet()){
			tempWDT = 1 + Math.log(termFreqHashmap.get(key));
			WDT[i] = tempWDT;
			i++;
		}
		return WDT;
	}

}
