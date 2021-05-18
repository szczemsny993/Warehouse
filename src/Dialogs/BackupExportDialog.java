package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import FileManager.Config;
import ImportExport.ExportPassword;
import ImportExport.ExportWarehouse;

import java.sql.*;

import java.io.File;

public class BackupExportDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	protected int screenWidth = 320;
	protected int screenHeigth = 290;
	
	protected JCheckBox usersCheckBox;
	protected JCheckBox warehouseCheckBox;
	protected JCheckBox passwordsCheckBox;
	
	protected JLabel displayLabel;
	protected JProgressBar progressBar;
	
	protected Connection connection;
	
	public BackupExportDialog()
	{
		super(null, "Exportuj dane", JDialog.ModalityType.DOCUMENT_MODAL);
		try
		{
			this.connection = Config.getConnection();
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		setSize(this.screenWidth, this.screenHeigth);
		setCenterPos();
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		container.add(createMainPanel(), BorderLayout.CENTER);
		container.add(createDownPanel(), BorderLayout.SOUTH);
	}
	
	public void showDialog()
	{
		setVisible(true);
	}
	
	protected void setCenterPos()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		int x = (screenSize.width - this.screenWidth) / 2;
		int y = (screenSize.height - this.screenHeigth) / 2;
		
		setLocation(x, y);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "export":
			{
				chooseFile();
				break;
			}
			case "save":
			{
				boolean users = false;
				boolean warehouse = false;
				boolean passwords = false;
				
				users = usersCheckBox.isSelected();
				warehouse = warehouseCheckBox.isSelected();
				passwords = passwordsCheckBox.isSelected();
				
				ExportWarehouse export = null;
				ExportPassword exportPassword = null;
				
				boolean isWarehouseBackup = false;
				boolean isPasswordsBackup = false;
				
				File file = new File(this.displayLabel.getText());
				file = createBackupFolder(file);
				
				if(users == false && warehouse == false && passwords == false)
				{
					JOptionPane.showMessageDialog(null, "Nie zaznaczono zadnego modulu do backupu.");
					break;
				}
				
				if(!file.exists())
				{
					JOptionPane.showMessageDialog(null, "Wystapil problem podczas tworzenia folderu na potrzeby backupu. Skontaktuj sie z tworca oprogramowania.");
					break;
				}
				
				if(warehouse)
				{
					export = new ExportWarehouse(file, this.progressBar);
					isWarehouseBackup = export.startBackup();
				}
				
				if(passwords)
				{
					exportPassword = new ExportPassword(file, this.progressBar);
					isPasswordsBackup = exportPassword.startBackup();
				}
				
				if(isWarehouseBackup && isPasswordsBackup)
				{
					JOptionPane.showMessageDialog(null, "Wykonano backup w lokalizacji: " + file.getPath());
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Niepowodzenie operacji");
				}
				
				break;
			}
			default:
				break;
		}
	}
	
	protected void chooseFile()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int option = chooser.showOpenDialog(this);
		
		if(option == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			this.displayLabel.setText(file.getAbsolutePath());
		}
		else
		{
			this.displayLabel.setText("");
		}
	}
	
	protected JPanel createMainPanel()
	{
		JPanel panel = new JPanel(new GridLayout(4, 2));
		
		JTextField userDisplayField = createDisplayField("Uzytkownicy:");
		this.usersCheckBox = new JCheckBox();
		this.usersCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		
		panel.add(userDisplayField);
		panel.add(this.usersCheckBox);
		
		JTextField warehouseDisplayField = createDisplayField("Magazyn:");
		this.warehouseCheckBox = new JCheckBox();
		this.warehouseCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		
		panel.add(warehouseDisplayField);
		panel.add(this.warehouseCheckBox);
		
		JTextField passwordsDisplayField = createDisplayField("Dane uzytkownikow:");
		this.passwordsCheckBox = new JCheckBox();
		this.passwordsCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		
		panel.add(passwordsDisplayField);
		panel.add(this.passwordsCheckBox);
		
		JTextField exportDisplayField = createDisplayField("Wybierz miejsce:");
		
		JPanel flowPanel = new JPanel(new FlowLayout());
		JButton exportButton = new JButton("Wskaż");
		exportButton.setActionCommand("export");
		exportButton.addActionListener(this);
		
		flowPanel.add(exportButton);
		
		panel.add(exportDisplayField);
		panel.add(flowPanel);
		
		return panel;
	}
	
	protected JTextField createDisplayField(String text) 
	{
		JTextField displayField = new JTextField(text);
		displayField.setEditable(false);
		displayField.setHorizontalAlignment(JTextField.RIGHT);
		
		return displayField;
	}
	
	protected JPanel createDownPanel()
	{
		JPanel panel = new JPanel(new GridLayout(3, 1));
		
		this.displayLabel = new JLabel("");
		this.displayLabel.setHorizontalAlignment(JLabel.CENTER);
		this.displayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		panel.add(this.displayLabel);
		
		this.progressBar = new JProgressBar();
		panel.add(this.progressBar);
		
		JPanel flowPanel = new JPanel(new FlowLayout());
		JButton button = new JButton("Wykonaj");
		button.setActionCommand("save");
		button.addActionListener(this);
		
		flowPanel.add(button);
		panel.add(flowPanel);
		
		return panel;
	}
	
	protected File createBackupFolder(File file)
	{
		File directory = null;
		
		try
		{
			String tmp = file.getPath();
			tmp += "\\mojserwis backup";
			
			directory = new File(tmp);
			if(directory.exists())
			{
				int ret_val = JOptionPane.showConfirmDialog(null, "Folder 'mojserwis backup' istnieje, czy chcesz kontynuowac?", "", JOptionPane.YES_NO_OPTION);
				if(ret_val == JOptionPane.YES_OPTION)
				{
					return directory;
				}
				else
				{
					return null;
				}
			}
			else
			{
				directory.mkdir();
			}
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		return directory;
	}
}















































