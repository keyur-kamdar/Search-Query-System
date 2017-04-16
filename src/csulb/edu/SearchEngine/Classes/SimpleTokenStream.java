package csulb.edu.SearchEngine.Classes;

import java.io.*;


import com.Ostermiller.util.StringTokenizer;

/**
 * Reads tokens one at a time from an input stream. Returns tokens with minimal
 * processing: removing all non-alphanumeric characters, and converting to
 * lowercase.
 */
public class SimpleTokenStream implements TokenStream {

	RandomAccessFile objFile;
	StringTokenizer tokens;
	String fullWord;
	
	

	/**
	 * Constructs a SimpleTokenStream to read from the specified file.
	 */
	public SimpleTokenStream(File fileToOpen) throws IOException {
		// mReader = new Scanner(new FileReader(fileToOpen));
		objFile = new RandomAccessFile(fileToOpen, "rw");
		objFile.seek(0);
	}

	/**
	 * Constructs a SimpleTokenStream to read from a String of text.
	 */
	public SimpleTokenStream(String text) {
		// mReader = new Scanner(text);
	}

	/**
	 * Returns true if the stream has tokens remaining.
	 */
	@Override
	public boolean hasNextToken() {
		// return mReader.hasNext();
		return tokens.hasMoreTokens();
		

	}
	
	public void ReadNextLine()
	{
		try {
			tokens = new StringTokenizer(objFile.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	public int GetCurrentWordPosition()
	{
		if(!tokens.hasNext())
		{
			return tokens.text.length()-fullWord.length();
		}
		return tokens.getCurrentPosition()-fullWord.length();
	}
	
	
	public long GetLinePointer () throws IOException
	{
		return objFile.getFilePointer();
	}
	public void CloseFile ()throws IOException
	{
		objFile.close();
	}
	public long GetLength() throws IOException {

		
		return objFile.length();
	}

	/**
	 * Returns the next token from the stream, or null if there is no token
	 * available.
	 */
	@Override
	public String nextToken() {
		if (!hasNextToken())
			return null;

		fullWord=tokens.nextToken();
		String next = fullWord.replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "").toLowerCase();
		
		
		
		

		return next.length() > 0 ? next : hasNextToken() ? nextToken() : null;
	}
}