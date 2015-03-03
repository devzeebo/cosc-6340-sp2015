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
        String select = "SELECT\\s+(\\w+),*\\s*(.*)\\s+FROM\\s+((\\w+)).*";
        String selectAll = "SELECT\\s+\\*\\s+FROM\\s+(.*).*\\s+WHERE\\s+(\\w+)\\s*=\\s*((\\\"*\\w+\\\"*))";
        String createTable = "CREATE\\s+(\\w+\\s+)\\((\\w+\\s+(INT|STRING),*\\s*)+\\)";
        String insertInto = "INSERT\\s+INTO\\s+(\\w+)\\s+\\((\"*\\w+\\\"*,*\\s*)+\\)";

        if(inputQuery.matches(select))
        {
            select = "SELECT\\s+(.*)\\s+FROM\\s+((\\w+)).*";
            //group 1 fields
            //group 2 table name
            String fields = "";
            String table = "";
            List<String> values;
            p = Pattern.compile(select);
            m = p.matcher(inputQuery);
            while(m.find())
            {
                fields = m.group(1);
                table = m.group(2);
                //FOR Debugging
                //for(int i = 0; i < m.groupCount(); i++){
                    //System.out.println("Group "+ i +":" + m.group(i));
                //}
            }

            values = Arrays.asList(fields.split("\\s*,\\s*"));

            Sql.select(values).from(Arrays.asList(table)).execute();
            Sql.printTable(table);
        }
        else if (inputQuery.matches(selectAll))
        {
            //group 1 table names
            //group 2 where 1
            //group 3 where 2
            List<String> tableNames;
            String tables = "";
            String field = "";
            String value = "";
            p = Pattern.compile(selectAll);
            m = p.matcher(inputQuery);
            while(m.find())
            {
                //FOR DEBUGGING
                //for(int i = 0; i < m.groupCount(); i++){
                    //System.out.println("Group "+ i +":" + m.group(i));
                //}
                tables = m.group(1);
                field = m.group(2);
                value = m.group(3);
            }
            tableNames = Arrays.asList(tables.split("\\s*,\\s*"));
            Sql.select(Arrays.asList("*")).from(tableNames).where(field, value).execute();
            for (int i = 0; i < tableNames.size(); i++) {
                Sql.printTable(tableNames.get(i));
            }
        }
        else if(inputQuery.matches(createTable))
        {
            createTable = "CREATE\\s+(\\w+)\\s+\\(((.*))\\)";
            //group 1 table name
            //group 2 column names and types
            String tableName = "";
            String fieldN = "";
            Map<String, String> map = new HashMap<String, String>();
            List<String> fields;
            p = Pattern.compile(createTable);
            m = p.matcher(inputQuery);

            //for debugging
            while(m.find())
            {
                tableName = m.group(1);
                fieldN = m.group(2);
                //for debugging
                for(int i = 0; i < m.groupCount(); i++)
                    System.out.println("Group "+ i +":" + m.group(i));
            }

            fields = Arrays.asList(fieldN.split("\\s*,\\s*"));
            for (int i = 0; i < fields.size(); i++) {
                String field = fields.get(i);
                List<String> fieldAndType = Arrays.asList(field.split("\\s+"));
                map.put(fieldAndType.get(1), fieldAndType.get(0));
            }

            Sql.createTable(tableName, map);
            Sql.printTable(tableName);
        }
        else if(inputQuery.matches(insertInto))
        {
            insertInto = "INSERT\\s+INTO\\s+(\\w+)\\s+\\(((.*))\\)";
            String tableName = "";
            String fields = "";
            List<String> values;

            p = Pattern.compile(insertInto);
            m = p.matcher(inputQuery);
            while(m.find())
            {
                for(int i = 0; i < m.groupCount(); i++){
                    System.out.println("Group "+ i +":" + m.group(i));
                }
            }
            values = Arrays.asList(fields.split("\"*\\s*,\\s*"));
            Sql.insertInto(tableName, values);
            Sql.printTable(tableName);
        }
        else { ThrowSyntaxError(inputQuery);}

    }

    public static void ThrowSyntaxError(String query)
    {
        System.out.println("Invalid Command: " + query);
    }
}
