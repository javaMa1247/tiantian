package com.ttsx.order.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttsx.bean.Addrinfo;
import com.ttsx.order.dao.AddrDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-10 下午 6:52
 */
@Service
public class AddrBiz {
    @Autowired
    private AddrDao dao;
    private int mno=3;
    public List<Addrinfo> showAddr(){
        List<Addrinfo> list = new ArrayList<>();
        try{
            QueryWrapper<Addrinfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mno", mno);
            list = dao.selectList(queryWrapper);

            if(list!=null&&list.size()>0){
                return list;
            }else{
                return null;
            }

        }catch (Exception e){
            return null;
        }

    }
    public Integer addAddr(Addrinfo newaddrinfo){
        try{
            Addrinfo addrinfo = dao.selectOne(new QueryWrapper<Addrinfo>().select("MAX(ano)"));
            String ano = Integer.valueOf(addrinfo.getAno())+1+"";

            newaddrinfo.setAno(ano);
            newaddrinfo.setMno(mno);
            newaddrinfo.setFlag(1);
            newaddrinfo.setStatus(1);
            int result = dao.insert(addrinfo);
            if(result!=0){
                return 1;
            }else{
                return 0;
            }

        }catch (Exception e){
            return 0;
        }
    }


    public Integer updateAddr(Addrinfo addrinfo){

        Addrinfo newAddrinfo = new Addrinfo();
        newAddrinfo.setName(addrinfo.getName());
        newAddrinfo.setTel(addrinfo.getTel());
        newAddrinfo.setArea(addrinfo.getArea());
        newAddrinfo.setCity(addrinfo.getCity());
        newAddrinfo.setAddr(addrinfo.getAddr());

        newAddrinfo.setProvince(addrinfo.getProvince());
        LambdaQueryWrapper<Addrinfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Addrinfo::getAno, addrinfo.getAno())
                .eq(Addrinfo::getMno,mno);

        int result = dao.update(newAddrinfo, wrapper);
        return result;
    }

    public Map<String ,Object> showAddrinfo(Addrinfo addrinfo){
        Map<String ,Object> map1 = new HashMap<>();
        String ano = addrinfo.getAno();
        LambdaQueryWrapper<Addrinfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Addrinfo::getAno,ano)
                .eq(Addrinfo::getMno,mno);
        List<Addrinfo> list = dao.selectList(wrapper);


        if(list!=null&&list.size()>0){
            Map map = new HashMap();
            map.put("addr",list.get(0));
            map.put("address",this.showAddress(list.get(0)));
            map1.put("code",1);
            map1.put("data",map);
        }else{
            map1.put("code",0);
        }
        return map1;
    }
    private String showAddress(Addrinfo addr){

//        北京市 海淀区 东北旺西路8号中关村软件园 （李思 收） 182****7528
        StringBuffer sb = new StringBuffer();
        String tel = (addr.getTel().substring(0,3)+"****"+addr.getTel().substring(addr.getTel().length()-4,addr.getTel().length()));
        sb.append(addr.getProvince()+" "+addr.getCity()+" "+addr.getArea()+addr.getAddr()+" ("+addr.getName()+"  )"+" "+tel);

        return sb.toString();
    }
}
