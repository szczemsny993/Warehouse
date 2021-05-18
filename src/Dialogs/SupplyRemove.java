package Dialogs;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import FileManager.Config;

import java.sql.*;

import java.util.Vector;

class SupplyRemove
{
	protected Connection connection;
	protected JTable table;
	
	public SupplyRemove(JTable table)
	{
		try
		{
			this.connection = Config.getConnection();
			this.table = table;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void deleteSelected()
	{
		Vector<Integer> idTable = getSelectedRows();
		
		int rev_val = JOptionPane.showConfirmDialog(table, "Czy na pewno chcesz usunąć: " + idTable.size() + " elementów?", "Potwierdz", JOptionPane.YES_NO_OPTION);
		if(rev_val == JOptionPane.YES_OPTION)
		{
			for(Integer row: idTable)
			{
				deleteSupply(row);
			}
			
			JOptionPane.showMessageDialog(table, "Usunięto: " + idTable.size() + " rekordów.", "", JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			return;
		}
	}
	
	protected Vector<Integer> getSelectedRows()
	{
		Vector<Integer> idTable = new Vector<Integer>();
		int[] selected = table.getSelectedRows();
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		
		for(Integer row : selected)
		{
			Object tmpObject = model.getValueAt(row, 0);
			Integer result = objectToInteger(tmpObject);
			idTable.add(result);
		}
		
		return idTable;
	}
	
	protected void deleteSupply(int supplyId)
	{
		PreparedStatement preparedStatement = null;
		Statement statement = null;
		Vector<Integer> contentId = getContentIdTable(supplyId);
		
		try
		{
			preparedStatement = this.connection.prepareStatement("DELETE FROM supply WHERE unique_id = ?");
			preparedStatement.setInt(1, supplyId);
			preparedStatement.executeUpdate();
			
			preparedStatement.close();
			
			for(int i = 0; i < contentId.size(); i++)
			{
				preparedStatement = this.connection.prepareStatement("DELETE FROM supply_private WHERE id = ?");
				preparedStatement.setInt(1, contentId.get(i));
				preparedStatement.executeUpdate();
				
				preparedStatement.close();
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
				if(preparedStatement != null)
				{
					preparedStatement.close();
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
	}
	
	protected Vector<Integer> getContentIdTable(int supplyId)
	{
		Vector<Integer> idTable = new Vector<Integer>();
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT id FROM supply_private WHERE supply_id = ?");
			preparedStatement.setInt(1, supplyId);
			resultSet = preparedStatement.executeQuery();
			
			if(checkResultSet(resultSet) == false)
			{
				JOptionPane.showMessageDialog(null, "Problem z pobraniem zawartości dostawy, skontatkuj sie z twórcą oprogramowania", "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			
			while(resultSet.next())
			{
				idTable.add(resultSet.getInt("id"));
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
				if(preparedStatement != null)
				{
					preparedStatement.close();
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
		
		return idTable;
	}
	
	protected boolean checkResultSet(ResultSet resultSet)
	{
		boolean statement = true;
		int counter = 0;
		
		if(resultSet == null)
		{
			statement = false;
			return statement;
		}
		
		try
		{
			while(resultSet.next())
			{
				counter++;
			}
			
			resultSet.absolute(0);
			
			if(counter == 0)
			{
				statement = false;
				return statement;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return statement;
	}
	
	protected String vectorToString(Vector<Integer> table)
	{
		String result = table.toString();
		
		result = result.replace("[", "(");
		result = result.replace("]", ")");
		
		return result;
	}
	
	protected Integer objectToInteger(Object object)
	{
		Integer result = 0;
		String tmp = "";
		
		try
		{
			tmp = object.toString();
			result = Integer.valueOf(tmp);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
}






























