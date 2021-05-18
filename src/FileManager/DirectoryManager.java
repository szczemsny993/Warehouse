package FileManager;

import javax.swing.JOptionPane;

import java.io.File;

import java.sql.Connection;

public class DirectoryManager 
{	
	protected File path;
	protected String stringPath;
	protected Connection connection;
	
	public DirectoryManager()
	{
		try
		{
			this.connection = Config.getConnection();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(!loadMainDirectory())
		{
			JOptionPane.showMessageDialog(null, "Blad ladowania folderu glownego.");
			return;
		}
	}
	
	protected boolean loadMainDirectory()
	{
		boolean isSuccess = false;
		String tmpPath = System.getProperty("user.home");
		tmpPath += "\\appdata\\roaming\\mojserwis";
		
		File file = new File(tmpPath);
		if(file.exists() && file.isDirectory())
		{
			path = file;
			stringPath = file.getPath();
			isSuccess = true;
		}
		
		return isSuccess;
	}
	
	//return null if fail
	public UserDirectory getUserDirectory(String userName)
	{
		UserDirectory userDirectory = null;
		
		if(userName.contains("Administrator") || userName.contains("administrator") || userName.length() <= 1)
		{
			return null;
		}
		
		userDirectory = new UserDirectory(userName);
		return userDirectory;
	}
}






























