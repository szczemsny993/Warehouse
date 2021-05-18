package FileManager;
import java.io.*;
//import java.nio.file.*;

import java.sql.Connection;

import java.util.Vector;

public class Config 
{
	protected final String configFileName = "config.txt";
	protected String databaseUser;
	protected String databaseUserPassword;
	protected String databaseName;
	protected String databaseHost;
	
	protected static Connection connection;
	
	public Config()
	{		
		Vector<String> lines = readLines();
		Vector<String> values = null;
		
		for(int i = 0; i < lines.size(); i++)
		{
			values = getLineValues(lines.get(i));
			adjustValues(values);
			
			/*
			for(int j = 0; j < values.size(); j++)
			{
				System.out.println(values.get(j));
			}
			*/
			
			values.clear();
		}
	}
	
	//allocators for databaseUser;
	public void setDatabaseUser(String value)
	{
		try
		{
			this.databaseUser = value;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getDatabaseUser()
	{
		return this.databaseUser;
	}
	
	//allocators for databaseUserPassword
	public void setDatabaseUserPassword(String value)
	{
		try
		{			
			if(value.contains("none"))
			{
				this.databaseUserPassword = new String("");
			}
			else if(value == "")
			{
				this.databaseUserPassword = "";
			}
			else
			{
				return;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getDatabaseUserPassword()
	{
		return this.databaseUserPassword;
	}
	
	//allocators for databaseName
	public void setDatabaseName(String value)
	{
		try
		{
			if(value == null)
				return;
			
			if(value == "")
				return;
			
			this.databaseName = value;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getDatabaseName()
	{
		return this.databaseName;
	}
	
	//allocators for databaseHost
	public void setDatabaseHost(String value)
	{
		try
		{
			if(value == null)
				return;
			
			if(value == "")
				return;
			
			this.databaseHost = value;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getDatabaseHost()
	{
		return this.databaseHost;
	}
	
	/* archive func for posterity
	//function which read whole file to string
	private String readFile()
	{
		String toParse = null;
		try
		{
			//create handle to config file
			File file = new File("files/" + configFileName);
			
			//read whole file to byte array
			byte[] tmpArray = Files.readAllBytes(file.toPath());
			
			//parse byte to String object
			toParse = new String(tmpArray);
			
			try
			{
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String s = null;
				
				while( (s = br.readLine()) != null )
					System.out.println(s);
				
				br.close();
			}
			catch(Exception f)
			{
				f.printStackTrace();
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return toParse;
	}
	
	*/
	
	//function extracion lines from whole config file
	private Vector<String> readLines()
	{
		//init vector for lines
		Vector<String> result = new Vector<String>();
		
		try
		{
			//create file class for config file
			File file = new File("files/" + configFileName);
			
			//init filereader and bufferedreader for read lines from file
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line = null;
			
			//loop until can read next line form fine
			while( (line = bufferedReader.readLine()) != null )
			{
				result.add(line);
			}
			
			//close file class
			bufferedReader.close();
			fileReader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	//function extract values from one line
	private Vector<String> getLineValues(String line)
	{
		Vector<String> lineValues = new Vector<String>();
		
		//extract values from line
		String[] tmp = line.split("=");
		lineValues.add(tmp[0]);
		lineValues.add(tmp[1]);
		
		return lineValues;
	}
	
	//function adjust values to var fields
	private void adjustValues(Vector<String> values)
	{
		//adjust values from vector to global var
		switch(values.get(0))
		{
			case "databaseuser":
			{
				//System.out.println(values.get(1));
				setDatabaseUser(values.get(1));
				break;
			}
			case "databasepassword":
			{
				//System.out.println(values.get(1));
				setDatabaseUserPassword(values.get(1));
				break;
			}
			case "databasehost":
			{
				//System.out.println(values.get(1));
				setDatabaseHost(values.get(1));
				break;
			}
			case "databasename":
			{
				//System.out.println(values.get(1));
				setDatabaseName(values.get(1));
				break;
			}
			default:
				break;
		}
	}
	
	//function for debug
	
	public void testValue()
	{
		System.out.println(databaseUser);
		System.out.println(databaseUserPassword);
		System.out.println(databaseName);
		System.out.println(databaseHost);
	}
	
	
	/*
	//to debug
	public static void main(String[] args)
	{
		Config config = new Config();
	}
	*/
	
	public static void setConnection(Connection con)
	{
		try
		{
			connection = con;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection()
	{
		return connection;
	}
}
















































