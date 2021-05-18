package Print;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFileChooser;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import FileManager.Config;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import java.sql.*;

public class SupplyPrintDoc 
{
	protected Connection connection;
	protected int supplyId;
	protected JTable table;
	
	protected String supplyDate;
	protected int computerAmount;
	protected int palleteCount;
	
	public SupplyPrintDoc(int supplyId, JTable table)
	{
		try
		{
			this.connection = Config.getConnection();
			this.supplyId = supplyId;
			this.table = table;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		loadSupplyData();
	}
	
	protected void loadSupplyData()
	{
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT * FROM supply WHERE unique_id = ?");
			preparedStatement.setInt(1, this.supplyId);
			resultSet = preparedStatement.executeQuery();
			
			if(checkResultSet(resultSet) == false)
			{
				//here is place to handle error message
				return;
			}
			
			while(resultSet.next())
			{
				this.supplyDate = resultSet.getString("date");
				this.computerAmount = resultSet.getInt("amount");
				this.palleteCount = resultSet.getInt("pallete_count");
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
	
	protected void createHeader(XWPFDocument document)
	{
		XWPFRun run = null;
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		
		run = paragraph.createRun();
		run.setFontSize(20);
		run.setText("Dostawa dnia " + this.supplyDate);
		run.addBreak();
		run.addBreak();
	}
	
	protected void createContentParagraph(XWPFDocument document)
	{
		XWPFParagraph paragraph = null;
		XWPFRun run = null;
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		
		paragraph = document.createParagraph();
		
		for(int i = 0; i < model.getRowCount(); i++)
		{
			run = paragraph.createRun();
			run.setFontSize(12);
			
			run.setText(String.format("%s %s %s %s - %dszt.", (String)model.getValueAt(i, 0), (String)model.getValueAt(i, 1), (String)model.getValueAt(i, 3),
																(String)model.getValueAt(i, 2), convertObjectToString(model.getValueAt(i, 8))));
			run.addBreak();
		}
	}
	
	protected int convertObjectToString(Object object)
	{
		int result = -1;
		String convertedObject = object.toString();
		result = Integer.valueOf(convertedObject);
		
		return result;
	}
	
	public void print()
	{
		JFileChooser fileChooser = new JFileChooser();
		FileOutputStream out = null;
		XWPFDocument document = new XWPFDocument();
		
		int ret_val = fileChooser.showSaveDialog(null);
		
		if(ret_val == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			
			if(!file.getName().toLowerCase().endsWith(".docx"))
			{
				file = new File(file.getParentFile(), file.getName() + ".docx");
			}
			
			try
			{
				createHeader(document);
				createContentParagraph(document);
				
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









































