import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

public class Main
{
	public static void main(String[] args)
	{
		String path = System.getProperty("user.dir");
		File file;
		int counter = 0;
		int fileCounter = 0;
		
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		
		path += "\\src";
		
		file = new File(path);
		
		File[] files = file.listFiles();
		
		for(File var: files)
		{
			try
			{
				String fileName = var.getName();
				System.out.println(fileName);
				
				fileReader = new FileReader(var);
				bufferedReader = new BufferedReader(fileReader);
			
				String line = "";
				while( (line = bufferedReader.readLine()) != null)
				{
					if(checkLine(line))
					{
						continue;
					}
					
					counter++;
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
					fileReader.close();
					bufferedReader.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			fileCounter++;
		}
		
		System.out.println("Plikow: " + fileCounter);
		System.out.println("Linii kodu: " + counter);
	}
	
	protected static boolean checkLine(String line)
	{
		boolean isTrash = false;
		
		if(line.length() < 2)
		{
			isTrash = true;
		}
		
		return isTrash;
	}
}