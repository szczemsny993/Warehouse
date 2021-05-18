package FileManager;
import java.io.File;

public class FirstRun 
{
	public FirstRun()
	{
		
	}
	
	public static boolean createMainFolder()
	{
		boolean success = false;
		String path = "";
		File file = null;
		
		if(isMainDirectoryExists())
		{
			return success;
		}
		
		path = System.getProperty("user.home") + "\\appdata\\roaming\\mojserwis";
		file = new File(path);
		file.mkdir();
		
		path = file.getPath();
		path += "\\users";
		file = new File(path);
		file.mkdir();
		
		if(file.exists())
		{
			success = true;
		}
		
		return success;
	}
	
	public static boolean isMainDirectoryExists()
	{
		boolean isExists = false;
		String path = System.getProperty("user.home") + "\\appdata\\roaming\\mojserwis";
		File file = new File(path);
		
		if(file.isDirectory())
		{
			return isExists;
		}
		
		if(file.exists())
		{
			isExists = true;
		}
		
		return isExists;
	}
	
	public static boolean isMainDirectoryCompleted()
	{
		boolean isCompleted = false;
		String path = System.getProperty("user.home") + "\\appdata\\roaming\\mojserwis\\users";
		File file = new File(path);
		
		if(file.exists() && file.isDirectory())
		{
			isCompleted = true;
		}
		
		return isCompleted;
	}
}
































