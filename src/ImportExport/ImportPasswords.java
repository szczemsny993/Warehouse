package ImportExport;
import javax.swing.JOptionPane;

import FileManager.Config;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ImportPasswords 
{
	protected Connection connection;
	protected File path;
	
	public ImportPasswords(File path)
	{
		try
		{
			this.connection = Config.getConnection();
			this.path = path;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean startRestore()
	{
		boolean isSuccess = false;
		
		if(!checkFile())
		{
			JOptionPane.showMessageDialog(null, "Nie mozna odnalezc pliku 'password_backup.sql'.");
			return isSuccess;
		}
		
		if(!isDatabaseEmpty())
		{
			int ret_val = JOptionPane.showConfirmDialog(null, "Baza danych zawiera dane, czy chcesz ja nadpisac?", "", JOptionPane.YES_NO_OPTION);
			if(ret_val == JOptionPane.YES_OPTION)
			{
				if(!cleanDatabase())
				{
					if(!restore())
					{
						return isSuccess;
					}
					else
					{
						isSuccess = true;
					}
				}
				else
				{
					restore();
					isSuccess = true;
				}
			}
		}
		else
		{
			restore();
			isSuccess = true;
		}
		
		return isSuccess;
	}
	
	protected boolean restore()
	{
		boolean isSuccess = false;
		String line = "";
		
		Statement statement = null;
		ResultSet resultSet = null;
		
		BufferedReader bufferedReader = null;
		FileReader fileReader = null;
		
		try
		{
			fileReader = new FileReader(this.path);
			bufferedReader = new BufferedReader(fileReader);
			
			statement = this.connection.createStatement();
			
			while((line = bufferedReader.readLine()) != null)
			{
				String table = checkWhichTable(line);
				statement.execute(line);
				resultSet = statement.executeQuery(String.format("SELECT id FROM %s LIMIT 2", table));
				
				if(countRecord(resultSet) <= 0)
				{
					JOptionPane.showMessageDialog(null, String.format("Problem z importowaniem danych do tabeli '%s'. Zmiany zostana cofniete.", table));
					return isSuccess;
				}
			}
			
			isSuccess = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(statement != null)
				{
					statement.close();
				}
				if(resultSet != null)
				{
					resultSet.close();
				}
				bufferedReader.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return isSuccess;
	}
	
	protected String checkWhichTable(String line)
	{
		String tableName = "";
		String tmp[] = null;
		
		tmp = line.split("VALUES");
		tmp = tmp[0].split(" ");
		
		tableName = tmp[2];
		
		return tableName;
	}
	
	protected boolean checkFile()
	{
		boolean isExists = false;
		
		String path = this.path.getPath();
		path += "\\password_backup.sql";
		
		try
		{
			this.path = new File(path);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(!this.path.exists() || this.path.isDirectory())
		{
			return isExists;
		}
		else
		{
			isExists = true;
		}
		
		return isExists;
	}
	
	protected boolean cleanDatabase()
	{
		boolean isSuccess = true;
		Statement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			statement = this.connection.createStatement();
			
			statement.execute("DELETE FROM client");
			resultSet = statement.executeQuery("SELECT id FROM client LIMIT 2");
			if(countRecord(resultSet) > 0)
			{
				JOptionPane.showMessageDialog(null, "Problem z czyszczeniem tabeli 'client'. Skontaktuj sie z tworca oprogramowania.");
				isSuccess = false;
				return isSuccess;
			}
			
			statement.execute("DELETE FROM passwords");
			resultSet = statement.executeQuery("SELECT id FROM passwords LIMIT 2");
			if(countRecord(resultSet) > 0)
			{
				JOptionPane.showMessageDialog(null, "Problem z czyszczeniem tabeli 'passwords'. Skontaktuj sie z tworca oprogramowania.");
				isSuccess = false;
				return isSuccess;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(statement != null)
				{
					statement.close();
				}
				if(resultSet != null)
				{
					resultSet.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return isSuccess;
	}
	
	protected boolean isDatabaseEmpty()
	{
		boolean isEmpty = true;
		Statement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			statement = this.connection.createStatement();
			resultSet = statement.executeQuery("SELECT id FROM client LIMIT 2");
			if(countRecord(resultSet) > 0)
			{
				isEmpty = false;
			}
			
			resultSet = statement.executeQuery("SELECT id FROM passwords LIMIT 2");
			if(countRecord(resultSet) > 0)
			{
				isEmpty = false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(statement != null)
				{
					statement.close();
				}
				if(resultSet != null)
				{
					resultSet.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return isEmpty;
	}
	
	protected int countRecord(ResultSet resultSet)
	{
		int counter = 0;
		
		try
		{
			while(resultSet.next())
			{
				counter++;
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return counter;
	}
}




























