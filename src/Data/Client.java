package Data;


public class Client 
{
	protected int id;
	protected String code;
	protected String name;
	protected String nip;
	protected String city;
	protected String post_code;
	protected String address;
	protected String telephone;
	
	public Client(int id, String code, String name, String nip, String city, String postCode, String address, String telephone)
	{
		try
		{
			this.id = id;
			this.code = code;
			this.nip = nip;
			this.city = city;
			this.post_code = postCode;
			this.address = address;
			this.telephone = telephone;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Client()
	{
		
	}
	
	public void setId(int id)
	{
		try
		{
			if(id < 0)
			{
				throw new Exception("ID must be signed");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		this.id = id;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public void setCode(String code)
	{
		this.code = code;
	}
	
	public String getCode()
	{
		return this.code;
	}
	
	public void setNip(String nip)
	{
		this.nip = nip;
	}
	
	public String getNip()
	{
		return this.nip;
	}
	
	public void setCity(String city)
	{
		this.city = city;
	}
	
	public String getCity()
	{
		return this.city;
	}
	
	public void setPostCode(String post_code)
	{
		this.post_code = post_code;
	}
	
	public String getPostCode()
	{
		return this.post_code;
	}
	
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	public String getAddress()
	{
		return this.address;
	}
	
	public void setTelephone(String telephone)
	{
		this.telephone = telephone;
	}
	
	public String getTelephone()
	{
		return this.telephone;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
}





























