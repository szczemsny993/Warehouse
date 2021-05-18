package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import FileManager.Config;

import java.sql.*;

public class ClientShowDialog extends JDialog implements ActionListener
{	
	private static final long serialVersionUID = 1L;
	
	private final static int fieldLength = 32;
	
	protected int clientId;
	protected Connection connection;
	
	private JTextField codeTextField;
	private JTextField nameTextField;
	private JTextField nipTextField;
	private JTextField cityTextField;
	private JTextField postCodeTextField;
	private JTextField addressTextField;
	private JTextField telephoneTextField;
	
	public ClientShowDialog(int id) throws IllegalArgumentException
	{
		super(null, "Klient", JDialog.ModalityType.DOCUMENT_MODAL);
		setSize(550, 200);
		setLocationRelativeTo(null);
		setResizable(false);
		
		try
		{
			if(checkId(id))
			{
				this.clientId = id;
			}
			else
			{
				throw new IllegalArgumentException("Wrong client id! check value");
			}
			
			this.connection =  Config.getConnection();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		JPanel centerPanel = createViewPanel();
		container.add(centerPanel, BorderLayout.CENTER);
		
		JToolBar toolBar = createToolBar();
		container.add(toolBar, BorderLayout.EAST);
		
		loadDataFromDatabase();
	}
	
	private JPanel createViewPanel()
	{
		JPanel panel = new JPanel(new FlowLayout());
		
		JPanel leftPanel = new JPanel(new GridLayout(7, 1));
		JPanel rightPanel = new JPanel(new GridLayout(7, 1));
		
		JTextField codeDisplayField = createDisplayField("Kod:");
		JTextField nameDisplayField = createDisplayField("Nazwa:");
		JTextField nipDisplayField = createDisplayField("NIP:");
		JTextField cityDisplayField = createDisplayField("Miasto:");
		JTextField postCodeDisplayField = createDisplayField("Kod Pocztowy:");
		JTextField addressDisplayField = createDisplayField("Ulica:");
		JTextField telephoneDisplayField = createDisplayField("Telefon:");
		
		leftPanel.add(codeDisplayField);
		leftPanel.add(nameDisplayField);
		leftPanel.add(nipDisplayField);
		leftPanel.add(cityDisplayField);
		leftPanel.add(postCodeDisplayField);
		leftPanel.add(addressDisplayField);
		leftPanel.add(telephoneDisplayField);
		
		this.codeTextField = new JTextField(fieldLength);
		this.nameTextField = new JTextField(fieldLength);
		this.nipTextField = new JTextField(fieldLength);
		this.cityTextField = new JTextField(fieldLength);
		this.postCodeTextField = new JTextField(fieldLength);
		this.addressTextField = new JTextField(fieldLength);
		this.telephoneTextField = new JTextField(fieldLength);
		
		rightPanel.add(this.codeTextField);
		rightPanel.add(this.nameTextField);
		rightPanel.add(this.nipTextField);
		rightPanel.add(this.cityTextField);
		rightPanel.add(this.postCodeTextField);
		rightPanel.add(this.addressTextField);
		rightPanel.add(this.telephoneTextField);
		
		panel.add(leftPanel);
		panel.add(rightPanel);
		
		return panel;
	}
	
	private JTextField createDisplayField(String text)
	{
		if(text == null)
		{
			return null;
		}
		
		JTextField textField = new JTextField(text);
		textField.setHorizontalAlignment(SwingConstants.RIGHT);
		textField.setEditable(false);
		
		return textField;
	}
	
	private JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon saveIcon = new ImageIcon("resources/discette_32px.png");
		ImageIcon printIcon = new ImageIcon("resources/printer_32px.png");
		
		JButton saveButton = new JButton(saveIcon);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);
		
		JButton printButton = new JButton(printIcon);
		printButton.setActionCommand("print");
		printButton.addActionListener(this);
		
		toolBar.add(saveButton);
		toolBar.add(printButton);
		
		return toolBar;
	}
	
	private boolean checkId(int id)
	{
		boolean statement = true;
		
		if(id <= 0)
		{
			statement = false;
		}
		
		return statement;
	}
	
	private void updateClient()
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("UPDATE client SET code = ?, name = ?, nip = ?, city = ?, post_code = ?, address = ?, telephone = ? WHERE id = ?");
			
			preparedStatement.setString(1, this.codeTextField.getText());
			preparedStatement.setString(2, this.nameTextField.getText());
			preparedStatement.setString(3, this.nipTextField.getText());
			preparedStatement.setString(4, this.cityTextField.getText());
			preparedStatement.setString(5, this.postCodeTextField.getText());
			preparedStatement.setString(6, this.addressTextField.getText());
			preparedStatement.setString(7, this.telephoneTextField.getText());
			preparedStatement.setInt(8, this.clientId);
			
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
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void loadDataFromDatabase()
	{
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT * FROM client WHERE id = ?");
			preparedStatement.setInt(1, this.clientId);
			
			resultSet = preparedStatement.executeQuery();
			
			if(checkResultSet(resultSet))
			{
				while(resultSet.next())
				{
					this.codeTextField.setText(resultSet.getString("code"));
					this.nameTextField.setText(resultSet.getString("name"));
					this.nipTextField.setText(resultSet.getString("nip"));
					this.cityTextField.setText(resultSet.getString("city"));
					this.postCodeTextField.setText(resultSet.getString("post_code"));
					this.addressTextField.setText(resultSet.getString("address"));
					this.telephoneTextField.setText(resultSet.getString("telephone"));
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
	
	private boolean checkResultSet(ResultSet resultSet)
	{
		boolean statement = true;
		
		if(resultSet == null)
		{
			statement = false;
		}
		
		return statement;
	}
	
	private void printDocument()
	{
		
	}

	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "save":
			{
				updateClient();
				dispose();
				break;
			}
			case "print":
			{
				printDocument();
			}
			default:
				break;
		}
	}
}


























