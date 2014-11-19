package  util.jxl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang3.StringUtils;


/***********************************************
 程序功能:
 编程人员:Liqiang
 编程日期:2011-7-26
 修改人员:  
 修改日期:
***********************************************/
public class ExcelTemplate {
	/**
	 * Excel模板数据类型<br>
	 * number:数字类型
	 */
	public static final String ExcelTPL_DataType_Number = "number";

	/**
	 * Excel模板数据类型<br>
	 * number:文本类型
	 */
	public static final String ExcelTPL_DataType_Label = "label";
	

	private List<Cell> staticObject = null;
	private List<Cell> parameterObjct = null;
	private List<Cell> fieldObjct = null;
	private List<Cell> variableObject = null;
	private String templatePath = null;
	private File templatePathFile = null;
	private String workbookName = null;
	
	/**
	 * 绝对路径	
	 * 
	 * @param templatePath
	 */
	public ExcelTemplate(String templatePath,String workbookName) {
		this.templatePath = templatePath;
		this.workbookName = workbookName;
	}
	
	/**
	 * @param templatePath
	 */
	public ExcelTemplate(File templatePath,String workbookName) {
		this.workbookName = workbookName;
		this.templatePathFile = templatePath;
		
		if(templatePathFile!=null)
		{
			this.templatePath = templatePathFile.getAbsolutePath();
		}
	}
	
	public ExcelTemplate() {
	}
	
	/**
	 * 解析Excel模板
	 */
	public void parse() {
		staticObject = new ArrayList<Cell>();
		parameterObjct = new ArrayList<Cell>();
		fieldObjct = new ArrayList<Cell>();
		variableObject = new ArrayList<Cell>();
		InputStream file = null;
		Workbook workbook = null;
		try {
			if(StringUtils.isNotBlank(templatePath)){
				
				 file = new FileInputStream(new File(templatePath));
			}
			
			if(this.templatePathFile!=null)
			{
				file = new FileInputStream(templatePathFile);
			}
			
			workbook = Workbook.getWorkbook(file);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Sheet sheet = workbookName==null?workbook.getSheet(0):workbook.getSheet(workbookName);
		
		if (sheet!=null) {
			int rows = sheet.getRows();
			for (int k = 0; k < rows; k++) {
				Cell[] cells = sheet.getRow(k);
				for (int j = 0; j < cells.length; j++) {
					String cellContent = cells[j].getContents().trim();
					if (!StringUtils.isEmpty(cellContent)) {
						if (cellContent.indexOf("$P") != -1 || cellContent.indexOf("$p") != -1) {
							parameterObjct.add(cells[j]);
						} else if (cellContent.indexOf("$F") != -1 || cellContent.indexOf("$f") != -1) {
							fieldObjct.add(cells[j]);
						} else if(cellContent.indexOf("$V") != -1 || cellContent.indexOf("$v") != -1) {
							variableObject.add(cells[j]);
						}else {
						}
					}
				}
			}
		} else {
			//log.error("模板工作表对象不能为空!");
		}
	}

	/**
	 * 增加一个静态文本对象
	 */
	public void addStaticObject(Cell cell) {
		staticObject.add(cell);
	}

	/**
	 * 增加一个参数对象
	 */
	public void addParameterObjct(Cell cell) {
		parameterObjct.add(cell);
	}

	/**
	 * 增加一个字段对象
	 */
	public void addFieldObjct(Cell cell) {
		fieldObjct.add(cell);
	}


	public List<Cell> getStaticObject() {
		return staticObject;
	}

	public List<Cell> getParameterObjct() {
		return parameterObjct;
	}

	public List<Cell> getFieldObjct() {
		return fieldObjct;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public List<Cell> getVariableObject() {
		return variableObject;
	}

	public String getWorkbookName() {
		return workbookName;
	}

	public void setWorkbookName(String workbookName) {
		this.workbookName = workbookName;
	}

	
	
}
