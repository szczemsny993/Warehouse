package Dialogs;

import Account.Account;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import FileManager.Config;
import Print.ShipmentBarCodesDoc;
import Print.ShipmentContentDoc;

import java.sql.*;

import java.util.Vector;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ShipmentShowDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	protected String shipmentNumber;
	protected Connection connection;
	
	protected JTable computerTable;
	protected JTable barCodesTable;
	
	protected JTextField dateTextField;
	protected JTextField idTextField;
	protected JTextField warrantyLengthTextField;
	protected JTextField palleteCountTextField;
	protected JTextArea describeArea;
	
	protected Vector<Integer> computerIdTable;
	protected Vector<Integer> barCodesIdTable;
	protected Vector<Integer> contentFieldTable;
	
	public ShipmentShowDialog(String shipmentNumber)
	{
		super(null, "", JDialog.ModalityType.DOCUMENT_MODAL);
		try
		{
			this.connection = Config.getConnection();
			this.shipmentNumber = shipmentNumber;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setTitle("Dostawa " + shipmentNumber);
		setSize(1100, 500);
		setLocationRelativeTo(null);
		setCenterPos();
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		container.add(createLeftPanel(), BorderLayout.WEST);
		container.add(createTablePanel(), BorderLayout.CENTER);
		container.add(createBottomPanel(), BorderLayout.SOUTH);
		
		
		loadData();
	}
	
	protected void loadData()
	{
		loadShipmentData();
		loadBarCodes();
		loadComputersTable();
	}
	
	protected void setCenterPos()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		int x = (screenSize.width - 1100) / 2;
		int y = (screenSize.height - 500) / 2;
		
		setLocation(x, y);
	}
	
	protected JPanel createBottomPanel()
	{
		JPanel panel = new JPanel(new FlowLayout());
		
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton saveButton = new JButton("Zapisz");
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);
		
		JButton cancelButton = new JButton("Anuluj");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		leftPanel.add(saveButton);
		leftPanel.add(cancelButton);
		
		panel.add(leftPanel);
		//panel.add(rightPanel);
		
		return panel;
	}
	
	protected JPanel createLeftPanel()
	{
		JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.setPreferredSize(new Dimension(170, 300));
		
		JPanel topPanel = new JPanel(new GridLayout(9, 1));
		JPanel bottomPanel = new JPanel(new GridLayout(1, 1));
		
		JTextField dateDisplayField = createDisplayField("Data:");
		this.dateTextField = new JTextField();
		
		JTextField idDisplayField = createDisplayField("Identyfikator:");
		this.idTextField = new JTextField();
		this.idTextField.setEditable(false);
		
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
		Border describeBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		this.describeArea.setBorder(describeBorder);
		
		bottomPanel.add(this.describeArea);
		
		panel.add(topPanel);
		panel.add(bottomPanel);
		
		return panel;
	}
	
	protected JTextField createDisplayField(String text)
	{
		JTextField field = new JTextField(text);
		
		field.setHorizontalAlignment(SwingConstants.CENTER);
		field.setEditable(false);
		field.setColumns(20);
		
		return field;
	}
	
	protected JPanel createTablePanel()
	{
		JPanel panel = new JPanel(new GridLayout(2, 1));
		
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel bottomPanel = new JPanel(new BorderLayout());
		
		Border topBorder, bottomBorder;
		topBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		bottomBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		
		topPanel.setBorder(topBorder);
		bottomPanel.setBorder(bottomBorder);
		
		this.barCodesTable = createBarCodeTable(createBarCodeModel());
		this.computerTable = createComputerTable(createComputerModel());
		
		JScrollPane upPane = new JScrollPane(this.barCodesTable);
		JScrollPane downPane = new JScrollPane(this.computerTable);
		
		topPanel.add(upPane, BorderLayout.CENTER);
		topPanel.add(createTopToolBar(), BorderLayout.EAST);

		bottomPanel.add(downPane, BorderLayout.CENTER);
		bottomPanel.add(createBottomToolBar(), BorderLayout.EAST);
		
		panel.add(topPanel);
		panel.add(bottomPanel);
		
		return panel;
	}
	
	protected JToolBar createTopToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon printIcon = new ImageIcon("resources/printer_32px.png");
		ImageIcon addIcon = new ImageIcon("resources/add_32px.png");
		ImageIcon removeIcon = new ImageIcon("resources/close_32px.png");
		
		JButton printButton = new JButton(printIcon);
		printButton.setActionCommand("bar_code_print");
		printButton.addActionListener(this);
		
		JButton addButton = new JButton(addIcon);
		addButton.setActionCommand("bar_code_add");
		addButton.addActionListener(this);
		
		JButton removeButton = new JButton(removeIcon);
		removeButton.setActionCommand("bar_code_remove");
		removeButton.addActionListener(this);
		
		toolBar.add(addButton);
		toolBar.add(removeButton);
		toolBar.addSeparator();
		toolBar.add(printButton);
		
		return toolBar;
	}
	
	protected JToolBar createBottomToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon printIcon = new ImageIcon("resources/printer_32px.png");
		ImageIcon addIcon = new ImageIcon("resources/add_32px.png");
		ImageIcon removeIcon = new ImageIcon("resources/close_32px.png");
		
		JButton printButton = new JButton(printIcon);
		printButton.setActionCommand("computer_print");
		printButton.addActionListener(this);
		
		JButton addButton = new JButton(addIcon);
		addButton.setActionCommand("computer_add");
		addButton.addActionListener(this);
		
		JButton removeButton = new JButton(removeIcon);
		removeButton.setActionCommand("computer_remove");
		removeButton.addActionListener(this);
		
		toolBar.add(addButton);
		toolBar.add(removeButton);
		toolBar.addSeparator();
		toolBar.add(printButton);
		
		return toolBar;
	}
	
	protected DefaultTableModel createBarCodeModel()
	{
		DefaultTableModel model = new DefaultTableModel();
		
		model.addColumn("S/N komputera");
		model.addColumn("Plyta glowna");
		model.addColumn("Dysk Twardy");
		model.addColumn("Zasilacz");
		model.addColumn("Naped DVD");
		
		return model;
	}
	
	protected JTable createBarCodeTable(DefaultTableModel model)
	{
		JTable table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		
		return table;
	}
	
	protected DefaultTableModel createComputerModel()
	{
		DefaultTableModel model = new DefaultTableModel()
				{
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int col)
					{
						if(col == 8)
						{
							return true;
						}
						else
						{
							return false;
						}
					}
				};
				
		model.addColumn("Marka");
		model.addColumn("Model");
		model.addColumn("Typ");
		model.addColumn("Procesor");
		model.addColumn("Ram");
		model.addColumn("HDD");
		model.addColumn("Naped");
		model.addColumn("Licencja");
		model.addColumn("Ilosc");
				
		return model;
	}
	
	protected JTable createComputerTable(DefaultTableModel model)
	{
		JTable table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		
		return table;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "bar_code_add":
			{
				DefaultTableModel model = (DefaultTableModel)this.barCodesTable.getModel();
				model.addRow(new Object[] {"", "", "", "", ""});
				this.barCodesIdTable.add(0);
		
				break;
			}
			case "bar_code_remove":
			{
				deleteBarCodes();
				loadData();
				break;
			}
			case "bar_code_print":
			{
				ShipmentBarCodesDoc doc = new ShipmentBarCodesDoc(this.shipmentNumber);
				break;
			}
			case "computer_add":
			{
				addComputer();
				break;
			}
			case "computer_remove":
			{
				deleteComputer();
				loadData();
				break;
			}
			case "computer_print":
			{
				ShipmentContentDoc doc = new ShipmentContentDoc(this.shipmentNumber);
				doc.print();
				break;
			}
			case "save":
			{
				if(Account.getPermisionLevel() <= 1)
				{
					JOptionPane.showMessageDialog(null, "Brak uprawnien do wykonania tej operacji.");
					break;
				}
				updateShipmentData();
				updateBarCodes();
				updateComputers();
				
				dispose();
				break;
			}
			case "cancel":
			{
				dispose();
				break;
			}
			default:
			{
				break;
			}
		}
	}
	
	protected void loadShipmentData()
	{
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT * FROM shipment WHERE shipment_id = ?");
			preparedStatement.setString(1, shipmentNumber);
			
			resultSet = preparedStatement.executeQuery();
			if(checkResultSet(resultSet) == false)
			{
				return;
			}
			
			while(resultSet.next())
			{
				this.dateTextField.setText(parseDateToString(resultSet.getDate("date")));
				this.idTextField.setText(resultSet.getString("shipment_id"));
				this.warrantyLengthTextField.setText(String.valueOf(resultSet.getInt("warranty_length")));
				this.palleteCountTextField.setText(String.valueOf(resultSet.getInt("pallete_count")));
				this.describeArea.setText(resultSet.getString("description"));
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
	
	protected void loadBarCodes()
	{
		DefaultTableModel model = (DefaultTableModel)this.barCodesTable.getModel();
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		int rowCount = 0;
		int counter = 0;
		this.barCodesIdTable = new Vector<Integer>();
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT * FROM bar_codes WHERE shipment_id = ?");
			preparedStatement.setString(1, this.shipmentNumber);
			
			resultSet = preparedStatement.executeQuery();
			
			if(checkResultSet(resultSet) == false)
			{
				return;
			}
			
			rowCount = getRowCount(resultSet);
			model.setRowCount(rowCount);
			
			while(resultSet.next())
			{
				barCodesIdTable.add(resultSet.getInt("id"));
				
				model.setValueAt(resultSet.getString("casing"), counter, 0);
				model.setValueAt(resultSet.getString("motherboard"), counter, 1);
				model.setValueAt(resultSet.getString("hdd"), counter, 2);
				model.setValueAt(resultSet.getString("power_supply"), counter, 3);
				model.setValueAt(resultSet.getString("dvd"), counter, 4);
				
				counter++;
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
	
	protected void loadComputersTable()
	{
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int counter = 0;
		
		this.computerIdTable = new Vector<Integer>();
		this.contentFieldTable = new Vector<Integer>();
		DefaultTableModel model = (DefaultTableModel)this.computerTable.getModel();
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT id, computer_id, computer_amount FROM shipment_content WHERE shipment_id = ?");
			preparedStatement.setString(1, this.shipmentNumber);
			resultSet = preparedStatement.executeQuery();
			
			if(checkResultSet(resultSet) == false)
			{
				return;
			}
			
			model.setRowCount(getRowCount(resultSet));
			
			while(resultSet.next())
			{
				this.computerIdTable.add(resultSet.getInt("computer_id"));
				this.contentFieldTable.add(resultSet.getInt("id"));
	
				model.setValueAt(resultSet.getInt("computer_amount"), counter, 8);
				
				counter++;
			}
			
			loadComputers(model, this.computerIdTable);
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
	
	protected void loadComputers(DefaultTableModel model, Vector<Integer> tab)
	{
		Statement statement = null;
		ResultSet resultSet = null;
		int counter = 0;
		String sqlQuery = "SELECT * FROM computer WHERE id IN ";
		String computerIdsBracket = vectorToString(tab);
		
		sqlQuery += computerIdsBracket;
		
		try
		{
			statement = this.connection.createStatement();
			resultSet = statement.executeQuery(sqlQuery);
			
			if(checkResultSet(resultSet) == false)
			{
				return;
			}
			
			for(int i = 0; i < tab.size(); i++)
			{
				while(resultSet.next())
				{
					if(resultSet.getInt("id") == tab.get(i))
					{
						model.setValueAt(resultSet.getString("brand"), counter, 0);
						model.setValueAt(resultSet.getString("model"), counter, 1);
						model.setValueAt(resultSet.getString("type"), counter, 2);
						model.setValueAt(resultSet.getString("processor"), counter, 3);
						model.setValueAt(resultSet.getString("ram"), counter, 4);
						model.setValueAt(resultSet.getString("hdd"), counter, 5);
						model.setValueAt(String.valueOf(resultSet.getBoolean("dvd")), counter, 6);
						model.setValueAt(String.valueOf(resultSet.getBoolean("license")), counter, 7);
						break;
					}
				}
				
				counter++;
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
				if(statement != null)
				{
					statement.close();
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
	
	protected void updateShipmentData()
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("UPDATE shipment "
					+ "SET date = ?, warranty_length = ?, pallete_count = ?, description = ? WHERE shipment_id = ?");
			preparedStatement.setDate(1, parseStringToDate(this.dateTextField.getText()));
			preparedStatement.setInt(2, Integer.valueOf(this.warrantyLengthTextField.getText()));
			preparedStatement.setInt(3, Integer.valueOf(this.palleteCountTextField.getText()));
			preparedStatement.setString(4, this.describeArea.getText());
			preparedStatement.setString(5, this.shipmentNumber);
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
	
	protected void updateBarCodes()
	{
		PreparedStatement preparedStatement = null;
		DefaultTableModel model = (DefaultTableModel)this.barCodesTable.getModel();
		
		try
		{
			for(int i = 0; i < this.barCodesIdTable.size(); i++)
			{
				if(this.barCodesIdTable.get(i) == 0)
				{
					//System.out.println("Wykonujeee");
					
					if(checkRow(model, i) == false)
					{
						continue;
					}
					
					preparedStatement = this.connection.prepareStatement("INSERT INTO bar_codes"
							+ " (shipment_id, casing, motherboard, hdd, power_supply, dvd) VALUES (?, ?, ?, ?, ?, ?)");
					preparedStatement.setString(1, this.shipmentNumber);
					preparedStatement.setString(2, (String)model.getValueAt(i, 0));
					preparedStatement.setString(3, (String)model.getValueAt(i, 1));
					preparedStatement.setString(4, (String)model.getValueAt(i, 2));
					preparedStatement.setString(5, (String)model.getValueAt(i, 3));
					preparedStatement.setString(6, (String)model.getValueAt(i, 4));
					preparedStatement.executeUpdate();
				}
				else
				{
					//System.out.println("I to rowniez wykonuje xD");
					preparedStatement = this.connection.prepareStatement("UPDATE bar_codes SET casing = ?, motherboard = ?, hdd = ?, "
							+ "power_supply = ?, dvd = ? WHERE id = ?");
					preparedStatement.setString(1, (String)model.getValueAt(i, 0));
					preparedStatement.setString(2, (String)model.getValueAt(i, 1));
					preparedStatement.setString(3, (String)model.getValueAt(i, 2));
					preparedStatement.setString(4, (String)model.getValueAt(i, 3));
					preparedStatement.setString(5, (String)model.getValueAt(i, 4));
					preparedStatement.setInt(6, this.barCodesIdTable.get(i));
					preparedStatement.executeUpdate();
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
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	/*
	 * Func checking vality row of bar code
	 * if 4 or more cells is empty, then row is invalid
	 * */
	protected boolean checkRow(DefaultTableModel model, int row)
	{
		boolean statement = true;
		String tmp = "";
		int counter = 0;
		
		for(int i = 0; i < model.getColumnCount(); i++)
		{
			tmp = (String)model.getValueAt(row, i);
			if(tmp.length() == 0)
			{
				counter++;
				continue;
			}
		}
		
		if(counter >= 5)
		{
			statement = false;
		}
		
		return statement;
	}
	
	protected void updateComputers()
	{
		PreparedStatement preparedStatement = null;
		DefaultTableModel model = (DefaultTableModel)this.computerTable.getModel();
		
		try
		{
			for(int i = 0; i < computerIdTable.size(); i++)
			{
				Object tmp = model.getValueAt(i, 8);
				String test = tmp.toString();
				int parsedInt = Integer.parseInt(test);
				
				/*
				System.out.println("Wiersz numer : " + (i + 1));
				System.out.println(parsedInt);
				System.out.println(this.shipmentNumber);
				System.out.println(this.computerIdTable.get(i));
				System.out.println("*****************************");
				*/

				preparedStatement = this.connection.prepareStatement("UPDATE shipment_content "
						+ "SET computer_amount = ? WHERE shipment_id = ? AND id = ?");
				preparedStatement.setInt(1, parsedInt);
				preparedStatement.setString(2, this.shipmentNumber);
				preparedStatement.setInt(3, this.contentFieldTable.get(i));
				preparedStatement.executeUpdate();
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
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	protected boolean checkResultSet(ResultSet resultSet)
	{
		boolean statement = true;
		int counter = 0;
		
		try
		{
			if(resultSet == null)
			{
				statement = false;
				return statement;
			}
			
			while(resultSet.next())
			{
				counter++;
			}
			
			resultSet.absolute(0);
			
			if(counter == 0)
			{
				statement = false;
				return statement;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return statement;
	}
	
	protected int getRowCount(ResultSet resultSet)
	{
		int rowCount = 0;
		
		try
		{
			while(resultSet.next())
			{
				rowCount++;
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return rowCount;
	}

	protected String vectorToString(Vector<Integer> table)
	{
		String result = table.toString();
		
		result = result.replace("[", "(");
		result = result.replace("]", ")");
		
		return result;
	}
	
	protected void addComputer()
	{
		HashMap<String, String> computer = null;
		PreparedStatement preparedStatement = null;
		DefaultTableModel model = (DefaultTableModel) this.computerTable.getModel();
		Vector<String> row = new Vector<String>();
		
		FindComputerDialog findComputerDialog = new FindComputerDialog();
		computer = findComputerDialog.showDialog();
		
		try
		{
			preparedStatement = this.connection.prepareStatement("INSERT INTO shipment_content (shipment_id, computer_id, computer_amount) VALUES (?, ?, 0)");
			preparedStatement.setString(1, this.shipmentNumber);
			preparedStatement.setInt(2, Integer.valueOf(computer.get("id")));
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
		
		this.computerIdTable.add(Integer.valueOf(computer.get("id")));
		
		row.add(computer.get("brand"));
		row.add(computer.get("model"));
		row.add(computer.get("type"));
		row.add(computer.get("processor"));
		row.add(computer.get("ram"));
		row.add(computer.get("hdd"));
		row.add(computer.get("dvd"));
		row.add(computer.get("license"));
		row.add("0");
		
		model.addRow(row);
	}
	
	protected String parseDateToString(Date date)
	{
		String result = "";
		String tmp = date.toString();
		
		String[] timePieces = tmp.split(Pattern.quote("-"));
		
		int day = Integer.valueOf(timePieces[2]) + 1;
		
		result = String.valueOf(day) + "." + timePieces[1] + "." + timePieces[0];
		
		return result;
	}
	
	protected Date parseStringToDate(String stringDate)
	{
		Date date = null;
		
		String[] words = stringDate.split(Pattern.quote("."));
		
		date = Date.valueOf(words[2] + "-" + words[1] + "-" + words[0]);
		
		return date;
	}
	
	protected void deleteBarCodes()
	{
		Statement statement = null;
		int[] selectedRows = this.barCodesTable.getSelectedRows();
		int ret_val = -1;
		
		if(selectedRows.length <= 0)
		{
			return;
		}
		
		ret_val = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz usunąć: " + selectedRows.length + " elementów?", "", JOptionPane.YES_NO_OPTION);
		
		if(ret_val == JOptionPane.YES_OPTION)
		{
			Vector<Integer> id = new Vector<Integer>();
			String query = "";
			
			for(int row : selectedRows)
			{
				id.add(this.barCodesIdTable.get(row));
			}
			
			query = createDeleteSqlQuery("bar_codes", id);
			System.out.println(query);
			
			try
			{
				statement = this.connection.createStatement();
				statement.execute(query);
				
				JOptionPane.showMessageDialog(this, "Usunięto " + selectedRows.length + " elementów.", "", JOptionPane.INFORMATION_MESSAGE);
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
		else
		{
			JOptionPane.showMessageDialog(this, "Musisz zaznaczyć wiersz", "", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	protected void deleteComputer()
	{
		Statement statement = null;
		int[] selectedRows = this.computerTable.getSelectedRows();
		int ret_val = -1;
		
		if(selectedRows.length <= 0)
		{
			return;
		}
		
		ret_val = JOptionPane.showConfirmDialog(this, "Czy chcesz usunąć: " + selectedRows.length + " elementów?", "", JOptionPane.YES_NO_OPTION);
		if(ret_val == JOptionPane.YES_OPTION)
		{
			Vector<Integer> id = new Vector<Integer>();
			String sql = "";
			
			for(int row : selectedRows)
			{
				id.add(this.contentFieldTable.get(row));
			}
			
			sql = createDeleteSqlQuery("shipment_content", id);
			System.out.println(sql);
			
			try
			{
				statement = this.connection.createStatement();
				statement.execute(sql);
				
				JOptionPane.showMessageDialog(this, "Usunięto: " + selectedRows.length + " rekordów.", "", JOptionPane.INFORMATION_MESSAGE);
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
		else
		{
			JOptionPane.showMessageDialog(this, "Musisz zaznaczyć wiersz", "", JOptionPane.INFORMATION_MESSAGE);
		}	
	}
	
	protected String createDeleteSqlQuery(String table_name, Vector<Integer> idTable)
	{
		String table = idTable.toString();
		String query = "";
		
		table = table.replace("[", "(");
		table = table.replace("]", ")");
		
		query = String.format("DELETE FROM %s WHERE id IN %s", table_name, table);
		
		return query;
	}
}


































