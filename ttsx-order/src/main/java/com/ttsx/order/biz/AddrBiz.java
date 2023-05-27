package com.ttsx.order.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttsx.bean.Addrinfo;
import com.ttsx.order.dao.AddrDao;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AddrBiz {
    @Autowired
    private AddrDao dao;

    public List<Addrinfo> showAddr(String mno){
//        int mno= user.getUserId();
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
    public Integer addAddr(Addrinfo newaddrinfo,String mno){
//        int mno= user.getUserId();
        try{

            String ano = Integer.valueOf(dao.getMaxAno())+1+"";

            log.info(ano);
            newaddrinfo.setAno(ano);
            newaddrinfo.setMno(Integer.parseInt(mno));
            newaddrinfo.setFlag(1);
            newaddrinfo.setStatus(1);
            int result = dao.insert(newaddrinfo);
            if(result!=0){
                return 1;
            }else{
                return 0;
            }

        }catch (Exception e){
            return 0;
        }
    }


    public Integer updateAddr(Addrinfo addrinfo,String mno){
//        int mno= user.getUserId();

        log.info("addrinfo:{}",addrinfo);
        addrinfo.setMno(Integer.parseInt(mno));

        int result = dao.updateById(addrinfo);
        return result;
    }

    public Map<String ,Object> showAddrinfo(Addrinfo addrinfo,String mno){
//        int mno= user.getUserId();
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
        sb.append(addr.getProvince()+" "+addr.getCity()+" "+addr.getArea()+" "+addr.getAddr()+" ("+addr.getName()+"  )"+" "+tel);

        return sb.toString();
    }
}
