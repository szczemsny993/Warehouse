package Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import FileManager.Config;
import Data.Client;

import java.sql.*;

public class PasswordsAddDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	protected final int textFieldLength = 32;
	
	protected final static int DEVICE_ID = 0;
	protected final static int PROGRAM_ID = 1;
	
	protected Connection connection;
	protected int client_id;
	
	protected Container mainContainer;
	
	protected JComboBox<String> comboBox;
	
	protected JTextField dateTextField;
	protected JTextField codeTextField;
	
	protected JTextField nameTextField;
	protected JTextField portTextField;
	protected JTextField userTextField;
	protected JTextField passwordTextField;
	protected JTextField descriptionTextField;
	
	protected JTextField locationTextField;
	protected JTextField serialTextField;
	protected JTextField macTextField;
	protected JTextField internalIPTextField;
	
	protected JTextField licenseTextField;
	protected JTextField positionTextField;
	
	protected Client client;
	
	public PasswordsAddDialog(Client client)
	{
		super(null, "Dodaj...", JDialog.ModalityType.DOCUMENT_MODAL);
		setLocationRelativeTo(null);
		
		try
		{
			this.connection = Config.getConnection();
			this.client = client;
			
			this.client_id = client.getId();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		this.mainContainer = getContentPane();
		this.mainContainer.setLayout(new BorderLayout());
		
		loadDeviceLayout();
	}
	
	protected void loadDeviceLayout()
	{
		this.mainContainer.removeAll();
		
		JPanel topPanel = createTopPanel(DEVICE_ID);
		this.mainContainer.add(topPanel, BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(new FlowLayout());
		centerPanel.add(createLeftPanel());
		centerPanel.add(createDevicePanel());
		
		this.mainContainer.add(centerPanel, BorderLayout.CENTER);
		
		setSize(750, 350);
		setVisible(true);
	}
	
	protected void loadProgramLayout()
	{
		this.mainContainer.removeAll();
		
		JPanel topPanel = createTopPanel(PROGRAM_ID);
		this.mainContainer.add(topPanel, BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(new FlowLayout());
		centerPanel.add(createLeftPanel());
		centerPanel.add(createProgramPanel());
		
		this.mainContainer.add(centerPanel, BorderLayout.CENTER);
		
		setSize(750, 350);
		setVisible(true);
	}
	
	protected JPanel createTopPanel(int id)
	{
		JPanel panel = new JPanel(new FlowLayout());
		
		String[] comboBoxValues = {"Urzadzenie", "Program"};
		this.comboBox = new JComboBox<String>(comboBoxValues);
		this.comboBox.setSelectedIndex(id);
		this.comboBox.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						JComboBox<String> comboBox = (JComboBox<String>)e.getSource();
						String choose = (String)comboBox.getSelectedItem();
						
						switch(choose)
						{
							case "Urzadzenie":
							{
								loadDeviceLayout();
								break;
							}
							case "Program":
							{
								loadProgramLayout();
								break;
							}
							default:
								break;
						}
					}
				});
		
		panel.add(this.comboBox);
		
		JTextField dateDisplayField = new JTextField("Data:");
		dateDisplayField.setEditable(false);
		dateDisplayField.setHorizontalAlignment(SwingConstants.RIGHT);
		
		this.dateTextField = new JTextField(16);
		panel.add(dateDisplayField);
		panel.add(this.dateTextField);
		
		JTextField codeDisplayField = new JTextField("Kod:");
		codeDisplayField.setEditable(false);
		codeDisplayField.setHorizontalAlignment(SwingConstants.RIGHT);
		
		this.codeTextField = new JTextField(16);
		panel.add(codeDisplayField);
		panel.add(this.codeTextField);
		
		JButton addButton = new JButton("Dodaj");
		addButton.setActionCommand("add");
		addButton.addActionListener(new ButtonListener());
		
		panel.add(addButton);
		
		return panel;
	}
	
	protected JPanel createLeftPanel()
	{
		JPanel panel = new JPanel(new GridLayout(12, 1));
		
		JTextField locationDisplayField = createDisplayField("Lokalizacja:");
		this.locationTextField = new JTextField(textFieldLength);
		
		panel.add(locationDisplayField);
		panel.add(this.locationTextField);
		
		JTextField nameDisplayField = createDisplayField("Nazwa:");
		this.nameTextField = new JTextField(textFieldLength);
		
		panel.add(nameDisplayField);
		panel.add(this.nameTextField);
		
		JTextField portDisplayField = createDisplayField("Port:");
		this.portTextField = new JTextField(textFieldLength);
		
		panel.add(portDisplayField);
		panel.add(this.portTextField);
		
		JTextField userDisplayField = createDisplayField("Uzytkownik:");
		this.userTextField = new JTextField(textFieldLength);
		
		panel.add(userDisplayField);
		panel.add(this.userTextField);
		
		JTextField passwordDisplayField = createDisplayField("Haslo:");
		this.passwordTextField = new JTextField(textFieldLength);
		
		panel.add(passwordDisplayField);
		panel.add(this.passwordTextField);
		
		JTextField descriptionDisplayField = createDisplayField("Opis:");
		this.descriptionTextField = new JTextField(textFieldLength);
		
		panel.add(descriptionDisplayField);
		panel.add(this.descriptionTextField);
		
		return panel;
	}
	
	protected JPanel createDevicePanel()
	{
		JPanel panel = new JPanel(new GridLayout(6, 1));
		
		JTextField serialDisplayField = createDisplayField("Numer Seryjny:");
		this.serialTextField = new JTextField(textFieldLength);
		
		panel.add(serialDisplayField);
		panel.add(this.serialTextField);
		
		JTextField internalDisplayField = createDisplayField("Ip wewnetrzne:");
		this.internalIPTextField = new JTextField(textFieldLength);
		
		panel.add(internalDisplayField);
		panel.add(this.internalIPTextField);
		
		JTextField macDisplayField = createDisplayField("Mac Adres:");
		this.macTextField = new JTextField(textFieldLength);
		
		panel.add(macDisplayField);
		panel.add(this.macTextField);
		
		return panel;
	}
	
	protected JPanel createProgramPanel()
	{
		JPanel panel = new JPanel(new GridLayout(6, 1));
		
		JTextField licenseDisplayField = createDisplayField("Licencja:");
		this.licenseTextField = new JTextField(textFieldLength);

		panel.add(licenseDisplayField);
		panel.add(this.licenseTextField);
		
		JTextField positionDisplayField = createDisplayField("Stanowisko:");
		this.positionTextField = new JTextField(textFieldLength);
		
		panel.add(positionDisplayField);
		panel.add(this.positionTextField);
		
		JTextField internalIPDisplayField = createDisplayField("IP wewnetrzne:");
		this.internalIPTextField = new JTextField(textFieldLength);
		
		panel.add(internalIPDisplayField);
		panel.add(this.internalIPTextField);
		
		return panel;
	}
	
	protected JTextField createDisplayField(String text)
	{
		JTextField textField = new JTextField(text);
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setEditable(false);
		
		return textField;
	}
	
	/*
	private int getClientID()
	{
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		int client_id = 0;
		
		try
		{
			statement = this.connection.prepareStatement("SELECT id FROM client WHERE code = ? AND name = ? AND nip = ? AND city = ?");
			statement.setString(1, checkString(this.data.get("code")));
			statement.setString(2, checkString(this.data.get("company")));
			statement.setString(3, checkString(this.data.get("nip")));
			statement.setString(4, checkString(this.data.get("city")));
			resultSet = statement.executeQuery();
			
			if(getRowCount(resultSet) != 1)
			{
				return -1;
			}
			
			while(resultSet.next())
			{
				client_id = resultSet.getInt("id");
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
		
		return client_id;
	}
	*/
	
	protected String checkString(String value)
	{
		String result = "";
		
		if(value.length() == 0)
		{
			result = "";
		}
		else
		{
			result = value;
		}
		
		return result;
	}
	
	protected void addDeviceToDatabase()
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = connection.prepareStatement("INSERT INTO "
					+ "passwords (id, client_id, type, date, code, name, port, user, password, description, serial, mac, internal_id, location)"
					+ " VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			preparedStatement.setInt(1, this.client_id);
			preparedStatement.setString(2, (String)this.comboBox.getSelectedItem());
			preparedStatement.setString(3, this.dateTextField.getText());
			preparedStatement.setString(4, this.codeTextField.getText());
			preparedStatement.setString(5, this.nameTextField.getText());
			preparedStatement.setInt(6, getPortValue());
			preparedStatement.setString(7, this.userTextField.getText());
			preparedStatement.setString(8, this.passwordTextField.getText());
			preparedStatement.setString(9, this.descriptionTextField.getText());
			preparedStatement.setString(10, this.serialTextField.getText());
			preparedStatement.setString(11, this.macTextField.getText());
			preparedStatement.setString(12, this.internalIPTextField.getText());
			preparedStatement.setString(13, this.locationTextField.getText());
			
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
	
	protected void addProgramToDatabase()
	{
		PreparedStatement statement = null;
		try
		{
			statement = this.connection.prepareStatement("INSERT INTO "
					+ "passwords (id, client_id, type, date, code, name, port, user, password, description, location, internal_id, license, position)"
					+ " VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			statement.setInt(1, this.client_id);
			statement.setString(2, (String)this.comboBox.getSelectedItem());
			statement.setString(3, this.dateTextField.getText());
			statement.setString(4, this.codeTextField.getText());
			statement.setString(5, this.nameTextField.getText());
			statement.setInt(6, getPortValue());
			statement.setString(7, this.userTextField.getText());
			statement.setString(8, this.passwordTextField.getText());
			statement.setString(9, this.descriptionTextField.getText());
			statement.setString(10, this.locationTextField.getText());
			statement.setString(11, this.internalIPTextField.getText());
			statement.setString(12, this.licenseTextField.getText());
			statement.setString(13, positionTextField.getText());
			
			statement.executeUpdate();
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
	/*
	private void addRecordToDatabase()
	{
		PreparedStatement preparedStatement = null;
		int port_number = 0;
		String portTmp = "";
		
		try
		{
			preparedStatement = connection.prepareStatement("INSERT INTO passwords VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			
			preparedStatement.setInt(1, this.client_id);
			preparedStatement.setString(2, this.codeTextField.getText());
			preparedStatement.setString(3, this.dateTextField.getText());
			preparedStatement.setString(4, this.nameTextField.getText());
			preparedStatement.setInt(5, getPortValue());
			preparedStatement.setString(6, this.userTextField.getText());
			preparedStatement.setString(7, this.passwordTextField());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			
		}
	}
	*/
	
	protected int getRowCount(ResultSet resultSet)
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
			return -1;
		}
		
		return rowCount;
	}
	
	protected int getPortValue()
	{
		int port_number = 0;
		String tmp = "";
		
		tmp = this.portTextField.getText();
		
		if(tmp.length() > 0)
		{
			try
			{
				port_number = Integer.valueOf(tmp);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			port_number = 0;
		}
		
		return port_number;
	}

	protected class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand())
			{
				case "add":
				{
					String value = (String) comboBox.getSelectedItem();
					if(value == "Urzadzenie")
					{
						addDeviceToDatabase();
						dispose();
					}
					else if(value == "Program")
					{
						addProgramToDatabase();
						dispose();
					}
					break;
				}
				default:
					break;
			}
		}
	}
}

































