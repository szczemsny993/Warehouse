package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Account.Account;
import FileManager.Config;

import java.sql.*;

import java.io.File;

public class LoginDialog extends JDialog implements ActionListener, KeyListener
{
	protected static final long serialVersionUID = 1L;
	
	protected String userDirectory;
	
	protected int permLevel = 0;
	
	protected Connection connection;
	
	protected JTextField userField;
	protected JPasswordField passwordField;
	
	protected JLabel warningLabel;
	
	public LoginDialog()
	{
		super(null, "Zaloguj...", Dialog.ModalityType.DOCUMENT_MODAL);
		setLocationRelativeTo(null);
		setSize(220, 150);
		addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent windowEvent)
					{
						System.exit(0);
					}
				});
		//setResizable(false);
		setCenterLocation();
		
		addKeyListener(this);
		
		try
		{
			this.connection = Config.getConnection();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		JPanel panel = createMainPanel();
		container.add(panel, BorderLayout.CENTER);
	}
	
	protected void setCenterLocation()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int x = (screenSize.width - getWidth()) / 2;
		int y = (screenSize.height - getHeight()) / 2;
		setLocation(x, y);
	}
	
	protected JPanel createMainPanel()
	{
		JPanel panel = new JPanel(new GridLayout(6, 1));
		panel.addKeyListener(this);
		
		JTextField userDisplayField = createDisplayField("Uzytkownik:");
		this.userField = new JTextField();
		this.userField.addKeyListener(this);
		
		JTextField passwordDisplayField = createDisplayField("Haslo:");
		this.passwordField = new JPasswordField();
		this.passwordField.addKeyListener(this);
		
		this.warningLabel = new JLabel("");
		this.warningLabel.addKeyListener(this);
		
		JButton logginButton = new JButton("Zaloguj!");
		logginButton.setActionCommand("login");
		logginButton.addActionListener(this);
		logginButton.addKeyListener(this);
		/*
		logginButton.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent keyEvent)
			{
				if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
				{
					System.exit(0);
				}
			}
		});
		*/
				
		panel.add(userDisplayField);
		panel.add(this.userField);
		panel.add(passwordDisplayField);
		panel.add(this.passwordField);
		panel.add(this.warningLabel);
		panel.add(logginButton);
		
		return panel;
	}
	
	protected JTextField createDisplayField(String textToDisplay)
	{
		if(textToDisplay == null)
		{
			throw new IllegalArgumentException("Podaj prawidlowy argument!");
		}
		
		JTextField textField = new JTextField(textToDisplay);
		textField.setEditable(false);
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.addKeyListener(this);
		
		return textField;
	}
	
	public int showDialog()
	{
		int result = 0;
		
		setVisible(true);
		
		result = this.permLevel;
		return result;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "login":
			{
				login();
				break;
			}
			default:
				break;
		}
	}
	
	protected void login()
	{
		this.warningLabel.setText("");
		this.permLevel = getPermisionLevel();
		//Account.setPermisionLevel(getPermisionLevel());
		
		if(this.permLevel > 0)
		{
			String userName = this.userField.getText();
			
			if(!userName.contains("administrator"))
			{
				findUserDirectory(userName);
			}
			else
			{
				Account.setUserPath("");
			}
			
			Account.setUser(userName);
			Account.setPermisionLevel(this.permLevel);
			loadModulePermission(userName);
			
			dispose();
		}
		else
		{
			this.passwordField.setText("");
			this.userField.setText("");
			this.warningLabel.setText("Blad logowania. Zly login lub haslo.");
		}
		
		
	}
	
	protected int getPermisionLevel()
	{
		int permisionLevel = 0;
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		
		String user = this.userField.getText();
		String password = String.valueOf(this.passwordField.getPassword());
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT permision_level FROM accounts WHERE user = ? AND hashpassword = ?");
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, Account.SHA(password));
			resultSet = preparedStatement.executeQuery();
			
			if(checkResultSet(resultSet) == false)
			{
				return 0;
			}
			
			while(resultSet.next())
			{
				permisionLevel = resultSet.getInt("permision_level");
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
		
		return permisionLevel;
	}
	
	protected boolean checkResultSet(ResultSet resultSet)
	{
		boolean statement = true;
		int counter = 0;
		
		try
		{
			if(resultSet == null)
			{
				statement = false;
				return statement;
			}
			
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
	
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			login();
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		
	}
	
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	protected boolean loadModulePermission(String userName)
	{
		boolean statement = true;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT module, value FROM account_permission WHERE account_name = ?");
			preparedStatement.setString(1, userName);
			resultSet = preparedStatement.executeQuery();
			
			if(!checkResultSet(resultSet))
			{
				JOptionPane.showMessageDialog(this, "Wystąpił problem z wczytywaniem danych z bazy, skonaktuj się z twórcą oprogramowania.", "", JOptionPane.ERROR_MESSAGE);
				
				statement = false;
				return statement;
			}
			
			while(resultSet.next())
			{
				matchModule(resultSet.getString("module"), resultSet.getBoolean("value"));
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
		
		return statement;
	}
	
	protected void matchModule(String moduleName, boolean value)
	{
		switch(moduleName)
		{
			case "warehouse":
			{
				Account.setWarehouseAccessValue(value);
				break;
			}
			case "shipment":
			{
				Account.setShipmentAccessValue(value);
				break;
			}
			case "supply":
			{
				Account.setSupplyAccessValue(value);
				break;
			}
			case "computer":
			{
				Account.setComputerAccessValue(value);
				break;
			}
			case "client":
			{
				Account.setClientAccessValue(value);
				break;
			}
			case "passwords":
			{
				Account.setPasswordAccessValue(value);
				break;
			}
			case "subscribtion":
			{
				Account.setSubscribeAccessValue(value);
				break;
			}
			case "backup":
			{
				Account.setBackupAccessValue(value);
				break;
			}
			default:
			{
				break;
			}
		}
	}
	
	protected boolean findUserDirectory(String user)
	{
		boolean isExists = false;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String userDirectory = "";
		
		String path = System.getProperty("user.home");
		path += "\\appdata\\roaming\\mojserwis\\users";
		
		File file = new File(path);
		if(!file.exists())
		{
			return isExists;
		}
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT user_directory FROM accounts WHERE user = ?");
			preparedStatement.setString(1, user);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				userDirectory = resultSet.getString("user_directory");
			}
			String tmp = file.getPath();
			tmp += String.format("\\%s", userDirectory);
			file = new File(tmp);
			
			if(file.exists())
			{
				Account.setUserPath(file.getPath());
				isExists = true;
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
		
		return isExists;
	}
	
	protected void setUserDirectory(String directoryName, String userName)
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			
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
































