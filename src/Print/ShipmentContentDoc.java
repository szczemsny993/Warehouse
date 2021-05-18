package Print;

import FileManager.Config;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import FileManager.DateParse;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import java.sql.*;

import java.util.Vector;

public class ShipmentContentDoc 
{
	protected Connection connection;
	
	protected String shipmentName;
	protected String shipmentDate;
	
	protected Vector<Integer> idTable;
	protected Vector<Integer> amountTable;
	
	public ShipmentContentDoc(String shipmentName)
	{
		try
		{
			this.connection = Config.getConnection();
			this.shipmentName = shipmentName;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		loadShipmentDate();
		loadShipmentData();
	}
	
	public void print()
	{
		runChooseDialog();
	}
	
	protected void loadShipmentDate()
	{
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT date FROM shipment WHERE shipment_id = ?");
			preparedStatement.setString(1, this.shipmentName);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				this.shipmentDate = DateParse.parseDateToString(resultSet.getDate("date"));
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
	
	protected void loadShipmentData()
	{
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		
		this.idTable = new Vector<Integer>();
		this.amountTable = new Vector<Integer>();
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT * FROM shipment_content WHERE shipment_id = ?");
			preparedStatement.setString(1, this.shipmentName);
			resultSet = preparedStatement.executeQuery();
			
			if(checkResultSet(resultSet) == false)
			{
				//here is place to handle error dialog
				return;
			}
			
			while(resultSet.next())
			{
				this.idTable.add(resultSet.getInt("computer_id"));
				this.amountTable.add(resultSet.getInt("computer_amount"));
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
	
	protected void createHeader(XWPFDocument document)
	{
		XWPFParagraph paragraph = null;
		XWPFRun run = null;
		String text = "Wysyłka dnia: " + this.shipmentDate + "\n\n";
		
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		
		run = paragraph.createRun();
		run.setFontSize(20);
		run.setBold(true);
		
		run.setText(text);
		run.addBreak();
	}
	
	protected void loadComputerIntoDocument(XWPFDocument document)
	{
		XWPFParagraph paragraph = document.createParagraph();
		XWPFRun run = null;
		
		ResultSet resultSet = null;
		Statement statement = null;
		
		try
		{
			statement = this.connection.createStatement();
			resultSet = statement.executeQuery(createComputerQuery(convertVectorToString()));
			
			for(int i = 0; i < this.idTable.size(); i++)
			{
				while(resultSet.next())
				{
					if(resultSet.getInt("id") == idTable.get(i))
					{
						run = paragraph.createRun();
						run.setFontSize(12);
						
						run.setText(String.format("%s %s %s %s - %dszt.", 
								resultSet.getString("brand"), resultSet.getString("model"),
								resultSet.getString("type"), resultSet.getString("processor"),
								this.amountTable.get(i)));
						run.addBreak();
						break;
					}
				}
				
				resultSet.absolute(0);
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
	
	protected String convertVectorToString()
	{
		String tableString = this.idTable.toString();
		
		tableString = tableString.replace("[", "(");
		tableString = tableString.replace("]", ")");
		
		return tableString;
	}
	
	protected String createComputerQuery(String computerTable)
	{
		String query = "SELECT id, brand, model, type, processor FROM computer WHERE id IN " + computerTable;
		//System.out.println(query);
		
		return query;
	}
	
	/*
	private boolean writeComputerIntoDocument(XWPFDocument document)
	{
		
	}
	*/
	
	protected void runChooseDialog()
	{
		JFileChooser fileChooser = new JFileChooser();
		FileOutputStream out = null;
		XWPFDocument document = new XWPFDocument();
		int retval = 0;
		
		createHeader(document);
		loadComputerIntoDocument(document);
		
		retval = fileChooser.showSaveDialog(null);
		if(retval == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			if(file == null)
			{
				return;
			}
			
			if(!file.getName().toLowerCase().endsWith(".docx"))
			{
				file = new File(file.getParentFile(), file.getName() + ".docx");
			}
			
			try
			{
				out = new FileOutputStream(file);
				document.write(out);
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
					document.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}







































