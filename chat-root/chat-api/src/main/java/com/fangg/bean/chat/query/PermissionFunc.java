package com.fangg.bean.chat.query;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 功能权限
 * 
 * @author fangg 2022年1月20日 上午10:27:15
 */
@Table(name="permission_func")
public class PermissionFunc implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = 9114526363218252044L;

	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="per_fun_id")
	private Integer perFunId;

	/**
	 * 功能权限编号
	 */
	private String functionCode;

	/**
	 * 功能权限名称
	 */
	private String functionName;

	/**
	 * 权限等级(0普通用户，1VIP用户，2MVP用户，3SUPER用户) 默认：0
	 */
	private Integer grade;
	private Integer limitNum;			// 数量限制，-1无限，默认NULL

	public Integer getPerFunId() {
		return perFunId;
	}

	public void setPerFunId(Integer perFunId) {
		this.perFunId = perFunId;
	}

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Integer getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(Integer limitNum) {
		this.limitNum = limitNum;
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append(", serialVersionUID=").append(serialVersionUID);
		sb.append(", perFunId=").append(perFunId);
		sb.append(", functionCode=").append(functionCode);
		sb.append(", functionName=").append(functionName);
		sb.append(", grade=").append(grade);
		sb.append("]");
		return sb.toString();
	}
	
}