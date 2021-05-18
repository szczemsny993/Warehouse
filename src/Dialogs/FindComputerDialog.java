package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import FileManager.Config;

import java.util.HashMap;
import java.util.Vector;

import java.sql.*;

public class FindComputerDialog extends JDialog implements MouseListener
{
	private static final long serialVersionUID = 1L;
	protected HashMap<String, String> row;
	protected Vector<Integer> idTable;
	
	protected Connection connection;
	
	protected JTable table;
	
	protected int selectedId;
	
	public FindComputerDialog()
	{
		super(null, "Znajdz komputer", JDialog.ModalityType.DOCUMENT_MODAL);

		try
		{
			this.connection = Config.getConnection();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setSize(800, 300);
		setCenterLocation();
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		this.table = createTable(createTableModel());
		
		container.add(new JScrollPane(this.table), BorderLayout.CENTER);
	}
	
	private JTable createTable(DefaultTableModel model)
	{
		JTable table = new JTable(model);
		table.addMouseListener(this);
		table.getTableHeader().setReorderingAllowed(false);
		
		return table;
	}
	
	private DefaultTableModel createTableModel()
	{
		DefaultTableModel model = new DefaultTableModel()
				{
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int col)
					{
						return false;
					}
				};
				
		model.addColumn("Marka");
		model.addColumn("Model");
		model.addColumn("Typ");
		model.addColumn("Procesor");
		model.addColumn("Ram");
		model.addColumn("HDD");
		model.addColumn("DVD");
		model.addColumn("Licencja");
		
		return model;
	}
	
	public HashMap<String, String> showDialog()
	{
		loadComputersFromDB();
		setVisible(true);
		
		return this.row;
	}
	
	
	public void mouseClicked(MouseEvent e)
	{
		JTable table = (JTable)e.getSource();
		Point point = e.getPoint();
		int row = table.rowAtPoint(point);
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		this.row = new HashMap<String, String>();
		
		if(e.getClickCount() == 2 && row > -1)
		{
			this.row.put("brand", (String)model.getValueAt(row, 0));
			this.row.put("model", (String)model.getValueAt(row, 1));
			this.row.put("type", (String)model.getValueAt(row, 2));
			this.row.put("processor", (String)model.getValueAt(row, 3));
			this.row.put("ram", (String)model.getValueAt(row, 4));
			this.row.put("hdd", (String)model.getValueAt(row, 5));
			this.row.put("dvd", (String)model.getValueAt(row, 6));
			this.row.put("license", (String)model.getValueAt(row, 7));
			this.row.put("id", String.valueOf(this.idTable.get(row)));
			
			dispose();
		}
	}
	
	public void mouseEntered(MouseEvent e)
	{
		
	}
	
	public void mouseExited(MouseEvent e)
	{
		
	}
	
	public void mousePressed(MouseEvent e)
	{
		
	}
	
	public void mouseReleased(MouseEvent e)
	{
		
	}
	
	private void loadComputersFromDB()
	{
		ResultSet resultSet = null;
		Statement statement = null;
		
		this.idTable = new Vector<Integer>();
		
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		int rowCount = 0;
		int counter = 0;
		
		try
		{
			statement = this.connection.createStatement();
			
			resultSet = statement.executeQuery("SELECT * FROM computer");
			
			rowCount = countRow(resultSet);
			model.setRowCount(rowCount);
			
			while(resultSet.next())
			{
				this.idTable.add(resultSet.getInt("id"));
				
				model.setValueAt((String)resultSet.getString("brand"), counter, 0);
				model.setValueAt((String)resultSet.getString("model"), counter, 1);
				model.setValueAt((String)resultSet.getString("type"), counter, 2);
				model.setValueAt((String)resultSet.getString("processor"), counter, 3);
				model.setValueAt((String)resultSet.getString("ram"), counter, 4);
				model.setValueAt((String)resultSet.getString("hdd"), counter, 5);
				model.setValueAt(String.valueOf(resultSet.getBoolean("dvd")), counter, 6);
				model.setValueAt(String.valueOf(resultSet.getBoolean("license")), counter, 7);
				
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
	}
	
	private int countRow(ResultSet resultSet)
	{
		int result = 0;
		
		if(resultSet == null)
		{
			return -1;
		}
		
		try
		{
			while(resultSet.next())
			{
				result++;
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	private void setCenterLocation()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		int width = (screenSize.width - 800) / 2;
		int height = (screenSize.height - 300) / 2;
		
		setLocation(width, height);
	}
}



























