package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import FileManager.Config;

import java.sql.*;

import java.util.HashMap;
import java.util.Vector;

import java.util.Calendar;
import java.util.regex.Pattern;

public class ShipmentSaveDialog extends JDialog implements ActionListener
{
	protected static final long serialVersionUID = 1L;
	
	protected static int shipmentCounter = 0;
	
	protected Vector<HashMap<String, String>> tableData;
	protected Connection connection;
	
	protected JTextField dateTextField;
	protected JTextField idTextField;
	protected JTextField warrantyLengthTextField;
	protected JTextField palleteCountTextField;
	protected JTextArea describeArea;
	
	protected JTable table;
	
	protected Vector<Integer> idTable;
	
	public ShipmentSaveDialog(Vector<HashMap<String, String>> tableData)
	{
		super(null, "Dodaj wysylke...", JDialog.ModalityType.DOCUMENT_MODAL);
		
		setSize(1000, 450);
		
		try
		{
			this.connection = Config.getConnection();
			this.tableData = tableData;
			
			this.idTable = new Vector<Integer>();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		container.add(createLeftPanel(), BorderLayout.WEST);
		
		this.table = createTable(createTableModel());
		container.add(new JScrollPane(this.table), BorderLayout.CENTER);
		container.add(createBottomPanel(), BorderLayout.SOUTH);
		
		loadID();
	}
	
	private JTable createTable(DefaultTableModel model)
	{
		if(model == null)
		{
			return null;
		}
		
		JTable table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		
		return table;
	}
	
	private DefaultTableModel createTableModel()
	{
		DefaultTableModel model = new DefaultTableModel()
				{
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int col)
					{
						if(col != 8)
						{
							return false;
						}
						else
						{
							return true;
						}
					}
				};
		
		model.addColumn("Marka");
		model.addColumn("Model");
		model.addColumn("Typ");
		model.addColumn("Procesor");
		model.addColumn("Ram");
		model.addColumn("HDD");
		model.addColumn("DVD");
		model.addColumn("Licencja");
		model.addColumn("Ilosc");
		
		return model;
	}
	
	private JPanel createLeftPanel()
	{
		JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.setPreferredSize(new Dimension(170, 300));
		
		JPanel topPanel = new JPanel(new GridLayout(9, 1));
		JPanel bottomPanel = new JPanel(new GridLayout(1, 1));
		
		JTextField dateDisplayField = createDisplayField("Data:");
		this.dateTextField = new JTextField();
		
		JTextField idDisplayField = createDisplayField("Identyfikator:");
		this.idTextField = new JTextField();
		
		JTextField warrantyLengthDisplayField = createDisplayField("Dlugosc gwarancji:");
		this.warrantyLengthTextField = new JTextField();
		
		JTextField palleteCountDisplayField = createDisplayField("Ilosc Palet");
		this.palleteCountTextField = new JTextField();
		
		JTextField describeDisplayField = createDisplayField("Opis:");
		
		topPanel.add(dateDisplayField);
		topPanel.add(this.dateTextField);
		topPanel.add(idDisplayField);
		topPanel.add(this.idTextField);
		topPanel.add(warrantyLengthDisplayField);
		topPanel.add(this.warrantyLengthTextField);
		topPanel.add(palleteCountDisplayField);
		topPanel.add(this.palleteCountTextField);
		topPanel.add(describeDisplayField);
		
		this.describeArea = new JTextArea();
		
		bottomPanel.add(this.describeArea);
		
		panel.add(topPanel);
		panel.add(bottomPanel);
		
		return panel;
	}
	
	private JTextField createDisplayField(String text)
	{
		JTextField field = new JTextField(text);
		
		field.setHorizontalAlignment(SwingConstants.CENTER);
		field.setEditable(false);
		field.setColumns(20);
		
		return field;
	}
	
	private JPanel createBottomPanel()
	{
		JPanel panel = new JPanel(new FlowLayout());
		
		JButton addButton = new JButton("Zapisz");
		addButton.setActionCommand("add");
		addButton.addActionListener(this);
		
		JButton cancelButton = new JButton("Anuluj");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		JButton chooseButton = new JButton("Znajdz");
		chooseButton.setActionCommand("choose");
		chooseButton.addActionListener(this);
		
		JTextField separator = new JTextField(65);
		separator.setBorder(null);
		separator.setEditable(false);
		
		
		panel.add(addButton);
		panel.add(cancelButton);
		panel.add(separator);
		panel.add(chooseButton);
		
		return panel;
	}
	
	private void loadID()
	{
		this.idTextField.setText(generateID());
	}
	
	private String generateID()
	{
		String id = "";
		
		int shipmentNumber = getShipmentCounter();
		int year = 0;
		int month = 0;
		int day = 0;
		
		Calendar calendar = Calendar.getInstance();
		
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		
		id = String.format("WYS %d/%d/%d nr.%d", day, month + 1, year, shipmentNumber);
		
		return id;
	}
	
