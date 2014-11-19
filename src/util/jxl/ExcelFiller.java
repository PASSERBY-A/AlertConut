package util.jxl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/***********************************************
 程序功能:填充器
 编程人员:Liqiang
 编程日期:2011-7-8
 修改人员:  
 修改日期:
***********************************************/
public class ExcelFiller{
	
	private Log log = LogFactory.getLog(ExcelFiller.class);
	
	private ExcelTemplate excelTemplate = null;
	
	private ExcelData excelData = null;
	
	private String workbookName = null;
	
	public ExcelFiller() {
	}
	
	/**
	 * @param pExcelTemplate
	 * @param pExcelData
	 * @param workbookName
	 */
	public ExcelFiller(ExcelTemplate pExcelTemplate, ExcelData pExcelData) {
		setWorkbookName(pExcelTemplate.getWorkbookName());
		setExcelData(pExcelData);
		setExcelTemplate(pExcelTemplate);
	}
	
	/**
	 * 数据填充 将ExcelData填入excel模板
	 * 
	 * @return ByteArrayOutputStream
	 */ 
	public ByteArrayOutputStream fill() {
		WritableSheet wSheet = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			File file = new File(getExcelTemplate().getTemplatePath());
			Workbook wb = Workbook.getWorkbook(file);
			WorkbookSettings s = new WorkbookSettings();
			s.setWriteAccess(null);
			WritableWorkbook wwb = Workbook.createWorkbook(bos, wb);
			//wSheet = wwb.getSheet(workbookName);
			wSheet = workbookName==null?wwb.getSheet(0):wwb.getSheet(workbookName);
			fillStatics(wSheet);
			fillParameters(wSheet);
			fillFields(wSheet);
/*			if (getExcelData().getFieldsList() != null) {
				// fillFields(wSheet);
			}
			
			*/
			log.info("wSheet" +wSheet);
			wwb.write();
			wwb.close();
			wb.close();
			bos.flush();
		} catch (Exception e) {
			log.error("基于模板生成可写工作表出错了!"+e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return bos;
	}
	
	/**
	 * 写入静态对象
	 */
	private void fillStatics(WritableSheet wSheet) {
		List<Cell> statics = getExcelTemplate().getStaticObject();
		for (int i = 0; i < statics.size(); i++) {
			Cell cell = (Cell) statics.get(i);
			Label label = new Label(cell.getColumn(), cell.getRow(),
					cell.getContents());
			label.setCellFormat(cell.getCellFormat());
			try {
				wSheet.addCell(label);
			} catch (Exception e) {
				 log.error("fillStatics is error!" + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 写入参数对象
	 */
	private void fillParameters(WritableSheet wSheet) {
		List<Cell> parameters = getExcelTemplate().getParameterObjct();
		Dto parameterDto = getExcelData().getParametersDto();
		for (Cell cell : parameters) {
			WritableCellFormat format = new WritableCellFormat(
					cell.getCellFormat());
			String key = getKey(cell.getContents().trim());
			String type = getType(cell.getContents().trim());
			try {
				if (!parameterDto.containsKey(key) || parameterDto.get(key)==null) {
					Label label = new Label(cell.getColumn(), cell.getRow(), "");
					label.setCellFormat(format);
					wSheet.addCell(label);
					continue;
				}
				
				if (type.equalsIgnoreCase(ExcelTemplate.ExcelTPL_DataType_Number)) {
					Number number = new Number(cell.getColumn(), cell.getRow(),
							Double.parseDouble( parameterDto.get(key).toString()) );
					number.setCellFormat(format);
					wSheet.addCell(number);
				}
				else {
					Label label = new Label(cell.getColumn(), cell.getRow(),
							parameterDto.get(key).toString());
					label.setCellFormat(format);
					wSheet.addCell(label);
				}
			} catch (Exception e) {
				 log.error("fillParameters is error!" + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 写入表格字段对象
	 * 
	 * @throws Exception
	 */
	private void fillFields(WritableSheet wSheet) throws Exception {
		List<Cell> fields = getExcelTemplate().getFieldObjct();
		List<Dto<String, String>> fieldList = getExcelData().getFieldsList();
		for (int j = 1; j < fieldList.size()+1; j++) {
			int index = 0;
			Dto data = fieldList.get(j-1);
			for (Cell cell : fields) {
				// Cell cell = (Cell) fields.get(i);
				String key = getKey(cell.getContents().trim());
				String type = getType(cell.getContents().trim());
				Object d = data.get(key);
				if (!data.containsKey(key)) {
					continue;
				}
				if(d==null)
				{
					continue;
					
				}
				try {
					if (type.equalsIgnoreCase(ExcelTemplate.ExcelTPL_DataType_Number)) {
						Number number=null;
                  try
                  {
	                  number = new Number(cell.getColumn(),cell.getRow()+j, Double.valueOf(d.toString()));
	                  WritableCellFormat format = new WritableCellFormat(cell.getCellFormat());
	                  number.setCellFormat(format);
                  }
                  catch (Exception e)
                  {
                  	log.error("fillFields is error!" + e.getMessage());
                  	e.printStackTrace();
                  }
						// number.setCellFormat(cell.getCellFormat());
                 
						wSheet.addCell(number);
						
					}
					else {
						
						Label label = new Label(cell.getColumn(), cell.getRow()+ j, (String) d);
						WritableCellFormat format = new WritableCellFormat(cell.getCellFormat());
						// format.setBorder(Border.LEFT,BorderLineStyle.THIN);
						label.setCellFormat(format);
						wSheet.addCell(label);
					}
				} catch (Exception e) {
					// log.error(GlobalConstants.Exception_Head +
					// "写入表格字段对象发生错误!");
					log.error("fillFields is error!" + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 写入变量对象
	 */
	private void fillVariables(WritableSheet wSheet, int row) {
		List variables = getExcelTemplate().getVariableObject();
		Dto parameterDto = getExcelData().getParametersDto();
		for (int i = 0; i < variables.size(); i++) {
			Cell cell = (Cell) variables.get(i);
			String key = getKey(cell.getContents().trim());
			String type = getType(cell.getContents().trim());
			try {
				if (type.equalsIgnoreCase(ExcelTemplate.ExcelTPL_DataType_Number)) {
					Number number = new Number(cell.getColumn(), row,
							(Double) parameterDto.get(key));
					number.setCellFormat(cell.getCellFormat());
					wSheet.addCell(number);
				}
				else {
					String content = parameterDto.get(key).toString();
					if (content == null && !key.equalsIgnoreCase("nbsp")) {
						content = key;
					}
					Label label = new Label(cell.getColumn(), row, content);
					label.setCellFormat(cell.getCellFormat());
					wSheet.addCell(label);
				}
			} catch (Exception e) {
				// log.error(GlobalConstants.Exception_Head + "写入表格变量对象发生错误!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取模板键名
	 * 
	 * @param pKey
	 *            模板元标记
	 * @return 键名
	 */
	private static String getKey(String pKey) {
		String key = null;
		int index = pKey.indexOf(":");
		if (index == -1) {
			key = pKey.substring(3, pKey.length() - 1);
		}
		else {
			key = pKey.substring(3, index);
		}
		return key;
	}
	
	/**
	 * 获取模板单元格标记数据类型
	 * 
	 * @param pType
	 *            模板元标记
	 * @return 数据类型
	 */
	private static String getType(String pType) {
		String type = ExcelTemplate.ExcelTPL_DataType_Label;
		if (pType.indexOf(":n") != -1 || pType.indexOf(":N") != -1) {
			type = ExcelTemplate.ExcelTPL_DataType_Number;
		}
		return type;
	}
	
	public ExcelTemplate getExcelTemplate() {
		return excelTemplate;
	}
	
	public void setExcelTemplate(ExcelTemplate excelTemplate) {
		this.excelTemplate = excelTemplate;
	}
	
	public ExcelData getExcelData() {
		return excelData;
	}
	
	public void setExcelData(ExcelData excelData) {
		this.excelData = excelData;
	}
	
	public void setWorkbookName(String workbookName) {
		this.workbookName = workbookName;
	}
	
}
