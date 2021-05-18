package Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Account.Account;
import FileManager.Config;

import java.sql.*;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;

public class UserAddDialog extends JDialog implements ActionListener
{
	protected static final long serialVersionUID = 1L;
	
	protected String directoryRandom;
	
	protected Connection connection;
	
	protected JTextField userTextField;
	protected JPasswordField passwordField;
	protected JPasswordField confirmField;
	
	protected JComboBox<String> userTypeComboBox;
	
	protected JCheckBox warehouseCheckBox;
	protected JCheckBox shipmentCheckBox;
	protected JCheckBox supplyCheckBox;
	protected JCheckBox computerCheckBox;
	protected JCheckBox clientCheckBox;
	protected JCheckBox passwordsCheckBox;
	protected JCheckBox subscribeCheckBox;
	protected JCheckBox backupCheckBox;
	
	public UserAddDialog()
	{
		super(null, "Dodaj użytkownika", JDialog.ModalityType.DOCUMENT_MODAL);
		
		try
		{
			this.connection = Config.getConnection();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setSize(280, 350);
		centerPos();
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		container.add(createMainPanel(), BorderLayout.CENTER);
		container.add(createButtonPanel(), BorderLayout.SOUTH);
	}
	
	protected void centerPos()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		int x = (screenSize.width - 280) / 2;
		int y = (screenSize.height - 350) / 2;
		
		setLocation(x, y);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "add":
			{
				String user, password;
			
				user = this.userTextField.getText().toLowerCase();
				if(!checkFields(user))
				{
					break;
				}
				
				if(!checkIsExistUser(user))
				{
					break;
				}
				
				password = String.valueOf(this.passwordField.getPassword());
				
				addUserToDatabase(user, password);
				setUserDirectory(user);
				dispose();
				break;
			}
			case "cancel":
			{
				break;
			}
			default:
			{
				break;
			}
		}
	}
	
	protected JComboBox<String> createUserTypeComboBox()
	{
		JComboBox<String> comboBox = null;
		String[] userType = {"admin", "user", "guest"};
		
		comboBox = new JComboBox<>(userType);
		comboBox.setSelectedIndex(1);
		
		return comboBox;
	}
	
	protected JPanel createMainPanel()
	{
		JPanel panel = new JPanel(new GridLayout(12, 2));
		
		JTextField userNameDisplayField = createDisplayField("Nazwa użytkownika:");
		this.userTextField = new JTextField();
		
		panel.add(userNameDisplayField);
		panel.add(this.userTextField);
		
		JTextField passwordDisplayField = createDisplayField("Hasło:");
		this.passwordField = new JPasswordField();
		
		panel.add(passwordDisplayField);
		panel.add(this.passwordField);
		
		JTextField confirmDisplayField = createDisplayField("Potwierdz hasło:");
		this.confirmField = new JPasswordField();
		
		panel.add(confirmDisplayField);
		panel.add(this.confirmField);
		
		JTextField userTypeDisplayField = createDisplayField("Typ konta:");
		this.userTypeComboBox = createUserTypeComboBox();
		
		panel.add(userTypeDisplayField);
		panel.add(this.userTypeComboBox);
		
		JTextField warehouseDisplayField = createDisplayField("Magazyn:");
		this.warehouseCheckBox = new JCheckBox();
		
		panel.add(warehouseDisplayField);
		panel.add(this.warehouseCheckBox);
		
		JTextField shipmentDisplayField = createDisplayField("Wysyłki:");
		this.shipmentCheckBox = new JCheckBox();
		
		panel.add(shipmentDisplayField);
		panel.add(this.shipmentCheckBox);
		
		JTextField supplyDisplayField = createDisplayField("Dostawy:");
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
		
		JTextField passwordsDisplayField = createDisplayField("Dane dostępowe:");
		this.passwordsCheckBox = new JCheckBox();
		
		panel.add(passwordsDisplayField);
		panel.add(this.passwordsCheckBox);
		
		JTextField subscribeDisplayField = createDisplayField("Subskrybcja:");
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
	
	protected JPanel createButtonPanel()
	{
		JPanel panel = new JPanel(new FlowLayout());
		
		JButton addButton = new JButton("Dodaj");
		addButton.setActionCommand("add");
		addButton.addActionListener(this);
		
		JButton cancelButton = new JButton("Anuluj");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		panel.add(addButton);
		panel.add(cancelButton);
		
		return panel;
	}
	
	protected void addUserToDatabase(String user, String password)
	{
		PreparedStatement preparedStatement = null;
		String cryptedPassword = Account.SHA(password);
		
		try
		{
			preparedStatement = this.connection.prepareStatement("INSERT INTO accounts (user, hashpassword, permision_level) VALUES (?, ?, ?)");
			preparedStatement.setString(1,user);
			preparedStatement.setString(2, cryptedPassword);
			preparedStatement.setInt(3, getUserType());
			preparedStatement.executeUpdate();
			
			addPermisionForAccount(user);
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
	
	protected void addPermisionForAccount(String userName)
	{
		Statement statement = null;
		String query = "";
		
		try
		{
			query = "INSERT INTO account_permission (account_name, module, value) VALUES " + makeValuesForQuery(userName);
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
	
	protected boolean checkPassword()
	{
		boolean statement = true;
		
		String password1 = String.valueOf(this.passwordField.getPassword());
		String password2 = String.valueOf(this.confirmField.getPassword());
		
		if(!password1.contains(password2))
		{
			JOptionPane.showMessageDialog(this, "Musisz podać takie same hasła", "Błąd", JOptionPane.ERROR_MESSAGE);
			statement = false;
			return statement;
		}
		
		return statement;
	}
	
	protected boolean checkFields(String user)
	{
		boolean statement = false;
		
		if(user.length() <= 3)
		{
			JOptionPane.showMessageDialog(this, "Użytkownik musi mieć co najmniej 4 znaki.", "Błąd", JOptionPane.ERROR_MESSAGE);
			return statement;
		}
		
		if(user.length() >= 32)
		{
			JOptionPane.showMessageDialog(this, "Użytkownik może mieć maksymalnie 32 znaki.", "Błąd", JOptionPane.ERROR_MESSAGE);
			return statement;
		}
		
		if(checkPassword())
		{
			statement = true;
		}
		
		return statement;
	}
	
	protected boolean checkIsExistUser(String user)
	{
		boolean statement = true;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT id FROM accounts WHERE user = ?");
			preparedStatement.setString(1, user);
			resultSet = preparedStatement.executeQuery();
			
			int counter = 0;
			
			while(resultSet.next())
			{
				counter++;
				if(counter >= 1)
				{
					break;
				}
			}
			
			if(counter >= 1)
			{
				JOptionPane.showMessageDialog(this, "Podana nazwa jest już zajęta.", "", JOptionPane.WARNING_MESSAGE);
				statement = false;
				return statement;
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
	
	protected int getUserType()
	{
		int level = 0;
		
		switch((String)this.userTypeComboBox.getSelectedItem())
		{
			case "admin":
			{
				level = Account.ADMINISTRATOR_LEVEL;
				break;
			}
			case "user":
			{
				level = Account.USER_LEVEL;
				break;
			}
			case "guest":
			{
				level = Account.GUEST_LEVEL;
				break;
			}
			default:
			{
				break;
			}
		}
		
		return level;
	}
	
	protected String makeValuesForQuery(String user)
	{
		String brackets = "";
		brackets += String.format("('%s', '%s', %d),", user, "warehouse", warehouseCheckBox.isSelected() ? 1 : 0);
		brackets += String.format("('%s', '%s', %d),", user, "shipment", shipmentCheckBox.isSelected() ? 1 : 0);
		brackets += String.format("('%s', '%s', %d),", user, "supply", supplyCheckBox.isSelected() ? 1 : 0);
		brackets += String.format("('%s', '%s', %d),", user, "computer", computerCheckBox.isSelected() ? 1 : 0);
		brackets += String.format("('%s', '%s', %d),", user, "client", clientCheckBox.isSelected() ? 1 : 0);
		brackets += String.format("('%s', '%s', %d),", user, "passwords", passwordsCheckBox.isSelected() ? 1 : 0);
		brackets += String.format("('%s', '%s', %d),", user, "subscribtion", subscribeCheckBox.isSelected() ? 1 : 0);
		brackets += String.format("('%s', '%s', %d)", user, "backup", backupCheckBox.isSelected() ? 1 : 0);
		
		//System.out.println(brackets);
		
		return brackets;
	}
	
	protected void setUserDirectory(String user)
	{
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		String random = "";
		int counter = 0;
		
		try
		{
			do
			{
				counter = 0;
				random = makeRandom();
				preparedStatement = this.connection.prepareStatement("SELECT id FROM accounts WHERE user_directory = ?");
				preparedStatement.setString(1, random);
				resultSet = preparedStatement.executeQuery();
			
				//System.out.println("getFetchSize: " + resultSet.getFetchSize());
				while(resultSet.next())
				{
					counter++;
				}
				
			}while(counter > 0);
			
			//CreateUserDirectory createDirectory = new CreateUserDirectory();
			//createDirectory.createDirectory(random);
			
			setUserDirectory(random, user);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected String makeRandom()
	{
		String result = "";
		int tab[] = new int[20];
		
		for(int i = 0; i < 20; i++)
		{
			int tmp = (int)(Math.random() * 9);
			tab[i] = tmp;
		}
		
		for(int var: tab)
		{
			result += var;
		}
		
		return result;
	}
	
	protected void setUserDirectory(String directoryName, String userName)
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("UPDATE accounts SET user_directory = ? WHERE user = ?");
			preparedStatement.setString(1, directoryName);
			preparedStatement.setString(2, userName);
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
}








































