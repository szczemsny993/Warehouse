package FileManager;

import java.io.File;

import javax.swing.JOptionPane;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;

public class UserDirectory 
{
	protected String path;
	protected String userName;
	protected Connection connection;
	
	public UserDirectory(String userName)
	{
		try
		{
			this.connection = Config.getConnection();
			
			if(userName == null || userName.length() <= 1)
			{
				return;
			}
			else
			{
				this.userName = userName;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(!isUserDirectoryExists())
		{
			JOptionPane.showMessageDialog(null, "Brak folderu z danymi w domyslnej lokalizacji.");
		}
	}
	
	protected boolean isUsersDirectoryExists()
	{
		boolean exists = false;
		File file = null;
		
		this.path = System.getProperty("user.home");
		this.path += "\\appdata\\roaming\\mojserwis\\users";
		
		file = new File(this.path);
		if(file.exists())
		{
			exists = true;
		}
		
		return exists;
	}
	
	public String getStringPath()
	{
		return this.path;
	}
	
	public File getFilePath()
	{
		File file = new File(this.path);
		return file;
	}
	
	public boolean setStringPath(String path)
	{
		boolean isSuccess = false;
		try
		{
			if(path.length() <= 2)
			{
				return isSuccess;
			}
			else
			{
				this.path = path;
				isSuccess = true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return isSuccess;
	}
	
	public boolean setFilePath(File file)
	{
		boolean isSuccess = false;
		
		try
		{
			if(!file.exists() || !file.isDirectory())
			{
				isSuccess = false;
			}
			else
			{
				this.path = file.getPath();
				isSuccess = true;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return isSuccess;
	}

	public boolean isUserDirectoryExists()
	{
		boolean isExists = false;
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		String userFolder = "";
		
		try
		{
			preparedStatement = this.connection.prepareStatement("SELECT user_directory FROM accounts WHERE user = ?");
			preparedStatement.setString(1, this.userName);
			resultSet = preparedStatement.executeQuery();
			
			if(resultSet.getFetchSize() == 0)
			{
				JOptionPane.showMessageDialog(null, "Problem z czytaniem danych odnosnie folderu z bazy danych.");
				return isExists;
			}
			
			while(resultSet.next())
			{
				userFolder = resultSet.getString("user_directory");
			}
			
			if(userFolder.length() > 10)
			{
				String tmp = this.path + "\\" + userFolder;
				File file = new File(tmp);
				if(file.exists() && file.isDirectory())
				{
					isExists = true;
				}
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
				if(resultSet != null)
				{
					resultSet.close();
				}
				
				if(preparedStatement != null)
				{
					preparedStatement.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return isExists;
	}
	
	public boolean createUserDirectory(String folderName)
	{
		boolean created = false;
		File file = null;
		
		this.path += "\\" + folderName;
		file = new File(this.path);
		file.mkdir();
		
		if(file.exists())
		{
			created = true;
		}
		
		return created;
	}
	
	public boolean deleteUserDirectory(String folderName)
	{
		boolean isSuccess = false;
		if(folderName == null || folderName.length() <= 10)
		{
			return isSuccess;
		}
		
		File userDir = new File(this.path + "\\" + folderName);
		if(userDir.exists() && userDir.isDirectory())
		{
			userDir.delete();
			isSuccess = true;
		}
		
		
		return isSuccess;
	}
	
	/*
	public File createTempFile(String fileName)
	{
		
	}
	
	public boolean deleteTempFile(String fileName)
	{
		
	}
	*/
}




































