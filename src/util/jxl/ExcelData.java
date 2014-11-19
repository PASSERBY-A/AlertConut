package util.jxl;

import java.util.List;



/***********************************************
 程序功能:
 编程人员:Liqiang
 编程日期:2011-7-26
 修改人员:  
 修改日期:
***********************************************/
public class ExcelData {

	/**
	 * Excel参数元数据对象
	 */
	private Dto parametersDto;

	/**
	 * Excel集合元对象
	 */
	private List<Dto<String,String>> fieldsList;

	/**
	 * 构造函数
	 * 
	 * @param pDto
	 *            元参数对象
	 * @param pList
	 *            集合元对象
	 */
	public ExcelData(Dto pDto, List<Dto<String,String>> pList) {
		setParametersDto(pDto);
		setFieldsList(pList);
	}

	public Dto getParametersDto() {
		return parametersDto;
	}

	public void setParametersDto(Dto parametersDto) {
		this.parametersDto = parametersDto;
	}

	public List<Dto<String,String>> getFieldsList() {
		return fieldsList;
	}

	public void setFieldsList(List<Dto<String,String>> fieldsList) {
		this.fieldsList = fieldsList;
	}

	 

 

	

}
