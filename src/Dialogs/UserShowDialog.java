package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import FileManager.Config;

import java.sql.*;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class UserShowDialog extends JDialog implements ActionListener
{
	protected static final long serialVersionUID = 1L;
	protected Connection connection;
	
	protected int dialogWidth = 350;
	protected int dialogHeight = 320;
	
	protected String userName;
	protected int permission_level;
	protected int userId;
	protected HashMap<String, Integer> moduleId;
	
	protected JTextField userTextField;
	
	protected JComboBox<String> userType;
	protected JCheckBox warehouseCheckBox;
	protected JCheckBox shipmentCheckBox;
	protected JCheckBox supplyCheckBox;
	protected JCheckBox computerCheckBox;
	protected JCheckBox clientCheckBox;
	protected JCheckBox passwordCheckBox;
	protected JCheckBox subscribeCheckBox;
	protected JCheckBox backupCheckBox;
	
	public UserShowDialog(String userName)
	{
		super(null, "Użytkownik " + userName, JDialog.ModalityType.DOCUMENT_MODAL);
		
		try
		{
			this.connection = Config.getConnection();
			this.userName = userName;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setSize(dialogWidth, dialogHeight);
		setCenterPos();
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		container.add(createMainPanel(), BorderLayout.CENTER);
		container.add(createToolBar(), BorderLayout.EAST);
	}
	
	public void showDialog()
	{
		this.userTextField.setText(this.userName);
		if(this.userName.contains("administrator") && this.userName.length() == 13)
		{
			this.userTextField.setEditable(false);
			this.userType.setEnabled(false);
		}
		
		getUserData();
		setUserType();
		
		getModuleAccess();
		
		setVisible(true);
	}
	
	protected void setCenterPos()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		int x = (screenSize.width - dialogWidth) / 2;
		int y = (screenSize.height - dialogHeight) / 2;
		
		setLocation(x, y);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "save":
			{
				String newUserName = this.userTextField.getText().toLowerCase();
				//System.out.println(newUserName);
				
				if(!(newUserName.contains(userName) && this.userTextField.getText().length() == userName.length()))
				{
					if(validateUser(newUserName))
					{
						int ret_val = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz zmienić nazwę użytkownika?", "", JOptionPane.YES_NO_OPTION);
						if(ret_val == JOptionPane.YES_OPTION)
						{
							changeUserName(this.userTextField.getText().toLowerCase());
							this.userName = newUserName;
						}
					}
				}
				
				if(permission_level != getPermissionLevel())
				{
					changePermissionLevel(getPermissionLevel());
				}
				
				updateContent();
				dispose();
				break;
			}
			case "password":
			{
				UserChangePasswordDialog dialog = new UserChangePasswordDialog(this.userId);
				dialog.showDialog();
				break;
			}
			default:
			{
				break;
			}
		}
	}
	
	protected JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon saveIcon = new ImageIcon("resources/discette_32px.png");
		ImageIcon passwordIcon = new ImageIcon("resources/038-padlock.png");
		
		JButton saveButton = new JButton(saveIcon);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);
		
		JButton passwordButton = new JButton(passwordIcon);
		passwordButton.setActionCommand("password");
		passwordButton.addActionListener(this);
		
		toolBar.add(saveButton);
		toolBar.addSeparator();
		toolBar.add(passwordButton);
		
		return toolBar;
	}
	
	protected JPanel createMainPanel()
	{
		JPanel panel = new JPanel(new GridLayout(10, 2));
		
		JTextField userDisplayField = createDisplayField("Użytkownik:");
		this.userTextField = new JTextField();
		
		panel.add(userDisplayField);
		panel.add(this.userTextField);
		
		JTextField userTypeDisplayField = createDisplayField("Typ konta:");
		this.userType = createUserTypeComboBox();
		
		panel.add(userTypeDisplayField);
		panel.add(this.userType);
		
		JTextField warehouseDisplayField = createDisplayField("Magazyn:");
		this.warehouseCheckBox = new JCheckBox();
		
		panel.add(warehouseDisplayField);
		panel.add(this.warehouseCheckBox);
		
		JTextField shipmentDisplayField = createDisplayField("Wysyłka:");
		this.shipmentCheckBox = new JCheckBox();
		
		panel.add(shipmentDisplayField);
		panel.add(this.shipmentCheckBox);
		
		JTextField supplyDisplayField = createDisplayField("Dostawa:");
		this.supplyCheckBox = new JCheckBox();
		
		panel.add(supplyDisplayField);
		panel.add(this.supplyCheckBox);
		
		JTextField computerDisplayField = createDisplayField("Komputery:");
		this.computerCheckBox = new JCheckBox();
		
		panel.add(computerDisplayField);
		panel.add(this.computerCheckBox);
		
		JTextField clientDisplayField = createDisplayField("Klienci:");
		this.clientCheckBox = new JCheckBox();
		
		panel.add(clientDisplayField);
		panel.add(this.clientCheckBox);
		
		JTextField passwordDisplayField = createDisplayField("Dane dostępowe:");
		this.passwordCheckBox = new JCheckBox();
		
		panel.add(passwordDisplayField);
		panel.add(this.passwordCheckBox);
		
		JTextField subscribeDisplayField = createDisplayField("Subskrybcje:");
		this.subscribeCheckBox = new JCheckBox();
		
		panel.add(subscribeDisplayField);
		panel.add(this.subscribeCheckBox);
		
		JTextField backupDisplayField = createDisplayField("Backupy:");
		this.backupCheckBox = new JCheckBox();
		
		panel.add(backupDisplayField);
		panel.add(this.backupCheckBox);
		
		return panel;
	}
	
	protected JTextField createDisplayField(String text)
	{
		JTextField field = new JTextField(text);
		field.setEditable(false);
		field.setHorizontalAlignment(JTextField.RIGHT);
		
		return field;
	}
	
	protected JComboBox<String> createUserTypeComboBox()
	{
		JComboBox<String> combo = null;
		String[] userType = {"admin", "user", "guest"};
		
		combo = new JComboBox<>(userType);
		combo.setSelectedItem(2);
		
		return combo;
	}
	
	protected void getUserData()
	{
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT id, permision_level FROM accounts WHERE user = ?");
			preparedStatement.setString(1, this.userName);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				this.userId = resultSet.getInt("id");
				this.permission_level = resultSet.getInt("permision_level");
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
	
	protected void getModuleAccess()
	{
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		this.moduleId = new HashMap<String, Integer>();
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT id, module, value FROM account_permission WHERE account_name = ?");
			preparedStatement.setString(1, this.userName);
			resultSet = preparedStatement.executeQuery();
			
			String moduleName = "";
			boolean moduleAccess = false;
			int id = 0;
			
			while(resultSet.next())
			{
				moduleName = resultSet.getString("module");
				moduleAccess = resultSet.getBoolean("value");
				id = resultSet.getInt("id");
				
				matchValuesToCheckBox(moduleName, moduleAccess);
				this.moduleId.put(moduleName, id);
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
	
	protected void matchValuesToCheckBox(String module, boolean value)
	{
		if(module == null || module.length() == 0)
		{
			return;
		}
		
		switch(module)
		{
			case "warehouse":
			{
				this.warehouseCheckBox.setSelected(value);
				break;
			}
			case "shipment":
			{
				this.shipmentCheckBox.setSelected(value);
				break;
			}
			case "supply":
			{
				this.supplyCheckBox.setSelected(value);
				break;
			}
			case "computer":
			{
				this.computerCheckBox.setSelected(value);
				break;
			}
			case "client":
			{
				this.clientCheckBox.setSelected(value);
				break;
			}
			case "passwords":
			{
				this.passwordCheckBox.setSelected(value);
				break;
			}
			case "subscribtion":
			{
				this.subscribeCheckBox.setSelected(value);
				break;
			}
			case "backup":
			{
				this.backupCheckBox.setSelected(value);
				break;
			}
			default:
			{
				break;
			}
		}
	}
	
	protected void setUserType()
	{
		switch(this.permission_level)
		{
			case 1:
			{
				this.userType.setSelectedIndex(2);
				break;
			}
			case 2:
			{
				this.userType.setSelectedIndex(1);
				break;
			}
			case 3:
			{
				this.userType.setSelectedIndex(0);
				break;
			}
			default:
			{
				break;
			}
		}
	}
	
	protected void changeUserName(String newUserName)
	{
		Statement statement = null;
		PreparedStatement preparedStatement = null;
		String query = String.format("UPDATE account_permission SET account_name = '%s' WHERE id IN %s", newUserName, convertTableToString());
		
		try
		{
			preparedStatement = this.connection.prepareStatement("UPDATE accounts SET user = ? WHERE id = ?");
			preparedStatement.setString(1, newUserName);
			preparedStatement.setInt(2, this.userId);
			preparedStatement.executeUpdate();
			
			//System.out.println(query);
			
			statement = this.connection.createStatement();
			statement.execute(query);
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
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	protected String convertTableToString()
	{
		Set<String> keySet = this.moduleId.keySet();
		Vector<Integer> idTable = new Vector<Integer>();
		String result = "";
		
		for(String module: keySet)
		{
			idTable.add(this.moduleId.get(module));
		}
		
		result = idTable.toString();
		
		result = result.replace("[", "(");
		result = result.replace("]", ")");
		
		//System.out.println(result);
		
		return result;
	}
	
	protected boolean isUserExist(String userName)
	{
		boolean statement = true;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int counter = 0;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT id FROM accounts WHERE user = ?");
			preparedStatement.setString(1, userName);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				counter++;
			}
			
			if(counter <= 0)
			{
				statement = false;
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
		
		return statement;
	}
	
	protected boolean validateUser(String newUserName)
	{
		boolean statement = true;
		
		//System.out.println(newUserName);
		
		if(newUserName.length() <= 0)
		{
			statement = false;
			return statement;
		}
		
		if(isUserExist(newUserName))
		{
			JOptionPane.showMessageDialog(this, "Podana nazwa użytkownika jest zajęta", "", JOptionPane.ERROR_MESSAGE);
			statement = false;
			return statement;
		}
		
		if(newUserName.length() <= 3)
		{
			JOptionPane.showMessageDialog(this, "Użytkownik musi zawierać co najmniej 4 litery", "", JOptionPane.ERROR_MESSAGE);
			statement = false;
			return statement;
		}
		
		return statement;
	}
	
	protected void updateContent()
	{
		PreparedStatement preparedStatement = null;
		Set<String> moduleTable = this.moduleId.keySet();
		
		try
		{
			for(String module : moduleTable)
			{
				preparedStatement = this.connection.prepareStatement("UPDATE account_permission SET value = ? WHERE id = ?");
				preparedStatement.setBoolean(1, isModuleSelected(module));
				preparedStatement.setInt(2, this.moduleId.get(module));
				preparedStatement.executeUpdate();
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
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	protected void changePermissionLevel(int permisionLevel)
	{
		PreparedStatement preparedStatement = null;		
		
		try
		{
			preparedStatement = this.connection.prepareStatement("UPDATE accounts SET permision_level = ? WHERE id = ?");
			preparedStatement.setInt(1, permisionLevel);
			preparedStatement.setInt(2, userId);
			preparedStatement.executeUpdate();
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
	
	protected int getPermissionLevel()
	{
		int permissionLevel = 0;
		String item = (String)this.userType.getSelectedItem();
		
		switch(item)
		{
			case "admin":
			{
				permissionLevel = 3;
				break;
			}
			case "user":
			{
				permissionLevel = 2;
				break;
			}
			case "guest":
			{
				permissionLevel = 1;
				break;
			}
			default:
			{
				break;
			}
		}
		
		return permissionLevel;
	}
	
	protected boolean isModuleSelected(String module)
	{
		boolean statement = false;
		
		switch(module)
		{
			case "warehouse":
			{
				statement = this.warehouseCheckBox.isSelected();
				break;
			}
			case "shipment":
			{
				statement = this.shipmentCheckBox.isSelected();
				break;
			}
			case "supply":
			{
				statement = this.supplyCheckBox.isSelected();
				break;
			}
			case "computer":
			{
				statement = this.computerCheckBox.isSelected();
				break;
			}
			case "client":
			{
				statement = this.computerCheckBox.isSelected();
				break;
			}
			case "passwords":
			{
				statement = this.passwordCheckBox.isSelected();
				break;
			}
			case "subscribtion":
			{
				statement = this.subscribeCheckBox.isSelected();
				break;
			}
			case "backup":
			{
				statement = this.backupCheckBox.isSelected();
				break;
			}
			default:
			{
				return false;
			}
		}
		
		return statement;
	}
}
































