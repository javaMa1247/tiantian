package com.ttsx.order.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ttsx.bean.Cartinfo;
import com.ttsx.bean.Goodsinfo;
import com.ttsx.feignApi.FeignApp;
import com.ttsx.order.dao.CartDao;
import com.ttsx.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-08 下午 2:55
 */
@Service
@Slf4j
public class CartBiz {
    @Autowired
    private FeignApp feignApp;
    @Autowired
    private CartDao dao;


    public List<Cartinfo> showAllCart(String mno){
//        int mno= user.getUserId();
        QueryWrapper<Cartinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Cartinfo::getMno, mno);

        List<Cartinfo> cartinfos = this.dao.selectList(queryWrapper);
        for (Cartinfo cartinfo : cartinfos) {
            cartinfo.setCount(cartinfo.getCount()+1);
            //TODO: 到nacos中查找res-foods服务中的   findById ，要得到菜品对象goods
            R<Goodsinfo> resultMap=this.feignApp.findById(   Integer.valueOf(cartinfo.getGno()) );
            Goodsinfo gs = resultMap.getData();
            cartinfo.setSmallCount(gs.getPrice()*cartinfo.getNum());
            cartinfo.setGoodsinfo(gs);

        }
        return cartinfos;
    }
    public int addCart(String gno,String num,String mno) {
//        int mno= user.getUserId();
//        mno = BaseContext.getCurrentId().intValue();
        int result = 0;
        try {
            //判断购物项是否已经存在
            QueryWrapper<Cartinfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("num")
                    .eq("gno", gno)
                    .eq("mno", mno);
            List<Object> numList = dao.selectObjs(queryWrapper);
            int num0 = numList.size()==0 ? 0 : (int) numList.get(0);

            if (num0 > 0) {
                int num2 = num0 + Integer.parseInt(num);
                if (num2 < 1) {
                    num2 = 1;
                }
                UpdateWrapper<Cartinfo> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("num", num2)
                        .eq("gno", gno)
                        .eq("mno", mno);
                int rows = dao.update(null, updateWrapper);
                return rows;
            } else {
//                String cno = String.valueOf((int) db.selectAggreation("select max(cno) from cartinfo") + 1);
//                db.doUpdata("INSERT INTO cartinfo VALUES (?, ?,?, ?)",cno,YcConstants.selectMno(request),gno,num);
                QueryWrapper<Cartinfo> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.select("max(cno)");
                Object re = dao.selectObjs(queryWrapper1).get(0);
                Integer maxCno = re == null ? null : (Integer.valueOf(re.toString()))+1 ;

                Cartinfo cartinfo = new Cartinfo(maxCno+"",gno,mno+"", Integer.valueOf(num));
                log.info(mno+"");
                int rows = dao.insert(cartinfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
    public int delgoods(String cno,String gno,String mno){
//        int mno= user.getUserId();
        int result = 0;
        try{
            LambdaQueryWrapper<Cartinfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Cartinfo::getCno, cno)
                    .eq(Cartinfo::getMno, mno)
                    .eq(Cartinfo::getGno, gno);
            result =  dao.delete(lambdaQueryWrapper);

        }catch (Exception e){
            return result;
        }

        return result;
    }
    public Integer cleanCart(List<Map<String, Object>> lists,String mno){
//        int mno= user.getUserId();
        int i = 0;
//        String ono = String.valueOf(db.selectAggreation("select MAX(ono) from orderinfo"));
        if(lists.size()>0){
            for(Map<String, Object> list :lists){
                String cno = (String) list.get("cno");
                LambdaQueryWrapper<Cartinfo> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Cartinfo::getCno, cno).eq(Cartinfo::getMno, mno);
                i = dao.delete(wrapper);
            }
        }else {
            return 0;
        }
        return i;
    }

    public List<Cartinfo> showOnecartInfo(String gno, String num) {
        Cartinfo cart = new Cartinfo();
        List<Cartinfo> list = new ArrayList<>();
        Goodsinfo goodsinfo = feignApp.findById(Integer.parseInt(gno)).getData();
        try{
            cart.setNum(Integer.parseInt(num));
            cart.setSmallCount(goodsinfo.getPrice()*cart.getNum());
            cart.setGoodsinfo(goodsinfo);
            cart.setGno(gno);
            list.add(cart);
            return list;

        }catch (Exception e){
            return  list;
        }
    }
}
