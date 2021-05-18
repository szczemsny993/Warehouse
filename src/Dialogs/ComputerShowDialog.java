package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import FileManager.Config;

import java.sql.*;

public class ComputerShowDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	protected final static int textLength = 32;
	
	protected Connection connection;
	protected int id;
	
	protected JTextField brandTextField;
	protected JTextField modelTextField;
	protected JTextField typeTextField;
	protected JTextField processorTextField;
	protected JTextField ramTextField;
	protected JTextField hddTextField;
	protected JCheckBox driveCheckBox;
	protected JCheckBox licenseCheckBox;
	
	public ComputerShowDialog(int id) throws NullPointerException, IllegalArgumentException
	{
		super(null, "Komputer", JDialog.ModalityType.DOCUMENT_MODAL);
		
		setSize(620, 200);
		//setResizable(false);
		setLocationRelativeTo(null);
		
		try
		{
			this.connection = Config.getConnection();
			
			if(checkId(id))
			{
				this.id = id;
			}
			else
			{
				throw new IllegalArgumentException("You must give signed number");
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
		
		JPanel bottomPanel = createBottomPanel();
		container.add(bottomPanel, BorderLayout.SOUTH);
		
		loadDataFromDatabase();
	}
	
	private boolean checkId(int id)
	{
		boolean statement = true;
		
		if(id < 0)
		{
			statement = false;
		}
		
		return statement;
	}
	
	private JPanel createMainPanel()
	{
		JPanel mainPanel = new JPanel(new FlowLayout());
		
		JPanel leftPanel = new JPanel(new GridLayout(8, 1));
		JPanel rightPanel = new JPanel(new GridLayout(8, 1));
		
		JTextField brandDisplayField = createDisplayField("Marka:");
		JTextField modelDisplayField = createDisplayField("Model:");
		JTextField typeDisplayField = createDisplayField("Typ:");
		JTextField processorDisplayField = createDisplayField("Processor:");
		JTextField ramDisplayField = createDisplayField("Ram:");
		JTextField hddDisplayField = createDisplayField("Dysk Twardy:");
		
		leftPanel.add(brandDisplayField);
		leftPanel.add(modelDisplayField);
		leftPanel.add(typeDisplayField);
		leftPanel.add(processorDisplayField);
		leftPanel.add(ramDisplayField);
		leftPanel.add(hddDisplayField);
		
		this.brandTextField = new JTextField(textLength);
		this.modelTextField = new JTextField(textLength);
		this.typeTextField = new JTextField(textLength);
		this.processorTextField = new JTextField(textLength);
		this.ramTextField = new JTextField(textLength);
		this.hddTextField = new JTextField(textLength);
		
		rightPanel.add(this.brandTextField);
		rightPanel.add(this.modelTextField);
		rightPanel.add(this.typeTextField);
		rightPanel.add(this.processorTextField);
		rightPanel.add(this.ramTextField);
		rightPanel.add(this.hddTextField);
		
		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);
		
		return mainPanel;
	}
	
	private JPanel createBottomPanel()
	{
		JPanel panel = new JPanel(new FlowLayout());
		
		this.driveCheckBox = new JCheckBox("Naped DVD");
		this.driveCheckBox.setEnabled(true);
		
		this.licenseCheckBox = new JCheckBox("Licencja");
		this.licenseCheckBox.setEnabled(true);
		
		panel.add(this.driveCheckBox);
		panel.add(this.licenseCheckBox);
		
		return panel;
	}
	
	private JTextField createDisplayField(String textDisplayed)
	{		
		JTextField textField = new JTextField();
		
		if(textDisplayed == null)
		{
			return null;
		}
		
		textField.setText(textDisplayed);
		textField.setEditable(false);
		textField.setHorizontalAlignment(SwingConstants.RIGHT);
		textField.setColumns(16);
		
		return textField;
	}
	
	private JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon saveIcon = new ImageIcon("resources/discette_32px.png");
		ImageIcon printIcon = new ImageIcon("resources/printer_32px.png");
		
		ButtonListener buttonListener = new ButtonListener();
		
		JButton saveButton = new JButton(saveIcon);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(buttonListener);
		
		JButton printButton = new JButton(printIcon);
		printButton.setActionCommand("print");
		printButton.addActionListener(buttonListener);
		
		toolBar.add(saveButton);
		toolBar.add(printButton);
		
		return toolBar;
	}
	
	private void loadDataFromDatabase()
	{
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT * FROM computer WHERE id = ?");
			preparedStatement.setInt(1, this.id);
			resultSet = preparedStatement.executeQuery();
			
			if(checkResultSet(resultSet) == false)
			{
				return;
			}
			
			while(resultSet.next())
			{
				this.brandTextField.setText(resultSet.getString("brand"));
				this.modelTextField.setText(resultSet.getString("model"));
				this.typeTextField.setText(resultSet.getString("type"));
				this.processorTextField.setText(resultSet.getString("processor"));
				this.ramTextField.setText(resultSet.getString("ram"));
				this.hddTextField.setText(resultSet.getString("hdd"));
				
				this.driveCheckBox.setSelected(resultSet.getBoolean("dvd"));
				this.licenseCheckBox.setSelected(resultSet.getBoolean("license"));
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
		
		int counter = 0;
		
		try
		{
			if(resultSet == null)
			{
				statement = false;
			}
			
			while(resultSet.next())
			{
				counter++;
			}
			
			resultSet.absolute(0);
			
			if(counter == 0)
			{
				statement = false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return statement;
	}
	
	private void updateComputer()
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("UPDATE computer SET brand = ?, model = ?, type = ?, processor = ?, "
					+ "ram = ?, hdd = ?, dvd = ?, license = ? WHERE id = ?");
			preparedStatement.setString(1, this.brandTextField.getText());
			preparedStatement.setString(2, this.modelTextField.getText());
			preparedStatement.setString(3, this.typeTextField.getText());
			preparedStatement.setString(4, this.processorTextField.getText());
			preparedStatement.setString(5, this.ramTextField.getText());
			preparedStatement.setString(6, this.hddTextField.getText());
			preparedStatement.setBoolean(7, this.driveCheckBox.isSelected());
			preparedStatement.setBoolean(8, this.licenseCheckBox.isSelected());
			preparedStatement.setInt(9, this.id);
			
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
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand())
			{
				case "save":
				{
					updateComputer();
					dispose();
					break;
				}
				case "print":
				{
					System.out.println("Print!");
					break;
				}
				default:
					break;
			}
		}
	}
}













































