package Data;

public class Computer 
{
	protected int id;
	protected String brand;
	protected String model;
	protected String type;
	protected String processor;
	protected String ram;
	protected String hdd;
	protected boolean dvd;
	protected boolean license;
	
	public Computer()
	{
		
	}
	
	public Computer(int id, String brand, String model, String type, String processor, String ram, String hdd, boolean dvd, boolean license)
	{
		try
		{
			this.id = id;
			this.brand = brand;
			this.model = model;
			this.type = type;
			this.processor = processor;
			this.ram = ram;
			this.hdd = hdd;
			this.dvd = dvd;
			this.license = license;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public void setBrand(String brand)
	{
		this.brand = brand;
	}
	
	public String getBrand()
	{
		return this.brand;
	}
	
	public void setModel(String model)
	{
		this.model = model;
	}
	
	public String getModel()
	{
		return this.model;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public void setProcessor(String processor)
	{
		this.processor = processor;
	}
	
	public String getProcessor()
	{
		return this.processor;
	}
	
	public void setRam(String ram)
	{
		this.ram = ram;
	}
	
	public String getRam()
	{
		return this.ram;
	}
	
	public void setHdd(String hdd)
	{
		this.hdd = hdd;
	}
	
	public String getHdd()
	{
		return this.hdd;
	}
	
	public void setDvd(boolean dvd)
	{
		this.dvd = dvd;
	}
	
	public boolean getDvd()
	{
		return this.dvd;
	}
	
	public void setLicense(boolean license)
	{
		this.license = license;
	}
	
	public boolean getLicense()
	{
		return this.license;
	}
}

































