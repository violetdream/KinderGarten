package com.personal.kindergarten.dao;

import com.personal.kindergarten.dao.KdWechat;
import com.personal.kindergarten.dao.KdWechatExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KdWechatMapper {
    long countByExample(KdWechatExample example);

    int deleteByExample(KdWechatExample example);

    int deleteByPrimaryKey(Long id);

    int insert(KdWechat record);

    int insertSelective(KdWechat record);

    List<KdWechat> selectByExample(KdWechatExample example);

    KdWechat selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") KdWechat record, @Param("example") KdWechatExample example);

    int updateByExample(@Param("record") KdWechat record, @Param("example") KdWechatExample example);

    int updateByPrimaryKeySelective(KdWechat record);

    int updateByPrimaryKey(KdWechat record);
}