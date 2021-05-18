package Data;

import java.sql.Date;

import FileManager.DateParse;

public class Supply 
{
	protected int id;
	protected int unique_id;
	protected Date date;
	protected int amount;
	protected int pallete_count;
	protected String comment;
	
	public Supply()
	{
		
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public void setUniqueId(int uniqueId)
	{
		this.unique_id = uniqueId;
	}
	
	public int getUniqueId()
	{
		return this.unique_id;
	}
	
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	public void setDateSring(String date)
	{
		this.date = DateParse.parseStringToDate(date);
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
	
	public void setAmount(int amount)
	{
		this.amount = amount;
	}
	
	public int getAmount()
	{
		return this.amount;
	}
	
	
}


































