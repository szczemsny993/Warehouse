package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import FileManager.Config;

import java.sql.*;

import java.util.Vector;

import java.io.File;

public class UserDialog extends JDialog implements ActionListener, MouseListener
{
	protected static final long serialVersionUID = 1L;
	
	protected Connection connection;
	protected Vector<Integer> idTable;
	
	protected JTable table;
	
	public UserDialog()
	{
		super(null, "Użytkownicy", JDialog.ModalityType.DOCUMENT_MODAL);
		
		try
		{
			this.connection = Config.getConnection();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setSize(400, 400);
		setCenterPos();
		setResizable(false);
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		this.table = createUsersTable(createUserModel());
		
		container.add(new JScrollPane(this.table), BorderLayout.CENTER);
		container.add(createToolBar(), BorderLayout.EAST);
		
		fillInModel();
	}
	
	protected void setCenterPos()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		int x = (screenSize.width - 400) / 2;
		int y = (screenSize.height - 400) / 2;
		
		setLocation(x, y);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "add":
			{
				UserAddDialog dialog = new UserAddDialog();
				dialog.setVisible(true);
				fillInModel();
				break;
			}
			case "remove":
			{
				int ret_val = -1;
				int[] selectedRows = this.table.getSelectedRows();
				if(selectedRows.length <= 0)
				{
					break;
				}
				
				ret_val = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz usunąć " + selectedRows.length + " elementów?", "", JOptionPane.YES_NO_OPTION);
				if(ret_val == JOptionPane.YES_OPTION)
				{
					Vector<String> usersToDelete = getSelectedUserNames(selectedRows);
					
					for(String user : usersToDelete)
					{
						if(user.contains("administrator") && user.length() == 13)
						{
							JOptionPane.showMessageDialog(this, "Nie możesz usunąć konta administratora!", "", JOptionPane.INFORMATION_MESSAGE);
							usersToDelete.remove("administrator");
							break;
						}
					}
					
					for(String user : usersToDelete)
					{
							deleteUser(user);
					}
					
					JOptionPane.showMessageDialog(this, "Usunięto " + usersToDelete.size() + " elementów.", "", JOptionPane.INFORMATION_MESSAGE);
					
					fillInModel();
				}
				
				break;
			}
			default:
			{
				break;
			}
		}
	}
	
	protected JTable createUsersTable(DefaultTableModel model)
	{
		JTable table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.addMouseListener(this);
		
		
		return table;
	}
	
	protected DefaultTableModel createUserModel()
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
				
		model.addColumn("Użytkownik");
		model.addColumn("Typ konta");
		
		return model;
	}
	
	protected JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon addIcon = new ImageIcon("resources/add_32px.png");
		ImageIcon removeIcon = new ImageIcon("resources/close_32px.png");
		
		JButton addButton = new JButton(addIcon);
		addButton.setActionCommand("add");
		addButton.addActionListener(this);
		addButton.setToolTipText("Dodaj nowego uzytkownika.");
		
		JButton removeButton = new JButton(removeIcon);
		removeButton.setActionCommand("remove");
		removeButton.addActionListener(this);
		removeButton.setToolTipText("Usun uzytkownika/uzytkownikow.");
		
		toolBar.add(addButton);
		toolBar.add(removeButton);
		
		return toolBar;
	}
	
	protected void fillInModel()
	{
		Statement statement = null;
		ResultSet resultSet = null;
		
		String query = "SELECT id, user, permision_level FROM accounts";
		
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		this.idTable = new Vector<Integer>();
		int counter = 0;
		String tmp = "";
		
		try
		{
			statement = this.connection.createStatement();
			resultSet = statement.executeQuery(query);
			
			model.setRowCount(getResultSetRowCount(resultSet));
			
			while(resultSet.next())
			{
				this.idTable.add(resultSet.getInt("id"));
				
				model.setValueAt(resultSet.getString("user"), counter, 0);
				
				tmp = convertPermisionValueToString(resultSet.getInt("permision_level"));
				model.setValueAt(tmp, counter, 1);
				
				counter++;
				tmp = "";
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
	
	protected int getResultSetRowCount(ResultSet resultSet)
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
	
	protected String convertPermisionValueToString(int level)
	{
		String levelName = "";
		
		switch(level)
		{
			case 1:
			{
				levelName = "Gość";
				break;
			}
			case 2:
			{
				levelName = "Użytkownik";
				break;
			}
			case 3:
			{
				levelName = "Administrator";
				break;
			}
			default:
			{
				JOptionPane.showMessageDialog(this, "Wystąpił problem z typem konta użytkownika, skontaktuj sie z twórcą oprogramowania.", "Błąd", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		return levelName;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		JTable table = (JTable)e.getSource();
		Point point = e.getPoint();
		int row = table.rowAtPoint(point);
		
		if(e.getClickCount() >= 2 && row > -1)
		{
			DefaultTableModel model = (DefaultTableModel)table.getModel();
			String user = (String)model.getValueAt(row, 0);
			
			//System.out.println(user);
			
			UserShowDialog dialog = new UserShowDialog(user);
			dialog.showDialog();
			fillInModel();
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
	protected void deleteUser(String userName)
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			
			
			preparedStatement = this.connection.prepareStatement("DELETE FROM accounts WHERE user = ?");
			preparedStatement.setString(1, userName);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			
			preparedStatement = this.connection.prepareStatement("DELETE FROM account_permission WHERE account_name = ?");
			preparedStatement.setString(1, userName);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			
			
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
	
	protected boolean deleteUserDirectory(String userName)
	{
		boolean isSuccess = false;
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		File file = null;
		String directoryName = "";
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT user_directory FROM accounts WHERE user = ?");
			preparedStatement.setString(1, userName);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				directoryName = resultSet.getString("user_directory");
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
	
	protected Vector<String> getSelectedUserNames(int[] selectedRow)
	{
		Vector<String> accountNameTable = new Vector<String>();
		DefaultTableModel model = null;
		
		if(selectedRow.length <= 0)
		{
			return null;
		}
		
		model = (DefaultTableModel)table.getModel();
		
		for(int row : selectedRow)
		{
			accountNameTable.add((String)model.getValueAt(row, 0));
		}
		
		return accountNameTable;
	}
}





























