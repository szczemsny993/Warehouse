package Dialogs;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import FileManager.Config;

import java.sql.*;

public class WarehouseShowSupply extends JDialog
{	
	protected static final long serialVersionUID = 1L;
	private Connection connection;
	
	private JTable table;
	
	public WarehouseShowSupply(String supplyId)
	{
		super(null, "Dostawa", Dialog.ModalityType.DOCUMENT_MODAL);
		setSize(500, 300);
		
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
		add(table, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	private void createTable()
	{
		table = new JTable(createModel());
	}
	
	private DefaultTableModel createModel()
	{
		DefaultTableModel model = new DefaultTableModel()
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
		
		return model;
	}
}













































