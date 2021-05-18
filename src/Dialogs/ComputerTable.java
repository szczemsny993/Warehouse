package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import FileManager.Config;

import java.sql.*;

import java.util.Vector;

public class ComputerTable extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	protected JScrollPane scrollPane;
	protected JTable table = null;
	protected DefaultTableModel model = null;
	
	protected JToolBar toolBar;
	
	protected Connection connection;
	protected Vector<Integer> idTable;
	
	
	public ComputerTable()
	{
		try
		{
			this.connection = Config.getConnection();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setLayout(new BorderLayout());
		
		createTable();
		
		add(scrollPane, BorderLayout.CENTER);
		createToolBar();
		
		add(toolBar, BorderLayout.EAST);
		
		loadDataFromDatabase();
	}
	
	private void createTable()
	{
		createModel();
		
		table = new JTable(model);
		table.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mousePressed(MouseEvent mouseEvent)
					{
						JTable table = (JTable) mouseEvent.getSource();
						Point point = (Point) mouseEvent.getPoint();
						int row = table.rowAtPoint(point);
						
						if(mouseEvent.getClickCount() == 2 && row > -1)
						{
							ComputerShowDialog dialog = new ComputerShowDialog(idTable.get(row));
							dialog.setVisible(true);
							
							loadDataFromDatabase();
						}
					}
				});
		
		this.scrollPane = new JScrollPane(table);
	}
	
	private void createModel()
	{
		model = new DefaultTableModel()
				{
					private static final long serialVersionUID = 1L;
					//unset editable cells in model
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
		model.addColumn("Pamieci");
		model.addColumn("Dysk");
		model.addColumn("Naped");
		model.addColumn("Licencja");
	}
	
	private void createToolBar()
	{
		//main init of jtoolbar
		toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		//create icons
		ImageIcon addIcon = new ImageIcon("resources/add_32px.png");
		ImageIcon removeIcon = new ImageIcon("resources/close_32px.png");
		ImageIcon printIcon = new ImageIcon("resources/printer_32px.png");
		ImageIcon findIcon = new ImageIcon("resources/find_32px.png");
		
		ButtonListener buttonListener = new ButtonListener();
		
		JButton addButton = new JButton(addIcon);
		addButton.setActionCommand("add");
		addButton.addActionListener(buttonListener);
		addButton.setToolTipText("Dodaj nowy komputer do spisu");
		
		JButton removeButton = new JButton(removeIcon);
		removeButton.setActionCommand("remove");
		removeButton.addActionListener(buttonListener);
		removeButton.setToolTipText("Usun zaznaczone elementy z bazy");
		
		JButton printButton = new JButton(printIcon);
		printButton.setActionCommand("print");
		printButton.addActionListener(buttonListener);
		
		JButton findButton = new JButton(findIcon);
		findButton.setActionCommand("find");
		findButton.addActionListener(buttonListener);
		
		//add buttons to containers
		toolBar.add(addButton);
		toolBar.add(removeButton);
		toolBar.addSeparator();
		toolBar.add(printButton);
		toolBar.add(findButton);
	}
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand())
			{
				case "add":
					{
						ComputerAddDialog dialog = new ComputerAddDialog();
						dialog.setVisible(true);
						loadDataFromDatabase();
						break;
					}
				case "remove":
					{
						ComputerRemove remove = new ComputerRemove(table);
						break;
					}
				case "print":
					System.out.println("Print");
					break;
				case "find":
					System.out.println("Find");
					break;
				default:
					System.out.println("Blad listenera, skontaktuj sie z tworca oprogramowania");
			}
				
		}
	}	
	
	
	//function which load data from database to tablemodel
	private void loadDataFromDatabase()
	{		
		int counter = 0;
		int rowCount = 0;
		
		this.idTable = new Vector<Integer>();
		
		Statement statement = null;
		ResultSet resultSet = null;
		
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		
		try
		{
			//get statement
			statement = connection.createStatement();
			//get data from database
			resultSet = statement.executeQuery("SELECT * FROM Computer");
			
			rowCount = getRowCount(resultSet);
			if(rowCount <= 0)
			{
				return;
			}
			
			model.setRowCount(rowCount);
			
			while(resultSet.next())
			{
				idTable.add(resultSet.getInt("id"));
				
				model.setValueAt(resultSet.getString("brand"), counter, 0);
				model.setValueAt(resultSet.getString("model"), counter, 1);
				model.setValueAt(resultSet.getString("type"), counter, 2);
				model.setValueAt(resultSet.getString("processor"), counter, 3);
				model.setValueAt(resultSet.getString("ram"), counter, 4);
				model.setValueAt(resultSet.getString("hdd"), counter, 5);
				model.setValueAt(resultSet.getBoolean("dvd"), counter, 6);
				model.setValueAt(resultSet.getBoolean("license"), counter, 7);
				
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
	
	private int getRowCount(ResultSet resultSet)
	{
		int rowCounter = 0;
		
		try
		{
			if(resultSet == null)
			{
				throw new NullPointerException("You transfer null pointer to getRowCount!");
			}
			
			while(resultSet.next())
			{
				rowCounter++;
			}
			
			resultSet.absolute(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return rowCounter;
	}
}






















