package com.ttsx.order.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttsx.bean.Goodsinfo;
import com.ttsx.bean.OrderIteminfo;
import com.ttsx.bean.Orderinfo;
import com.ttsx.feignApi.FeignApp;
import com.ttsx.order.dao.OrderDao;
import com.ttsx.utils.PageBean;
import com.ttsx.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-11 下午 7:53
 */
@Service
public class OrderBizTmpl {

    @Autowired
    private OrderDao dao;
    @Autowired
    private FeignApp feignApp;
    public PageBean findByPage (PageBean pageBean, String mno) {

        List<Orderinfo> dataset = this.fingByPage(pageBean.getPageno(),pageBean.getPagesize(),mno);
        Long total = this.countAll(mno);

        pageBean.setDataset(dataset);
        pageBean.setTotal(total);


        long totalPages = total%pageBean.getPagesize()==0?total/pageBean.getPagesize():total/pageBean.getPagesize()+1;
        pageBean.setTotalpages((int)totalPages);

        if(pageBean.getPageno()<=1){
            pageBean.setPre(1);
        }else{
            pageBean.setPre(pageBean.getPageno()-1);
        }

        if(pageBean.getPageno()==totalPages){
            pageBean.setNext((int)totalPages);
        }else{
            pageBean.setNext(pageBean.getPageno()+1);
        }
        if(totalPages==0){
            pageBean.setPre(1);
            pageBean.setNext(1);
            pageBean.setTotalpages(1);
        }
        return pageBean;
    }

    private Long countAll(String mno) {

        Integer countByMno = this.dao.getOrderInfoCountByMno(mno);
        Long longValue = countByMno.longValue();
        return longValue;
    }


//    private IPage<Orderinfo> getAllOrderByPage(String mno, int pageNum, int pageSize) {
//        Page<Orderinfo> page = new Page<>(pageNum, pageSize);
//        return this.dao.selectAllOrderByPage(page, mno);
//    }

    private List<Orderinfo> fingByPage( int pageno, int pagesize,String mno) {
        int start = (pageno-1)*pagesize;

        List<Orderinfo> list = this.dao.selectAllOrderByPage(mno,start,pagesize);

        if(list.size()<=0){
            return null;
        }
        for(Orderinfo order:list){
            String ono = order.getOno();
            List<OrderIteminfo> orderItemList = dao.selectOrderItemByOno(ono);
            for (OrderIteminfo iteminfo : orderItemList) {
                R<Goodsinfo> gno = this.feignApp.findById(Integer.valueOf(iteminfo.getGno()));
                iteminfo.setGoodsinfo(gno.getData());
            }
            order.setOrderItem(orderItemList);
        }
        return list;
    }

}
