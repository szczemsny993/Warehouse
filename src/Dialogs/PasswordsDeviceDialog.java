package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import FileManager.Config;

import java.sql.*;

public class PasswordsDeviceDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	protected Connection connection;
	protected int deviceId;
	
	protected JTextField locationTextField;
	protected JTextField nameTextField;
	protected JTextField serialNumberTextField;
	protected JTextField internalIPTextField;
	protected JTextField macAdressTextField;
	protected JTextField userTextField;
	protected JTextField passwordTextField;
	protected JTextField portTextField;
	protected JTextField shortDescriptionTextField;
	
	public PasswordsDeviceDialog(int deviceId) throws IllegalArgumentException
	{
		super(null, "Szczegoly urzadzenia", JDialog.ModalityType.DOCUMENT_MODAL);
		setSize(560, 240);
		setLocationRelativeTo(null);
		setResizable(false);
		
		try
		{
			this.connection = Config.getConnection();
			
			if(checkClientId(deviceId))
			{
				this.deviceId = deviceId;
			}
			else
			{
				throw new IllegalArgumentException("Number must be signed");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		JPanel mainPanel = createMainPanel();
		container.add(mainPanel, BorderLayout.CENTER);
		
		JToolBar toolBar = createToolBar();
		container.add(toolBar, BorderLayout.EAST);
		
		loadData();
		
		setVisible(true);
	}
	
	private JPanel createMainPanel()
	{
		JPanel panel = new JPanel(new FlowLayout());
		
		JPanel leftPanel = new JPanel();
		JPanel rightPanel = new JPanel();
		
		JTextField locationDisplayField = createField("Lokalizacja:");
		JTextField nameDisplayField = createField("Nazwa:");
		JTextField serialNumberDisplayField = createField("Numer seryjny:");
		JTextField internalIPDisplayField = createField("Wewnetrzny Adres IP:");
		JTextField macDisplayField = createField("Mac Adres:");
		JTextField userDisplayField = createField("Uzytkownik:");
		JTextField passwordDisplayField = createField("Haslo:");
		JTextField portDisplayField = createField("Port:");
		JTextField shortDescriptionDisplayField = createField("Krotki opis:");
		
		leftPanel.setLayout(new GridLayout(9, 1));
		
		leftPanel.add(locationDisplayField);
		leftPanel.add(nameDisplayField);
		leftPanel.add(serialNumberDisplayField);
		leftPanel.add(internalIPDisplayField);
		leftPanel.add(macDisplayField);
		leftPanel.add(userDisplayField);
		leftPanel.add(passwordDisplayField);
		leftPanel.add(portDisplayField);
		leftPanel.add(shortDescriptionDisplayField);
		
		this.locationTextField = new JTextField(32);
		this.nameTextField = new JTextField(32);
		this.serialNumberTextField = new JTextField(32);
		this.internalIPTextField = new JTextField(32);
		this.macAdressTextField = new JTextField(32);
		this.userTextField = new JTextField(32);
		this.passwordTextField = new JTextField(32);
		this.portTextField = new JTextField(32);
		this.shortDescriptionTextField = new JTextField(32);
		
		rightPanel.setLayout(new GridLayout(9, 1));
		
		rightPanel.add(this.locationTextField);
		rightPanel.add(this.nameTextField);
		rightPanel.add(this.serialNumberTextField);
		rightPanel.add(this.internalIPTextField);
		rightPanel.add(this.macAdressTextField);
		rightPanel.add(this.userTextField);
		rightPanel.add(this.passwordTextField);
		rightPanel.add(this.portTextField);
		rightPanel.add(this.shortDescriptionTextField);
		
		panel.add(leftPanel);
		panel.add(rightPanel);
		
		return panel;
	}
	
	private JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon saveIcon = new ImageIcon("resources/discette_32px.png");
		JButton saveButton = new JButton(saveIcon);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(new ButtonListener());
		
		toolBar.add(saveButton);
		
		return toolBar;
	}
	
	private JTextField createField(String name)
	{
		JTextField textField = new JTextField(name);
		textField.setHorizontalAlignment(SwingConstants.RIGHT);
		textField.setEditable(false);
		
		return textField;
	}
	
	private void loadData()
	{
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT * FROM passwords WHERE id = ?");
			preparedStatement.setInt(1, deviceId);
		
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				this.locationTextField.setText(resultSet.getString("location"));
				this.nameTextField.setText(resultSet.getString("name"));
				this.serialNumberTextField.setText(resultSet.getString("serial"));
				this.internalIPTextField.setText(resultSet.getString("internal_id"));
				this.userTextField.setText(resultSet.getString("user"));
				this.passwordTextField.setText(resultSet.getString("password"));
				this.portTextField.setText(String.valueOf(resultSet.getInt("port")));
				this.shortDescriptionTextField.setText(resultSet.getString("description"));
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
	
	private void updateDevice()
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("UPDATE passwords"
					+ " SET name = ?, port = ?, user = ?, password = ?, description = ?,"
					+ " serial = ?, mac = ?, internal_id = ?, location = ?"
					+ " WHERE id = ?");
			
			preparedStatement.setString(1, this.nameTextField.getText());
			
			if(checkPortValue())
			{
				preparedStatement.setInt(2, Integer.valueOf(this.portTextField.getText()));
			}
			else
			{
				preparedStatement.setInt(2, 0);
			}
			
			preparedStatement.setString(3, this.userTextField.getText());
			preparedStatement.setString(4, this.passwordTextField.getText());
			preparedStatement.setString(5, this.shortDescriptionTextField.getText());
			preparedStatement.setString(6, this.serialNumberTextField.getText());
			preparedStatement.setString(7, this.macAdressTextField.getText());
			preparedStatement.setString(8, this.internalIPTextField.getText());
			preparedStatement.setString(9, this.locationTextField.getText());
			
			preparedStatement.setInt(10, deviceId);
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
	
	private boolean checkClientId(int id)
	{
		boolean statement = false;
		
		if(id <= 0)
		{
			statement = false;
		}
		else
		{
			statement = true;
		}
		
		return statement;
	}
	
	private boolean checkPortValue()
	{
		boolean statement = true;
		String port = this.portTextField.getText();
		
		if(port == null || port.length() == 0)
		{
			statement = false;
			return statement;
		}
		
		int portInt = Integer.valueOf(port);
		if(portInt < 0 || portInt > 32767)
		{
			statement = false;
		}
		
		return statement;
	}
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand())
			{
				case "save":
				{
					updateDevice();
					dispose();
					break;
				}
			}
		}
	}
}



























