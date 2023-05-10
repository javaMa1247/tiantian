package com.ttsx.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttsx.bean.Addrinfo;
import org.apache.ibatis.annotations.Select;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-10 下午 6:51
 */
public interface AddrDao extends BaseMapper<Addrinfo> {
    @Select("select max(ano) from addrinfo")
    String getMaxAno();
}
