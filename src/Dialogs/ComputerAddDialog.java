package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import FileManager.Config;

import java.sql.*;
public class ComputerAddDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	protected final static int textFieldLength = 32;
	
	protected Connection connection;
	
	protected JTextField brandTextField;
	protected JTextField modelTextField;
	protected JTextField typeTextField;
	protected JTextField processorTextField;
	protected JTextField ramTextField;
	protected JTextField hddTextField;
	protected JCheckBox driveCheckBox;
	protected JCheckBox licenseCheckBox;
	
	public ComputerAddDialog()
	{
		super(null, "Dodaj komputer...", JDialog.ModalityType.DOCUMENT_MODAL);
		setLocationRelativeTo(null);
		setSize(550, 200);
		setCenterPos();

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
		
		JPanel mainPanel = createMainPanel();
		container.add(mainPanel, BorderLayout.CENTER);
		
		JToolBar toolBar = createToolBar();
		container.add(toolBar, BorderLayout.EAST);
		
		JPanel bottomPanel = createBottomPanel();
		container.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void setCenterPos()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		int x = (screenSize.width - 550) / 2;
		int y = (screenSize.height - 200) / 2;
		
		setLocation(x, y);
	}
	
	private JPanel createMainPanel()
	{
		JPanel mainPanel = new JPanel(new FlowLayout());
		
		JPanel leftPanel = new JPanel(new GridLayout(6, 1));
		JPanel rightPanel = new JPanel(new GridLayout(6, 1));
		
		JTextField brandDisplayField = createDisplayField("Marka:");
		JTextField modelDisplayField = createDisplayField("Model:");
		JTextField typeDisplayField = createDisplayField("Typ:");
		JTextField processorDisplayField = createDisplayField("Procesor:");
		JTextField ramDisplayField = createDisplayField("Ram:");
		JTextField hddDisplayField = createDisplayField("Dysk Twardy:");
		
		this.brandTextField = new JTextField(textFieldLength);
		this.modelTextField = new JTextField(textFieldLength);
		this.typeTextField = new JTextField(textFieldLength);
		this.processorTextField = new JTextField(textFieldLength);
		this.ramTextField = new JTextField(textFieldLength);
		this.hddTextField = new JTextField(textFieldLength);
		
		leftPanel.add(brandDisplayField);
		leftPanel.add(modelDisplayField);
		leftPanel.add(typeDisplayField);
		leftPanel.add(processorDisplayField);
		leftPanel.add(ramDisplayField);
		leftPanel.add(hddDisplayField);
	
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
		JPanel bottomPanel = new JPanel(new FlowLayout());
		
		this.driveCheckBox = new JCheckBox("Naped DVD");
		this.driveCheckBox.setEnabled(true);
		
		this.licenseCheckBox = new JCheckBox("Licencja");
		this.licenseCheckBox.setEnabled(true);
		
		bottomPanel.add(this.driveCheckBox);
		bottomPanel.add(this.licenseCheckBox);
		
		return bottomPanel;
	}
	
	private JTextField createDisplayField(String text)
	{
		JTextField displayField = new JTextField();
		if(text != null && text.length() > 0)
		{
			displayField.setText(text);
		}
		else
		{
			return null;
		}
		
		displayField.setColumns(10);
		displayField.setEditable(false);
		displayField.setHorizontalAlignment(SwingConstants.RIGHT);
		
		return displayField;
	}
	
	private JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon saveIcon = new ImageIcon("resources/discette_32px.png");
		
		ButtonListener buttonListener = new ButtonListener();
		
		JButton saveButton = new JButton(saveIcon);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(buttonListener);
		
		toolBar.add(saveButton);
		
		return toolBar;
	}
	
	private void addComputerToDatabase()
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("INSERT INTO computer (id, brand, model, type, processor, ram, hdd, dvd, license)"
					+ " VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			preparedStatement.setString(1, this.brandTextField.getText());
			preparedStatement.setString(2, this.modelTextField.getText());
			preparedStatement.setString(3, this.typeTextField.getText());
			preparedStatement.setString(4, this.processorTextField.getText());
			preparedStatement.setString(5, this.ramTextField.getText());
			preparedStatement.setString(6, this.hddTextField.getText());
			preparedStatement.setBoolean(7, convertCheckBoxToBoolean(this.driveCheckBox));
			preparedStatement.setBoolean(8, convertCheckBoxToBoolean(this.licenseCheckBox));
			
			preparedStatement.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			e.getMessage();
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
	
	private boolean convertCheckBoxToBoolean(JCheckBox checkBox)
	{	
		if(checkBox == null)
		{
			throw new NullPointerException("Check box in class add computer is null!");
		}
		boolean statement = checkBox.isSelected();
	
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
					addComputerToDatabase();
					dispose();
					break;
				}
				default:
					break;
			}
		}
	}
}














































