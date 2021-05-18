package Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import FileManager.Config;
import Data.Client;

import java.sql.*;

import java.util.Vector;

public class PasswordsFindDialog extends JDialog implements ActionListener
{
	private int screenWidth = 600;
	private int screenHeight = 200;
	
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	private JTextField inputTextField;
	private JComboBox<String> comboBox;
	private JTable table;
	
	private Vector<Client> clientVector;
	private Vector<Integer> modelIdTable;
	
	public PasswordsFindDialog()
	{
		super(null, "Dodaj Klienta", JDialog.ModalityType.DOCUMENT_MODAL);
		setSize(this.screenWidth, this.screenHeight);
		setCenterPos();
		
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
		
		container.add(createTopPanel(), BorderLayout.NORTH);
		
		this.table = createTable(createModel());
		container.add(new JScrollPane(this.table), BorderLayout.CENTER);
	}

	private void setCenterPos()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();
		
		int x = (dimension.width - this.screenWidth) / 2;
		int y = (dimension.height - this.screenHeight) / 2;
		
		setLocation(x, y);
	}
	
	private JTable createTable(DefaultTableModel model)
	{
		JTable table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mousePressed(MouseEvent mouseEvent)
					{
						
					}
				});
		
		return table;
	}
	
	private DefaultTableModel createModel()
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
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "find":
			{
				String value = this.inputTextField.getText();
				String criterion = (String)this.comboBox.getSelectedItem();
				
				search(value, translateValue(criterion));
			}
		}
	}
	
	public void showDialog()
	{
		setVisible(true);
	}
	
	private JPanel createTopPanel()
	{
		JPanel panel = new JPanel(new FlowLayout());
		
		JTextField inputDisplatField = new JTextField("Wyszukaj:");
		inputDisplatField.setEditable(false);
		inputDisplatField.setHorizontalAlignment(JTextField.LEFT);
		
		this.inputTextField = new JTextField(20);
		
		panel.add(inputDisplatField);
		panel.add(this.inputTextField);
		
		JTextField parDisplayField = new JTextField("Parametr:");
		parDisplayField.setEditable(false);
		parDisplayField.setHorizontalAlignment(JTextField.LEFT);

		this.comboBox = createComboBox();
		
		panel.add(parDisplayField);
		panel.add(this.comboBox);
		
		JButton button = new JButton("Szukaj");
		button.setActionCommand("find");
		button.addActionListener(this);
		
		panel.add(button);
		
		return panel;
	}
	
	
	private JComboBox<String> createComboBox()
	{
		String[] values = {"Typ", "Data", "Kod", "Nazwa", "Port", "Uzytkownik", "Haslo", "Opis", "Lokalizacja", "Serial", "Mac Adress", "Wewnetrzne ip", "Licencja", "Pozycja"};
		JComboBox<String> combo = new JComboBox<String>(values);
		
		
		return combo;
	}
	
	private String translateValue(String value)
	{
		String result = "";
		switch(value)
		{
			case "Typ":
			{
				result = "type";
				break;
			}
			case "Data":
			{
				result = "date";
				break;
			}
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
			case "Port":
			{
				result = "port";
				break;
			}
			case "Uzytkownik":
			{
				result = "user";
				break;
			}
			case "Haslo":
			{
				result = "password";
				break;
			}
			case "Opis":
			{
				result = "description";
				break;
			}
			case "Lokalizacja":
			{
				result = "location";
				break;
			}
			case "Serial":
			{
				result = "serial";
				break;
			}
			case "Mac Adress":
			{
				result = "mac";
				break;
			}
			case "Wewnetrzne ip":
			{
				result = "internal_id";
				break;
			}
			case "Licencja":
			{
				result = "license";
				break;
			}
			case "Pozycja":
			{
				result = "position";
				break;
			}
		}
		
		return result;
	}
	
	private boolean search(String searchValue, String criterion)
	{
		boolean isSuccess = false;
		Statement statement = null;
		ResultSet resultSet = null;
		Vector<Integer> idTable = new Vector<Integer>();
		
		try
		{
			String query = createQuery(searchValue, criterion);
			if(query == null)
			{
				return isSuccess;
			}
			
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			
			if(countResultSet(resultSet) <= 0)
			{
				return isSuccess;
			}
			
			while(resultSet.next())
			{
				idTable.add((Integer)resultSet.getInt("client_id"));
			}
			
			//fillInModel(idTable);
			this.clientVector = getClientDataBy(idTable);
			fillInModel(this.clientVector);
			
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
		
		return isSuccess;
	}
	
	private String createQuery(String searchValue, String criterion)
	{
		String query = "";
		
		if(criterion.contains("port"))
		{
			if(!isNumber(searchValue))
			{
				return null;
			}
			
			int port = Integer.valueOf(searchValue);
			query = String.format("SELECT client_id FROM passwords WHERE %s = '%s'", criterion, port);
			
		}
		else
		{
			query = String.format("SELECT client_id FROM passwords WHERE %s LIKE ", criterion);
			query += "'%";
			query += searchValue;
			query += "%'";
		}
		
		return query;
	}
	
	private void fillInModel(Vector<Client> table)
	{
		DefaultTableModel model = (DefaultTableModel)this.table.getModel();
		
		model.setRowCount(table.size());
		this.modelIdTable = new Vector<Integer>(table.size());
		
		for(int i = 0; i < table.size(); i++)
		{
			Client clientTmp = table.get(i);
			
			model.setValueAt(clientTmp.getCode(), i, 0);
			model.setValueAt(clientTmp.getName(), i, 1);
			model.setValueAt(clientTmp.getNip(), i, 2);
			model.setValueAt(clientTmp.getCity(), i, 3);
			model.setValueAt(clientTmp.getPostCode(), i, 4);
			model.setValueAt(clientTmp.getAddress(), i, 5);
			model.setValueAt(clientTmp.getTelephone(), i, 6);
			
			this.modelIdTable.add(clientTmp.getId());
		}
	}
	
	private Vector<Client> getClientDataBy(Vector<Integer> idTable)
	{
		Vector<Client> table = new Vector<Client>();
		Statement statement = null;
		ResultSet resultSet = null;
		
		try
		{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(String.format("SELECT * FROM client WHERE id IN %s", vectorToBrackets(idTable)));
			
			while(resultSet.next())
			{
				Client client = new Client();
				
				client.setCode(resultSet.getString("code"));
				client.setName(resultSet.getString("name"));
				client.setId(resultSet.getInt("id"));
				client.setNip(resultSet.getString("nip"));
				client.setCity(resultSet.getString("city"));
				client.setPostCode(resultSet.getString("post_code"));
				client.setAddress(resultSet.getString("address"));
				client.setTelephone(resultSet.getString("telephone"));
				
				table.add(client);
			}
			
			//System.out.println("Rozmiar tabeli z clientami: " + table.size());
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
		
		return table;
	}
	
	private String vectorToBrackets(Vector<Integer> idTable)
	{
		String brackets = idTable.toString();
		
		brackets = brackets.replace("[", "(");
		brackets = brackets.replace("]", ")");
		
		return brackets;
	}
	
	private int countResultSet(ResultSet resultSet)
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
	
	private boolean isNumber(String value)
	{
		boolean isNumber = true;
		
		char array[]  = value.toCharArray();
		for(char zm: array)
		{
			if(!Character.isDigit(zm))
			{
				isNumber = false;
				return isNumber;
			}
		}
		
		return isNumber;
	}
}













































