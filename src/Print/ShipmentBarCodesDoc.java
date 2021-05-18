package Print;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.io.File;
import java.io.FileOutputStream;
import org.apache.poi.xssf.usermodel.*;

import FileManager.Config;
import FileManager.DateParse;

import org.apache.poi.ss.usermodel.*;

import java.time.*;

import java.sql.*;

public class ShipmentBarCodesDoc 
{
	protected Connection connection;
	//protected JTable barCodeTable;
	protected String shipmentName;
	
	protected String date;
	protected int warrantyLength;
	
	public ShipmentBarCodesDoc(String shipmentName)
	{
		try
		{
			this.connection = Config.getConnection();
			//this.barCodeTable = table;
			this.shipmentName = shipmentName;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		loadShipmentData();
		
		chooseDialog();
	}
	
	private void loadShipmentData()
	{
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT date, warranty_length FROM shipment WHERE shipment_id = ?");
			preparedStatement.setString(1, this.shipmentName);
			resultSet = preparedStatement.executeQuery();
			
			if(checkResultSet(resultSet) == false)
			{
				return;
			}
			
			while(resultSet.next())
			{
				this.date = DateParse.parseDateToString(resultSet.getDate("date"));
				this.warrantyLength = resultSet.getInt("warranty_length");
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
	}
	
	private boolean checkResultSet(ResultSet resultSet)
	{
		boolean statement = true;
		
		int counter = 0;
		
		try
		{
			if(resultSet == null)
			{
				statement = false;
				return statement;
			}
			
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
	
	private XSSFWorkbook createExcelFile()
	{
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet spreadsheet = workbook.createSheet("Wysylka");
		XSSFRow row = null;
		
		int counter = 0;
		
		String newWarrantyDate = warrantyNewDate(this.date, this.warrantyLength);
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT * FROM bar_codes WHERE shipment_id = ?");
			preparedStatement.setString(1, this.shipmentName);
			resultSet = preparedStatement.executeQuery();
			
			
			
			while(resultSet.next())
			{
				row = spreadsheet.createRow(counter);
				
				row.createCell(0).setCellValue((String)resultSet.getString("casing"));
				row.createCell(1).setCellValue((String)resultSet.getString("motherboard"));
				row.createCell(2).setCellValue((String)resultSet.getString("hdd"));
				row.createCell(3).setCellValue((String)resultSet.getString("power_supply"));
				row.createCell(4).setCellValue((String)resultSet.getString("dvd"));
				row.createCell(5).setCellValue(newWarrantyDate);
				
				counter++;
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
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return workbook;
	}
	
	private void chooseDialog()
	{
		JFileChooser fileChooser = new JFileChooser();
		FileOutputStream out = null;
		XSSFWorkbook workbook = createExcelFile();
		int returnValue = fileChooser.showSaveDialog(null);
		
		
		if(returnValue == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			if(file == null)
			{
				//pleace to handle error
				return;
			}
			
			if(!file.getName().toLowerCase().endsWith(".xlsx"))
			{
				file = new File(file.getParentFile(), file.getName() + ".xlsx");
			}
			
			try
			{
				out = new FileOutputStream(file);
				workbook.write(out);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					out.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private String warrantyNewDate(String stringDate, int length)
	{		
		Date date = DateParse.parseStringToDate(stringDate);
		LocalDate local = DateParse.parseDateToLocalDate(date);
		local = local.plusMonths(length);
		
		String newWarrantyDate = DateParse.localDateToString(local);
		//System.out.println(newWarrantyDate);
		
		return newWarrantyDate;
	}
}


































