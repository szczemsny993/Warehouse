package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.sql.*;

import java.util.HashMap;

public class WarehouseAddDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	protected Connection connection;
	
	protected JTable table;
	
	protected int computerId;
	
	protected JTextField brandTextField;
	protected JTextField modelTextField;
	protected JTextField typeTextField;
	protected JTextField processorTextField;
	protected JTextField ramTextField;
	protected JTextField hddTextField;
	protected JCheckBox dvdCheckBox;
	protected JCheckBox licenseCheckBox;
	protected JTextField amountTextField;
	
	public WarehouseAddDialog()
	{
		super(null, "Dodaj do magazynu", JDialog.ModalityType.DOCUMENT_MODAL);
		
		try
		{
			this.connection = connection;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setSize(400, 220);
		setCenterPos();
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		container.add(createMainPanel(), BorderLayout.CENTER);
		container.add(createToolBar(), BorderLayout.EAST);
		container.add(createDownPanel(), BorderLayout.SOUTH);
	}
	
	protected void setCenterPos()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		int x = (screenSize.width - 400) / 2;
		int y = (screenSize.height - 220) / 2;
		
		setLocation(x, y);
	}
	
	protected JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon saveIcon = new ImageIcon("resources/discette_32px.png");
		ImageIcon findIcon = new ImageIcon("resources/find_32px.png");
		
		JButton saveButton = new JButton(saveIcon);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);
		
		JButton findButton = new JButton(findIcon);
		findButton.setActionCommand("find");
		findButton.addActionListener(this);

		toolBar.add(saveButton);
		toolBar.add(findButton);
		
		return toolBar;
	}
	
	protected JPanel createMainPanel()
	{
		JPanel panel = new JPanel(new GridLayout(7, 2));
		
		JTextField brandDisplayField = createDisplayField("Marka:");
		this.brandTextField = createDisplayField("");
		
		JTextField modelDisplayField = createDisplayField("Model:");
		this.modelTextField = createDisplayField("");
		
		JTextField typeDisplayField = createDisplayField("Typ:");
		this.typeTextField = createDisplayField("");
		
		JTextField processorDisplayField = createDisplayField("Procesor:");
		this.processorTextField = createDisplayField("");
		
		JTextField ramDisplayField = createDisplayField("Ram:");
		this.ramTextField = createDisplayField("");
		
		JTextField hddDisplayField = createDisplayField("HDD:");
		this.hddTextField = createDisplayField("");
		
		JTextField amountDisplayField = createDisplayField("Ilość:");
		this.amountTextField = new JTextField();
		
		panel.add(brandDisplayField);
		panel.add(this.brandTextField);
		
		panel.add(modelDisplayField);
		panel.add(this.modelTextField);
		
		panel.add(typeDisplayField);
		panel.add(this.typeTextField);
		
		panel.add(processorDisplayField);
		panel.add(this.processorTextField);
		
		panel.add(ramDisplayField);
		panel.add(this.ramTextField);
		
		panel.add(hddDisplayField);
		panel.add(this.hddTextField);
		
		panel.add(amountDisplayField);
		panel.add(this.amountTextField);
		
		return panel;
	}
	
	protected JPanel createDownPanel()
	{
		JPanel panel = new JPanel(new FlowLayout());
		
		this.dvdCheckBox = new JCheckBox("DVD");		
		this.dvdCheckBox.setEnabled(false);
		
		this.licenseCheckBox = new JCheckBox("Licencja");
		this.licenseCheckBox.setEnabled(false);
		
		panel.add(this.dvdCheckBox);
		panel.add(this.licenseCheckBox);
		
		return panel;
	}
	
	protected JTextField createDisplayField(String textToDisplay)
	{
		JTextField textField = new JTextField(textToDisplay);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		textField.setEditable(false);
		
		return textField;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "save":
			{
				if(!isPossibleToSave())
				{
					return;
				}
				addComputerToWarehouse();
				dispose();
				break;
			}
			case "find":
			{
				FindComputerDialog findComputerDialog = new FindComputerDialog();
				HashMap<String, String> data = findComputerDialog.showDialog();
				if(data != null)
				{
					assignValuesToFields(data);
				}
				break;
			}
			default:
			{
				break;
			}
		}
	}
	
	protected boolean isPossibleToSave()
	{
		boolean statement = true;
		
		if(this.brandTextField.getText().length() == 0)
		{
			statement = false;
			return statement;
		}
		
		return statement;
	}
	
	protected void assignValuesToFields(HashMap<String, String> computerData)
	{
		try
		{
			this.computerId = Integer.valueOf(computerData.get("id"));
			
			this.brandTextField.setText(computerData.get("brand"));
			this.modelTextField.setText(computerData.get("model"));
			this.typeTextField.setText(computerData.get("type"));
			this.processorTextField.setText(computerData.get("processor"));
			this.ramTextField.setText(computerData.get("ram"));
			this.hddTextField.setText(computerData.get("hdd"));
			
			boolean tmp = Boolean.valueOf(computerData.get("dvd"));
			if(tmp)
			{
				this.dvdCheckBox.setSelected(true);
			}
			
			tmp = Boolean.valueOf(computerData.get("license"));
			if(tmp)
			{
				this.licenseCheckBox.setSelected(true);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void addComputerToWarehouse()
	{
		PreparedStatement preparedStatement = null;
		int amount = 0;
		
		String amountField = this.amountTextField.getText();
		if(amountField.length() <= 0)
		{
			JOptionPane.showMessageDialog(this, "Musisz wpisać liczbę sztuk.", "", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(checkFieldIsDigit(this.amountTextField.getText()) == false)
		{
			JOptionPane.showMessageDialog(this, "Musisz podać liczbę w polu z ilością sztuk.", "", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		amount = Integer.valueOf(this.amountTextField.getText());
		
		try
		{			
			preparedStatement = this.connection.prepareStatement("INSERT INTO warehouse VALUES (default, ?, ?)");
			preparedStatement.setInt(1, this.computerId);
			preparedStatement.setInt(2, amount);
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
	
	protected boolean checkFieldIsDigit(String text)
	{
		boolean statement = true;
		char[] textArray = text.toCharArray();
		
		for(char zn : textArray)
		{
			if(!Character.isDigit(zn))
			{
				statement = false;
				return statement;
			}
		}
		
		return statement;
	}
}



































