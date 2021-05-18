package Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import FileManager.Config;

import java.util.Vector;

import java.sql.*;

public class ClientFindDialog extends JDialog implements ActionListener, MouseListener
{
	protected static final long serialVersionUID = 1L;
	
	protected Connection connection;
	
	protected JTable table;
	
	protected JTextField inputField;
	protected JComboBox<String> comboBox;
	
	protected Vector<Integer> idTable;
	
	public ClientFindDialog()
	{
		super(null, "Szukaj...", JDialog.ModalityType.DOCUMENT_MODAL);
		setSize(900, 400);
		setLocationRelativeTo(null);
		
		try
		{
			this.connection = Config.getConnection();
			this.table = createTable(createModel());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		JPanel topPanel = createSearchBar();
		container.add(topPanel, BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane(this.table);
		container.add(scrollPane, BorderLayout.CENTER);
	}
	
	protected JTable createTable(DefaultTableModel model)
	{
		JTable table = new JTable(model);
		table.addMouseListener(this);
		
		return table;
	}
	
	protected DefaultTableModel createModel()
	{
		DefaultTableModel model = new DefaultTableModel()
				{
					private static final long serialVersionUID = 1L;
					
					@Override
					public boolean isCellEditable(int col, int row)
					{
						return false;
					}
				};
		
		model.addColumn("Kod");
		model.addColumn("Nazwa");
		model.addColumn("NIP");
		model.addColumn("Miasto");
		model.addColumn("Kod pocztowy");
		model.addColumn("Ulica");
		model.addColumn("Telefon");
		
		return model;
	}
	
	public void showDialog()
	{
		
	}
	
	protected JPanel createSearchBar()
	{
		JPanel panel = new JPanel(new FlowLayout());
		
		JPanel leftPanel, rightPanel;
		
		leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		this.inputField = new JTextField(48);
		leftPanel.add(this.inputField);
		
		rightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		this.comboBox = createComboBox();
		rightPanel.add(this.comboBox);
		
		JButton findButton = new JButton("Szukaj");
		findButton.setActionCommand("find");
		findButton.addActionListener(this);
		
		rightPanel.add(findButton);
		
		panel.add(leftPanel);
		panel.add(rightPanel);
		
		return panel;
	}
	
	protected JComboBox<String> createComboBox()
	{
		String[] values = {"Kod", "Nazwa", "NIP", "Miasto", "Kod pocztowy", "Ulica", "Telefon"};
		JComboBox<String> comboBox = new JComboBox<>(values);
		
		comboBox.setSelectedIndex(0);
		
		return comboBox;
	}
	
	protected String getSelected()
	{
		String comboSelected = "";
		
		comboSelected = (String)this.comboBox.getSelectedItem();
		return comboSelected;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "find":
			{
				String column = translateColumn(getSelected());
				if(!find(column))
				{
					JOptionPane.showMessageDialog(null, "Brak wynikow wyszukiwania.");
					break;
				}
				
				break;
			}
		}
	}
	
	protected boolean find(String columnName)
	{
		boolean isSuccess = false;
		ResultSet resultSet = null;
		Statement statement = null;
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		int counter = 0;
		
		String query = createLikeQuery(columnName, this.inputField.getText());
		
		try
		{
			statement = this.connection.createStatement();
			resultSet = statement.executeQuery(query);
			
			counter = countRecord(resultSet);
			if(counter <= 0)
			{
				isSuccess = false;
				return isSuccess;
			}
			
			model.setRowCount(counter);
			this.idTable = new Vector<Integer>(counter);
			
			for(int i = 0; i < counter; i++)
			{
				resultSet.next();
				
				idTable.add(resultSet.getInt("id"));
				
				model.setValueAt(resultSet.getString("code"), i, 0);
				model.setValueAt(resultSet.getString("name"), i, 1);
				model.setValueAt(resultSet.getString("nip"), i, 2);
				model.setValueAt(resultSet.getString("city"), i, 3);
				model.setValueAt(resultSet.getString("post_code"), i, 4);
				model.setValueAt(resultSet.getString("address"), i, 5);
				model.setValueAt(resultSet.getString("telephone"), i, 6);
			}
			
			isSuccess = true;
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
		
		return isSuccess;
	}
	
	protected int countRecord(ResultSet resultSet)
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
	
	protected String createLikeQuery(String columnName, String inputText)
	{
		String result = "";
		
		result += String.format("SELECT * FROM client WHERE %s LIKE '", columnName);
		result += "%";
		result += inputText;
		result += "%'";
		
		return result;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		JTable table = (JTable)e.getComponent();
		Point point = e.getPoint();
		int row = table.rowAtPoint(point);
		
		if(e.getClickCount() == 2 && row != -1)
		{
			ClientShowDialog dialog = new ClientShowDialog(this.idTable.get(row));
			dialog.setVisible(true);
		}
	}
	
	public void mouseEntered(MouseEvent e)
	{
		
	}
	
	public void mouseExited(MouseEvent e)
	{
		
	}
	
	public void mousePressed(MouseEvent e)
	{
		
	}
	
	public void mouseReleased(MouseEvent e)
	{
		
	}
	
	protected String translateColumn(String var)
	{
		String result = "";
		
		switch(var)
		{
			case "Kod":
			{
				result = "code";
				break;
			}
			case "Nazwa":
			{
				result = "name";
				break;
			}
			case "NIP":
			{
				result = "nip";
				break;
			}
			case "Miasto":
			{
				result = "city";
				break;
			}
			case "Kod pocztowy":
			{
				result = "post_code";
				break;
			}
			case "Ulica":
			{
				result = "address";
				break;
			}
			case "Telefon":
			{
				result = "telephone";
				break;
			}
			default:
			{
				break;
			}
		}
		
		return result;
	}
}



































