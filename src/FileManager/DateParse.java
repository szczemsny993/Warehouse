package FileManager;
import java.util.regex.Pattern;
import java.time.*;

import java.sql.Date;

import java.util.HashMap;

public class DateParse 
{
	public DateParse()
	{
		
	}
	
	public static Date parseStringToDate(String stringDate)
	{
		Date date = null;
		String[] words = stringDate.split(Pattern.quote("."));
		
		date = createDate(Integer.valueOf(words[2]), Integer.valueOf(words[1]), Integer.valueOf(words[0]));
		
		return date;
	}
	
	public static String parseDateToString(Date date)
	{
		String result = "";
		String tmp = date.toString();
		
		String[] timePieces = tmp.split(Pattern.quote("-"));
		
		int day = Integer.valueOf(timePieces[2]) + 1;
		
		result = String.valueOf(day) + "." + timePieces[1] + "." + timePieces[0];
		
		return result;
	}
	
	private static String parseToString(Date date)
	{
		String result = "";
		String tmp = date.toString();
		
		String[] timePieces = tmp.split(Pattern.quote("-"));
		
		int day = Integer.valueOf(timePieces[2]);
		
		result = String.valueOf(day) + "." + timePieces[1] + "." + timePieces[0];
		
		return result;
	}
	
	public static LocalDate parseDateToLocalDate(Date date)
	{
		LocalDate localDate;
		String string = parseToString(date);
		
		HashMap<String, Integer> parsedMap = parseStringToArray(string);
	
		localDate = LocalDate.of(parsedMap.get("year"), parsedMap.get("month"), parsedMap.get("day"));
		
		return localDate;
	}
	
	public static Date createDate(int year, int month, int day)
	{
		LocalDate local = LocalDate.of(year, month, day);
		Date date = Date.valueOf(local);
		
		return date;
	}
	
	public static String localDateToString(LocalDate localDate)
	{
		String result = String.format("%s.%s.%s", localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());
		
		return result;
	}
	
	private static HashMap<String, Integer> parseStringToArray(String toParse)
	{
		String[] result = toParse.split(Pattern.quote("."));
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		map.put("day", Integer.valueOf(result[0]));
		map.put("month", Integer.valueOf(result[1]));
		map.put("year", Integer.valueOf(result[2]));
		
		return map;
	}
}

