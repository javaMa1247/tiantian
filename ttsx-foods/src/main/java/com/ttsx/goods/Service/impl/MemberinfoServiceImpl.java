package com.ttsx.goods.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttsx.bean.Memberinfo;
import com.ttsx.goods.Service.MemberinfoService;
import com.ttsx.goods.mapper.MemberinfoMapper;
import org.springframework.stereotype.Service;

/**
 * @Author: mqb
 * @Date: 2023/5/11
 * @Time: 19:58
 * @Description:
 */
@Service
public class MemberinfoServiceImpl extends ServiceImpl<MemberinfoMapper, Memberinfo> implements MemberinfoService {
}
