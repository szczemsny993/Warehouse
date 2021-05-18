package Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import FileManager.Config;

import java.util.Vector;
import java.util.Calendar;
import java.util.HashMap;

import java.sql.*;

public class SupplySaveDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	protected Connection connection;
	protected JTable table;
	protected Vector<HashMap<String, String>> data;
	
	private JPanel leftPanel;
	private JPanel downPanel;
	
	private JTextField dateField;
	private JTextField idField;
	private JTextField amountPalleteField;
	private JTextField amountField;
	private JTextArea descriptionFieldArea;
	
	public SupplySaveDialog(Vector<HashMap<String, String>> data)
	{
		super(null, "Dostawa", JDialog.ModalityType.DOCUMENT_MODAL);
		
		try
		{
			this.connection = Config.getConnection();
			this.data = data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setLayout(new BorderLayout());
		setSize(1200, 600);
		setLocationRelativeTo(null);
		
		this.table = createTable();
		fillInDataModel(this.data, this.table);
		
		JScrollPane scrollPane = new JScrollPane(this.table);
		add(scrollPane, BorderLayout.CENTER);
		
		this.leftPanel = createLeftPanel();
		add(this.leftPanel, BorderLayout.WEST);
		
		this.downPanel = createDownPanel();
		add(this.downPanel, BorderLayout.SOUTH);
		
		this.idField.setText(makeID(createLastNumber()));
		this.amountField.setText(String.valueOf(countComputerAmount()));
		
		setVisible(true);
	}
	
	private JPanel createDownPanel()
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JButton okButton = new JButton("Dodaj");
		okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(registerSupply())
						{
							if(saveComputers())
								dispose();
						}
					}
				});
		
		JButton cancelButton = new JButton("Anuluj");
		cancelButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						dispose();
					}
				});
		
		
		panel.add(okButton);
		panel.add(cancelButton);
		
		return panel;
	}
	
	private JPanel createLeftPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBounds(1, 1, 80, 400);
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		
		JLabel dateLabel = new JLabel("Data:");
		dateLabel.setHorizontalAlignment(JLabel.CENTER);
		this.dateField = new JTextField(16);
		
		JLabel idLabel = new JLabel("Identyfikator:");
		idLabel.setHorizontalAlignment(JLabel.CENTER);
		this.idField = new JTextField(16);
		this.idField.setEditable(false);
		
		JLabel amountPalleteLabel = new JLabel("Ilosc palet:");
		amountPalleteLabel.setHorizontalAlignment(JLabel.CENTER);
		this.amountPalleteField = new JTextField(3);
		
		JLabel amountLabel = new JLabel("Sztuk:");
		amountLabel.setHorizontalAlignment(JLabel.CENTER);
		this.amountField = new JTextField(8);
		this.amountField.setEditable(false);
		
		JLabel descriptionLabel = new JLabel("Opis:");
		descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
		
		panel1.add(dateLabel);
		panel1.add(this.dateField);
		panel1.add(idLabel);
		panel1.add(this.idField);
		panel1.add(amountPalleteLabel);
		panel1.add(this.amountPalleteField);
		panel1.add(amountLabel);
		panel1.add(this.amountField);
		panel1.add(descriptionLabel);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		
		this.descriptionFieldArea = new JTextArea();
		this.descriptionFieldArea.setLineWrap(true);
		
		JScrollPane descScrollPane = new JScrollPane(this.descriptionFieldArea);
		descScrollPane.setPreferredSize(new Dimension(80, 400));
		
		panel2.add(descScrollPane);
		
		panel.add(panel1);
		panel.add(panel2);
		
		return panel;
	}
	
	private JTable createTable()
	{
		JTable table = new JTable(createModel());
		table.getTableHeader().setReorderingAllowed(false);
		
		return table;
	}
	
	
	private DefaultTableModel createModel()
	{
		DefaultTableModel model = new DefaultTableModel()
				{
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int column)
					{
						return false;
					}
				};
				
		model.addColumn("Marka");
		model.addColumn("Model");
		model.addColumn("Typ");
		model.addColumn("Processor");
		model.addColumn("Ram");
		model.addColumn("Dysk Twardy");
		model.addColumn("Naped DVD");
		model.addColumn("Licencja");
		model.addColumn("Ilosc");
		
		return model;
	}

	private void fillInDataModel(Vector<HashMap<String, String>> data, JTable table)
	{
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		
		int rowCount = data.size();
		model.setRowCount(rowCount);
		
		for(int i = 0; i < rowCount; i++)
		{
			HashMap<String, String> row = data.get(i);
			
			model.setValueAt((String)row.get("brand"), i, 0);
			model.setValueAt((String)row.get("model"), i, 1);
			model.setValueAt((String)row.get("type"), i, 2);
			model.setValueAt((String)row.get("processor"), i, 3);
			model.setValueAt((String)row.get("ram"), i, 4);
			model.setValueAt((String)row.get("hdd"), i, 5);
			model.setValueAt((String)row.get("dvd"), i, 6);
			model.setValueAt((String)row.get("license"), i, 7);
			model.setValueAt((String)row.get("amount"), i, 8);
		}
	}
	
	private boolean registerSupply()
	{
		boolean result = false;
		PreparedStatement statement = null;
		String dateValue, idValue, amountValue, palleteValue, commentValue = "";
		
		try
		{
			statement = connection.prepareStatement("INSERT INTO supply values(default, ?, ?, ?, ?, ?) ");
			
			idValue = (String)this.idField.getText();
			int integerIdValue = Integer.valueOf(idValue);
			statement.setInt(1, integerIdValue);	
			
			dateValue = (String)this.dateField.getText();
			if(dateValue == "" || dateValue == null)
			{
				statement.setString(2, "");
			}
			else
			{
				statement.setString(2, dateValue);
			}
			
			amountValue = (String)this.amountField.getText();
			int integerAmountValue = Integer.valueOf(amountValue);
			statement.setInt(3, integerAmountValue);
			
			palleteValue = (String)this.amountPalleteField.getText();
			int integerPalleteValue = Integer.valueOf(palleteValue);
			
			if(integerPalleteValue != -1)
			{
				statement.setInt(4, integerPalleteValue);
			}
			else
			{
				statement.setString(4, "0");
			}
			
			commentValue = (String)this.descriptionFieldArea.getText();
			if(commentValue == "" || commentValue == null)
			{
				statement.setString(5, "");
			}
			else
			{
				if(commentValue.length() <= 255)
				{
					statement.setString(5, commentValue);
				}
				else
				{
					return false;
				}
			}
			
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			//clean statement
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
		
		result = true;
		return result;
	}
	
	private boolean saveComputers()
	{
		HashMap<String, String> row = null;
		PreparedStatement prepStatement = null;
		ResultSet resultSet = null;
		Statement statement = null;
		
		int computerId = 0;
		int supplyId = 0;
		int amount = 0;
		
		String row_brand, row_model, row_type, row_processor, row_ram, row_hdd, row_dvd, row_license;
		String retriveBrand, retriveModel, retriveType, retriveProcessor, retriveRam, retriveHdd, retriveDvd, retriveLicense;
		
		try
		{
			supplyId = Integer.valueOf(this.idField.getText());
			
			statement = this.connection.createStatement();
			
			resultSet = statement.executeQuery("SELECT * FROM computer");
			
			for(int i = 0; i < this.data.size(); i++)
			{
				//get row data from vector of rows
				row = this.data.get(i);
				
				row_brand = row.get("brand");
				row_model = row.get("model");
				row_type = row.get("type");
				row_processor = row.get("processor");
				row_ram = row.get("ram");
				row_hdd = row.get("hdd");
				row_dvd = row.get("dvd");
				row_license = row.get("license");
				amount = Integer.valueOf(row.get("amount"));
				
				while(resultSet.next())
				{
					retriveBrand = resultSet.getString("brand");
					retriveModel = resultSet.getString("model");
					retriveType = resultSet.getString("type");
					retriveProcessor = resultSet.getString("processor");
					retriveRam = resultSet.getString("ram");
					retriveHdd = resultSet.getString("hdd");
					retriveDvd = String.valueOf(resultSet.getBoolean("dvd"));
					retriveLicense = String.valueOf(resultSet.getBoolean("license"));
					
					
					if(row_brand.compareTo(retriveBrand) == 0 && row_model.compareTo(retriveModel) == 0
						&& row_type.compareTo(retriveType) == 0 && row_processor.compareTo(retriveProcessor) == 0
						&& row_ram.compareTo(retriveRam) == 0 && row_hdd.compareTo(retriveHdd) == 0
						&& row_dvd.compareTo(retriveDvd) == 0 && row_license.compareTo(retriveLicense) == 0)
					{
						computerId = resultSet.getInt("id");
						
						prepStatement = connection.prepareStatement("INSERT INTO supply_private (supply_id, computer_id, amount) values (?, ?, ?)");
						prepStatement.setInt(1, supplyId);
						prepStatement.setInt(2, computerId);
						prepStatement.setInt(3, amount);
						prepStatement.executeUpdate();
					}
				}
				
				resultSet.absolute(0);
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
				if(resultSet != null)
				{
					resultSet.close();
				}
				
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
		
		return true;
	}
	
	//private void insertAmountToDB
	
	private int countComputerAmount()
	{
		int result = 0;
		
		HashMap<String, String> line = null;
		String amount = "";
		int integerRepresentation = 0;
		//System.out.println("Data size:" + this.data.size());
		
		for(int i = 0; i < this.data.size(); i++)
		{
			line = this.data.get(i);
			amount = line.get("amount");
			
			integerRepresentation = Integer.valueOf(amount);
			//System.out.println(integerRepresentation);
			
			result += integerRepresentation;
		}
		
		return result;
	}
	
	private int createLastNumber()
	{
		int result = 0;
		
		Calendar calendar = Calendar.getInstance();
		
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minuts = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
	
		result = hour + minuts + seconds;

		result = result % 10;
		
		return result;
	}
	
	private String makeID(int lastNumber)
	{	
		String id = "";
		Calendar calendar = Calendar.getInstance();
		
		int year = calendar.get(Calendar.YEAR);
		year -= 2000;
		
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		
		System.out.println(month);
		System.out.println(day);
		System.out.println(hour);
		
		id += "0";
		id += String.valueOf(year);
		
		if(month < 10)
		{
			id += "0";
			id += String.valueOf(month);
		}
		else
		{
			id += String.valueOf(month);
		}
		
		if(day < 10)
		{
			id += "0";
			id += String.valueOf(day);
		}
		else
		{
			id += String.valueOf(day);
		}
		
		if(hour > 10)
		{
			id += 0;
			id += String.valueOf(hour);
		}
		else
		{
			id += String.valueOf(hour);
		}
		
		id += String.valueOf(lastNumber);
		
		System.out.println(id);
		
		return id;
	}
}

















