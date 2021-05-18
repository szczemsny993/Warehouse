package ImportExport;
import javax.swing.*;

import FileManager.Config;

import java.sql.*;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class ExportPassword 
{
	protected Connection connection;
	protected File path;
	protected JProgressBar progressBar;
	
	public ExportPassword(File file, JProgressBar progressBar)
	{
		try
		{
			if(connection != null)
			{
				this.connection = Config.getConnection();
			}
			
			if(file != null)
			{
				this.path = file;
			}
			
			if(progressBar != null)
			{
				this.progressBar = progressBar;
			}
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
	}
	
	protected boolean createFile()
	{
		boolean isSuccess = false;
		
		String tmpPath = this.path.getPath();
		tmpPath += "\\password_backup.sql";
		this.path = new File(tmpPath);
		
		try
		{
			if(this.path.exists() && this.path.isFile())
			{
				int ret_val = JOptionPane.showConfirmDialog(null, "Plik 'password_backup' istnieje. Czy chcesz go nadpisać?", "", JOptionPane.YES_NO_OPTION);
				if(ret_val == JOptionPane.YES_OPTION)
				{
					this.path.delete();
					this.path.createNewFile();
					
					isSuccess = true;
				}
				else
				{
					return false;
				}
			}
			else
			{
				this.path.createNewFile();
				
				isSuccess = true;
			}
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		return isSuccess;
	}
	
	public boolean startBackup()
	{
		boolean isSuccess = false;
		Statement statement = null;
		ResultSet resultSet = null;
		
		try
		{			
			if(!createFile())
			{
				JOptionPane.showMessageDialog(null, "Problem z utworzeniem pliku 'password_backup.sql");
				return isSuccess;
			}
			
			statement = this.connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM client");
			
			if(!backupClients(resultSet))
			{
				JOptionPane.showMessageDialog(null, "Wystapil problem z backupem klientow, skontaktuj sie z tworca oprogramowania");
				return isSuccess;
			}
			
			resultSet = statement.executeQuery("SELECT * FROM passwords");
			if(!backupPasswords(resultSet))
			{
				JOptionPane.showMessageDialog(null, "Wystapil problem z backupem zawartosci 'dane dostepowe'. Skontaktuj sie z tworca oprogramowania");
				return isSuccess;
			}
			
			isSuccess = true;
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
		
		return isSuccess;
	}
	
	protected boolean backupClients(ResultSet resultSet)
	{
		boolean isSuccess = true;
		
		String record = "";
		String recordsTable = "";
		
		int recordCount = 0;
		int counter = 0;
		
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		BoundedRangeModel progressBarModel = null;
		
		if(resultSet == null)
		{
			isSuccess = false;
			return isSuccess;
		}
		
		try
		{
			recordCount = countRecords(resultSet);
			progressBarModel = createProgressBarModel(0, recordCount);
			this.progressBar.setModel(progressBarModel);
			
			while(resultSet.next())
			{
				record = String.format("('%d', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", resultSet.getInt("id"), resultSet.getString("code"), 
						resultSet.getString("name"), resultSet.getString("nip"), resultSet.getString("city"), resultSet.getString("post_code"),
						resultSet.getString("address"), resultSet.getString("telephone"));
				
				recordsTable += record;
				counter++;
				this.progressBar.setValue(counter);
			}
			
			recordsTable += ";\n";
			recordsTable = recordsTable.replace(")(", "),(");
			
			fileWriter = new FileWriter(this.path, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("INSERT INTO client VALUES ");
			
			bufferedWriter.write(recordsTable);
			bufferedWriter.close();
			resultSet.close();
			
			isSuccess = true;
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		return isSuccess;
	}
	
	protected boolean backupPasswords(ResultSet resultSet)
	{
		boolean isSuccess = false;
		String record = "";
		String recordsTable = "";
		
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		
		int counter = 0;
		int recordCount = 0;
		BoundedRangeModel model = null;
		
		if(resultSet == null)
		{
			return isSuccess;
		}
		
		try
		{
			recordCount = countRecords(resultSet);
			model = createProgressBarModel(0, recordCount);
			this.progressBar.setModel(model);
			
			while(resultSet.next())
			{
				record = String.format("('%d', '%d', '%s', '%s', '%s', '%s', '%d', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", resultSet.getInt("id"),
						resultSet.getInt("client_id"), resultSet.getString("type"), resultSet.getString("date"), resultSet.getString("code"),
						resultSet.getString("name"), resultSet.getInt("port"), resultSet.getString("user"), resultSet.getString("password"),
						resultSet.getString("description"), resultSet.getString("location"), resultSet.getString("serial"), resultSet.getString("mac"),
						resultSet.getString("internal_id"), resultSet.getString("license"), resultSet.getString("position"));
				
				recordsTable += record;
				counter++;
				this.progressBar.setValue(counter);
			}
			
			recordsTable += ";\n";
			recordsTable = recordsTable.replace(")(", "),(");
			
			fileWriter = new FileWriter(this.path, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("INSERT INTO passwords VALUES ");
			
			bufferedWriter.write(recordsTable);
			bufferedWriter.close();
			resultSet.close();
			
			isSuccess = true;
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		return isSuccess;
	}
	
	protected int countRecords(ResultSet resultSet)
	{
		int rowCount = 0;
		
		try
		{
			while(resultSet.next())
			{
				rowCount++;
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		return rowCount;
	}
	
	protected BoundedRangeModel createProgressBarModel(int min, int max)
	{
		BoundedRangeModel model = new DefaultBoundedRangeModel();
		model.setMinimum(min);
		model.setMaximum(max);
		
		return model;
	}
}













































