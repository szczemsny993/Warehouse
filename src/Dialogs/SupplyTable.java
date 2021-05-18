package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import FileManager.Config;

import java.sql.*;

import java.util.Vector;

public class SupplyTable extends JPanel
{
	private static final long serialVersionUID = 1L;
	protected Connection connection;
	
	protected JTable mainTable;
	
	public SupplyTable()
	{
		try
		{
			this.connection = Config.getConnection();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setLayout(new BorderLayout());
		
		this.mainTable = createTable();
		add(new JScrollPane(this.mainTable), BorderLayout.CENTER);
		
		add(createToolBar(), BorderLayout.EAST);
		
		fillModel();
	}
	
	private JTable createTable()
	{
		mainTable = new JTable(createModel());
		mainTable.getTableHeader().setReorderingAllowed(false);
		mainTable.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mousePressed(MouseEvent mouseEvent)
					{
						JTable table = (JTable)mouseEvent.getSource();
						Point point = mouseEvent.getPoint();
						int row = table.rowAtPoint(point);
						
						int supplyId = 0;
						
						if(mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1)
						{
							DefaultTableModel model = (DefaultTableModel) table.getModel();
							supplyId = (Integer)model.getValueAt(row, 0);
							
							SupplyShowDialog dialog = new SupplyShowDialog(supplyId);
							dialog.setVisible(true);
							fillModel();
						}
					}
				});
		
		return mainTable;
	}
	
	private DefaultTableModel createModel()
	{
		DefaultTableModel model = new DefaultTableModel()
				{
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int column)
					{
						return false;
					}
				};
		
				
		model.addColumn("Identyfikator");
		model.addColumn("Data");
		model.addColumn("Ilosc Sztuk");
		model.addColumn("Ilosc Palet");
		model.addColumn("Komentarz");
		
		return model;
	}
	
	private JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon addIcon = new ImageIcon("resources/add_32px.png");
		ImageIcon removeIcon = new ImageIcon("resources/close_32px.png");
		ImageIcon printIcon = new ImageIcon("resources/printer_32px.png");
		
		ButtonListener buttonListener = new ButtonListener();
		
		JButton addButton = new JButton(addIcon);
		addButton.setActionCommand("add");
		addButton.addActionListener(buttonListener);
		
		JButton removeButton = new JButton(removeIcon);
		removeButton.setActionCommand("remove");
		removeButton.addActionListener(buttonListener);
		
		JButton printButton = new JButton(printIcon);
		printButton.setActionCommand("print");
		printButton.addActionListener(buttonListener);
		
		toolBar.add(addButton);
		toolBar.add(removeButton);
		toolBar.addSeparator();
		//toolBar.add(printButton);
		
		return toolBar;
	}
	
	private void fillModel()
	{
		DefaultTableModel model = (DefaultTableModel) this.mainTable.getModel();
		
		Statement statement = null;
		ResultSet resultSet = null;
		int numRowsToSet = 0;
		
		try
		{
			statement = connection.createStatement();
			
			resultSet = statement.executeQuery("SELECT * FROM supply");
			numRowsToSet = getRowCount(resultSet);
			
			model.setRowCount(numRowsToSet);
			numRowsToSet = 0;
			
			while(resultSet.next())
			{				
				model.setValueAt(resultSet.getInt("unique_id"), numRowsToSet, 0);
				model.setValueAt(resultSet.getString("date"), numRowsToSet, 1);
				model.setValueAt(resultSet.getInt("amount"), numRowsToSet, 2);
				model.setValueAt(resultSet.getInt("pallete_count"), numRowsToSet, 3);
				model.setValueAt(resultSet.getString("comment"), numRowsToSet, 4);
				
				numRowsToSet++;
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
	
	private int getRowCount(ResultSet resultSet)
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
			e.printStackTrace();
		}
		
		
		return rowCount;
	}
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand())
			{
				case "add":
					{
						SupplyAddDialog dialog = new SupplyAddDialog();
						fillModel();
						break;
					}
				case "remove":
					{
						SupplyRemove remove = new SupplyRemove(mainTable);
						remove.deleteSelected();
						fillModel();
						break;
					}
				case "print":
					{
						break;
					}
				default:
					break;
			}
		}
	}
	
}















































