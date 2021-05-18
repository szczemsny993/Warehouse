package Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import FileManager.Config;
import Data.Client;

import java.sql.*;

import java.util.HashMap;
import java.util.Vector;

public class PasswordsShowDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	protected Connection connection;
	protected JTable table;
	
	protected Vector<Integer> idTable;
	
	protected int clientId;
	
	protected Client client;
	
	public PasswordsShowDialog(Client client)
	{
		super(null, "Dane dostepowe", JDialog.ModalityType.DOCUMENT_MODAL);
		setSize(800, 400);
		setLocationRelativeTo(null);
		
		try
		{
			this.connection = Config.getConnection();
			this.table = createTable(createModel());
			
			this.client = client;
			
			this.clientId = client.getId();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		JToolBar toolBar = createToolBar();
		container.add(toolBar, BorderLayout.EAST);
		
		JScrollPane scrollPane = new JScrollPane(this.table);
		container.add(scrollPane, BorderLayout.CENTER);
		
		loadData();
	}
	
	public void showDialog()
	{
		setVisible(true);
	}
	
	private void loadData()
	{
		fillModelWithData(this.table, this.clientId);
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
		
		model.addColumn("Typ");
		model.addColumn("Lokalizacja");
		model.addColumn("Nazwa");
		model.addColumn("Uzytkownik");
		
		return model;
	}
	
	private JTable createTable(DefaultTableModel model)
	{
		JTable table = new JTable(model);
		//table.setAutoCreateRowSorter(true);
		table.addMouseListener(new MouseAdapter()
				{
					public void mousePressed(MouseEvent mouseEvent)
					{
						JTable table = (JTable) mouseEvent.getSource();
						Point point = mouseEvent.getPoint();
						int row = table.rowAtPoint(point);
						
						if(mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1)
						{
							DefaultTableModel model = (DefaultTableModel) table.getModel();
							String type = (String) model.getValueAt(row, 0);
							
							if(type.compareTo("Urzadzenie") == 0)
							{
								PasswordsDeviceDialog dialog = new PasswordsDeviceDialog(idTable.get(row));
							}
							else if(type.compareTo("Program") == 0)
							{
								PasswordsProgramDialog dialog = new PasswordsProgramDialog(idTable.get(row));
							}
							else
							{
								
							}
						}
					}
				});
		
		return table;
	}
	
	private JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon addIcon = new ImageIcon("resources/add_32px.png");
		ImageIcon removeIcon = new ImageIcon("resources/close_32px.png");
		
		JButton addButton = new JButton(addIcon);
		addButton.setActionCommand("add");
		addButton.addActionListener(this);
		
		JButton removeButton = new JButton(removeIcon);
		removeButton.setActionCommand("remove");
		removeButton.addActionListener(this);
		
		
		toolBar.add(addButton);
		toolBar.add(removeButton);
		
		return toolBar;
	}
	
	private void fillModelWithData(JTable table, int client_id)
	{
		if(table == null || client_id <= 0)
			return;
		
		int rowCount = 0;
		int modelIterator = 0;
		DefaultTableModel model = (DefaultTableModel) table.getModel();		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		this.idTable = new Vector<Integer>();
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT id, type, location, name, user FROM passwords WHERE client_id = ?");
			preparedStatement.setInt(1, client_id);
			
			resultSet = preparedStatement.executeQuery();
			
			checkResultSet(resultSet);
			rowCount = getRowCount(resultSet);
			
			model.setRowCount(rowCount);
			
			while(resultSet.next())
			{
				this.idTable.add(resultSet.getInt("id"));
				
				model.setValueAt(resultSet.getString("type"), modelIterator, 0);
				model.setValueAt(resultSet.getString("location"), modelIterator, 1);
				model.setValueAt(resultSet.getString("name"), modelIterator, 2);
				model.setValueAt(resultSet.getString("user"), modelIterator, 3);
				
				modelIterator++;
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
	}
	
	private boolean checkResultSet(ResultSet resultSet)
	{
		boolean isExist = true;
		
		if(resultSet == null)
			isExist = false;
		
		int counter = 0;
		try
		{
			while(resultSet.next())
			{
				counter++;
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(counter == 0)
			isExist = false;
		
		return isExist;
	}
	
	private int getRowCount(ResultSet resultSet)
	{
		int rowCounter = 0;
		
		try
		{
			while(resultSet.next())
			{
				rowCounter++;
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return rowCounter;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "add":
			{
				PasswordsAddDialog dialog = new PasswordsAddDialog(this.client);
				loadData();
				break;
			}
			case "remove":
			{
				int selectedItems[] = this.table.getSelectedRows();
				int ret_val = JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usunac zasnaczone elementy?", "", JOptionPane.YES_NO_OPTION);
				
				if(ret_val == JOptionPane.OK_OPTION)
				{
					if(removeSelected(selectedItems))
					{
						JOptionPane.showMessageDialog(null, "Usunieto " + selectedItems.length + " elementow.");
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Blad podczas usuwania. Skontakuj sie z tworca oprogramowania.");
					}
				}
				else
				{
					break;
				}
			}
			default:
				break;
		}
	}
	
	private boolean removeSelected(int[] selectedItems)
	{
		boolean isSuccess = false;
		PreparedStatement preparedStatement = null;
	
		Vector<Integer> toDelete = new Vector<Integer>();
		
		for(int i = 0; i < selectedItems.length; i++)
		{
			toDelete.add(idTable.get(selectedItems[i]));
		}
		
		try
		{
			for(int i = 0; i < toDelete.size(); i++)
			{
				preparedStatement = this.connection.prepareStatement("DELETE FROM passwords WHERE id = ?");
				preparedStatement.setInt(1, toDelete.get(i));
				preparedStatement.executeUpdate();
				
				if(!isRemoved(toDelete.get(i)))
				{
					isSuccess = false;
					break;
				}
			}
			
			isSuccess = true;
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
		
		return isSuccess;
	}
	
	private boolean isRemoved(int id)
	{
		boolean isRemoved = false;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT * FROM passwords WHERE id = ? LIMIT 2");
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			
			if(getRowCount(resultSet) == 0)
			{
				isRemoved = true;
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
		
		return isRemoved;
	}
}


























