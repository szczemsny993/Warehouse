package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import FileManager.Config;

import java.util.Calendar;
import java.util.Vector;
import java.util.HashMap;

import java.sql.*;

public class SupplyAddDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private JScrollPane scrollPane;
	private JTable mainTable;
	private JToolBar toolBar;
	
	public SupplyAddDialog()
	{
		super(null, "Dostawa", Dialog.ModalityType.DOCUMENT_MODAL);
		setSize(900, 400);
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
		
		createTable();
		container.add(scrollPane, BorderLayout.CENTER);
		
		createToolBar();
		container.add(toolBar, BorderLayout.EAST);
		
		setVisible(true);
	}
	
	private void createTable()
	{
		this.mainTable = new JTable(createModel());
		this.mainTable.getTableHeader().setReorderingAllowed(false);
		
		scrollPane = new JScrollPane(this.mainTable);
	}
	
	private DefaultTableModel createModel()
	{
		DefaultTableModel model = new DefaultTableModel()
				{
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int column)
					{
						if(column == 8)
							return true;
						else
							return false;
					}
				};
		
		model.addColumn("Marka");
		model.addColumn("Model");
		model.addColumn("Typ");
		model.addColumn("Procesor");
		model.addColumn("Ram");
		model.addColumn("Dysk Twardy");
		model.addColumn("Naped DVD");
		model.addColumn("Licencja");
		model.addColumn("Ilosc");
		
		return model;
	}
	
	private void createToolBar()
	{
		toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon addIcon = new ImageIcon("resources/add_32px.png");
		ImageIcon removeIcon = new ImageIcon("resources/close_32px.png");
		ImageIcon printIcon = new ImageIcon("resources/printer_32px.png");
		ImageIcon saveIcon = new ImageIcon("resources/discette_32px.png");
		
		ButtonListener buttonListener = new ButtonListener();
		
		JButton addButton = new JButton(addIcon);
		addButton.setActionCommand("add");
		addButton.addActionListener(buttonListener);
		
		JButton removeButton = new JButton(removeIcon);
		removeButton.setActionCommand("remove");
		removeButton.addActionListener(buttonListener);
		
		JButton saveButton = new JButton(saveIcon);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(buttonListener);
		
		toolBar.add(addButton);
		toolBar.add(removeButton);
		toolBar.addSeparator();
		toolBar.add(saveButton);
	}
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand())
			{
				case "add":
				{
					addDialog();
					break;
				}
				case "remove":
				{
					if(!removeSelected())
					{
						JOptionPane.showMessageDialog(null, "Niepowodzenie usuwania.");
						break;
					}
					break;
				}
				case "save":
				{
					if(!checkAmountValue())
					{
						JOptionPane.showMessageDialog(null, "Musisz wpisac dodatnia liczbe sztuk!", "Warning!", JOptionPane.WARNING_MESSAGE);
						break;
					}
					
					dispose();
					SupplySaveDialog dialog = new SupplySaveDialog(getAddDialogData(mainTable));
					break;
				}
				default:
					break;
			}
		}
	}
	
	private void addDialog()
	{
		JDialog dialog = new JDialog(this, "Znajdz", Dialog.ModalityType.DOCUMENT_MODAL);
		dialog.setLocationRelativeTo(null);
		dialog.setSize(700, 200);
		
		Container container = dialog.getContentPane();
		container.setLayout(new BorderLayout());
		
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
		model.addColumn("Procesor");
		model.addColumn("Ram");
		model.addColumn("Dysk Twardy");
		model.addColumn("Naped DVD");
		model.addColumn("Licencja");
		
		JTable table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mousePressed(MouseEvent mouseEvent)
					{
						Vector<String> computerVariable = new Vector<String>();
						
						JTable table = (JTable)mouseEvent.getSource();
						Point point = mouseEvent.getPoint();
						int row = table.rowAtPoint(point);
						
						if(mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1)
						{
							DefaultTableModel model = (DefaultTableModel) table.getModel();
							
							computerVariable.add((String)model.getValueAt(row, 0));
							computerVariable.add((String)model.getValueAt(row, 1));
							computerVariable.add((String)model.getValueAt(row, 2));
							computerVariable.add((String)model.getValueAt(row, 3));
							computerVariable.add((String)model.getValueAt(row, 4));
							computerVariable.add((String)model.getValueAt(row, 5));
							computerVariable.add((String)model.getValueAt(row, 6));
							computerVariable.add((String)model.getValueAt(row, 7));
							
							DefaultTableModel targetModel = (DefaultTableModel) mainTable.getModel();
							targetModel.addRow(computerVariable);
							
							dialog.dispose();
						}
					}
				});
		
		JScrollPane localScrollPane = new JScrollPane(table);
		
		container.add(localScrollPane, BorderLayout.CENTER);
		
		fillModel(table);
		
		dialog.setVisible(true);
		//return dialog;
	}
	
	/*
	 *Funkcja pobiera wszystkie komputery z bazy danych i wrzuca je do modelu pobranego z parametru 
	 *@JTable table - do jakiej tabeli mają zostać wrzucone dane
	 */
	
	private void fillModel(JTable table)
	{
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		Statement statement = null;
		ResultSet resultSet = null;
		
		int rowCount = 0;
		
		try
		{
			//create statement from connection
			statement = connection.createStatement();
			
			//get result from query
			resultSet = statement.executeQuery("SELECT * FROM computer");
			
			//set count of rows
			rowCount = getRowCount(resultSet);
			if(rowCount != 0)
			{
				model.setRowCount(rowCount);
			}
			
			rowCount = 0;
			while(resultSet.next())
			{				
				model.setValueAt((String)resultSet.getString("brand"), rowCount, 0);
				model.setValueAt((String)resultSet.getString("model"), rowCount, 1);
				model.setValueAt((String)resultSet.getString("type"), rowCount, 2);
				model.setValueAt((String)resultSet.getString("processor"), rowCount, 3);
				model.setValueAt((String)resultSet.getString("ram"), rowCount, 4);
				model.setValueAt((String)resultSet.getString("hdd"), rowCount, 5);
				model.setValueAt(String.valueOf(resultSet.getBoolean("dvd")), rowCount, 6);
				model.setValueAt(String.valueOf(resultSet.getBoolean("license")), rowCount, 7);
			
				rowCount++;
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
					statement.close();
				
				if(resultSet != null)
					resultSet.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/*
	 *Funkcja getRowCount zwraca ilosc wierszy z klasy ResultSet
	 *@ResultSet resultSet
	 */
	
	private int getRowCount(ResultSet resultSet)
	{
		int counter = 0;
		try
		{
			while(resultSet.next())
			{
				counter++;
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return counter;
	}
	
	/*funkcja pobierająca dane z modelu i zwracająca wektor danych w postaci tablicy hashmap
	 * @JTable table - tabela z której ma zostać pobrany model danych
	 */
	
	private Vector<HashMap<String, String>> getAddDialogData(JTable table)
	{
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rowCount = model.getRowCount();
		
		Vector<HashMap<String, String>> dataVector = new Vector<HashMap<String, String>>(rowCount);
		//HashMap<String, String>[] singleRow = new HashMap<String, String>()[rowCount];
		
		for(int i = 0; i < rowCount; i++)
		{			
			HashMap<String, String> row = new HashMap<String, String>();
			
			row.put("brand", (String)model.getValueAt(i, 0));
			row.put("model", (String)model.getValueAt(i, 1));
			row.put("type", (String)model.getValueAt(i, 2));
			row.put("processor", (String)model.getValueAt(i, 3));
			row.put("ram", (String)model.getValueAt(i, 4));
			row.put("hdd", (String)model.getValueAt(i, 5));
			row.put("dvd", (String)model.getValueAt(i, 6));
			row.put("license", (String)model.getValueAt(i, 7));
			row.put("amount", (String)model.getValueAt(i, 8));
			
			dataVector.add(i, row);
		}
		
		return dataVector;
	}
	
	private boolean checkAmountValue()
	{
		boolean statement = true;
		DefaultTableModel model = (DefaultTableModel) this.mainTable.getModel();
		int rowAmount = model.getRowCount();
		String tmp = "";
		
		for(int i = 0; i < rowAmount; i++)
		{
			tmp = (String)model.getValueAt(i, 8);
			
			if(tmp == null || tmp.length() == 0)
			{
				statement = false;
				return statement;
			}
			
			if(Integer.valueOf(tmp) == 0)
			{
				statement = false;
			}
		}
		
		return statement;
	}
	
	private boolean removeSelected()
	{
		boolean isSuccess = false;
		DefaultTableModel model = (DefaultTableModel) this.mainTable.getModel();
		int selectedItems[] = this.mainTable.getSelectedRows();
		
		if(selectedItems.length <= 0)
		{
			return isSuccess;
		}
		
		for(int rowNumber: selectedItems)
		{
			model.removeRow(rowNumber);
		}
		
		isSuccess = true;
		JOptionPane.showMessageDialog(null, "Usunieto " + selectedItems.length + " elementow.");
		
		return isSuccess;
	}
}















































