package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import FileManager.Config;

import java.sql.*;

import java.util.Vector;
import java.util.HashMap;

public class ShipmentAddDialog extends JDialog implements ActionListener, KeyListener
{
	protected static final long serialVersionUID = 1L;
	
	protected Connection connection;
	protected String shipmentCode;
	
	protected JTable table;
	
	public ShipmentAddDialog(String shipmentCode)
	{
		super(null, "Dodaj dostawe..", JDialog.ModalityType.DOCUMENT_MODAL);
		setSize(800, 200);
		setCenterLocation();
		
		try
		{
			this.connection = Config.getConnection();
			this.shipmentCode = shipmentCode;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		this.table = createTable(createModel());
		JScrollPane scrollPane = new JScrollPane(this.table);
		container.add(scrollPane, BorderLayout.CENTER);
		
		JToolBar toolBar = createToolBar();
		container.add(toolBar, BorderLayout.EAST);
	}
	
	private JTable createTable(DefaultTableModel model)
	{
		if(model == null)
		{
			return null;
		}
		
		JTable table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(true);
		
		table.addKeyListener(this);
		
		return table;
	}
	
	private DefaultTableModel createModel()
	{
		DefaultTableModel model = new DefaultTableModel();
		
		model.addColumn("S/N Komputera");
		model.addColumn("Plyta Glowna");
		model.addColumn("Dysk Twardy");
		model.addColumn("Zasilacz");
		model.addColumn("Naped DVD");
		
		model.setRowCount(1);
		
		return model;
	}
	
	private JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon saveIcon = new ImageIcon("resources/discette_32px.png");
		
		JButton saveButton = new JButton(saveIcon);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);
		
		
		toolBar.add(saveButton);
		
		return toolBar;
	}	
	
	private void setCenterLocation()
	{
		Toolkit toolKit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolKit.getScreenSize();
		
		int x = (screenSize.width - getWidth()) / 2;
		int y = (screenSize.height - getHeight()) / 2;
		
		setLocation(x, y);
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "save":
			{
				dispose();
				
				ShipmentSaveDialog dialog = new ShipmentSaveDialog(getDataFromModel());
				dialog.setVisible(true);

				break;
			}
		}
	}
	
	public void keyPressed(KeyEvent keyEvent)
	{
		if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
		{
			int rowCount = this.table.getSelectedRow();
			int columnSelected = this.table.getSelectedColumn();
			
			//System.out.println(rowCount);
			//System.out.println(columnSelected);
			
			/*
			
			if(rowCount == 0)
			{
				rowCount += 1;
			}
			*/
			
			switch(columnSelected)
			{
				case 0:
				{
					this.table.changeSelection(rowCount - 1, 1, false, false);
					break;
				}
				case 1:
				{
					this.table.changeSelection(rowCount - 1, 2, false, false);
					break;
				}
				case 2:
				{
					this.table.changeSelection(rowCount - 1, 3, false, false);
					break;
				}
				case 3:
				{
					this.table.changeSelection(rowCount - 1, 4, false, false);
					break;
				}
				case 4:
				{
					DefaultTableModel model = (DefaultTableModel)this.table.getModel();
					model.addRow(new Object[] {"", "", "", "", ""});
					this.table.changeSelection(rowCount, 0, false, false);
					break;
				}
				default:
					break;
			}
		}
	}
	
	public void keyReleased(KeyEvent keyEvent)
	{
		
	}
	
	public void keyTyped(KeyEvent keyEvent)
	{
		
	}
	
	private boolean checkRow(int rowNumber)
	{
		boolean statement = true;
		String cellValue = "";
		
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		int columnCount = this.table.getColumnCount();
		
		int cellEmpty = 0;
		
		for(int i = 0; i < columnCount; i++)
		{
			cellValue = (String)model.getValueAt(rowNumber, i);
			if(cellValue.length() == 0)
			{
				cellEmpty++;
			}
		}
		
		if(cellEmpty == 5)
		{
			statement = false;
		}
		
		return statement;
	}
	
	private Vector<HashMap<String, String>> getDataFromModel()
	{		
		Vector<HashMap<String, String>> data = new Vector<HashMap<String, String>>();
		
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		int rowCount = this.table.getRowCount();
		
		for(int i = 0; i < rowCount; i++)
		{
			if(checkRow(i) == false)
			{
				continue;
			}
			
			HashMap<String, String> row = new HashMap<String, String>();
			
			row.put("Obudowa", (String)model.getValueAt(i, 0));
			row.put("Plyta glowna", (String)model.getValueAt(i, 1));
			row.put("Dysk", (String)model.getValueAt(i, 2));
			row.put("Zasilacz", (String)model.getValueAt(i, 3));
			row.put("Naped", (String)model.getValueAt(i, 4));
			
			data.add(row);
		}
		
		//printGetDataFromTableTest(data);
		
		return data;
	}
	
	/*
	private void printGetDataFromTableTest(Vector<HashMap<String, String>> data)
	{
		int rowCount = data.size();
		
		for(int i = 0; i < rowCount; i++)
		{
			HashMap<String, String> tmp = data.get(i);
			
			System.out.println("Wiersz numer: " + (i + 1));
			
			System.out.println("Obudowa: " +  (String)tmp.get("Obudowa"));
			System.out.println("Plyta glowna: " + (String)tmp.get("Plyta glowna"));
			System.out.println("Dysk: " + (String)tmp.get("Dysk"));
			System.out.println("Zasilacz: " + (String)tmp.get("Zasilacz"));
			System.out.println("Naped" + (String)tmp.get("Naped"));
		}
	}
	*/
}


































