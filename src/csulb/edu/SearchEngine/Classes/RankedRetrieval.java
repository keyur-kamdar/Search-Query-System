package csulb.edu.SearchEngine.Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import edu.csulb.SearchEngine.Forms.MainPage;

public class RankedRetrieval {

	
	DiskPositionalIndex diskPositionalIndex ;
	public RankedRetrieval(DiskPositionalIndex diskPositionalIndex) {

		this.diskPositionalIndex=diskPositionalIndex;
	}

	public List<Integer> ProcessRankRetrieval(String query)
	{
		String[] queryArray = query.split(" ");
		float wQT;
		float wDT;
		float tempAcc = 0;
		
		float docWeight = 0;
		HashMap<Integer, Float> docFreq = new HashMap<Integer, Float>();
		
		
		
		for(int i=0;i<queryArray.length;i++)
		{
			
			
			DictionaryPosition[] postingList= diskPositionalIndex.GetPostingsWithPosition(PorterStemmer.processToken(queryArray[i]));
			
			
			if(postingList==null)
			{
				continue;
			}
			
			
			for(int j=0;j<postingList.length;j++)
			{
			
				if(!docFreq.containsKey(postingList[j].documentID))
				{
					docFreq.put(postingList[j].documentID, (float) 0);
				}
			}
			
			wQT = (float) Math.log(1 + ((float)SimpleEngine.listOfFiles.length/(float)postingList.length));
			//System.out.println("N:"+SimpleEngine.listOfFiles.length+",Dft:"+postingList.length);
			
			
			
			
			for(int j = 0; j<postingList.length; j++)
			{
				
				wDT = (float) (1 + Math.log(postingList[j].lst.length));
				tempAcc = docFreq.get(postingList[j].documentID);
				
				
				
				tempAcc += (wQT * wDT);
				docFreq.put(postingList[j].documentID, tempAcc);
				//System.out.println("DocumntID:"+ SimpleEngine.listOfFiles[postingList[j].documentID].getName() +",WQT :"+wQT+",WDT:"+wDT);
			}
					
			
		}
		for(Integer docKey : docFreq.keySet())
		{
			if(docFreq.get(docKey) != 0.0){
				docWeight = diskPositionalIndex.readDocWeightsFile(docKey);
				float a=(float)docFreq.get(docKey)/docWeight;
				//System.out.println("DocWeight:"+SimpleEngine.listOfFiles[docKey]+", DocWeight:"+  docWeight+ "; a=" + a + "; docFreq" + docFreq.get(docKey));
				docFreq.put(docKey, a);
			}
				
		}
	
		
		return implementPriorityQueue(docFreq);
	}
	
	public List<Integer> ProcessRankRetrieval_ImpactOrder(String query)
	{
		String[] queryArray = query.split(" ");
		float wQT;
		float wDT;
		float tempAcc = 0;
		
		float docWeight = 0;
		HashMap<Integer, Float> docFreq = new HashMap<Integer, Float>();
		
		
		
		for(int i=0;i<queryArray.length;i++)
		{
			int thresholdValue=0;
			
			DictionaryPosition[] postingList= diskPositionalIndex.GetPostingsWithPosition(PorterStemmer.processToken(queryArray[i]));
			
			
			if(postingList==null)
			{
				continue;
			}
			
			for(int j=0;j<postingList.length;j++)
			{
				thresholdValue+=postingList[j].lst.length;
			}
			thresholdValue=thresholdValue/postingList.length;
			for(int j=0;j<postingList.length;j++)
			{
				if(postingList[j].lst.length<=thresholdValue)
				{
					break;
				}
				if(!docFreq.containsKey(postingList[j].documentID))
				{
					docFreq.put(postingList[j].documentID, (float) 0);
				}
			}
			
			wQT = (float) Math.log(1 + ((float)SimpleEngine.listOfFiles.length/(float)postingList.length));
			//System.out.println("N:"+SimpleEngine.listOfFiles.length+",Dft:"+postingList.length);
			
			
			
			
			for(int j = 0; j<postingList.length; j++)
			{
				if(postingList[j].lst.length<=thresholdValue)
				{
					break;
				}
				wDT = (float) (1 + Math.log(postingList[j].lst.length));
				tempAcc = docFreq.get(postingList[j].documentID);
				
				
				
				tempAcc += (wQT * wDT);
				docFreq.put(postingList[j].documentID, tempAcc);
				//System.out.println("DocumntID:"+ SimpleEngine.listOfFiles[postingList[j].documentID].getName() +",WQT :"+wQT+",WDT:"+wDT);
			}
					
			
		}
		for(Integer docKey : docFreq.keySet())
		{
			if(docFreq.get(docKey) != 0.0){
				docWeight = diskPositionalIndex.readDocWeightsFile(docKey);
				float a=(float)docFreq.get(docKey)/docWeight;
				//System.out.println("DocWeight:"+SimpleEngine.listOfFiles[docKey]+", DocWeight:"+  docWeight+ "; a=" + a + "; docFreq" + docFreq.get(docKey));
				docFreq.put(docKey, a);
			}
				
		}
	
		
		return implementPriorityQueue(docFreq);
	}
	
 private class DocWeightPair implements Comparable {
		public int docId;
		public float score;
		
		public DocWeightPair(int did, float s) {
			docId = did;
			score = s;
		}
		
		public int compareTo(Object other) {
			DocWeightPair o = (DocWeightPair)other;
			
			if (score < o.score)
				return 1;
			if (score > o.score)
				return -1;
			return 0;
		}
	}
	
	private List<Integer> implementPriorityQueue(HashMap<Integer, Float> docFreq) {
		
		DocWeightPair temp = null;
		PriorityQueue<DocWeightPair> rankedRetrievalQueue = new PriorityQueue<DocWeightPair>();
		for(Integer key : docFreq.keySet()){
			rankedRetrievalQueue.add(new DocWeightPair(key, docFreq.get(key)));
		}
		
		List<Integer> lst =new ArrayList<Integer>();
		int tempLen=10;
		if(rankedRetrievalQueue.size()<10)
		{
			tempLen=rankedRetrievalQueue.size();
		}
		for(int i = 0; i<tempLen; i++){
			temp = rankedRetrievalQueue.remove();
			lst.add(temp.docId);
			//System.out.println(SimpleEngine.listOfFiles[temp.docId] + ":" + temp.score);
			
			
		}
		
		return lst;
		
		
	}
}
