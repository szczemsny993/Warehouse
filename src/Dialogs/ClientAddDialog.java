package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import FileManager.Config;

import java.sql.*;

public class ClientAddDialog extends JDialog
{
	protected Connection connection;
	
	private JTextField codeTextField;
	private JTextField nameTextField;
	private JTextField nipTextField;
	private JTextField cityTextField;
	private JTextField postCodeTextField;
	private JTextField addressTextField;
	private JTextField telephoneTextField;
	
	private static final long serialVersionUID = 1L;
	
	public ClientAddDialog()
	{
		super(null, "Dodaj kontrahenta", JDialog.ModalityType.DOCUMENT_MODAL);
		setSize(400, 220);
		setLocationRelativeTo(null);
		
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
		
		JPanel leftPanel = createLeftPanel();
		container.add(leftPanel, BorderLayout.WEST);
		
		JPanel centerPanel = createCenterPanel();
		container.add(centerPanel, BorderLayout.CENTER);
		
		JToolBar toolBar = createToolBar();
		container.add(toolBar, BorderLayout.EAST);
		
		setVisible(true);
	}
	
	private JPanel createCenterPanel()
	{
		JPanel panel = new JPanel(new GridLayout(7, 1));
		
		this.codeTextField = new JTextField(32);		
		this.nameTextField = new JTextField(32);		
		this.nipTextField = new JTextField(32);		
		this.cityTextField = new JTextField(32);
		this.postCodeTextField = new JTextField(32);
		this.addressTextField = new JTextField(32);		
		this.telephoneTextField = new JTextField(32);
		
		panel.add(this.codeTextField);
		panel.add(this.nameTextField);	
		panel.add(this.nipTextField);
		panel.add(this.cityTextField);
		panel.add(this.postCodeTextField);
		panel.add(this.addressTextField);	
		panel.add(this.telephoneTextField);
		
		return panel;
	}
	
	private JPanel createLeftPanel()
	{
		JPanel panel = new JPanel(new GridLayout(7, 1));
		
		JTextField codeDisplayField = createDisplayField("Kod:");
		JTextField nameDisplayField = createDisplayField("Nazwa:");
		JTextField nipDisplayField = createDisplayField("NIP:");
		JTextField cityDisplayField = createDisplayField("Miasto:");
		JTextField postCodeDisplayField = createDisplayField("Kod pocztowy:");
		JTextField addressDisplayField = createDisplayField("Adres:");
		JTextField telephoneDisplayField = createDisplayField("Telefon:");
		
		panel.add(codeDisplayField);
		panel.add(nameDisplayField);
		panel.add(nipDisplayField);
		panel.add(cityDisplayField);
		panel.add(postCodeDisplayField);
		panel.add(addressDisplayField);
		panel.add(telephoneDisplayField);
		
		return panel;
	}
	
	private JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon saveIcon = new ImageIcon("resources/discette_32px.png");
		ImageIcon cancelIcon = new ImageIcon("resources/close_32px.png");
		
		ButtonListener buttonListener = new ButtonListener();
		
		JButton saveButton = new JButton(saveIcon);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(buttonListener);
		
		JButton cancelButton = new JButton(cancelIcon);
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(buttonListener);
		
		toolBar.addSeparator(new Dimension(10, 15));
		toolBar.add(saveButton);
		toolBar.addSeparator();
		toolBar.add(cancelButton);
		
		return toolBar;
	}
	
	private JTextField createDisplayField(String content)
	{
		if(content == null)
		{
			return null;
		}
			
		JTextField textField = new JTextField(content);
		textField.setColumns(10);
		textField.setHorizontalAlignment(SwingConstants.RIGHT);
		textField.setEditable(false);
		
		return textField;
	}
	
	private void addClientToDatabase()
	{
		PreparedStatement statement = null;
		
		try
		{
			statement = connection.prepareStatement("INSERT INTO client VALUES (default, ?, ?, ?, ?, ?, ?, ?)");
			
			statement.setString(1, (String)this.codeTextField.getText());
			statement.setString(2, (String)this.nameTextField.getText());
			statement.setString(3, (String)this.nipTextField.getText());
			statement.setString(4, (String)this.cityTextField.getText());
			statement.setString(5, (String)this.postCodeTextField.getText());
			statement.setString(6, (String)this.addressTextField.getText());
			statement.setString(7, (String)this.telephoneTextField.getText());
			
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
			
			dispose();
		}
	}
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand())
			{
				case "save":
				{
					addClientToDatabase();
					break;
				}
				case "cancel":
				{
					dispose();
					break;
				}
			}
		}
	}
}





















