package com.fangg.bean.chat.vo.record;

import java.io.Serializable;

/** 
 * 日记分组VO
 * @author fangg
 * 2022年2月21日 上午10:11:56
 */
public class RecordGroupVO implements Serializable {
    /** 
     * 串行版本ID
    */
    private static final long serialVersionUID = -7580332072052530159L;

    /** 
     */ 
    private Integer recordGroupId;

    /** 
     */ 
    private String userCode;

    /** 
     */ 
    private String groupName;

    /** 
     * 分组编号(默认分组编号为"DEFAULT")
     */ 
    private String groupCode;

	public Integer getRecordGroupId() {
		return recordGroupId;
	}

	public void setRecordGroupId(Integer recordGroupId) {
		this.recordGroupId = recordGroupId;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
    
}