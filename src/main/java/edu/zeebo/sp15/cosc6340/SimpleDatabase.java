package edu.zeebo.sp15.cosc6340;

import java.util.*;

/**
 * User: Eric
 * Date: 2/17/15
 */
public class SimpleDatabase
{

	public static void main(String[] args)
    {
        Scanner s = new Scanner(System.in);
        ArrayList<String> query = new ArrayList<String>();

        boolean running = true;

        while(running)
        {
            query.clear();
            String inputQuery = "";
            System.out.println("Please enter your command");

            inputQuery = s.nextLine();

            query.addAll(ParseString(inputQuery));

            InterpretQuery(query);
            PrintTable();
        }
	}

    public static void InterpretQuery(ArrayList<String> query)
    {
        String select = "SELECT";
        String create = "CREATE";
        String insert = "INSERT INTO";

        //Check syntax and semantics
        //Eric, what do you need me to return to you so that you can execute the command?
    }

    public static ArrayList<String> ParseString(String inputQuery)
    {
        //break up string into array
        Scanner input = new Scanner(inputQuery);
        ArrayList<String> brokenUpQuery = new ArrayList<String>();
        while(input.hasNext())
        {
            brokenUpQuery.add(input.next());
        }
        //FOR DEBUGGING
        for (int i = 0; i < brokenUpQuery.size(); i++)
        {
            System.out.println(brokenUpQuery.get(i));
        }
        return brokenUpQuery;
    }

    public static void PrintTable()
    {
        ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
        //LOAD TABLE TO DATA STRUCTURE HERE?
        
        int rows = 5; //table.size();
        int columns = 5; //table.get(i).size()

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print("+----------");//change this to not be hard coded?
            }
            System.out.println("+");

            for(int k = 0; k < columns; k++){
                //print tuple value here
                System.out.print("|" + "tupleValue");
            }
            System.out.println("|");
        }
        for (int l = 0; l < columns; l++) {
            System.out.print("+----------");
        }
        System.out.println("+");
    }

    public static void ThrowSyntaxError(String query)
    {
        System.out.println("You have a syntax error: " + query);
    }
}
