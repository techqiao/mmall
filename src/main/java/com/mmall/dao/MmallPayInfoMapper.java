package com.mmall.dao;

import com.mmall.domain.MmallPayInfo;
import com.mmall.domain.MmallPayInfoCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface MmallPayInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_pay_info
     *
     * @mbggenerated Sun Apr 08 16:54:27 CST 2018
     */
    int countByExample(MmallPayInfoCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_pay_info
     *
     * @mbggenerated Sun Apr 08 16:54:27 CST 2018
     */
    int deleteByExample(MmallPayInfoCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_pay_info
     *
     * @mbggenerated Sun Apr 08 16:54:27 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_pay_info
     *
     * @mbggenerated Sun Apr 08 16:54:27 CST 2018
     */
    int insert(MmallPayInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_pay_info
     *
     * @mbggenerated Sun Apr 08 16:54:27 CST 2018
     */
    int insertSelective(MmallPayInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_pay_info
     *
     * @mbggenerated Sun Apr 08 16:54:27 CST 2018
     */
    List<MmallPayInfo> selectByExampleWithRowbounds(MmallPayInfoCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_pay_info
     *
     * @mbggenerated Sun Apr 08 16:54:27 CST 2018
     */
    List<MmallPayInfo> selectByExample(MmallPayInfoCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_pay_info
     *
     * @mbggenerated Sun Apr 08 16:54:27 CST 2018
     */
    MmallPayInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_pay_info
     *
     * @mbggenerated Sun Apr 08 16:54:27 CST 2018
     */
    int updateByExampleSelective(@Param("record") MmallPayInfo record, @Param("example") MmallPayInfoCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_pay_info
     *
     * @mbggenerated Sun Apr 08 16:54:27 CST 2018
     */
    int updateByExample(@Param("record") MmallPayInfo record, @Param("example") MmallPayInfoCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_pay_info
     *
     * @mbggenerated Sun Apr 08 16:54:27 CST 2018
     */
    int updateByPrimaryKeySelective(MmallPayInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mmall_pay_info
     *
     * @mbggenerated Sun Apr 08 16:54:27 CST 2018
     */
    int updateByPrimaryKey(MmallPayInfo record);
}