package com.fangg.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fangg.bean.chat.query.CompanyInfo;
import com.fangg.bean.chat.to.UserCompanyTO;
import com.fangg.bean.chat.vo.CompanyInfoVO;
import com.fangg.bean.chat.vo.UserCompanyVO;
import com.xclj.tk.dao.BaseTKMapper;

/**
 * @author fangg
 * date:2021/12/19 07:54
 */
@Mapper
public interface CompanyInfoMapper extends BaseTKMapper<CompanyInfo> {
	
	/** 批量新增企业信息 **/
	int insertCompanyInfoByBatch(List<CompanyInfoVO> companyInfoVOList);
	
	/** 查询用户对应企业信息列表 **/
	List<UserCompanyTO> selectUserCompanyInfoForList(CompanyInfoVO companyInfoVO);
	
	/** 新增企业外键表信息 **/
	int insertUserCompanyInfo(UserCompanyVO userCompanyVO);
	
	/** 客服对应所属信息 **/
	UserCompanyTO selectUserCompanyInfo(UserCompanyVO userCompanyVO);
	
	/** 客服信息列表(与企业关联的) **/
	List<UserCompanyTO> selectCustomerServiceList(UserCompanyVO userCompanyVO);
	
}