package com.ttsx.index.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttsx.bean.Memberinfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-07 下午 8:56
 */
@Mapper
public interface UserMapper extends BaseMapper<Memberinfo> {
}
