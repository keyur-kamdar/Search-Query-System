package csulb.edu.SearchEngine.Classes;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class SearchWord {

	
	
	List<String> lstUserEnteredWords = new ArrayList<String>();
	int[] lstCommanDocument = new int[0];
	PorterStemmer objPorterStemmer = new PorterStemmer();
	DiskPositionalIndex objDiskPositionalIndex;

	public SearchWord(DiskPositionalIndex objDiskPositionalIndex) {
		
		this.objDiskPositionalIndex=objDiskPositionalIndex;
	}

	public int[] FindWord(String str)
	{
		int[] totalpostingList=new int[0];
		
		
			
		Pattern Type2=Pattern.compile("[\\(].+?[\\)]");
		Pattern Type3=Pattern.compile("[\"].+?[\"]");
		String[] FormWord;
		String[] lstWord;
		str=str.trim();
		int ContentType=1;
		
		
		if(Type2.matcher(str).matches())
		{
			str=str.replaceAll("\\(", "").replaceAll("\\)", "");
			FormWord= str.split(" ");
			ContentType=2;
		
		}
		else if(Type3.matcher(str).matches())
		{
			
			str=str.replaceAll("\"", "");
			
			FormWord= str.split(" ");
			ContentType=3;
		
		}
		else 
		{
			
			if(str.contains("-"))
			{
				FormWord= str.split("[-]");
			}
			else
			{
				FormWord= str.split(" ");	
			}	
			ContentType=1;
		}
		
		
		for(int i=0;i<FormWord.length;i++)
		{
			FormWord[i]=FormWord[i].trim();
		}
		lstWord=new String[FormWord.length];
		for(int i=0;i<FormWord.length;i++)
		{
				
			 
			int[] postingsList = objDiskPositionalIndex.GetPostings(PorterStemmer.processToken(FormWord[i]));
			if(postingsList!=null)
			{
				lstWord[i]=PorterStemmer.processToken((FormWord[i]));
			}
			else
			{
				return totalpostingList;
			}
			
			
		}
		
		
		if(ContentType==2||ContentType==1||(ContentType==3 && FormWord.length==1))
		{	
			
			for(int i=0;i<FormWord.length;i++)
			{
				
				
				int[] postingsList = objDiskPositionalIndex.GetPostings(PorterStemmer.processToken(FormWord[i]));
				totalpostingList=AdditionOfArray(postingsList, totalpostingList);
				
			}	
			totalpostingList=toUniqueArray(totalpostingList);

		}
		else if (ContentType == 3) 
		{
			
			if(lstWord[0]==null)
			{
				return totalpostingList;
			}
			if(Properties.Impactorder)
			{
				totalpostingList=GetPharseQueryPosting_ImpactOrder(1, lstWord, objDiskPositionalIndex);
			}
			else
			{
				totalpostingList=GetPharseQueryPosting(1, lstWord, objDiskPositionalIndex);	
			}
			
			
			
			
		}
		
		
		return totalpostingList;
	}
 private boolean isUnique(int[] array, int num) {
	        for (int i = 0; i < array.length; i++) {
	            if (array[i] == num) {
	                return false;
	            }
	        }
	        return true;
	}
	
	private int[] AddValueToArray(int[] d1, int value)
	{
		int[] d=new int[d1.length+1];
		int count=0;
		for(int i=0;i<d1.length;i++)
		{
			d[count]=d1[i];
			count++;
		}
		d[count]=value;
		return d;
		
		
	}
	private int[] AdditionOfArray(int[] d1, int[] d2)
	{
		int[] d3=new int[d1.length+d2.length];
		int count=0;
		for(int i=0;i<d1.length;i++)
		{
			d3[count]=d1[i];
			count++;
		}
		for(int i=0;i<d2.length;i++)
		{
			d3[count]=d2[i];
			count++;
		}
		return d3;
	}
	
	private int[] GetUniqueDoc(int[] d1, int[] d2)
	{
		int[] d3=new int[d1.length+d2.length];
		int count=0;
		for(int i=0;i<d3.length;i++)
		{
			d3[i]=-1;
		}
		for(int i=0;i<d1.length;i++)
		{
			d3[count]=d1[i];
			count++;
		}
		for(int i=0;i<d2.length;i++)
		{
			if(isUnique(d3, d2[i]))
			{
				d3[count]=d2[i];
				count++;
			}
		}
		
		int[] d4=new int[count];
		System.arraycopy(d3, 0, d4, 0, d4.length);
		return d4;
		
	}
	
	private int[] intersectSortedArrays(int[] a, int[] b)
	{
	    int[] c = new int[Math.min(a.length, b.length)]; 
	    int ai = 0, bi = 0, ci = 0;
	    while (ai < a.length && bi < b.length) 
	    {
	        if (a[ai] < b[bi]) 
	        {
	            ai++;
	        } 
	        else if (a[ai] > b[bi]) 
	        {
	            bi++;
	        } 
	        else 
	        {
	            if (ci == 0 || a[ai] != c[ci - 1]) 
	            {
	                c[ci++] = a[ai];
	            }
	            ai++; bi++;
	        }
	    }
	    
	    
	    
	    return Arrays.copyOfRange(c, 0, ci); 
	}
	 private int[] toUniqueArray(int[] array) {
	        int[] temp = new int[array.length];
	 
	        for (int i = 0; i < temp.length; i++) {
	            temp[i] = -1; // in case u have value of 0 in he array
	        }
	        int counter = 0;
	 
	        for (int i = 0; i < array.length; i++) 
	        {
	            if (isUnique(temp, array[i]))
	            {
	                temp[counter++] = array[i];
	            }
	        }
	        int[] uniqueArray = new int[counter];
	 
	        System.arraycopy(temp, 0, uniqueArray, 0, uniqueArray.length);
	 
	        return uniqueArray;
	    }
	
	 
	 
	 
	private int[] GetPharseQueryPosting(int WordSpace,String[] lstWord,DiskPositionalIndex objDiskPositionalIndex)
	{
		int[] totalpostingList=new int[0];
		int index=0;
		int index2=index+1;
		
		
		
		
		int findAllDoc=0;
		int[] doc=new int[0];
		for(int i=0;i<lstWord.length;i++)
		{
			DictionaryPosition[] dp = objDiskPositionalIndex.GetPostingsWithPosition(lstWord[i]);
			int[] d=new int[dp.length];
			for(int j=0;j<dp.length;j++)
			{
				d[j]=dp[j].documentID;
			}
			Arrays.sort(d);
			if(i==0)
			{
				doc=AdditionOfArray(doc, d);
			}
			doc=intersectSortedArrays(doc, d);
			
		}
		
		
		
		DictionaryPosition[] lstdp1 = objDiskPositionalIndex.GetPostingsWithPosition(lstWord[index]);
		for (int j = 0; j < lstdp1.length; j++) 
		{
			if(!BinarySearch(lstdp1[j].documentID, doc))
			{
				continue;
			}
		
			for (int k = 0; k < lstdp1[j].lst.length; k++) 
			{
				int pos = lstdp1[j].lst[k];
				findAllDoc=0;
				
				
				for (int l = index2; l < lstWord.length; l++) 
				{
					DictionaryPosition[] lstdp2 = objDiskPositionalIndex.GetPostingsWithPosition(lstWord[l]);
					
					int result=BinarySearchForDictionary(lstdp1[j].documentID, lstdp2);
					if(result==-1)
					{
						break;
					}
					else
					{
						
						int pos2=BinarySearchForPosition(lstdp2[result].lst,pos,WordSpace);
						if(pos2==-1)
						{
							break;
						}
						else
						{
							pos++;
							findAllDoc++;
							//skipDoc=false;
							continue;
						}
						
						
					}
				}
				if (findAllDoc+1 >= lstWord.length) 
				{
					totalpostingList = AddValueToArray(totalpostingList,lstdp1[j].documentID);
					break;

				}
				
				
			}
		}
		
		
		
		
		return totalpostingList;
	}
	 
	private int[] GetPharseQueryPosting_ImpactOrder(int WordSpace,String[] lstWord,DiskPositionalIndex objDiskPositionalIndex)
	{
		int[] totalpostingList=new int[0];
		int index=0;
		int index2=index+1;
		
		
		int findAllDoc=0;
		int[] doc=new int[0];
		for(int i=0;i<lstWord.length;i++)
		{
			DictionaryPosition[] dp = objDiskPositionalIndex.GetPostingsWithPosition(lstWord[i]);
			int[] d=new int[dp.length];
			for(int j=0;j<dp.length;j++)
			{
				d[j]=dp[j].documentID;
			}
			Arrays.sort(d);
			if(i==0)
			{
				doc=AdditionOfArray(doc, d);
			}
			doc=intersectSortedArrays(doc, d);
			
		}
		
		
		
		DictionaryPosition[] lstdp1 = objDiskPositionalIndex.GetPostingsWithPosition(lstWord[index]);
		DictionaryPosition_Comparable[] lstdpComp1=new DictionaryPosition_Comparable[lstdp1.length];
		for(int x=0;x<lstdpComp1.length;x++)
		{
			lstdpComp1[x]=new DictionaryPosition_Comparable(lstdp1[x].documentID, lstdp1[x].lst);
		}
		Arrays.sort(lstdpComp1);
		for (int j = 0; j < lstdpComp1.length; j++) 
		{
			if(!BinarySearch(lstdpComp1[j].documentID, doc))
			{
				continue;
			}
		
			for (int k = 0; k < lstdpComp1[j].lst.length; k++) 
			{
				int pos = lstdpComp1[j].lst[k];
				findAllDoc=0;
				
				
				for (int l = index2; l < lstWord.length; l++) 
				{
					DictionaryPosition[] lstdp2 = objDiskPositionalIndex.GetPostingsWithPosition(lstWord[l]);
					DictionaryPosition_Comparable[] lstdpComp2=new DictionaryPosition_Comparable[lstdp2.length];
					for(int x=0;x<lstdpComp2.length;x++)
					{
						lstdpComp2[x]=new DictionaryPosition_Comparable(lstdp2[x].documentID, lstdp2[x].lst);
					}
					Arrays.sort(lstdpComp2);
					int result=BinarySearchForDictionary_ImpactOrder(lstdpComp1[j].documentID, lstdpComp2);
					if(result==-1)
					{
						break;
					}
					else
					{
						
						int pos2=BinarySearchForPosition(lstdpComp2[result].lst,pos,WordSpace);
						if(pos2==-1)
						{
							break;
						}
						else
						{
							pos++;
							findAllDoc++;
							//skipDoc=false;
							continue;
						}
						
						
					}
				}
				if (findAllDoc+1 >= lstWord.length) 
				{
					totalpostingList = AddValueToArray(totalpostingList,lstdpComp1[j].documentID);
					break;

				}
				
				
			}
		}
		
		
		
		
		return totalpostingList;
	}
	
	private boolean BinarySearch(int doc,int[] lst)
	{
		
		int m=0; 
		int n=lst.length-1;
		
		while(m<=n)
		{
			int o=(m+n)/2;
			if(lst[o]==doc)
			{
				return true;
			}
			else if(lst[o]<doc)
			{
				m=o+1;
			}
			else
			{
				n=o-1;
				
			}
		}
		
		
		return false;
	}
	
	private int BinarySearchForPosition(int[] lst,int pos,int WordSpace)
	{
		int result=-1;
		int m=0; 
		int n=lst.length-1;
		while(m<=n)
		{
			int o=(m+n)/2;
			
			if(lst[o]==pos+WordSpace)
			{
				return o;
			}
			else if(lst[o]<pos+WordSpace)
			{
				m=o+1;
			}
			else
			{
				n=o-1;
				
			}
			
		}
		
		
		
		return result;
	}
	private int BinarySearchForDictionary(int DictionaryID, DictionaryPosition[] lstdp2)
	{
		int result=-1;
		int m = 0;
		int n = lstdp2.length-1;	
		
		while(m<=n)
		{
			int o=(m+n)/2;
			
			if(lstdp2[o].documentID==DictionaryID)
			{
				return o;
			}
			else if(lstdp2[o].documentID<DictionaryID)
			{
				m=o+1;
			}
			else
			{
				n=o-1;
				
						
			}
			
			
		}
		
		return result;
		
	}
	private int BinarySearchForDictionary_ImpactOrder(int DictionaryID, DictionaryPosition_Comparable[] lstdp2)
	{
		int result=-1;
		int m = 0;
		int n = lstdp2.length-1;	
		
		while(m<=n)
		{
			int o=(m+n)/2;
			
			if(lstdp2[o].documentID==DictionaryID)
			{
				return o;
			}
			else if(lstdp2[o].documentID<DictionaryID)
			{
				m=o+1;
			}
			else
			{
				n=o-1;
				
						
			}
			
			
		}
		
		return result;
		
	}
	
	
	
	public int[] GetNear(String str)
	{
		int[] totalpostingList=new int[0];
		int numberOfPosition = Integer.parseInt(str.toLowerCase().replaceFirst(".*?[near/](\\d+).*", "$1"));
		
		String[] val=str.toLowerCase().split("near/"+String.valueOf(numberOfPosition));
		for(int i=0;i<val.length;i++)
		{
			val[i]=val[i].trim();
		}
		String[] lstWord=new String[val.length];

		for(int i=0;i<val.length;i++)
		{
					
			int[] postingsList = objDiskPositionalIndex.GetPostings(PorterStemmer.processToken(val[i]));
			if(postingsList!=null)
			{
				lstWord[i]=PorterStemmer.processToken((val[i]));
			}
			
			
		}
		
		if(lstWord[0]==null)
		{
			return totalpostingList;
		}
		
		
		if(Properties.Impactorder)
		{
			totalpostingList=GetPharseQueryPosting_ImpactOrder(numberOfPosition, lstWord, objDiskPositionalIndex);
		}
		else
		{
			totalpostingList=GetPharseQueryPosting(numberOfPosition, lstWord, objDiskPositionalIndex);	
		}
		
		
		return totalpostingList;
	}

	
	
	
	
	public int[] SearchResult(String str) {

		
		
		List<String> lst=new ArrayList<String>();
		/*String[] str1=str.split("((?<=+)|(?=;+))");*/
		if(str.contains("+"))
		{
			this.lstUserEnteredWords=Arrays.asList(str.split("[+]"));
		}
		else 
		{
			this.lstUserEnteredWords.add(str);
		}
		
		for(int i=0;i<lstUserEnteredWords.size();i++)
		{
			lstUserEnteredWords.set(i, lstUserEnteredWords.get(i).trim());
			lst=new ArrayList<String>();
			
			Matcher m = Pattern.compile("([^\"\\(]\\S*|\".+?\"|\\(.+?\\))\\s*").matcher(lstUserEnteredWords.get(i));
			
			while (m.find())
			{
									
			    lst.add(m.group().toLowerCase()); 
			}
			int[] lstInternal=new int[0];
			for(int j=0;j<lst.size();j++)
			{
				int[] lstDoc=FindWord(lst.get(j));
				Arrays.sort(lstDoc);
					
				
				if(j==0)
				{
					lstInternal=AdditionOfArray(lstInternal, lstDoc);
				}
				lstInternal=intersectSortedArrays(lstInternal, lstDoc);

				
			}
			
			lstCommanDocument=AdditionOfArray(lstInternal, lstCommanDocument);
			
			lstCommanDocument=GetUniqueDoc(lstInternal, lstCommanDocument);
		
			
		}
		
		

		return lstCommanDocument;
	 


	}

}
