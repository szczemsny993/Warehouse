package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import FileManager.Config;

import java.sql.*;

import java.util.Vector;

public class ClientTable extends JPanel
{
	protected static final long serialVersionUID = 1L;
	
	protected Connection connection;
	protected JTable table;
	
	protected Vector<Integer> idTable;
	
	public ClientTable()
	{
		super(new BorderLayout());
		
		try
		{
			this.connection = Config.getConnection();
			this.table = createTable(createModel());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//create JScrollPane and put in container
		JScrollPane scrollPane = new JScrollPane(this.table);
		add(scrollPane, BorderLayout.CENTER);
		
		JToolBar toolBar = createToolBar();
		add(toolBar, BorderLayout.EAST);
		
		loadDataFromDatabase();
	}
	
	private JTable createTable(DefaultTableModel model)
	{
		JTable table = new JTable(model);
		table.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mousePressed(MouseEvent mouseEvent)
					{
						JTable table =  (JTable)mouseEvent.getSource();
						Point point = mouseEvent.getPoint();
						int row = table.rowAtPoint(point);
						
						if(mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1)
						{
							ClientShowDialog dialog = new ClientShowDialog(idTable.get(row));
							dialog.setVisible(true);
						}
					}
				});
		
		
		return table;
	}
	
	private DefaultTableModel createModel()
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
		
		model.addColumn("Kod");
		model.addColumn("Nazwa");
		model.addColumn("NIP");
		model.addColumn("Miasto");
		model.addColumn("Kod pocztowy");
		model.addColumn("Ulica");
		model.addColumn("Telefon");
		
		return model;
	}
	
	private JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon addIcon = new ImageIcon("resources/add_32px.png");
		ImageIcon removeIcon = new ImageIcon("resources/close_32px.png");
		ImageIcon findIcon = new ImageIcon("resources/find_32px.png");
		
		ButtonListener buttonListener = new ButtonListener();
		
		JButton addButton = new JButton(addIcon);
		addButton.setActionCommand("add");
		addButton.addActionListener(buttonListener);
		
		JButton removeButton = new JButton(removeIcon);
		removeButton.setActionCommand("remove");
		removeButton.addActionListener(buttonListener);
		
		JButton findButton = new JButton(findIcon);
		findButton.setActionCommand("find");
		findButton.addActionListener(buttonListener);
		
		toolBar.add(addButton);
		toolBar.add(removeButton);
		toolBar.addSeparator();
		toolBar.add(findButton);
		
		return toolBar;
	}
	
	private void loadDataFromDatabase()
	{
		Statement statement = null;
		ResultSet resultSet = null;
		
		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		this.idTable = null;
		this.idTable = new Vector<Integer>();
		
		int rowCount = 0;
		
		try
		{
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM client");
			
			rowCount = countRow(resultSet);
			model.setRowCount(rowCount);
			rowCount = 0;
			
			while(resultSet.next())
			{				
				this.idTable.add(resultSet.getInt("id"));
				
				model.setValueAt(resultSet.getString("code"), rowCount, 0);
				model.setValueAt(resultSet.getString("name"), rowCount, 1);
				model.setValueAt(resultSet.getString("nip"), rowCount, 2);
				model.setValueAt(resultSet.getString("city"), rowCount, 3);
				model.setValueAt(resultSet.getString("post_code"), rowCount, 4);
				model.setValueAt(resultSet.getString("address"), rowCount, 5);
				model.setValueAt(resultSet.getString("telephone"), rowCount, 6);
				
				rowCount++;
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
	
	private Vector<Integer> getSelectedId()
	{
		Vector<Integer> ids = new Vector<Integer>();
		int[] selectedRow = this.table.getSelectedRows();
		
		if(selectedRow.length <= 0)
		{
			return null;
		}
		
		try
		{
			for(int i = 0; i < selectedRow.length; i++)
			{
				ids.add(this.idTable.get(selectedRow[i]));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ids;
	}
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand())
			{
				case "add":
				{
					ClientAddDialog dialog = new ClientAddDialog();
					loadDataFromDatabase();
					break;
				}
				case "remove":
				{
					Vector<Integer> ids = getSelectedId(); 
					try
					{
						ClientRemove remove = new ClientRemove(ids);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					
					loadDataFromDatabase();
					break;
				}
				case "find":
				{
					ClientFindDialog dialog = new ClientFindDialog();
					break;
				}
				default:
					break;
			}
		}
	}
}














