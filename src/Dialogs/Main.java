package Dialogs;

import Account.Account;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import FileManager.Config;

import java.sql.*;

public class Main extends JFrame
{
	protected static final long serialVersionUID = 1L;
	
	protected static JFrame mainFrame;
	protected static JPanel mainPanel;
	protected static JMenuBar menuBar;

	protected static Connection connection;
	
	protected static Config config;
	
	public Main()
	{
		
	}
	
	protected static void prepareGUI()
	{
		mainFrame = new JFrame("MojSerwis");
		mainFrame.setSize(800, 500);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent windowEvent)
			{
				try
				{
					if(connection != null)
					{
						connection.close();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					System.exit(0);
				}
			}
		});
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		mainFrame.add(mainPanel);
		mainFrame.setVisible(true);
	}
	
	protected static void prepareMenuBar()
	{
		//create main MenuBar
		menuBar = new JMenuBar();
		
		//create submenus
		JMenu fileMenu = new JMenu("Plik");
		JMenu warehouseMenu = new JMenu("Magazyn");
		JMenu administrateMenu = new JMenu("Administruj");
		JMenu editMenu = new JMenu("Edytuj");
		
		//menu item listener
		MenuBarListener menuItemListener = new MenuBarListener();
		
		//create menu items for 'file' submenu
		JMenuItem exit_exitItem = new JMenuItem("Wyjscie");
		exit_exitItem.setActionCommand("exit");
		exit_exitItem.addActionListener(menuItemListener);
		
		//create menu items for 'warehouse' submenu
		JMenuItem warehouse_computerItem = new JMenuItem("Komputery");
		warehouse_computerItem.setActionCommand("computer");
		warehouse_computerItem.addActionListener(menuItemListener);
		
		JMenuItem warehouse_supplyItem = new JMenuItem("Dostawy");
		warehouse_supplyItem.setActionCommand("supply");
		warehouse_supplyItem.addActionListener(menuItemListener);
		
		JMenuItem warehouse_consignmentItem = new JMenuItem("Wysylki");
		warehouse_consignmentItem.setActionCommand("shipment");
		warehouse_consignmentItem.addActionListener(menuItemListener);
		
		JMenuItem warehouse_warehouseItem = new JMenuItem("Magazyn");
		warehouse_warehouseItem.setActionCommand("warehouse");
		warehouse_warehouseItem.addActionListener(menuItemListener);
		
		
		//create menu items for 'administrate' submenu
		JMenuItem administrate_customerItem = new JMenuItem("Klienci");
		administrate_customerItem.setActionCommand("customer");
		administrate_customerItem.addActionListener(menuItemListener);
		
		JMenuItem administrate_passwordsItem = new JMenuItem("Dane Dostepowe");
		administrate_passwordsItem.setActionCommand("password");
		administrate_passwordsItem.addActionListener(menuItemListener);
		
		JMenuItem administrate_subscription = new JMenuItem("Subskrybcja");
		administrate_subscription.setActionCommand("subscribe");
		administrate_subscription.addActionListener(menuItemListener);
		
		//create menu items for 'edit' submenu
		JMenuItem edit_userItem = new JMenuItem("Użytkownicy");
		edit_userItem.setActionCommand("users");
		edit_userItem.addActionListener(menuItemListener);
		
		JMenuItem edit_backupItem = new JMenuItem("Exportuj dane..");
		edit_backupItem.setActionCommand("backup");
		edit_backupItem.addActionListener(menuItemListener);
		
		JMenuItem edit_restoreItem = new JMenuItem("Importuj dane..");
		edit_restoreItem.setActionCommand("restore");
		edit_restoreItem.addActionListener(menuItemListener);
		
		//pack into submenu file
		fileMenu.add(exit_exitItem);
		
		//pack items to submenu
		warehouseMenu.add(warehouse_computerItem);
		warehouseMenu.add(warehouse_supplyItem);
		warehouseMenu.add(warehouse_consignmentItem);
		warehouseMenu.add(warehouse_warehouseItem);
		
		administrateMenu.add(administrate_customerItem);
		administrateMenu.add(administrate_passwordsItem);
		administrateMenu.add(administrate_subscription);
		
		editMenu.add(edit_userItem);
		editMenu.add(edit_backupItem);
		editMenu.add(edit_restoreItem);
	
		//add submenus to main menu
		menuBar.add(fileMenu);
		menuBar.add(warehouseMenu);
		menuBar.add(administrateMenu);
		menuBar.add(editMenu);
		
		mainFrame.setJMenuBar(menuBar);
		mainFrame.setVisible(true);
	}
	
	protected static class MenuBarListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand())
			{
				case "exit":
					System.exit(0);
					break;
				case "computer":
					{
						if(!Account.getComputerAccessValue())
						{
							JOptionPane.showMessageDialog(null, "Brak uprawnien dla tej sekcji programu.");
							break;
						}
						
						mainPanel.removeAll();
						ComputerTable computerTable = new ComputerTable();
						mainPanel.add(computerTable);
						
						mainFrame.setVisible(true);
						break;
					}
				case "warehouse":
					{
						if(!Account.getWarehouseAccessValue())
						{
							JOptionPane.showMessageDialog(null, "Brak uprawnien dla tej sekcji programu.");
							break;
						}
						
						mainPanel.removeAll();
						WarehouseTable warehouseTable = new WarehouseTable();
						mainPanel.add(warehouseTable);
						
						mainFrame.setVisible(true);
						break;
					}
				case "shipment":
				{
					if(!Account.getShipmentAccessValue())
					{
						JOptionPane.showMessageDialog(null, "Brak uprawnien dla tej sekcji programu.");
						break;
					}
						
					mainPanel.removeAll();
					ShipmentTable shipmentTable = new ShipmentTable();
					mainPanel.add(shipmentTable);
					
					mainFrame.setVisible(true);
				}
					break;
				case "supply":
					{
						if(!Account.getSupplyAccessValue())
						{
							JOptionPane.showMessageDialog(null, "Brak uprawnien dla tej sekcji programu.");
							break;
						}
						
						mainPanel.removeAll();
						SupplyTable supplyTable = new SupplyTable();
						mainPanel.add(supplyTable);
						
						mainFrame.setVisible(true);
						break;
					}
				case "customer":
					{
						if(!Account.getClientAccessValue())
						{
							JOptionPane.showMessageDialog(null, "Brak uprawnien dla tej sekcji programu.");
							break;
						}
							
						mainPanel.removeAll();
						ClientTable clientTable = new ClientTable();
						mainPanel.add(clientTable);
						
						mainFrame.setVisible(true);
						break;
					}
				case "password":
					{
						if(!Account.getPasswordAccessValue())
						{
							JOptionPane.showMessageDialog(null, "Brak uprawnien dla tej sekcji programu.");
							break;
						}
							
						mainPanel.removeAll();
						PasswordsTable passwordsTable = new PasswordsTable();
						passwordsTable.create();
						mainPanel.add(passwordsTable);
						
						mainFrame.setVisible(true);
						break;
					}
				case "users":
				{					
					if(Account.getPermisionLevel() != 3)
					{
						JOptionPane.showMessageDialog(null, "Brak uprawnien dla tej sekcji programu.");
						break;
					}
					
					UserDialog dialog = new UserDialog();
					dialog.setVisible(true);
					break;
				}
				case "backup":
				{
					if(!Account.getBackupAccessValue())
					{
						JOptionPane.showMessageDialog(null, "Brak uprawnien dla tej sekcji programu.");
						break;
					}
					
					BackupExportDialog dialog = new BackupExportDialog();
					dialog.showDialog();
					break;
				}
				case "restore":
				{
					if(!Account.getBackupAccessValue())
					{
						JOptionPane.showMessageDialog(null, "Brak uprawnien dla tej sekcji programu.");
						break;
					}
					
					BackupImportDialog dialog = new BackupImportDialog();
					dialog.showDialog();
					break;
				}
				default:
					System.out.println("Event handle error.");
					break;
			}
		}
	}
	
	protected static void loadConfig()
	{
		try
		{
			config = new Config();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected static void createDataBaseConnection(String connectionUrl)
	{	
		try
		{
			//register driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			//open a connection
			connection = DriverManager.getConnection(connectionUrl, config.getDatabaseUser(), config.getDatabaseUserPassword());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(mainFrame, "Mysql serwer not sersponding, please contant with system administrator.", "Error.", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	
	protected static String createConnectionURL()
	{
		String url = "";
		try
		{
			url += "jdbc:mysql://" + config.getDatabaseHost();
			url += "/" + config.getDatabaseName();
			url+= "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return url;
	}
	
	
	public static void main(String[] args)
	{
		//create config object
		loadConfig();
		
		//String debuggg = createConnectionURL();
		String connectURL = createConnectionURL();
		
		//create connection to database
		createDataBaseConnection(connectURL);
		Config.setConnection(connection);
		
		LoginDialog loginDialog = new LoginDialog();
		int result = loginDialog.showDialog();
		
		//init gui and menubar
		prepareGUI();
		prepareMenuBar();
	}
}































