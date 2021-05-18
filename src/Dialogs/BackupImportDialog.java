package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import FileManager.Config;
import ImportExport.ImportPasswords;
import ImportExport.ImportWarehouse;

import java.sql.*;

import java.io.File;

public class BackupImportDialog extends JDialog implements ActionListener
{
	protected static final long serialVersionUID = 1L;
	
	protected int screenWidth = 250;
	protected int screenHeight = 280;
	
	protected Connection connection;
	
	protected JCheckBox usersCheckBox;
	protected JCheckBox warehouseCheckBox;
	protected JCheckBox passwordsCheckBox;
	
	protected JLabel displayLabel;
	protected JProgressBar progressBar;
	
	public BackupImportDialog()
	{
		super(null, "Importuj dane", JDialog.ModalityType.DOCUMENT_MODAL);
		try
		{
			this.connection = Config.getConnection();
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		setSize(this.screenWidth, this.screenHeight);
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		container.add(createMainPanel(), BorderLayout.CENTER);
		container.add(createDownPanel(), BorderLayout.SOUTH);
	}
	
	public void showDialog()
	{
		setCenterPos();
		setVisible(true);
	}
	
	protected void setCenterPos()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int x = (screenSize.width - this.screenWidth) / 2;
		int y = (screenSize.height - this.screenHeight) / 2;
		
		setLocation(x, y);
	}
	
	protected JPanel createMainPanel()
	{
		JPanel panel = new JPanel(new GridLayout(4, 2));
		
		JTextField userDisplayField = createDisplayField("Uzytkownicy:");
		this.usersCheckBox = new JCheckBox();
		this.usersCheckBox.setHorizontalAlignment(JCheckBox.CENTER);
		
		panel.add(userDisplayField);
		panel.add(this.usersCheckBox);
		
		JTextField warehouseDisplayField = createDisplayField("Magazyn:");
		this.warehouseCheckBox = new JCheckBox();
		this.warehouseCheckBox.setHorizontalAlignment(JCheckBox.CENTER);
		
		panel.add(warehouseDisplayField);
		panel.add(this.warehouseCheckBox);
		
		JTextField passwordDisplayField = createDisplayField("Dane uzytkownikow:");
		this.passwordsCheckBox = new JCheckBox();
		this.passwordsCheckBox.setHorizontalAlignment(JCheckBox.CENTER);
		
		panel.add(passwordDisplayField);
		panel.add(this.passwordsCheckBox);
		
		JTextField chooseDisplayField = createDisplayField("Wskaz sciezke:");
		JButton chooseButton = new JButton("Wybierz");
		chooseButton.setActionCommand("choose");
		chooseButton.addActionListener(this);
		
		JPanel choosePanel = new JPanel(new FlowLayout());
		choosePanel.add(chooseButton);
		
		panel.add(chooseDisplayField);
		panel.add(choosePanel);
		
		return panel;
	}
	
	protected JPanel createDownPanel()
	{
		JPanel panel = new JPanel(new GridLayout(3, 1));
		
		this.displayLabel = new JLabel("");
		this.displayLabel.setHorizontalAlignment(JLabel.CENTER);
		
		panel.add(this.displayLabel);
		
		this.progressBar = new JProgressBar();
		panel.add(this.progressBar);
		
		JButton button = new JButton("Importuj");
		button.setActionCommand("import");
		button.addActionListener(this);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(button);
		
		panel.add(buttonPanel);
		
		return panel;
	}
	
	protected JTextField createDisplayField(String text)
	{
		JTextField displayField = new JTextField(text);
		displayField.setHorizontalAlignment(JTextField.RIGHT);
		displayField.setEditable(false);
		
		return displayField;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case "choose":
			{
				chooseFolder();
				break;
			}
			case "import":
			{
				boolean users = false;
				boolean warehouse = false;
				boolean passwords = false;
				
				File file = new File(this.displayLabel.getText());
				/*
				if(file == null)
				{
					JOptionPane.showMessageDialog(this, "Blad z wczytaniem folderu. Skontaktuj sie z tworca oprogramowania");
					break;
				}
				*/
				
				users = this.usersCheckBox.isSelected();
				warehouse = this.warehouseCheckBox.isSelected();
				passwords = this.passwordsCheckBox.isSelected();
				
				
				if(users == false && warehouse == false && passwords == false)
				{
					JOptionPane.showMessageDialog(this, "Musisz wybrac jakis modul do zaimportowania!");
					break;
				}
				
				if(!file.exists())
				{
					JOptionPane.showMessageDialog(this, "Podana sciezka nie istnieje, sprobuj ponownie.");
					break;
				}
				if(file.isFile())
				{
					JOptionPane.showMessageDialog(this, "Wskazales plik zamiast folderu, sprobuj ponownie.");
					break;
				}
				
				if(users)
				{
					
				}
				
				if(warehouse)
				{					
					ImportWarehouse warehouseRestore = new ImportWarehouse(file);
					boolean test = warehouseRestore.startRestore();
					System.out.println(test);
				}
				
				if(passwords)
				{
					ImportPasswords importPasswords = new ImportPasswords(file);
					boolean test = importPasswords.startRestore();
					System.out.println(test);
				}
				
				break;
			}
			default:
				break;
		}
	}
	
	protected void chooseFolder()
	{
		File file = null;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int option = fileChooser.showOpenDialog(this);
		
		if(option == JFileChooser.APPROVE_OPTION)
		{
			file = fileChooser.getSelectedFile();
			this.displayLabel.setText(file.getPath());
		}
		else
		{
			this.displayLabel.setText("");
		}
	}
}





























