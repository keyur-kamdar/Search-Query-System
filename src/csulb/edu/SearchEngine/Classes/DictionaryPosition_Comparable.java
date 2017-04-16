package csulb.edu.SearchEngine.Classes;





public class DictionaryPosition_Comparable implements Comparable<DictionaryPosition_Comparable> {

	public int documentID;
	public int[] lst;
	
	public DictionaryPosition_Comparable(int did, int[] l) {
		super();
		documentID = did;
		lst = l;
	}
	
	public int compareTo(DictionaryPosition_Comparable other) {
		
		
		int d=other.documentID;
		return this.documentID-d;
		
	}
	
	

}
