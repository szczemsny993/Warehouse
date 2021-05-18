package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import FileManager.Config;
import Print.SupplyPrintDoc;

import javax.swing.Timer;

import java.sql.*;

import java.util.Vector;
import java.util.HashMap;

public class SupplyShowDialog extends JDialog implements ActionListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	protected final static int bottomTextLength = 8;
	
	protected Connection connection;
	protected int supplyId;
	protected Vector<Integer> idTable;
	protected Vector<Integer> recordIdTable;
	
	protected JTable table;
	
	protected JTextField dateTextField;
	protected JTextField palleteAmountTextField;
	protected JTextField amountTextField;
	protected JTextField descriptionTextField;
	
	public SupplyShowDialog(int supplyId)
	{
		super(null, "Dostawa", JDialog.ModalityType.DOCUMENT_MODAL);
		try
		{
			this.connection = Config.getConnection();
			this.table = null;
			this.supplyId = supplyId;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setLocationRelativeTo(null);
		setSize(1050, 400);
		setCenterPos();
		setResizable(false);
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		JToolBar toolBar = createToolBar();
		container.add(toolBar, BorderLayout.EAST);
		
		this.table = createTable(createModel());
		JScrollPane scrollPane = new JScrollPane(this.table);
		container.add(scrollPane, BorderLayout.CENTER);
		
		JPanel bottomPanel = createBottomPanel();
		container.add(bottomPanel, BorderLayout.SOUTH);
		
		getDataFromDB();
		fillInModel();
	}
	
	protected void setCenterPos()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		int x = (screenSize.width - 1050) / 2;
		int y = (screenSize.height - 400) / 2;
		
		setLocation(x, y);
	}
	
	protected JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon addIcon = new ImageIcon("resources/add_32px.png");
		ImageIcon removeIcon = new ImageIcon("resources/close_32px.png");
		ImageIcon saveIcon = new ImageIcon("resources/discette_32px.png");
		ImageIcon printIcon = new ImageIcon("resources/printer_32px.png");
		
		
		JButton addButton = new JButton(addIcon);
		addButton.setActionCommand("add");
		addButton.addActionListener(this);
		
		JButton removeButton = new JButton(removeIcon);
		removeButton.setActionCommand("remove");
		removeButton.addActionListener(this);
		
		JButton saveButton = new JButton(saveIcon);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);
		
		JButton printButton = new JButton(printIcon);
		printButton.addActionListener(this);
		printButton.setActionCommand("print");
		
		toolBar.add(addButton);
		toolBar.add(removeButton);
		toolBar.addSeparator();
		toolBar.add(saveButton);
		toolBar.add(printButton);
		
		return toolBar;
	}
	
	protected JTable createTable(DefaultTableModel model)
	{
		JTable table = new JTable(model);
		
		table.getTableHeader().setReorderingAllowed(false);
		table.addKeyListener(this);
		
		return table;
	}
	
	protected DefaultTableModel createModel()
	{
		DefaultTableModel model = new DefaultTableModel()
				{
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int column)
					{
						if(column == 8)
						{
							return true;
						}
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
	
	protected JPanel createBottomPanel()
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JTextField dateDisplayField = createDisplayField("Data:");
		this.dateTextField = new JTextField(bottomTextLength);
		
		JTextField amountPalleteDisplayField = createDisplayField("Ilosc palet:");
		this.palleteAmountTextField = new JTextField(bottomTextLength);
		
		JTextField amountDisplayField = createDisplayField("Sztuk razem:");
		this.amountTextField = new JTextField(bottomTextLength);
		this.amountTextField.setEditable(false);
		
		JTextField descriptionDisplayField = createDisplayField("Opis:");
		this.descriptionTextField = new JTextField(32);
		
		panel.add(dateDisplayField);
		panel.add(this.dateTextField);
		panel.add(amountPalleteDisplayField);
		panel.add(this.palleteAmountTextField);
		panel.add(amountDisplayField);
		panel.add(this.amountTextField);
		panel.add(descriptionDisplayField);
		panel.add(this.descriptionTextField);
		
		return panel;
	}
	
	protected JTextField createDisplayField(String displayedText)
	{
		if(displayedText == null || displayedText.length() <= 0)
		{
			throw new IllegalArgumentException("You must give not null text!");
		}
		
		JTextField textField = new JTextField(displayedText);
		textField.setColumns(7);
		textField.setEditable(false);
		textField.setHorizontalAlignment(SwingConstants.RIGHT);
		
		return textField;
	}
	
	protected void getDataFromDB()
	{		
		PreparedStatement preparedStatement = null;;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT * FROM supply WHERE unique_id = ?");
			preparedStatement.setInt(1, this.supplyId);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				this.dateTextField.setText((String)resultSet.getString("date"));
				this.amountTextField.setText(String.valueOf(resultSet.getInt("amount")));
				this.palleteAmountTextField.setText(String.valueOf(resultSet.getInt("pallete_count")));
				this.descriptionTextField.setText((String)resultSet.getString("comment"));
				
				setTitle("Dostawa z dnia " + resultSet.getString("date"));
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
	
	protected void fillInModel()
	{
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		int rowCount = 0;
		Vector<Integer> amountTable;
		
		String query = "SELECT * FROM computer WHERE id IN ";
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Statement statement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT * FROM supply_private WHERE supply_id = ?");
			preparedStatement.setInt(1, this.supplyId);
			resultSet = preparedStatement.executeQuery();
			
			if(resultSet == null)
			{
				return;
			}
			
			amountTable = getAmountTable(resultSet);
			rowCount = getRowCount(resultSet);
			if(rowCount <= 0)
			{
				JOptionPane.showMessageDialog(this, "Wystapil problem z ta dostawa, skontaktuj sie z pomoca techniczna :)", "Error!", JOptionPane.ERROR_MESSAGE);
				return;
			}
			/*SELECT * FROM `Table` WHERE `id` IN (1, 2, 3)*/
			model.setRowCount(rowCount);
			
			this.idTable = getIdTable(resultSet);
			this.recordIdTable = getRecordsId(resultSet);
			
			preparedStatement.close();
			resultSet.close();
			
			query += createStringRepOfTable(this.idTable);
			
			statement = this.connection.createStatement();
			resultSet = statement.executeQuery(query);
			
			for(int i = 0; i < this.idTable.size(); i++)
			{
				int id = 0;
				while(resultSet.next())
				{
					id = resultSet.getInt("id");
					
					if(this.idTable.get(i) == id)
					{
						model.setValueAt(resultSet.getString("brand"), i, 0);
						model.setValueAt(resultSet.getString("model"), i, 1);
						model.setValueAt(resultSet.getString("type"), i, 2);
						model.setValueAt(resultSet.getString("processor"), i, 3);
						model.setValueAt(resultSet.getString("ram"), i, 4);
						model.setValueAt(resultSet.getString("hdd"), i, 5);
						model.setValueAt(String.valueOf(resultSet.getBoolean("dvd")), i, 6);
						model.setValueAt(String.valueOf(resultSet.getBoolean("license")), i, 7);
						model.setValueAt(amountTable.get(i), i, 8);
					}
					else
					{
						continue;
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
	
	protected Vector<Integer> getIdTable(ResultSet resultSet)
	{
		Vector<Integer> idTable = new Vector<Integer>();
		
		try
		{
			resultSet.absolute(0);
			
			while(resultSet.next())
			{
				idTable.add(resultSet.getInt("computer_id"));
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return idTable;
	}
	
	protected Vector<Integer> getRecordsId(ResultSet resultSet)
	{
		Vector<Integer> recordsId = new Vector<Integer>();
		
		try
		{
			while(resultSet.next())
			{
				recordsId.add(resultSet.getInt("id"));
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return recordsId;
	}
	
	protected Vector<Integer> getAmountTable(ResultSet resultSet)
	{
		Vector<Integer> amountTable = new Vector<Integer>();
		
		try
		{
			while(resultSet.next())
			{
				amountTable.add(resultSet.getInt("amount"));
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return amountTable;
	}
	
	protected String createStringRepOfTable(Vector<Integer> idTable)
	{
		String strTabRep = idTable.toString();
		
		strTabRep = strTabRep.replace('[', '(');
		strTabRep = strTabRep.replace(']', ')');
		
		return strTabRep;
	}
	
	protected int getRowCount(ResultSet resultSet)
	{
		int numRows = 0;
		
		try
		{
			while(resultSet.next())
			{
				numRows++;
			}
				
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return numRows;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "add":
			{

				addComputer();
				fillInModel();
				break;
			}
			case "remove":
			{
				removeRecords();
				updateComputerAmount();
				fillInModel();
				break;
			}
			case "save":
			{
				updateSupplyContent();
				updateComputerAmount();
				updateSupply();
				dispose();
				break;
			}
			case "print":
			{
				SupplyPrintDoc doc = new SupplyPrintDoc(this.supplyId, this.table);
				doc.print();
				break;
			}
		}
	}
	
	protected void addComputer()
	{
		PreparedStatement preparedStatement = null;
		HashMap<String, String> computer = null;
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		
		computer = new FindComputerDialog().showDialog();
		if(computer == null || computer.size() == 0)
		{
			return;
		}
		
		model.addRow(new Object[]{computer.get("brand"), computer.get("model"), computer.get("type"), computer.get("processor"),
								computer.get("ram"), computer.get("hdd"), computer.get("dvd"), computer.get("license"), "0" });
		
		try
		{
			preparedStatement = this.connection.prepareStatement("INSERT INTO supply_private(computer_id, amount, supply_id) VALUES (?, ?, ?)");
			preparedStatement.setInt(1, Integer.valueOf(computer.get("id")));
			preparedStatement.setInt(2, 0);
			preparedStatement.setInt(3, this.supplyId);
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
	
	protected void updateSupplyContent()
	{
		PreparedStatement preparedStatement = null;
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		
		try
		{
			for(int i = 0; i < this.idTable.size(); i++)
			{
				preparedStatement = this.connection.prepareStatement("UPDATE supply_private SET amount = ? WHERE supply_id = ? AND id = ?");
				preparedStatement.setInt(1, convertObjectToInteger(model.getValueAt(i, 8)));
				preparedStatement.setInt(2, this.supplyId);
				preparedStatement.setInt(3, this.recordIdTable.get(i));
				preparedStatement.executeUpdate();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void updateSupply()
	{
		PreparedStatement preparedStatement = null;
		
		try
		{
			preparedStatement = this.connection.prepareStatement("UPDATE supply SET date = ?, amount = ?, pallete_count = ?, comment = ? WHERE unique_id = ?");
			preparedStatement.setString(1, this.dateTextField.getText());
			preparedStatement.setInt(2, Integer.valueOf(this.amountTextField.getText()));
			preparedStatement.setInt(3,Integer.valueOf(this.palleteAmountTextField.getText()));
			preparedStatement.setString(4, this.descriptionTextField.getText());
			preparedStatement.setInt(5, this.supplyId);
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
	
	protected void updateComputerAmount()
	{
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		int resultOfAddition = 0;
		int rowCount = model.getRowCount();
		
		for(int i = 0; i < rowCount; i++)
		{
			resultOfAddition += convertObjectToInteger(model.getValueAt(i, 8));
		}
		
		this.amountTextField.setText(String.valueOf(resultOfAddition));
	}
	
	protected Integer convertObjectToInteger(Object object)
	{
		Integer result = null;
		String tmp = object.toString();
		
		result = Integer.valueOf(tmp);
		//System.out.println("Integer test, value: " + result);
		
		return result;
	}

	
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER && this.table.getEditingColumn() == 8)
		{			
			updateComputerAmount();
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		
	}
	
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	protected void removeRecords()
	{
		int[] selectedRows = this.table.getSelectedRows();
		int ret_val = -1;
		
		if(selectedRows.length == 0)
		{
			JOptionPane.showMessageDialog(this, "Nie można wykonać operacji.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		ret_val = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz usunąć " + selectedRows.length + " elementów?", "", JOptionPane.YES_NO_OPTION);
		if(ret_val == JOptionPane.YES_OPTION)
		{
			Vector<Integer> id = new Vector<Integer>();			
			String sql = "";
			Statement statement = null;
			String bracketString = "";
			
			for(int row: selectedRows)
			{
				id.add(this.recordIdTable.get(row));
			}
			
			bracketString = createStringRepOfTable(id);
			sql = String.format("DELETE FROM supply_private WHERE id IN %s", bracketString);
			
			try
			{
				statement = this.connection.createStatement();
				statement.execute(sql);
				
				JOptionPane.showMessageDialog(this, "Usunięto: " + selectedRows.length + " elementów.", "", JOptionPane.INFORMATION_MESSAGE);
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
			return;
		}
	}
	
	protected String tableToBracket(int[] table)
	{
		Vector<Integer> tmp = new Vector<Integer>();
		String result = "";
		
		for(int val: table)
		{
			tmp.add(this.recordIdTable.get(val));
		}
		
		result = tmp.toString();
		result = result.replace("[", "(");
		result = result.replace("]", ")");
		
		return result;
	}
}




























