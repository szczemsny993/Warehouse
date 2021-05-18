package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JTextField;

import Account.Account;
import FileManager.Config;

import javax.swing.JPasswordField;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import java.sql.*;


public class UserChangePasswordDialog extends JDialog implements ActionListener
{
	protected static final long serialVersionUID = 1L;
	
	protected Connection connection;
	protected int userId;
	
	protected JPasswordField oldPasswordField;
	protected JPasswordField newPasswordField;
	protected JPasswordField repeatPasswordField;
	
	public UserChangePasswordDialog(int userId)
	{
		super(null, "Zmień hasło...", JDialog.ModalityType.DOCUMENT_MODAL);
		
		try
		{
			this.connection = Config.getConnection();
			this.userId = userId;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setSize(400, 170);
		setPosCenter();
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		container.add(createFieldPanel(), BorderLayout.CENTER);
		container.add(createButtonPanel(), BorderLayout.SOUTH);
	}
	
	protected void setPosCenter()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		int x = (screenSize.width - 400) / 2;
		int y = (screenSize.height - 170)  / 2;
		
		setLocation(x, y);
	}
	
	public void showDialog()
	{
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "change":
			{
				String newPassword = "";
				String confirmPassword = "";
				
				String oldPassword = getOldPassword();
				System.out.println(oldPassword);
				System.out.println(Account.SHA(String.valueOf(this.oldPasswordField.getPassword())));
				
				if(this.oldPasswordField.getPassword().length == 0)
				{
					JOptionPane.showMessageDialog(this, "Musisz podać aktualne hasło.", "", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				
				if(!oldPassword.contains(Account.SHA(String.valueOf(this.oldPasswordField.getPassword()))))
				{
					JOptionPane.showMessageDialog(this, "Musisz podać prawidłowe stare hasło.", "", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				
				newPassword = String.valueOf(this.newPasswordField.getPassword());
				confirmPassword = String.valueOf(this.repeatPasswordField.getPassword());
				
				if(newPassword.length() == 0 || confirmPassword.length() == 0)
				{
					JOptionPane.showMessageDialog(this, "Musisz uzupełnić pola z nowym hasłem.", "", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				
				if(!newPassword.contains(confirmPassword) || newPassword.length() != confirmPassword.length())
				{
					JOptionPane.showMessageDialog(this, "Pola z nowym hasłem muszą zawierać te same wyrazy.", "", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				
				if(updatePassword(newPassword))
				{
					JOptionPane.showMessageDialog(this, "Hasło zostało zmienione.", "", JOptionPane.INFORMATION_MESSAGE);
					dispose();
				}
				else
				{
					JOptionPane.showMessageDialog(this, "Wystąpił problem ze zmianą hasła. Skontaktuj się z twórcą oprogramowania.", "", JOptionPane.ERROR_MESSAGE);
					break;
				}
				
				break;
			}
			case "cancel":
			{
				dispose();
				break;
			}
		}
	}
	
	protected JPanel createFieldPanel()
	{
		JPanel panel = new JPanel(new GridLayout(3, 2));
		
		JTextField oldPasswordDisplayField = createDisplayField("Stare hasło:");
		this.oldPasswordField = new JPasswordField();
		
		panel.add(oldPasswordDisplayField);
		panel.add(this.oldPasswordField);
		
		JTextField newPasswordDisplayField = createDisplayField("Nowe hasło:");
		this.newPasswordField = new JPasswordField();
		
		panel.add(newPasswordDisplayField);
		panel.add(this.newPasswordField);
		
		JTextField repeatPasswordDisplayField = createDisplayField("Ponów hasło:");
		this.repeatPasswordField = new JPasswordField();
		
		panel.add(repeatPasswordDisplayField);
		panel.add(this.repeatPasswordField);
		
		return panel;
	}
	
	protected JPanel createButtonPanel()
	{
		JPanel panel = new JPanel(new FlowLayout());
		
		JButton changeButton = new JButton("Zmień");
		changeButton.setActionCommand("change");
		changeButton.addActionListener(this);
		
		JButton cancelButton = new JButton("Anuluj");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		panel.add(changeButton);
		panel.add(cancelButton);
		
		return panel;
	}
	
	protected JTextField createDisplayField(String text)
	{
		JTextField textField = new JTextField(text);
		
		textField.setHorizontalAlignment(JTextField.RIGHT);
		textField.setEditable(false);
		
		return textField;
	}
	
	protected String getOldPassword()
	{
		String oldPassword = "";
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT hashpassword FROM accounts WHERE id = ?");
			preparedStatement.setInt(1, this.userId);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				oldPassword = resultSet.getString("hashpassword");
				break;
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
		
		return oldPassword;
	}
	
	protected boolean updatePassword(String newHashPassword)
	{
		boolean statement = false;
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("UPDATE accounts SET hashpassword = ? WHERE id = ?");
			preparedStatement.setString(1, Account.SHA(newHashPassword));
			preparedStatement.setInt(2, this.userId);
			preparedStatement.executeUpdate();
			
			statement = true;
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
		
		return statement;
	}
}




































