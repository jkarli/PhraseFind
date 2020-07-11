// Project for CS445 Spring 2020
// Credit to Professor John Ramirez for project idea

import java.io.*;
import java.util.*;

public class PhraseFind 
{

    public static void main(String[] args) 
    {
        new PhraseFind();
    }
    
    public PhraseFind()
    {
        Scanner inScan = new Scanner(System.in);
        Scanner fReader;
        File fName;
        String fString = "", phrase = "";
       
       	//Make sure the file name is valid
        while (true)
        {
           try
           {
               System.out.println("Please enter grid filename:");
               fString = inScan.nextLine();
               fName = new File(fString);
               fReader = new Scanner(fName);
              
               break;
           }
           catch (IOException e)
           {
               System.out.println("Problem " + e);
           }
        }

		//Parse input file to create 2-d grid of characters
		String [] dims = (fReader.nextLine()).split(" ");
		int rows = Integer.parseInt(dims[0]);
		int cols = Integer.parseInt(dims[1]);
		
		char [][] theBoard = new char[rows][cols];

        //Initialize grid with characters from input file
		for (int i = 0; i < rows; i++)
		{
			String rowString = fReader.nextLine();
			for (int j = 0; j < rowString.length(); j++)
			{
				theBoard[i][j] = Character.toLowerCase(rowString.charAt(j));
			}
		}

		//Show user the grid
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				System.out.print(theBoard[i][j] + " ");
			}
			System.out.println();
		}
        
        System.out.println();
        System.out.println("Please enter phrase (sep. by single spaces):");
        phrase = (inScan.nextLine()).toLowerCase().trim(); //Get initial phrase to search for
        Deque<String> coordinates = new LinkedList<>(); //Double ended queue to store the coordinates 
                                                        //of the words in the phrase      
        //Loop until the user enters nothing to search for
        while(!phrase.equals(""))
        {
            String [] words = phrase.split(" "); //Determines how many words are in the phrase
            System.out.println("Looking for: " + phrase);
            int numWords = words.length;
            System.out.println("containing " + numWords + " words");
        
            boolean found = false;          
            for (int r = 0; (r < rows && !found); r++)
            {
                for (int c = 0; (c < cols && !found); c++)
				{
                    found = findPhrase(r, c, 0, theBoard, phrase, coordinates, null);
                    if (found)
                    {
                        System.out.println("The phrase: " + phrase);
                        System.out.println("was found:");
                        //Prints out the words in the phrase with thier corresponding coordinates
                        for(int i = 0; i < words.length; i++)
                        {
                            System.out.println(words[i] + ": " + coordinates.pollLast() + " to " + coordinates.pollLast());
                        }
                        //Prints out the board with the phrase in upper case and then sets the characters to lower case
                        for (int i = 0; i < rows; i++)
                        {
                            for (int j = 0; j < cols; j++)
                            {
                                System.out.print(theBoard[i][j] + " ");
                                theBoard[i][j] = Character.toLowerCase(theBoard[i][j]);
                            }
                            System.out.println();
                        }
                    }
                }              
            }
            if(!found)
            {
                System.out.println("The phrase: " + phrase);
                System.out.println("was not found");
            }
            System.out.println();
            System.out.println("Please enter phrase (sep. by single spaces):");
            phrase = (inScan.nextLine()).toLowerCase().trim(); //get next phrase to search for
        }
    }
    
    public boolean findPhrase(int r, int c, int loc, char[][] bo, String phrase,
            Deque<String> coor, String direction)
    {     
        boolean answer; 
        //Check boundary conditions
        if (r >= bo.length || r < 0 || c >= bo[0].length || c < 0)
        {
            return false;
			
        //Current character does not match 
        }else if (bo[r][c] != phrase.charAt(loc))
        {
            return false;
			
        //Current character does match
        }else
		{
            bo[r][c] = Character.toUpperCase(bo[r][c]);
			
            //Base case - phrase found, we return true
            if (loc == phrase.length()-1)
            {
                coor.push("(" + r + "," + c + ")");
				answer = true;
				
            //Recursive case 1 - Matching the second character of the first word. No direction has been 
            //determined yet, so we try right, down, left, and up in that order (if needed)
            }else if(direction == null)
            {
                coor.push("(" + r + "," + c + ")"); //Adds coordinates of first character to queue
                answer = findPhrase(r, c + 1, loc + 1, bo, phrase, coor, "Right");  // Right
                if (!answer)
                {
                    answer = findPhrase(r + 1, c, loc + 1, bo, phrase, coor, "Down");  // Down
                }
                if (!answer)
                {
                    answer = findPhrase(r, c - 1, loc + 1, bo, phrase, coor, "Left");  // Left
                }
                if (!answer)
                {
                    answer = findPhrase(r - 1, c, loc + 1, bo, phrase, coor, "Up");  // Up 
                }
                //First character matches, but second character is not found. Remove the coordinates of 
                //the first character from the queue
                if(!answer)
				{
                    coor.pop();
                }
				
            //Recursive case 2 - Reached the end of a word. Search right, down, left, and up in that order 
            //(if needed) for the first character of the next word in the phrase. Adds and removes coordinates
            //from the queue as necessary
            }else if(phrase.charAt(loc + 1) == ' ') 
            {
                coor.push("(" + r + "," + c + ")"); //Adds coordinates of the last character of the previous word to queue
                coor.push("(" + r + "," + (c + 1) + ")");
                answer = findPhrase(r, c + 1, loc + 2, bo, phrase, coor, "Right");  // Right
                if (!answer)
                {
                    coor.pop();
                    coor.push("(" + (r + 1) + "," + c + ")");
                    answer = findPhrase(r + 1, c, loc + 2, bo, phrase, coor, "Down");  // Down
                }
                if (!answer)
                {
                    coor.pop();
                    coor.push("(" + r + "," + (c - 1) + ")");
                    answer = findPhrase(r, c - 1, loc + 2, bo, phrase, coor, "Left");  // Left
                }
                if (!answer)
                {
                    coor.pop();
                    coor.push("(" + (r - 1) + "," + c + ")");
                    answer = findPhrase(r - 1, c, loc + 2, bo, phrase, coor, "Up");  // Up
                }
                //First character of the next word not found. Remove the coordinates from the "Up" recurisve call from the
                //queue and remove the coordinates of the last character of the previous word from the queue
                if(!answer){
                    coor.pop();
                    coor.pop();
                }
            }
            //Recurisve case 3 - Attempting to match the remaining characters of a word once the first 
            //character has been matched and a direction has been determined (it will continue in that 
            //same direction)
            else
            {
				if(direction.equals("Right"))
					answer = findPhrase(r, c + 1, loc + 1, bo, phrase, coor, "Right");
				else if(direction.equals("Down"))
                    answer = findPhrase(r + 1, c, loc + 1, bo, phrase, coor, "Down");
				else if(direction.equals("Left")) 
                    answer = findPhrase(r, c - 1, loc + 1, bo, phrase, coor, "Left");
				else
                    answer = findPhrase(r - 1, c, loc + 1, bo, phrase, coor, "Up");
            }
            //If answer was not found, backtrack
            if (!answer)
            {
                bo[r][c] = Character.toLowerCase(bo[r][c]);
            } 
            return answer;
        }
    }
}