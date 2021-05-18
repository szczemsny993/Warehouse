package Data;

import java.lang.NullPointerException;

import java.sql.Date;
import FileManager.DateParse;

public class Shipment 
{
	protected int id;
	protected Date date;
	protected String shipment_id;
	protected int warranty_length;
	protected int pallete_count;
	protected String description;
	
	public Shipment()
	{
		
	}
	
	public Shipment(int id, Date date, String shipmentId, int warranty_length, int pallete_count, String description)
	{
		try
		{
			this.id = id;
			this.date = date;
			this.shipment_id = shipmentId;
			this.warranty_length = warranty_length;
			this.pallete_count = pallete_count;
			this.description = description;
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	public Date getDate()
	{
		return this.date;
	}
	
	public String getDateString()
	{
		String result = DateParse.parseDateToString(this.date);
		return result;
	}
	
	public void setDateString(String date)
	{
		this.date = DateParse.parseStringToDate(date);
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
	
	public void setWarrantyLength(int length)
	{
		this.warranty_length = length;
	}
	
	public int getWarrantyLength()
	{
		return this.warranty_length;
	}
	
	public void setPalleteCount(int count)
	{
		this.pallete_count = count;
	}
	
	public int getPalleteCount()
	{
		return this.pallete_count;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}




























