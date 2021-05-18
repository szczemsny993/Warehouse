package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import FileManager.Config;

import java.sql.*;

public class PasswordsProgramDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	protected static final int textFieldLength = 32;
	protected Connection connection;
	protected int programId;
	
	protected JTextField nameTextField;
	protected JTextField portTextField;
	protected JTextField userTextField;
	protected JTextField passwordTextField;
	protected JTextField descriptionTextField;
	protected JTextField locationTextField;
	protected JTextField internalIDTextField;
	protected JTextField licenseTextField;
	protected JTextField positionTextField;
	
	public PasswordsProgramDialog(int programId)
	{
		super(null, "Szczegoly programu", JDialog.ModalityType.DOCUMENT_MODAL);
		setSize(550, 260);
		setLocationRelativeTo(null);
		setResizable(false);
		
		try
		{
			this.connection = Config.getConnection();
			this.programId = programId;
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
		
		loadDataFromDatabase();
		
		setVisible(true);
	}
	
	private JPanel createMainPanel()
	{
		JPanel panel = new JPanel(new FlowLayout());
		
		JPanel leftSide = new JPanel(new GridLayout(9, 1));
		JPanel rightSide = new JPanel(new GridLayout(9, 1));
		
		JTextField nameDisplayField = createDisplayField("Nazwa:");
		JTextField portDisplayField = createDisplayField("Port:");
		JTextField userDisplayField = createDisplayField("Uzytkownik:");
		JTextField passwordDisplayField = createDisplayField("Haslo:");
		JTextField descriptionDisplayField = createDisplayField("Krotki opis:");
		JTextField locationDisplayField = createDisplayField("Lokalizacja:");
		JTextField internalIPDisplayField = createDisplayField("Wewnetrzne ip:");
		JTextField licenseDisplayField = createDisplayField("Licencja:");
		JTextField positionDisplayField = createDisplayField("Stanowisko:");
		
		leftSide.add(nameDisplayField);
		leftSide.add(portDisplayField);
		leftSide.add(userDisplayField);
		leftSide.add(passwordDisplayField);
		leftSide.add(descriptionDisplayField);
		leftSide.add(locationDisplayField);
		leftSide.add(internalIPDisplayField);
		leftSide.add(licenseDisplayField);
		leftSide.add(positionDisplayField);
		
		this.nameTextField = new JTextField(textFieldLength);
		this.portTextField = new JTextField(textFieldLength);
		this.userTextField = new JTextField(textFieldLength);
		this.passwordTextField = new JTextField(textFieldLength);
		this.descriptionTextField = new JTextField(textFieldLength);
		this.locationTextField = new JTextField(textFieldLength);
		this.internalIDTextField = new JTextField(textFieldLength);
		this.licenseTextField = new JTextField(textFieldLength);
		this.positionTextField = new JTextField(textFieldLength);
		
		rightSide.add(this.nameTextField);
		rightSide.add(this.portTextField);
		rightSide.add(this.userTextField);
		rightSide.add(this.passwordTextField);
		rightSide.add(this.descriptionTextField);
		rightSide.add(this.locationTextField);
		rightSide.add(this.internalIDTextField);
		rightSide.add(this.licenseTextField);
		rightSide.add(this.positionTextField);
		
		panel.add(leftSide);
		panel.add(rightSide);
		
		return panel;
	}
	
	private JTextField createDisplayField(String text)
	{
		JTextField field = new JTextField(text);
		field.setEditable(false);
		field.setHorizontalAlignment(SwingConstants.RIGHT);
		
		return field;
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
	
	private void loadDataFromDatabase()
	{
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT name, port, user, password, description, location, internal_id, license, position"
					+ " FROM passwords WHERE id = ?");
			preparedStatement.setInt(1, programId);
			
			resultSet = preparedStatement.executeQuery();
			if(resultSet != null)
			{
				while(resultSet.next())
				{
					this.nameTextField.setText(resultSet.getString("name"));
					this.portTextField.setText(String.valueOf(resultSet.getInt("port")));
					this.userTextField.setText(resultSet.getString("user"));
					this.passwordTextField.setText(resultSet.getString("password"));
					this.descriptionTextField.setText(resultSet.getString("description"));
					this.locationTextField.setText(resultSet.getString("location"));
					this.internalIDTextField.setText(resultSet.getString("internal_id"));
					this.licenseTextField.setText(resultSet.getString("license"));
					this.positionTextField.setText(resultSet.getString("position"));
				}
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
	
	private void updateProgram()
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("UPDATE passwords SET name = ?, port = ?, user = ?, password = ?, description = ?,"
					+ " location = ?, internal_id = ?, license = ?, position = ? WHERE id = ?");
			
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
			preparedStatement.setString(5, this.descriptionTextField.getText());
			preparedStatement.setString(6, this.locationTextField.getText());
			preparedStatement.setString(7, this.internalIDTextField.getText());
			preparedStatement.setString(8, this.licenseTextField.getText());
			preparedStatement.setString(9, this.positionTextField.getText());
			preparedStatement.setInt(10, programId);
			
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
	
	private boolean checkPortValue()
	{
		boolean statement = true;
		
		String portFieldValue = this.portTextField.getText();
		int port = Integer.valueOf(portFieldValue);
		
		if(port < 0 || port > 32767)
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
					updateProgram();
					dispose();
					break;
				}
				default:
				{
					dispose();
					break;
				}
			}
		}
	}
}









