	private static int getShipmentCounter()
	{
		shipmentCounter++;
		
		return shipmentCounter;
	}
	
	private void setRow(HashMap<String, String> row)
	{
		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		
		try
		{
			this.idTable.add(Integer.valueOf(row.get("id")));
			
			Vector<String> rowToInsert = new Vector<String>();
			rowToInsert.add((String)row.get("brand"));
			rowToInsert.add((String)row.get("model"));
			rowToInsert.add((String)row.get("type"));
			rowToInsert.add((String)row.get("processor"));
			rowToInsert.add((String)row.get("ram"));
			rowToInsert.add((String)row.get("hdd"));
			rowToInsert.add((String)row.get("dvd"));
			rowToInsert.add((String)row.get("license"));
			
			model.addRow(rowToInsert);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "choose":
			{
				FindComputerDialog findDialog = new FindComputerDialog();
				setRow(findDialog.showDialog());
				break;
			}
			case "add":
			{
				registerShipment();
				addCodeBars();
				addComputerAmount(prepareComputerAmountQuery());
				
				dispose();
				break;
			}
			case "cancel":
			{
				dispose();
				break;
			}
			default:
				break;
		}
	}
	
	private void registerShipment()
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			if(checkShipmentValues() == false)
			{
				return;
			}
			
			preparedStatement = this.connection.prepareStatement("INSERT INTO shipment VALUES (default, ?, ?, ?, ?, ?)");
			preparedStatement.setDate(1, parseStringToDate(this.dateTextField.getText()));
			preparedStatement.setString(2, this.idTextField.getText());
			preparedStatement.setInt(3, Integer.valueOf(this.warrantyLengthTextField.getText()));
			preparedStatement.setInt(4, Integer.valueOf(this.palleteCountTextField.getText()));
			preparedStatement.setString(5, this.describeArea.getText());
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
	
	private boolean checkShipmentValues()
	{
		boolean statement = true;
		
		if(this.dateTextField.getText().length() == 0)
		{
			statement = false;
			JOptionPane.showMessageDialog(this, "Musisz uzupelnic pole z data wysylki.", "Error", JOptionPane.ERROR_MESSAGE);
			return statement;
		}
		
		if(this.warrantyLengthTextField.getText().length() == 0)
		{
			statement = false;
			JOptionPane.showMessageDialog(this, "Musisz uzupelnic pole z dlugoscia gwarancji.", "Error", JOptionPane.ERROR_MESSAGE);
			return statement;
		}
		
		return statement;
	}
	
	private void addCodeBars()
	{
		Statement statement = null;
		String barCodes = prepareBarCodes();
		
		try
		{
			statement = this.connection.createStatement();
			statement.execute(String.format("INSERT INTO bar_codes (shipment_id, casing, motherboard, hdd, power_supply, dvd) VALUES %s", barCodes));
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
	
	private String prepareBarCodes()
	{
		HashMap<String, String> onceRow;
		String result = "";
		String singleBracket = "";
		
		for(int i = 0; i < this.tableData.size(); i++)
		{
			onceRow = this.tableData.get(i);
			
			singleBracket = String.format("('%s', '%s', '%s', '%s', '%s', '%s')", this.idTextField.getText(), 
					onceRow.get("Obudowa"), onceRow.get("Plyta glowna"), onceRow.get("Dysk"), onceRow.get("Zasilacz"), onceRow.get("Naped"));
			
			result += singleBracket;
			singleBracket = "";
		}
		
		result = result.replace(")(", "), (");
		//System.out.println(result);
		
		return result;
	}
	
	private void addComputerAmount(String query)
	{
		Statement statement = null;
		
		if(query == null || query.length() == 0)
		{
			return;
		}
		
		try
		{
			statement = this.connection.createStatement();
			statement.execute(query);
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
	
	private String prepareComputerAmountQuery()
	{
		String result = "INSERT INTO shipment_content (shipment_id, computer_id, computer_amount) VALUES ";
		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		int rowCount = model.getRowCount();
		String brackets = "";
		
		for(int i = 0; i < rowCount; i++)
		{
			String tmp = String.format("('%s', '%d', '%d')", this.idTextField.getText(), this.idTable.get(i), Integer.valueOf((String)model.getValueAt(i, 8)));
			
			brackets += tmp;
		}
		
		brackets = brackets.replace(")(", "),(");
		
		result += brackets;
		
		//System.out.println(result);
		
		return result;
	}
	
	private Date parseStringToDate(String stringDate)
	{
		Date date = null;
		
		String[] words = stringDate.split(Pattern.quote("."));
		
		date = Date.valueOf(words[2] + "-" + words[1] + "-" + words[0]);
		
		return date;
	}
	
	private String parseDateToString(Date date)
	{
		String result = "";
		String tmp = date.toString();
		
		System.out.println(tmp);
		
		String[] timePieces = tmp.split(Pattern.quote("-"));
		
		result = timePieces[2] + "." + timePieces[1] + "." + timePieces[0];
		
		System.out.println(result);
		
		return result;
	}
}










































