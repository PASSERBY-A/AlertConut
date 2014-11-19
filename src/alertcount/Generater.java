package alertcount;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import util.PropertiesUtil;
import util.jxl.Dto;
import util.jxl.ExcelData;
import util.jxl.ExcelFiller;
import util.jxl.ExcelTemplate;


public class Generater {

	
	private   JdbcTemplate nnmJdbcTemplate=null ;
	
	private  JdbcTemplate avmonJdbcTemplate=null;
	
	
	
	public static void main(String[] args) throws Exception {
		
		
		
		ApplicationContext   context =new  ClassPathXmlApplicationContext("/spring/alert-conut.xml");;
		
		//Generater g = new Generater();
		
		//g.process();
		
	}

	public void process()
	{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar c =  Calendar.getInstance();
		
		c.setTime(new Date());
		
		String	end =  sdf.format(c.getTime());
		
		c.add(Calendar.DAY_OF_YEAR,-7);
		
		String begin =  sdf.format(c.getTime());
		
		try {
			Generate(begin,end);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public void Generate(String begin,String end) throws Exception
	{
		
		
		String nnmsql = PropertiesUtil.getInstance().getProperty("nnmsql");
		
		List<Dto<String,String>>  r = getNNMAlertCount(nnmsql,begin,end);
		List<Dto<String,String>>  r1 = getAvmonAlertCount(begin, end);

		for(Dto<String,String> d:r1)
		{
			r.add(d);
		}
		
		Dto<String,String> d = new Dto<String, String>();
		
		d.put("begin", begin);
		
		d.put("end", end);
		
		ExcelData data = new ExcelData(d, r);
		
		
		ExcelTemplate t = new ExcelTemplate(new File(PropertiesUtil.getInstance().getProperty("template")),null);
		
		t.parse();
		
		ExcelFiller filter = new ExcelFiller(t, data);
		
		FileOutputStream out = new FileOutputStream( PropertiesUtil.getInstance().getProperty("out.file"));
		
		out.write(filter.fill().toByteArray());
		
		out.flush();
		
		out.close();
		
		
		
		
	}
	
	
	

	private List<Dto<String,String>> getNNMAlertCount(String nnmsql,String begin,String end) {
		
		
		
		
		List<Dto<String,String>>  r = nnmJdbcTemplate.query(nnmsql.format(nnmsql,begin,end),new RowMapper<Dto<String,String>>() {

			
			@Override
			public Dto<String,String> mapRow(ResultSet rs, int index) throws SQLException {
				
				Dto<String,String> result = new Dto<String, String>();
				
				String name = rs.getString("name");
				
				result.put("nnm.name", name);
				
				result.put("nnm.count", rs.getString("count"));
				
				String desc = nnmJdbcTemplate.queryForObject("SELECT ic.description FROM nms_incident_config ic where ic.name='"+name+"'", String.class);		
				
				result.put("nnm.desc", desc);
				
				return result;
			}
			
		});
		
		return r;
		
	}
	
	
	private List<Dto<String, String>> getAvmonAlertCount(String begin,String end) {
		List<Dto<String, String>> result = new ArrayList<Dto<String,String>>();
		
		Dto<String,String> dto = new Dto<String, String>();
		
		
		
		
		String sql1 = PropertiesUtil.getInstance().getProperty("avmonsql1");
		String sql2 = PropertiesUtil.getInstance().getProperty("avmonsql2");
		String sql3 = PropertiesUtil.getInstance().getProperty("avmonsql3");
		String sql4 = PropertiesUtil.getInstance().getProperty("avmonsql4");
		
		Integer nullkpiCountHistory=0;
		Integer nullkpiCount=0;
		try {
			nullkpiCountHistory = avmonJdbcTemplate.queryForObject(sql1,new String[]{begin,end},Integer.class);
			nullkpiCount = avmonJdbcTemplate.queryForObject(sql2,new String[]{begin,end},Integer.class);
			
		} catch (EmptyResultDataAccessException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		dto.put("avmon.count", String.valueOf(nullkpiCountHistory+nullkpiCount));
		dto.put("avmon.desc","主机不可用");
		dto.put("avmon.kpiname","null");
		
		result.add(dto);
		
		final Map<String,String> kpis = new HashMap<String, String>();
		
		avmonJdbcTemplate.query(sql3, new String[]{begin,end}, new RowMapper<Dto<String, String>>(){

			@Override
			public Dto<String, String> mapRow(ResultSet rs, int i)
					throws SQLException {
				
				Dto<String,String> dto = new Dto<String, String>();
				String name = rs.getString(1);
				String desc = rs.getString(2);
				String count = rs.getString(3);
				dto.put("avmon.kpiname",name);
				dto.put("avmon.desc", desc);
				dto.put("avmon.count", count);
				
				if(!kpis.containsKey(desc))
				{
					kpis.put(desc, count);
				}
				else{
					
					Integer c = Integer.valueOf(kpis.get(desc))+Integer.valueOf(count);
					kpis.put(desc, String.valueOf(c));
				}
				
				
				return dto;
			}});
		
		
		 avmonJdbcTemplate.query(sql4, new String[]{begin,end}, new RowMapper<Dto<String, String>>(){

			@Override
			public Dto<String, String> mapRow(ResultSet rs, int i)
					throws SQLException {
				
				Dto<String,String> dto = new Dto<String, String>();
				String name = rs.getString(1);
				String desc = rs.getString(2);
				String count = rs.getString(3);
				dto.put("avmon.kpiname",name);
				dto.put("avmon.desc", desc);
				dto.put("avmon.count", count);
				
				if(!kpis.containsKey(desc))
				{
					kpis.put(desc, count);
				}
				else{
					
					Integer c = Integer.valueOf(kpis.get(desc))+Integer.valueOf(count);
					kpis.put(desc, String.valueOf(c));
				}
				
				
				return dto;
			}});
		
		
		 
		  for(String s:kpis.keySet())
		  {
			  Dto<String,String> d = new Dto<String, String>();
			  
			  d.put("avmon.desc", s);
			  
			  d.put("avmon.count", kpis.get(s));
			  
			  result.add(d);
		  }	  
			  
		return result;
	}

	public void setNnmJdbcTemplate(JdbcTemplate nnmJdbcTemplate) {
		this.nnmJdbcTemplate = nnmJdbcTemplate;
	}

	public void setAvmonJdbcTemplate(JdbcTemplate avmonJdbcTemplate) {
		this.avmonJdbcTemplate = avmonJdbcTemplate;
	}
	
	
	
	
	
	

}
