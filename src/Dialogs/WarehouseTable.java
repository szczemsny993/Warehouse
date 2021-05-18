package Dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import FileManager.Config;

import java.sql.*;


public class WarehouseTable extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private JTable table;
	private DefaultTableModel model;
	private JScrollPane scrollPane;
	
	
	private JToolBar toolBar;
	
	private Connection connection;
	
	public WarehouseTable()
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
		createToolBar();
		
		add(scrollPane, BorderLayout.CENTER);
		add(toolBar, BorderLayout.EAST);
	}
	
	private void createTable()
	{
		createModel();
		
		this.table = new JTable(model);
		this.table.setAutoCreateRowSorter(true);
		
		this.scrollPane = new JScrollPane(this.table);
	}
	
	private void createModel()
	{
		this.model = new DefaultTableModel()
				{
					protected static final long serialVersionUID = 1L;
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
		model.addColumn("Ilosc");
	}
	
	private void createToolBar()
	{
		this.toolBar = new JToolBar(JToolBar.VERTICAL);
		this.toolBar.setFloatable(false);
		
		ButtonListener buttonListener = new ButtonListener();
		
		//create icons for buttons
		ImageIcon addIcon = new ImageIcon("resources/add_32px.png");
		ImageIcon removeIcon = new ImageIcon("resources/close_32px.png");
		ImageIcon printIcon = new ImageIcon("resources/printer_32px.png");
		ImageIcon statisticIcon = new ImageIcon("resources/statistics_32px.png");
		
		//create add button
		JButton addButton = new JButton(addIcon);
		addButton.setActionCommand("add");
		addButton.addActionListener(buttonListener);
		
		//create remove button
		JButton removeButton = new JButton(removeIcon);
		removeButton.setActionCommand("remove");
		removeButton.addActionListener(buttonListener);
		
		//create print button
		JButton printButton = new JButton(printIcon);
		printButton.setActionCommand("print");
		printButton.addActionListener(buttonListener);
		
		//create statistic button
		JButton statisticButton = new JButton(statisticIcon);
		statisticButton.setActionCommand("statistics");
		statisticButton.addActionListener(buttonListener);
		
		toolBar.add(addButton);
		toolBar.add(removeButton);
		toolBar.addSeparator();
		toolBar.add(printButton);
		toolBar.add(statisticButton);
		
	}
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand())
			{
				case "add":
				{
					WarehouseAddDialog dialog = new WarehouseAddDialog();
					dialog.setVisible(true);
					break;
				}
			}
		}
	}
	
	/*
	
	private void loadDataFromDatabase()
	{
		
	}
	*/
}





















