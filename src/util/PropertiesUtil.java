package  util;

import java.util.Properties;


public class PropertiesUtil
{
	public static PropertiesUtil instance;
	
	public static PropertiesUtil getInstance(){
		
		if(instance==null)
		{
			instance = new PropertiesUtil();
		}
		return instance;
	}
	
	private PropertiesUtil(){}
	
	private static Properties prop;
	
	static {
		
		prop = new Properties();
		
		try
      {
	      prop.load(PropertiesUtil.class.getResourceAsStream("/conf.properties"));
      }
      catch (Exception e)
      {
      	throw new RuntimeException("conf.properties init Exception cause:"+e.getMessage());
      }
	}
	
	public String getProperty(String key)
	{
		String property = prop.getProperty(key);
		
		return property;
	
	}
	
	
	
	
}
