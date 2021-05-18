package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import FileManager.Config;

import java.sql.*;

import java.util.Vector;

public class ComputerRemove 
{
	private JTable table;
	private Connection connection;
	
	
	public ComputerRemove(JTable table)
	{
		this.table = table;
		this.connection = Config.getConnection();
		removeRow();
	}
	
	public void removeRow()
	{
		//get defaultablemodel from table
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		
		try
		{
			//choose yes no option
			int result = JOptionPane.showConfirmDialog(null, "Sure? you want to delete " + table.getSelectedRowCount() + " elements?",
					"Confirm",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			
			if(result == JOptionPane.YES_OPTION)
			{
			
				//get count of selected row
				int[] selectedRow = table.getSelectedRows();
				
				//repeat until delete all elements
				for(int i = selectedRow.length; i > 0; i--)
				{
					int currentRow = selectedRow[i - 1];
					removeFromDatabase(getRowData(currentRow));
					
					System.out.println(currentRow);
					model.removeRow(currentRow);
					//removeFromDatabase(getRowData(i - 1));					
				}
			}
			else if(result == JOptionPane.NO_OPTION)
			{
				return;
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
	
	private Vector<String> getRowData(int row)
	{
		//get data model from table
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		
		//create vector to store data
		Vector<String> rowData = new Vector<String>(4);
		try
		{
			for(int i = 0; i < 4; i++)
			{
				//get data from model and insert into vector
				rowData.add((String)model.getValueAt(row, i));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return rowData;
	}
	
	private void removeFromDatabase(Vector<String> row)
	{
		PreparedStatement preparedStatement = null;
		try
		{
			//LIMIT 0, 1preparedStatement = connection.preapreStatement("SELECT * FROM computer where brand = ? model = ? type = ? processor = ?;");
			//get prepared statement
			preparedStatement = connection.prepareStatement("DELETE from computer where brand = ? AND  model = ? AND type = ? AND processor = ?;");
			
			//fill data with values
			for(int i = 0; i < row.size(); i++)
			{
				preparedStatement.setString(i + 1, row.get(i));
			}
			
			//execute query
			preparedStatement.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
























