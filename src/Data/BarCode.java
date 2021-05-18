package Data;

public class BarCode 
{
	protected int id;
	protected String shipment_id;
	protected String casing;
	protected String motherboard;
	protected String hdd;
	protected String power_supply;
	protected String dvd;
	
	public BarCode()
	{
		
	}
	
	public BarCode(int id, String shipment_id, String casing, String motherboard, String hdd, String power_supply, String dvd)
	{
		try
		{
			this.id = id;
			this.shipment_id = shipment_id;
			this.casing = casing;
			this.motherboard = motherboard;
			this.hdd = hdd;
			this.power_supply = power_supply;
			this.dvd = dvd;
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
	
	public void setShipmentId(String shipmentId)
	{
		this.shipment_id = shipmentId;
	}
	
	public String getShipmentId()
	{
		return this.shipment_id;
	}
	
	public void setCasing(String casing)
	{
		this.casing = casing;
	}
	
	public String getCasing()
	{
		return this.casing;
	}
	
	public void setMotherboard(String motherboard)
	{
		this.motherboard = motherboard;
	}
	
	public String getMotherboard()
	{
		return this.motherboard;
	}
	
	public void setHdd(String hdd)
	{
		this.hdd = hdd;
	}
	
	public String getHdd()
	{
		return this.hdd;
	}
	
	public void setPowerSupply(String powerSupply)
	{
		this.power_supply = powerSupply;
	}
	
	public String getPowerSupply()
	{
		return this.power_supply;
	}
	
	public void setDvd(String dvd)
	{
		this.dvd = dvd;
	}
	
	public String getDvd()
	{
		return this.dvd;
	}
}







































