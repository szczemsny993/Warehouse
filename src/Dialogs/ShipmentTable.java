package Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import FileManager.Config;
import Account.Account;

import java.sql.*;

import java.util.Vector;
import java.util.regex.Pattern;

public class ShipmentTable extends JPanel implements ActionListener
{
	protected static final long serialVersionUID = 1L;
	
	protected Connection connection;
	protected JTable table;
	
	protected Vector<Integer> idTable;
	
	public ShipmentTable()
	{
		super(new BorderLayout());
		
		try
		{
			this.connection = Config.getConnection();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.table = createTable(createModel());
		JScrollPane scrollPane = new JScrollPane(this.table);
		add(scrollPane, BorderLayout.CENTER);
		
		JToolBar toolBar = createToolBar();
		add(toolBar, BorderLayout.EAST);
		
		loadShipments();
	}
	
	protected JTable createTable(DefaultTableModel model)
	{
		if(model == null)
		{
			return null;
		}
		
		JTable table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent mouseEvent)
				{
					JTable table = (JTable)mouseEvent.getComponent();
					Point point = mouseEvent.getPoint();
					int row = table.rowAtPoint(point);
					
					if(mouseEvent.getClickCount() == 2 && row != -1)
					{
						DefaultTableModel model = (DefaultTableModel)table.getModel();
						
						ShipmentShowDialog shipmentDialog = new ShipmentShowDialog((String)model.getValueAt(row, 0));
						shipmentDialog.setVisible(true);
					}
				}
			});
		
		return table;
	}
	
	protected DefaultTableModel createModel()
	{
		DefaultTableModel model = new DefaultTableModel()
				{
					protected static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int col)
					{
						return false;
					}
				};
		
		model.addColumn("Kod Wysylki");
		model.addColumn("Data");
		model.addColumn("Dlugosc Gwarancji");
		model.addColumn("Ilosc Palet");
		model.addColumn("Opis");
		
		return model;
	}
	
	protected JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		
		ImageIcon addIcon = new ImageIcon("resources/add_32px.png");
		ImageIcon removeIcon = new ImageIcon("resources/close_32px.png");
		ImageIcon findIcon = new ImageIcon("resources/find_32px.png");
		
		JButton addButton = new JButton(addIcon);
		addButton.setActionCommand("add");
		addButton.addActionListener(this);
		
		JButton removeButton = new JButton(removeIcon);
		removeButton.setActionCommand("remove");
		removeButton.addActionListener(this);
		
		JButton findButton = new JButton(findIcon);
		findButton.setActionCommand("find");
		findButton.addActionListener(this);
		
		toolBar.add(addButton);
		toolBar.add(removeButton);
		toolBar.addSeparator();
		toolBar.add(findButton);
		
		return toolBar;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "add":
			{
				if(Account.getPermisionLevel() <= 1)
				{
					JOptionPane.showMessageDialog(null, "Brak uprawnien do wykonania tej akcji.");
					break;
				}
				
				ShipmentAddDialog dialog = new ShipmentAddDialog("test");
				dialog.setVisible(true);
				
				loadShipments();
				break;
			}
			case "remove":
			{
				if(Account.getPermisionLevel() <= 1)
				{
					JOptionPane.showMessageDialog(null, "Brak uprawnien do wykonania tej akcji.");
					break;
				}
				
				int[] selectedItems = this.table.getSelectedRows();
				if(selectedItems.length == 0)
				{
					break;
				}
				
				int ret_val = JOptionPane.showConfirmDialog(null, "Czy na pewno chcesz usunac zaznaczone elementy?", "", JOptionPane.YES_NO_OPTION);
				if(ret_val == JOptionPane.YES_OPTION)
				{
					Vector<String> toDelete = getShipmentNames(selectedItems);
					int counter = 0;
					boolean isRemoved = false;
					
					for(int i = 0; i < toDelete.size(); i++)
					{
						ShipmentRemove remove = new ShipmentRemove(toDelete.get(i));
						if(!remove.removeShipment())
						{
							JOptionPane.showMessageDialog(null, "Wystapil problem z usuwaniem wysylki o nazwie" + toDelete.get(i) + " Zmiany zostana cofniete.");
							return;
						}
						else
						{
							isRemoved = true;
						}
						
						if(isRemoved)
						{
							counter++;
						}
					}
					
					JOptionPane.showMessageDialog(null, "Usunieto " + counter + " elementow.");
				}
				else
				{
					break;
				}
				
				loadShipments();
				break;
			}
			case "find":
			{
				break;
			}
			default:
			{
				System.out.println("Event handle error in shipment table");
				break;
			}
		}
	}
	
	protected void loadShipments()
	{
		Statement statement = null;
		ResultSet resultSet = null;
		
		this.idTable = new Vector<Integer>();
		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		int counter = 0;
		
		try
		{
			statement = this.connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM shipment");
			
			model.setRowCount(countResultSetRow(resultSet));
			
			while(resultSet.next())
			{
				this.idTable.add((Integer)resultSet.getInt("id"));
				
				model.setValueAt((String)resultSet.getString("shipment_id"), counter, 0);
				model.setValueAt(parseDateToString(resultSet.getDate("date")), counter, 1);
				model.setValueAt(String.valueOf(resultSet.getString("warranty_length")), counter, 2);				
				model.setValueAt(String.valueOf(resultSet.getInt("pallete_count")), counter, 3);
				model.setValueAt((String)resultSet.getString("description"), counter, 4);
				
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
	
	protected int countResultSetRow(ResultSet resultSet)
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
	
	protected String parseDateToString(Date date)
	{
		String result = "";
		String tmp = date.toString();
		
		String[] timePieces = tmp.split(Pattern.quote("-"));
		
		int day = Integer.valueOf(timePieces[2]) + 1;
		
		result = String.valueOf(day) + "." + timePieces[1] + "." + timePieces[0];
		
		return result;
	}
	
	protected Vector<String> getShipmentNames(int[] tab)
	{
		Vector<String> result = new Vector<String>(tab.length);
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		
		for(int element: tab)
		{
			result.add((String)model.getValueAt(element, 0));
		}
		
		return result;
	}
}



























