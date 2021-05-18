package Account;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Account 
{
	static protected String userPath;
	static protected String user;
	static protected int permisionLevel;
	
	static public int ADMINISTRATOR_LEVEL = 3;
	static public int USER_LEVEL = 2;
	static public int GUEST_LEVEL = 1;
	
	protected static boolean warehouseAccess;
	protected static boolean shipmentAccess;
	protected static boolean supplyAccess;
	protected static boolean computerAccess;
	protected static boolean clientAccess;
	protected static boolean passwordAccess;
	protected static boolean subscribeAccess;
	protected static boolean backupAccess;
	
	public Account()
	{
		permisionLevel = 0;
	}
	
	//accessor for user
	public static String getUser()
	{
		return user;
	}
	
	public static void setUser(String nameUser)
	{
		user = nameUser;
	}
	
	//accessor for permision level
	public static int getPermisionLevel()
	{
		return permisionLevel;
	}
	
	public static void setPermisionLevel(int lvl)
	{
		permisionLevel = lvl;
	}
	
	//accessor for warehouse module
	public static void setWarehouseAccessValue(boolean value)
	{
		warehouseAccess = value;
	}
	
	public static boolean getWarehouseAccessValue()
	{
		return warehouseAccess;
	}
	
	//accessor for shipment module
	public static void setShipmentAccessValue(boolean value)
	{
		shipmentAccess = value;
	}
	
	public static boolean getShipmentAccessValue()
	{
		return shipmentAccess;
	}
	
	//accessor for supply module
	public static void setSupplyAccessValue(boolean value)
	{
		supplyAccess = value;
	}
	
	public static boolean getSupplyAccessValue()
	{
		return supplyAccess;
	}
	
	//accessor for computer module
	public static void setComputerAccessValue(boolean value)
	{
		computerAccess = value;
	}
	
	public static boolean getComputerAccessValue()
	{
		return computerAccess;
	}
	
	//accessor for client module
	public static void setClientAccessValue(boolean value)
	{
		clientAccess = value;
	}
	
	public static boolean getClientAccessValue()
	{
		return clientAccess;
	}
	
	//accessor for password module
	public static void setPasswordAccessValue(boolean value)
	{
		passwordAccess = value;
	}
	
	public static boolean getPasswordAccessValue()
	{
		return passwordAccess;
	}
	
	//accessor for subscribe module
	public static void setSubscribeAccessValue(boolean value)
	{
		subscribeAccess = value;
	}
	
	public static boolean getSubscribeAccessValue()
	{
		return subscribeAccess;
	}
	
	//accessor for backup module
	public static void setBackupAccessValue(boolean value)
	{
		backupAccess = value;
	}
	
	public static boolean getBackupAccessValue()
	{
		return backupAccess;
	}	
	
	public static String SHA(String input)
	{
		String result = "";
		
		try
		{
			//static getInstance method is called with hash SHA
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			
			//digest() method called to calculate message digest of an input and return array of byte
			byte[] messageDigest = md.digest(input.getBytes());
			
			//convert byte array into signum representatium
			BigInteger no = new BigInteger(1, messageDigest);
			
			//convert message digest into hex value
			String hashText = no.toString(16);
			
			while(hashText.length() < 32)
			{
				hashText = "0" + hashText;
			}
			
			result = hashText;
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
		
		return result;
	}
	
	public static void setUserPath(String value)
	{
		userPath = value;
	}
	
	public static String getUserPath()
	{
		return userPath;
	}
}





































