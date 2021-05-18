package ImportExport;
import java.sql.*;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import javax.swing.JOptionPane;

import FileManager.Config;

public class ImportWarehouse 
{
	protected Connection connection;
	protected File path;
	
	public ImportWarehouse(File path)
	{
		try
		{
			this.connection = Config.getConnection();
			this.path = path;
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
	}
	
	public boolean startRestore()
	{
		boolean isSuccess = false;
		
		if(checkFile())
		{
			restore();
		}
		
		if(!checkDatabase())
		{
			int ret_val = JOptionPane.showConfirmDialog(null, "Baza danych w module 'magazyn' zawiera dane. Czy chcesz je nadpisac?", "", JOptionPane.YES_NO_OPTION);
			if(ret_val == JOptionPane.YES_OPTION)
			{
				eraseWarehouse();
				if(restore())
				{
					JOptionPane.showMessageDialog(null, "Przywrocono modul 'magazyn'.");
					isSuccess = true;
					return isSuccess;
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Nie udalo sie przywrocic modulu 'magazyn'. Skontaktuj sie z tworca oprogramowania.");
					return isSuccess;
				}
			}
			else
			{
				return isSuccess;
			}
		}
		
		
		return isSuccess;
	}
	
	//return true if is empty
	protected boolean checkDatabase()
	{
		boolean isEmpty = true;
		
		Statement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			statement = this.connection.createStatement();
			resultSet = statement.executeQuery("SELECT id FROM bar_codes LIMIT 2");
			
			if(countRecord(resultSet) > 0)
			{
				isEmpty = false;
				return isEmpty;
			}
			
			resultSet = statement.executeQuery("SELECT id FROM computer LIMIT 2");
			if(countRecord(resultSet) > 0)
			{
				isEmpty = false;
				return isEmpty;
			}
			
			resultSet = statement.executeQuery("SELECT id FROM shipment LIMIT 2");
			if(countRecord(resultSet) > 0)
			{
				isEmpty = false;
				return isEmpty;
			}
			
			resultSet = statement.executeQuery("SELECT id FROM shipment_content LIMIT 2");
			if(countRecord(resultSet) > 0)
			{
				isEmpty = false;
				return isEmpty;
			}
			
			resultSet = statement.executeQuery("SELECT id FROM supply LIMIT 2");
			if(countRecord(resultSet) > 0)
			{
				isEmpty = false;
				return isEmpty;
			}
			
			resultSet = statement.executeQuery("SELECT id FROM supply_private LIMIT 2");
			if(countRecord(resultSet) > 0)
			{
				isEmpty = false;
				return isEmpty;
			}
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
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
				System.err.println(e.toString());
				e.printStackTrace();
			}
		}
		return isEmpty;
	}
	
	protected int countRecord(ResultSet resultSet)
	{
		int recordsCounter = 0;
		
		try
		{
			while(resultSet.next())
			{
				recordsCounter++;
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		return recordsCounter;
	}
	
	protected boolean eraseWarehouse()
	{
		boolean eraseSuccess = true;
		
		Statement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			statement = this.connection.createStatement();
			statement.execute("DELETE FROM computer");
			resultSet = statement.executeQuery("SELECT id FROM computer LIMIT 2");
			
			if(countRecord(resultSet) > 0)
			{
				eraseSuccess = false;
				return eraseSuccess;
			}
			
			statement.execute("DELETE FROM bar_codes");
			resultSet = statement.executeQuery("SELECT id FROM bar_codes LIMIT 2");
			if(countRecord(resultSet) > 0)
			{
				eraseSuccess = false;
				return eraseSuccess;
			}
			
			statement.execute("DELETE FROM shipment");
			resultSet = statement.executeQuery("SELECT id FROM shipment LIMIT 2");
			if(countRecord(resultSet) > 0)
			{
				eraseSuccess = false;
				return eraseSuccess;
			}
			
			statement.execute("DELETE FROM shipment_content");
			resultSet = statement.executeQuery("SELECT id FROM shipment_content");
			if(countRecord(resultSet) > 0)
			{
				eraseSuccess = false;
				return eraseSuccess;
			}
			
			statement.execute("DELETE FROM supply");
			resultSet = statement.executeQuery("SELECT id FROM supply");
			if(countRecord(resultSet) > 0)
			{
				eraseSuccess = false;
				return eraseSuccess;
			}
			
			statement.execute("DELETE FROM supply_private");
			resultSet = statement.executeQuery("SELECT id FROM supply_private");
			if(countRecord(resultSet) > 0)
			{
				eraseSuccess = false;
				return eraseSuccess;
			}
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
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
				System.err.println(e.toString());
				e.printStackTrace();
			}
		}
		
		return eraseSuccess;
	}
	
	/*
	protected boolean isThisTable(String line, String table)
	{
		boolean isFind = false;
		
		String tmp[] = line.split("VALUES");
		
		for(String var: tmp)
		{
			System.out.println(var);
		}
		
		return isFind;
	}
	*/
	
	protected boolean restore()
	{
		boolean isSuccess = false;
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		
		String line = "";
		
		Statement statement = null;
		
		try
		{
			fileReader = new FileReader(this.path);
			bufferedReader = new BufferedReader(fileReader);
			
			statement = this.connection.createStatement();
			
			while((line = bufferedReader.readLine()) != null)
			{
				String tableName = checkWhichTable(line);
				statement.execute(line);
				
				if(!isTableRestored(tableName))
				{
					JOptionPane.showMessageDialog(null, "Wystapil problem z przywracaniem danych.\nDokonane zmiany zostana cofniete.\n Skontakuj sie z producentem oprogramowania.");
					eraseWarehouse();
					return isSuccess;
				}
			}
			
			isSuccess = true;
			bufferedReader.close();
			fileReader.close();
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
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
				bufferedReader.close();
			}
			catch(Exception e)
			{
				System.err.println(e.toString());
				e.printStackTrace();
			}
		}
		
		return isSuccess;
	}
	
	protected String checkWhichTable(String line)
	{
		String result = "";
		String tmp[] = line.split("VALUES");
		
		tmp = tmp[0].split(" ");
		result = tmp[2];
		
		return result;
	}
	
	protected boolean checkFile()
	{
		boolean isExists = false;
		String tmp = "";
		
		try
		{
			tmp = this.path.getPath();
			tmp += "\\warehouse_backup.sql";
			
			this.path = new File(tmp);
			if(!this.path.exists() || !this.path.isFile())
			{
				JOptionPane.showMessageDialog(null, "Brak pliku 'warehouse_backup.sql'. Niepowodzenie operacji.");
				return isExists;
			}
			
			isExists = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return isExists;
	}
	
	protected boolean isTableRestored(String tableName)
	{
		boolean isSuccess = false;
		Statement statement = null;
		ResultSet resultSet = null;
		
		int counter = 0;
		
		try
		{
			statement = this.connection.createStatement();
			resultSet = statement.executeQuery(String.format("SELECT id FROM %s LIMIT 2", tableName));
			
			while(resultSet.next())
			{
				counter++;
			}
			
			if(counter > 0)
			{
				isSuccess = true;
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
}









































