package Dialogs;

import FileManager.Config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import javax.swing.JOptionPane;

public class ShipmentRemove
{
	
	
	protected Connection connection;
	protected String shipmentName;
	
	public ShipmentRemove(String shipmentId)
	{
		try
		{
			this.connection = Config.getConnection();
			this.shipmentName = shipmentId;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean removeShipment()
	{
		boolean isSuccess = false;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int counter = 0;
		
		try
		{
			if(!removeBarCodes())
			{
				return isSuccess;
			}
			
			if(!removeShipmentContent())
			{
				return isSuccess;
			}
			
			preparedStatement = this.connection.prepareStatement("DELETE FROM shipment WHERE shipment_id = ?");
			preparedStatement.setString(1, this.shipmentName);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			
			preparedStatement = this.connection.prepareStatement("SELECT id FROM shipment WHERE shipment_id = ?");
			preparedStatement.setString(1, this.shipmentName);
			resultSet = preparedStatement.executeQuery();
			
			counter = resultSet.getFetchSize();
			if(counter <= 0)
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
		
		return isSuccess;
	}
	
	protected boolean removeBarCodes()
	{
		boolean isSuccess = false;
		
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		
		int fetchCount = 0;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("DELETE FROM bar_codes WHERE shipment_id = ?");
			preparedStatement.setString(1, this.shipmentName);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			
			preparedStatement = this.connection.prepareStatement("SELECT id FROM bar_codes WHERE shipment_id = ? LIMIT 1");
			preparedStatement.setString(1, shipmentName);
			resultSet = preparedStatement.executeQuery();
			
			fetchCount = resultSet.getFetchSize();
			if(fetchCount <= 0)
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
				if(resultSet != null)
				{
					resultSet.close();
				}
				
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
		
		return isSuccess;
	}
	
	protected boolean removeShipmentContent()
	{
		boolean isSuccess = false;
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		int counter = 0;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("DELETE FROM shipment_content WHERE shipment_id = ?");
			preparedStatement.setString(1, this.shipmentName);
			preparedStatement.execute();
			preparedStatement.close();
			
			preparedStatement = this.connection.prepareStatement("SELECT id FROM shipment_content WHERE shipment_id = ? LIMIT 1");
			preparedStatement.setString(1, this.shipmentName);
			resultSet = preparedStatement.executeQuery();
			
			counter = resultSet.getFetchSize();
			if(counter <= 0)
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
		
		return isSuccess;
	}
	
	
}









































