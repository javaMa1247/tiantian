package com.ttsx.goods.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttsx.BeanDto.DiscussDto;
import com.ttsx.bean.Discuss;
import com.ttsx.bean.Goodsinfo;
import com.ttsx.bean.Goodstype;
import com.ttsx.bean.Memberinfo;
import com.ttsx.goods.Service.DiscussService;
import com.ttsx.goods.Service.GoodsService;
import com.ttsx.goods.Service.GoodsTypeService;
import com.ttsx.goods.Service.MemberinfoService;
import com.ttsx.goods.common.CustomException;
import com.ttsx.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-08 下午 3:13
 */
@RestController
@RequestMapping("goods")
@Slf4j
public class GoodController {
    @Autowired
    private GoodsTypeService goodsTypeService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private DiscussService discussService;

    @Autowired
    private MemberinfoService memberinfoService;

    @Autowired
    private RedisTemplate redisTemplate;
    //根据fid查询商品信息
    @RequestMapping("findById/{fid}")
    public R<Goodsinfo> findById(@PathVariable Integer fid){
        Goodsinfo good = goodsService.getById(fid);
        return R.success(good);

    }

    @PostMapping("showGoodsInformation")
    public R<Goodsinfo> showGoodsInformation(String Gno){
        LambdaQueryWrapper<Goodsinfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Goodsinfo::getGno,Gno);
        Goodsinfo goods = goodsService.getOne(lambdaQueryWrapper);

        return R.success(goods);
    }

    //展示商品信息
    @GetMapping("showGoodsInfo")
    public R<List<Goodsinfo>> selectGoodsInfo(){
        List<Goodsinfo> list = goodsService.list();

        return R.success(list);
    }

    //展示商品类型
    @GetMapping("showGoodsType")
    public R<List<Goodstype>> selectGoodsType(){
        List<Goodstype> list = goodsTypeService.list();

        return R.success(list);
    }

    //页面搜索查询，模糊查询商品并分页
    @PostMapping("findGoods")
    public R<Page> findGoods( String pageno, String goodsname, String pagesize){
        Page<Goodsinfo> page = new Page<>(Integer.parseInt(pageno),Integer.parseInt(pagesize),true);
        LambdaQueryWrapper<Goodsinfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Goodsinfo::getGname,goodsname);
        goodsService.page(page,lambdaQueryWrapper);

        return R.success(page);
    }

    //显示最新商品
    //String sql="select * from goodsinfo order by gno desc limit 0,2; ";
    @GetMapping("showNewGoods")
    public R<List<Goodsinfo>> showNewGoods(){
        LambdaQueryWrapper<Goodsinfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.orderByDesc(Goodsinfo::getGno)
                          .last(" limit 2 ");
        List<Goodsinfo> list = goodsService.list(lambdaQueryWrapper);

        return R.success(list);
    }

    //根据Gno查询分类下的商品信息
    @PostMapping("showGoodsTno")
    public R<Page<Goodsinfo>> showGoodsByTno( String pageno, String pagesize, String tno){
        Page<Goodsinfo> page = new Page<>(Integer.parseInt(pageno),Integer.parseInt(pagesize),true);
        LambdaQueryWrapper<Goodsinfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Goodsinfo::getTno,tno);
        goodsService.page(page,lambdaQueryWrapper);

        return R.success(page);
    }

    //删除评论
    //delete from discuss where did = ? and mno = ?
    @DeleteMapping("del")
    public R<String> del(String did){
        int mno= Integer.parseInt(redisTemplate.opsForValue().get("mno")+"");
        LambdaQueryWrapper<Discuss> lambdaQueryWrapper = new LambdaQueryWrapper<Discuss>();
        lambdaQueryWrapper.eq(Discuss::getDid,did)
                          .eq(Discuss::getMno,mno);
        boolean remove = discussService.remove(lambdaQueryWrapper);
        if(remove==false){
            throw new CustomException("删除失败");
        }

        return R.success("删除评论成功");
    }

    //根据商品id显示评论
    //" select * from discuss,memberinfo where gno=? and discuss.mno=memberinfo.mno;";
    @PostMapping("showDiscuss")
    public R<List<DiscussDto>> selectDiscuss(String Gno){
        LambdaQueryWrapper<Discuss> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Discuss::getGno,Gno);
        List<Discuss> list = discussService.list(lambdaQueryWrapper);
        List<DiscussDto> listDto = list.stream().map((item)->{
            DiscussDto discussDto = new DiscussDto();
            BeanUtils.copyProperties(item,discussDto);
            Memberinfo byId = memberinfoService.getById(item.getMno());
            discussDto.setNickName(byId.getNickName());
            return discussDto;
        }).collect(Collectors.toList());

        return R.success(listDto);
    }

    //评论
    //"insert into discuss(mno,gno,dis,publishtime) values(?,?,?,now()); "
    @PutMapping("addDiscuss")
    public R<String> addDiscuss(int Gno,@RequestParam(value = "discuss") String dis){
        Discuss discuss = new Discuss();
        int mno= Integer.parseInt(redisTemplate.opsForValue().get("mno")+"");
        discuss.setMno(mno);
        discuss.setGno(Gno);
        discuss.setDis(dis);
        discuss.setPublishtime(LocalDateTime.now().toString());
        discussService.save(discuss);

        return R.success("评论成功");
    }

}
