package edu.zeebo.sp15.cosc6340;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Eric
 * Date: 2/17/15
 */
public class SimpleDatabase
{

	public static void main(String[] args)
    {
        Scanner s = new Scanner(System.in);

        boolean running = true;

        while(running)
        {
            String inputQuery = "";
            System.out.println();
            System.out.println("Please enter your command (Press Q to Quit)");

            inputQuery = s.nextLine();
            if(inputQuery.toUpperCase().equals("Q")) {
                running = false;
                break;
            }

            ParseString(inputQuery);


        }
	}

    public static void ParseString(String inputQuery)
    {
        Pattern p = null;
        Matcher m = null;
        //regexes
        String select = "SELECT\\s+(\\w+),*\\s*(\\w*)\\s+FROM\\s+(\\w+)(,\\s+(\\w+))*\\s*(WHERE\\s+(\\w+)\\s*=\\s*\"*\\w+\"*)*";
        String selectAll = "SELECT\\s+\\*\\s+FROM\\s+(\\w+)(,\\s+(\\w+))*\\s+(WHERE\\s+(\\w+)\\s*=\\s*\"*\\w+\"*)*";
        String createTable = "CREATE\\s+(\\w+\\s+)\\((\\w+\\s+(INT|STRING),*\\s*)+\\)";
        String insertInto = "INSERT\\s+INTO\\s+(\\w+)\\s+\\((\"*\\w+\\\"*,*\\s*)+\\)";


        if(inputQuery.matches(select))
        {
            //group 1 selected fields
            //group 2 from field 1
            //group 3 from field 2
            //group 4 table name
            //group 5 where variable
            p = Pattern.compile(select);
            m = p.matcher(inputQuery);
            while(m.find())
            {
                for(int i = 0; i < m.groupCount(); i++){
                    System.out.println("Group "+ i +":" + m.group(i));
                }
            }
            System.out.println("Good Query: " + inputQuery);
        }
        else if (inputQuery.matches(selectAll))
        {
            //group 1 from fields
            //group 2 where clause
            p = Pattern.compile(selectAll);
            m = p.matcher(inputQuery);
            while(m.find())
            {
                for(int i = 0; i < m.groupCount(); i++){
                    System.out.println("Group "+ i +":" + m.group(i));
                }
            }
            System.out.println("Good Query: " + inputQuery);

        }
        else if(inputQuery.matches(createTable))
        {
            //group 1 table name
            //group 2 column names and types
            p = Pattern.compile(createTable);
            m = p.matcher(inputQuery);
            while(m.find())
            {
                for(int i = 0; i < m.groupCount(); i++){
                    System.out.println("Group "+ i +":" + m.group(i));
                }
            }
            System.out.println("Good Query: " + inputQuery);
        }
        else if(inputQuery.matches(insertInto))
        {
            p = Pattern.compile(insertInto);
            m = p.matcher(inputQuery);
            while(m.find())
            {
                for(int i = 0; i < m.groupCount(); i++){
                    System.out.println("Group "+ i +":" + m.group(i));
                }
            }
            System.out.println("Good Query: " + inputQuery);

        }
        else { ThrowSyntaxError(inputQuery);}

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
        System.out.println("Invalid Command: " + query);
    }
}
