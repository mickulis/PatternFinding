/**
 *
 * TODO:
 * 	fix a bug with text: ??B?, word: AAB
 * 	idea: move prefixTable to the left and adjust some formulas, so when B is matched, it goes to prefixTable[indexOfB], but if it's not matched it goes to prefixTable[indexOfB - 1] ????
 *
 */





import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class KMP
{
	public boolean questionMarkMatchesAll = false;
	boolean readFromStream;
	int[] prefixTable;
	String word;
	String text;
	Reader reader;
	ArrayList<Integer> listOfWordIndices = new ArrayList<>();
	String white = (char)27 + "[00m";
	String color = (char)27 + "[31m";
	
	KMP(String word, String text)
	{
		this.text = text;
		this.word = word;
		readFromStream = false;
	}
	
	KMP(InputStream wordInputStream, InputStream textInputStream) throws IOException
	{
		this.reader = new Reader(wordInputStream);
		StringBuilder stringBuilder = new StringBuilder();
		while(reader.hasNext())
		{
			stringBuilder.append((char)reader.read());
		}
		this.word = stringBuilder.toString();
		System.out.println(word);
		this.reader = new Reader(textInputStream);
		readFromStream = true;
	}
	
	void generatePrefixTable()
	{
		prefixTable = new int[word.length()];
		prefixTable[0] = -1;
		int prefixSuffixLength = -1;
		for(int i = 1; i < prefixTable.length; i++)
		{
			while(prefixSuffixLength > -1 && !areMatch(prefixSuffixLength, word.charAt(i - 1)))
			{
				prefixSuffixLength = prefixTable[prefixSuffixLength];
			}
			prefixSuffixLength++;
			prefixTable[i] = prefixSuffixLength;
			}
		System.out.println(Arrays.toString(prefixTable));
	}
	
	void searchForWord() throws IOException
	{
		if(readFromStream)
			searchForWordInStream();
		else
			searchForWordInText();
	}
	
	
	void searchForWordInText()
	{
		int matchedPrefixLength = 0;
		for(int i = 0; i < text.length(); i++)
		{
			while(matchedPrefixLength > -1 && !areMatch(matchedPrefixLength, text.charAt(i)))
			{
				matchedPrefixLength = prefixTable[matchedPrefixLength];
			}
			matchedPrefixLength++;
			if(matchedPrefixLength == word.length())
			{
				listOfWordIndices.add(i - word.length() + 1);
				System.out.println(i - word.length() + 1);
				matchedPrefixLength = prefixTable[matchedPrefixLength - 1];
			}
			
		}
	}
	
	void searchForWordInStream() throws IOException
	{
		int counter = 0;
		ArrayList<Character> characters = new ArrayList<>();
		int matchedPrefixLength = 0;
		while(reader.hasNext())
		{
			counter++;
			char currentChar = (char)reader.read();
			characters.add(currentChar);
			while(matchedPrefixLength > -1 && !areMatch(matchedPrefixLength, currentChar))
			{
				matchedPrefixLength = prefixTable[matchedPrefixLength];
			}
			matchedPrefixLength++;
			if(matchedPrefixLength == word.length())
			{
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(color);
				while(characters.size() > 0)
				{
					stringBuilder.append(characters.remove(0));
				}
				stringBuilder.append(white);
				System.out.print(stringBuilder.toString());
				matchedPrefixLength = prefixTable[matchedPrefixLength - 1];
				if(questionMarkMatchesAll)
					if(currentChar == '?')
						matchedPrefixLength++;
				listOfWordIndices.add(counter);
			}
			else
			{
				while(characters.size() >= word.length())
					System.out.print(characters.remove(0));
			}
		}
		while(characters.size() > 0)
			System.out.print(characters.remove(0));
	}
	
	private boolean areMatch(int matchedPrefixLength, char currentChar)
	{
		if(questionMarkMatchesAll)
			if(currentChar == '?' || word.charAt(matchedPrefixLength) == '?')
				return true;
		return word.charAt(matchedPrefixLength) == currentChar;
	}
	
	
	public static void main(String[] args) throws IOException
	{
		String word = "AAB";
		String text = "AAAzzzzzBAAABAAAABA";
		//			   01234567890123
		
		String white = (char)27 + "[00m";
		String color = (char)27 + "[31m";
		
		
		
		//KMP kmp = new KMP(System.in, System.in);
		
		KMP kmp = new KMP(new FileInputStream("word"), new FileInputStream("text"));
		kmp.questionMarkMatchesAll = true;
		kmp.generatePrefixTable();
		kmp.searchForWord();
		
		//System.out.println(color+"asdf"+white+"asdf");
		ArrayList<Integer> patternMatches = kmp.listOfWordIndices;
		System.out.println();
		System.out.println(Arrays.toString(patternMatches.toArray()));

//
//
//
//		int matchIterator = 0;
//		for(int i = 0; i < text.length(); i++)
//		{
//			while(matchIterator < patternMatches.size() && patternMatches.get(matchIterator) + word.length() < i + 1)
//			{
//				matchIterator++;
//			}
//
//			if(matchIterator < patternMatches.size() && patternMatches.get(matchIterator) <= i)
//				System.out.print(color + text.charAt(i));
//			else
//				System.out.print(white + text.charAt(i));
//		}
	}
	
}


class Reader
{
	InputStream inputStream;
	InputStreamReader inputStreamReader;
	Integer buffer;
	
	
	
	Reader(InputStream inputStream) throws UnsupportedEncodingException
	{
		this.inputStream = inputStream;
		inputStreamReader = new InputStreamReader(inputStream, "UTF8");
	}
	
	int read() throws IOException
	{
		int temp;
		if(buffer != null)
		{
			temp = buffer;
			buffer = null;
			return temp;
		}
		temp = inputStreamReader.read();
		
		while(Pattern.matches("\\s", ("" + (char)temp)))
			temp = inputStreamReader.read();
		
		if(temp == '止')
		{
			return -1;
		}
		return temp;
	}
	
	boolean hasNext()
	{
		if(buffer != null)
			return true;
		try
		{
			buffer = read();
		}
		catch(IOException ioException)
		{
			System.err.println(ioException);
			return false;
		}
		return (buffer != -1);
	}
	
}
