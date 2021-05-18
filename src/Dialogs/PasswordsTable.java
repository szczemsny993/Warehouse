package Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import Account.Account;
import FileManager.Config;
import Data.Client;

import java.sql.*;

import java.util.Vector;

public class PasswordsTable extends JPanel implements ActionListener, MouseListener
{
	private static final long serialVersionUID = 1L;
	protected Connection connection;
	protected JTable table;
	
	protected Vector<Client> clientTable;
	protected Vector<Integer> idTable;
	
	public PasswordsTable()
	{
		super(new BorderLayout());
		
		try
		{
			this.connection = Config.getConnection();
			this.table = createTable(createModel());
			
			this.idTable = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		JScrollPane scrollPane = new JScrollPane(this.table);
		add(scrollPane, BorderLayout.CENTER);
		
		JToolBar toolBar = createToolBar();
		add(toolBar, BorderLayout.EAST);
	}
	
	public boolean create()
	{
		boolean isSuccess = false;
		
		if(loadDataFromDatabase())
		{
			loadDataFromObjects();
			isSuccess = true;
		}
		
		return isSuccess;
	}
	
	protected JTable createTable(DefaultTableModel model)
	{
		JTable table = new JTable(model);
		table.addMouseListener(this);
		
		return table;
	}
	
	protected DefaultTableModel createModel()
	{
		DefaultTableModel model = new DefaultTableModel()
				{
					protected static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int col)
					{
						return false;
					}
				};
		
		model.addColumn("Kod");
		model.addColumn("Nazwa Firmy");
		model.addColumn("NIP");
		model.addColumn("Miasto");
		model.addColumn("Kod pocztowy");
		model.addColumn("Adres");
		model.addColumn("Telefon");
		
		return model;
	}
	
	protected JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon findIcon = new ImageIcon("resources/find_32px.png");
		
		JButton findButton = new JButton(findIcon);
		findButton.setActionCommand("find");
		findButton.addActionListener(this);
		
		toolBar.add(findButton);
		
		return toolBar;
	}
	
	
	protected boolean loadDataFromDatabase()
	{
		boolean isSuccess = false;
		
		Statement statement = null;
		ResultSet resultSet = null;
		int rowCount = 0;
		
		try
		{
			statement = this.connection.createStatement();
			
			resultSet = statement.executeQuery("SELECT * FROM client");
			rowCount = getRowCount(resultSet);
			if(rowCount == 0)
			{
				return isSuccess;
			}
			
			this.clientTable = new Vector<Client>(rowCount);
			
			for(int i = 0; i < rowCount; i++)
			{
				resultSet.next();
				Client client = new Client();
				
				client.setId(resultSet.getInt("id"));
				client.setCode(resultSet.getString("code"));
				client.setName(resultSet.getString("name"));
				client.setCity(resultSet.getString("city"));
				client.setPostCode(resultSet.getString("post_code"));
				client.setAddress(resultSet.getString("address"));
				client.setTelephone(resultSet.getString("telephone"));
				
				this.clientTable.add(client);
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
		
		return isSuccess;
	}
	
	protected void checkIdVector(Vector<Integer> vector)
	{
		try
		{
			for(int i = 0; i < vector.size(); i++)
			{
				System.out.println(vector.get(i));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected int getRowCount(ResultSet resultSet)
	{
		int rows = 0;
		
		try
		{
			while(resultSet.next())
			{
				rows++;
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return rows;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "find":
			{
				PasswordsFindDialog dialog = new PasswordsFindDialog();
				dialog.showDialog();
				break;
			}
		}
	}
	
	protected void loadDataFromObjects()
	{
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		model.setRowCount(this.clientTable.size());
		
		this.idTable = new Vector<Integer>(this.clientTable.size());
		
		for(int i = 0; i < this.clientTable.size(); i++)
		{
			Client client = this.clientTable.get(i);
			
			this.idTable.add(client.getId());
			
			model.setValueAt(client.getCode(), i, 0);
			model.setValueAt(client.getName(), i, 1);
			model.setValueAt(client.getNip(), i, 2);
			model.setValueAt(client.getCity(), i, 3);
			model.setValueAt(client.getPostCode(), i, 4);
			model.setValueAt(client.getAddress(), i, 5);
			model.setValueAt(client.getTelephone(), i, 6);
		}
	}
	
	public void mousePressed(MouseEvent e)
	{
		JTable table = (JTable) e.getSource();
		Point point = (Point) e.getPoint();
		int row = table.rowAtPoint(point);
		
		if(e.getClickCount() == 2 && row > -1)
		{
			Client client = clientTable.get(row);
			
			PasswordsShowDialog showDialog = new PasswordsShowDialog(client);
			showDialog.showDialog();
		}
	}
	
	public void mouseClicked(MouseEvent e)
	{
		
	}
	
	public void mouseEntered(MouseEvent e)
	{
		
	}
	
	public void mouseExited(MouseEvent e)
	{
		
	}
	
	public void mouseReleased(MouseEvent e)
	{
		
	}
}


































