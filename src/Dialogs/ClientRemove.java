package Dialogs;

import javax.swing.*;

import FileManager.Config;

import java.sql.*;

import java.util.Vector;

public class ClientRemove
{
	protected Connection connection;
	protected Vector<Integer> id;
	
	public ClientRemove(Vector<Integer> id) throws IllegalArgumentException
	{
		int result = 0;
		
		try
		{
			this.connection = Config.getConnection();
			if(checkTable(id))
			{
				this.id = id;
			}
			else
			{
				throw new IllegalArgumentException("Number must be positive.");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		result = JOptionPane.showConfirmDialog(null, "Czy chcesz usunac " + this.id.size() + " elementow?","Potwierdzenie", 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if(result == JOptionPane.YES_OPTION)
		{
			removeRow();
		}
		else if(result == JOptionPane.NO_OPTION)
		{
			return;
		}
		else if(result == JOptionPane.CANCEL_OPTION)
		{
			return;
		}
		
		JOptionPane.showMessageDialog(null, "Usunieto" + this.id.size() + " elementow.", "Dialog", JOptionPane.PLAIN_MESSAGE);
	}
	
	private boolean checkTable(Vector<Integer> idTable)
	{
		boolean statement = true;
		
		if(idTable == null || idTable.size() <= 0)
		{
			return statement;
		}
		
		return statement;
	}
	
	private void removeRow()
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			for(int i = 0; i < this.id.size(); i++)
			{
				preparedStatement = this.connection.prepareStatement("DELETE FROM client WHERE id = ?");
				preparedStatement.setInt(1, this.id.get(i));
				preparedStatement.executeUpdate();
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
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/*
	private void printRemoveDialog()
	{
		
	}
	*/
}

































