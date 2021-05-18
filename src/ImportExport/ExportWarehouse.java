package ImportExport;
import javax.swing.*;

import FileManager.Config;

import java.sql.*;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class ExportWarehouse 
{
	protected Connection connection;
	protected File path;
	
	protected JProgressBar progressBar;
	
	public ExportWarehouse(File path, JProgressBar progressBar)
	{
		try
		{
			this.connection = Config.getConnection();
			this.path = path;
			this.progressBar = progressBar;
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
	}
	
	protected int countRecords(ResultSet resultSet)
	{
		int rowCount = 0;
		
		if(resultSet == null)
		{
			JOptionPane.showMessageDialog(null, "Problem z pobraniem danych do zapisu, skontaktuj sie z Tworca oprogramowania.");
			return -1;
		}
		
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
	
	protected boolean createFile()
	{
		boolean createFileSuccess = false;
		
		String path = this.path.toString();
		
		if(!path.toLowerCase().endsWith(".sql"))
		{
			path+= "\\warehouse_backup.sql";
		}
		
		try
		{
			this.path = new File(path);
			
			if(this.path.exists())
			{
				int opt = JOptionPane.showConfirmDialog(null, "Plik 'warehouse_backup.sql' istnieje. Czy chcesz go zastapic?", "", JOptionPane.YES_NO_OPTION);
				if(opt == JOptionPane.YES_OPTION)
				{
					this.path.delete();
					createFileSuccess = this.path.createNewFile();
					return createFileSuccess;
					
				}
				else
				{
					return false;
				}
			}
			else
			{
				createFileSuccess = this.path.createNewFile();
			}
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		return createFileSuccess;
	}
	
	public boolean startBackup()
	{
		boolean success = true;
		
		if(!createFile())
		{
			success = false;
			return success;
		}
		
		if(!backupData())
		{
			success = false;
			return success;
		}
		
		
		return success;
	}
	
	protected boolean backupData()
	{
		boolean success = false;
		ResultSet resultSet = null;
		Statement statement = null;
		
		try
		{
			statement = this.connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM bar_codes");
			
			if(!backupBarCodes(resultSet))
			{
				JOptionPane.showMessageDialog(null, "Problem z backupem kodow kreskowych, skontaktuj sie z tworca oprogramowania.");
				return false;
			}
			
			resultSet = statement.executeQuery("SELECT * FROM computer");
			if(!backupComputers(resultSet))
			{
				JOptionPane.showMessageDialog(null, "Problem z backupem komputerow, skontaktuj sie z tworca oprogramowania.");
				return false;
			}
			
			resultSet = statement.executeQuery("SELECT * FROM shipment");
			if(!backupShipment(resultSet))
			{
				JOptionPane.showMessageDialog(null, "Problem z backupem wysylek, skontaktuj sie z tworca oprogramowania");
				return false;
			}
			
			resultSet = statement.executeQuery("SELECT * FROM shipment_content");
			if(!backupShipmentPrivate(resultSet))
			{
				JOptionPane.showMessageDialog(null, "Problem z backupem zawartosci wysylek, skontaktuj sie z tworca oprogramowania");
				return false;
			}
			
			resultSet = statement.executeQuery("SELECT * FROM supply");
			if(!backupSupply(resultSet))
			{
				JOptionPane.showMessageDialog(null, "Problem z backupem dostaw, skontaktuj sie z tworca oprogramowania");
				return false;
			}
			
			resultSet = statement.executeQuery("SELECT * FROM supply_private");
			if(!backupSupplyPrivate(resultSet))
			{
				JOptionPane.showMessageDialog(null, "Problem z backupem zawartosci dostaw, skontaktuj sie z tworca oprogramowania");
				return false;
			}
			
			success = true;
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
				if(resultSet != null)
				{
					resultSet.close();
				}
				
				if(statement != null)
				{
					statement.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return success;
	}
	
	protected boolean backupBarCodes(ResultSet resultSet)
	{
		boolean isSuccess = false;
		String record = "";
		String recordsTable = "";
		
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		
		int rowCount = 0;
		int counter = 0;
		BoundedRangeModel model = null;
		
		try
		{
			rowCount = countRecords(resultSet);
			model = createBoundedRangeModel(0, rowCount);
			this.progressBar.setModel(model);
			
			while(resultSet.next())
			{
				record = String.format("('%d', '%s', '%s', '%s', '%s', '%s', '%s')", resultSet.getInt("id"), resultSet.getString("shipment_id"),
						resultSet.getString("casing"), resultSet.getString("motherboard"), resultSet.getString("hdd"), resultSet.getString("power_supply"),
						resultSet.getString("dvd"));
				recordsTable += record;
				
				counter++;
				this.progressBar.setValue(counter);
			}
			
			fileWriter = new FileWriter(this.path, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(new String("INSERT INTO bar_codes VALUES "));
			
			recordsTable += ";\n";
			recordsTable = recordsTable.replace(")(", "),(");
			
			bufferedWriter.write(recordsTable);
			bufferedWriter.close();
			resultSet.close();
			
			isSuccess = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return isSuccess;
	}
	
	protected boolean backupComputers(ResultSet resultSet)
	{
		boolean isSuccess = false;
		String record = "";
		String recordsTable = "";
		
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		
		int rowCount = 0;
		int counter = 0;
		BoundedRangeModel model = null;
		
		try
		{
			rowCount = countRecords(resultSet);
			model = createBoundedRangeModel(0, rowCount);
			this.progressBar.setModel(model);
			
			while(resultSet.next())
			{
				record = String.format("('%d', '%s', '%s', '%s', '%s', '%s', '%s', '%d', '%d')", resultSet.getInt("id"), resultSet.getString("brand"), 
						resultSet.getString("model"), resultSet.getString("type"), resultSet.getString("processor"), resultSet.getString("ram"), 
						resultSet.getString("hdd"), (resultSet.getBoolean("dvd") ? 1 : 0), (resultSet.getBoolean("license") ? 1 : 0));
				recordsTable += record;
				
				counter++;
				this.progressBar.setValue(counter);
			}
			
			fileWriter = new FileWriter(this.path, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("INSERT INTO computer VALUES ");
			
			recordsTable += ";\n";
			recordsTable = recordsTable.replace(")(", "),(");
			
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
	
	protected boolean backupShipment(ResultSet resultSet)
	{
		boolean isSuccess = false;
		String record = "";
		String recordsTable = "";
		
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		
		int rowCount = 0;
		int counter = 0;
		BoundedRangeModel model = null;
		
		try
		{
			rowCount = countRecords(resultSet);
			model = createBoundedRangeModel(0, rowCount);
			this.progressBar.setModel(model);
			
			while(resultSet.next())
			{
				record = String.format("('%d', '%s', '%s', '%d', '%d', '%s')", resultSet.getInt("id"), resultSet.getString("date"),
						resultSet.getString("shipment_id"), resultSet.getInt("warranty_length"), resultSet.getInt("pallete_count"),
						resultSet.getString("description"));
				
				recordsTable += record;
				
				counter++;
				this.progressBar.setValue(counter);
			}
			
			fileWriter = new FileWriter(this.path, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("INSERT INTO shipment VALUES ");
			
			recordsTable += ";\n";
			recordsTable = recordsTable.replace(")(", "),(");
			
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
	
	protected boolean backupShipmentPrivate(ResultSet resultSet)
	{
		boolean isSuccess = false;
		String record = "";
		String recordTable = "";
		
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		
		int rowCount = 0;
		int counter = 0;
		BoundedRangeModel model = null;
		
		try
		{
			rowCount = countRecords(resultSet);
			model = createBoundedRangeModel(0, rowCount);
			this.progressBar.setModel(model);
			
			while(resultSet.next())
			{
				record = String.format("('%d', '%s', '%d', '%d')", resultSet.getInt("id"), resultSet.getString("shipment_id"),
						resultSet.getInt("computer_id"), resultSet.getInt("computer_amount"));
				
				recordTable += record;
				
				counter++;
				this.progressBar.setValue(counter);
			}
			
			recordTable += ";\n";
			recordTable = recordTable.replace(")(", "),(");
			
			fileWriter = new FileWriter(this.path, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			
			bufferedWriter.write("INSERT INTO shipment_content VALUES ");
			bufferedWriter.write(recordTable);
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
	
	protected boolean backupSupply(ResultSet resultSet)
	{
		boolean isSuccess = false;
		String record = "";
		String recordTable = "";
		
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		
		int rowCount = 0;
		int counter = 0;
		BoundedRangeModel model = null;
		
		try
		{
			rowCount = countRecords(resultSet);
			model = createBoundedRangeModel(0, rowCount);
			this.progressBar.setModel(model);
			
			while(resultSet.next())
			{
				record = String.format("('%d', '%d', '%s', '%d', '%d', '%s')", resultSet.getInt("id"), resultSet.getInt("unique_id"),
						resultSet.getString("date"), resultSet.getInt("amount"), resultSet.getInt("pallete_count"),
						resultSet.getString("comment"));
				
				recordTable += record;
				
				counter++;
				this.progressBar.setValue(counter);
			}
			
			recordTable += ";\n";
			recordTable = recordTable.replace(")(", "),(");
			
			fileWriter = new FileWriter(this.path, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("INSERT INTO supply VALUES ");
			
			bufferedWriter.write(recordTable);
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
	
	protected boolean backupSupplyPrivate(ResultSet resultSet)
	{
		boolean isSuccess = false;
		String record = "";
		String recordTable = "";
		
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		
		int rowCount = 0;
		int counter = 0;
		BoundedRangeModel model = null;
		
		try
		{
			rowCount = countRecords(resultSet);
			model = createBoundedRangeModel(0, rowCount);
			this.progressBar.setModel(model);
			
			while(resultSet.next())
			{
				record = String.format("('%d', '%d', '%d', '%d')", resultSet.getInt("id"), resultSet.getInt("supply_id"),
						resultSet.getInt("computer_id"), resultSet.getInt("amount"));
				
				recordTable += record;
				
				counter++;
				this.progressBar.setValue(counter);
			}
			
			recordTable += ";\n";
			recordTable = recordTable.replace(")(", "),(");
			
			fileWriter = new FileWriter(this.path, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("INSERT INTO supply_private VALUES ");
			
			bufferedWriter.write(recordTable);
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
	
	protected BoundedRangeModel createBoundedRangeModel(int min, int max) 
	{
		BoundedRangeModel model = new DefaultBoundedRangeModel();
		model.setMinimum(min);
		model.setMaximum(max);
		
		return model;
	}
}









































