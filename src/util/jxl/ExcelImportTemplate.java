package util.jxl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/***********************************************
 程序功能:
 编程人员:Liqiang
 编程日期:2011-7-26
 修改人员:  
 修改日期:
***********************************************/
public class ExcelImportTemplate {

	private static Log	                 log	= LogFactory .getLog(ExcelImportTemplate.class);

	private String templatePath = null;
	private File templatePathFile = null;
	private String workbookName = null;
	
	private int tableHeaderRow=0;
	
	
	/**
	 * 绝对路径	
	 * 
	 * @param templatePath
	 */
	public ExcelImportTemplate(String templatePath,String workbookName,int tableHeaderRow) {
		this.templatePath = templatePath;
		this.workbookName = workbookName;
		this.tableHeaderRow = tableHeaderRow;
	}
	
	/**
	 * @param templatePath
	 */
	public ExcelImportTemplate(File templatePath,String workbookName,int tableHeaderRow) {
		this.workbookName = workbookName;
		this.templatePathFile = templatePath;
		this.tableHeaderRow = tableHeaderRow;
		if(templatePathFile!=null)
		{
			this.templatePath = templatePathFile.getAbsolutePath();
		}
	}
	
	/**
	 * 解析Excel模板
	 * @return 
	 */
	public List<Map<String, String>> parse() {
		InputStream file = null;
		Workbook workbook = null;
		try {
			if(org.apache.commons.lang3.StringUtils.isNotBlank(templatePath)){
				
				 file = new FileInputStream(new File(templatePath));
			}
			
			if(this.templatePathFile!=null)
			{
				file = new FileInputStream(templatePathFile);
			}
			log.info(file);
			workbook = Workbook.getWorkbook(file);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Sheet sheet = workbookName==null?workbook.getSheet(0):workbook.getSheet(workbookName);
		
		List<String> tableHeaderRowArray = new LinkedList<String>();
		
		if (sheet!=null) {
			
			Cell[] cells = sheet.getRow(this.tableHeaderRow);
			
			for (int i = 0; i < cells.length; i++) 
 			{
				String header = cells[i].getContents().trim();
				
				if(StringUtils.isNotBlank(header))
				{
					tableHeaderRowArray.add(header);
				}
				
 			}
				
			int rows = sheet.getRows();
			
			List<Map<String,String>> contents = new ArrayList<Map<String,String>>();
			
			for (int j = tableHeaderRow+2; j < rows; j++)
			{
				Cell[] c= sheet.getRow(j);
				
				Map<String,String> rowContent = new LinkedHashMap<String, String>(); 
				
				for (int k = 0; k < cells.length; k++) 
				{
					String content = c[k].getContents().trim();
					
					rowContent.put(tableHeaderRowArray.get(k), content);
					
				}
				
				contents.add(rowContent);
			}
			return contents;
			
		} else {
			//log.error("模板工作表对象不能为空!");
		}
		return null;
	}


	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public String getWorkbookName() {
		return workbookName;
	}

	public void setWorkbookName(String workbookName) {
		this.workbookName = workbookName;
	}

	public static void main(String[] args)
   {

		ExcelImportTemplate template = new ExcelImportTemplate("D:\\WorkSpace\\smp\\hazard\\config\\aa.xls", "漏洞",3);
		
		List<Map<String, String>>  list = template.parse();
		
		
		System.out.println(list);
		
   }
	
}
