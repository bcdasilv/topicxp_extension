package jgibblda;

import java.io.*;
import java.util.Scanner;

class Datafiler {
	static final String stopwords[] = {"*", "/**", "", "}", "{","//","+",
										"=","*/", "<","\"","+=", "-", "-=", ""};
	
	public static void main(String args[]){
		if (args.length < 2) showHelp();
		
		String dataFileName = args[0];
		String documents[] = new String[args.length - 1]; 
		
		for (int i = 1 ; i < args.length ; i++) {
			documents[i-1] = args[i];
		}
		
		createDatafile(dataFileName, documents);
		
	}
	
	public static void createDatafile(String fileName, String documents[]) {
		FileOutputStream dataFile;
		try {
			dataFile = new FileOutputStream(fileName);
			PrintStream out = new PrintStream(dataFile);
			out.println(documents.length);
		
			for (String document : documents) {
				out.print(document+ " =");
				Scanner scan = new Scanner(new FileInputStream(document));
				
				while (scan.hasNextLine()) {
					String line = scan.nextLine();
					String words[] = line.split("\\s+");
					for (String word : words) {
						if (!isStopWord(word)) // skip non-alpha words
						out.print(" " + word);
					}
				}
				out.print("\n");
			}
			
			out.close();
			dataFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static boolean isStopWord(String word) {
		for (String stopWord : stopwords) {
			if (stopWord.equals(word)) return true;
		}
		return false;
	}

	public static void showHelp(){
		System.out.println("datafiler [datfile.dat] [document1 document2 ...]");
		System.exit(0);
	}
	
}
