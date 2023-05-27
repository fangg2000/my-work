package com.fangg.service;

import java.util.List;

import com.fangg.bean.chat.query.CompanyInfo;
import com.fangg.bean.chat.to.UserCompanyTO;
import com.fangg.bean.chat.vo.CompanyInfoVO;
import com.fangg.bean.chat.vo.SysUserVO;
import com.fangg.bean.chat.vo.UserCompanyVO;
import com.xclj.replay.ResultEntity;
import com.xclj.tk.service.BaseService;

public interface CompanyInfoService extends BaseService<CompanyInfo>{

	/** 批量新增企业信息 **/
	ResultEntity insertCompanyInfoByBatch(List<CompanyInfoVO> companyInfoVOList);
	
	/** 新增企业信息 **/
	ResultEntity insertCompanyInfo(CompanyInfoVO companyInfoVO);
	
	/** 查询用户对应企业信息列表 **/
	List<UserCompanyTO> getUserCompanyInfoForList(CompanyInfoVO companyInfoVO);
	
	/** 新增企业用户信息 **/
	ResultEntity insertUserCompanyInfo(SysUserVO sysUserVO);
	
	/** 客服对应所属信息 **/
	UserCompanyTO getCustomerServiceCompanyInfo(UserCompanyVO userCompanyVO);

	/** 客服信息列表(与企业关联的) **/
	List<UserCompanyTO> getCustomerServiceList(UserCompanyVO userCompanyVO);
	
}