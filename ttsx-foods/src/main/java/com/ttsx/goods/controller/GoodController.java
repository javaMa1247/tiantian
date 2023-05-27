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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    private RedisTemplate<String,String> redisTemplate;
    //根据fid查询商品信息
    @RequestMapping("findById/{fid}")
    public R<Goodsinfo> findById(@PathVariable Integer fid){
        Goodsinfo good = goodsService.getById(fid);
        return R.success(good);

    }

    //根据tno查询商品类型
    @RequestMapping("findTypeByTno")
    public R<Goodstype> findTypeByTno(Integer tno){
        Goodstype goodstype = goodsTypeService.getById(tno);
        return R.success(goodstype);

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
    public R<String> del(@RequestHeader String uid,String did){
        int mno= Integer.parseInt(uid);
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
    public R<String> addDiscuss(@RequestHeader String uid,int Gno,@RequestParam(value = "discuss") String dis){
        Discuss discuss = new Discuss();
        int mno= Integer.parseInt(uid);
        discuss.setMno(mno);
        discuss.setGno(Gno);
        discuss.setDis(dis);
        discuss.setPublishtime(LocalDateTime.now().toString());
        discussService.save(discuss);

        return R.success("评论成功");
    }

    //历史记录

    @PostMapping("setHistory")
    public R<String> setHistory (@RequestHeader String uid,HttpServletRequest request){

        String userId = uid;
        // 构造 Redis 键名
        String productId = request.getParameter("gno");
        // 将浏览记录添加到 ZSET 中，得分为当前时间戳
        String key = "user:" + userId + ":views";
        // 判断该用户是否已经浏览过该商品
        Double score = redisTemplate.opsForZSet().score(key, productId);
        if (score != null) {
            // 如果该商品已经浏览过，则将其得分更新为当前时间戳
            redisTemplate.opsForZSet().add(key, productId, System.currentTimeMillis());
        } else {
            // 如果该商品尚未浏览过，则将其添加到 ZSET 中，得分为当前时间戳
            redisTemplate.opsForZSet().add(key, productId, System.currentTimeMillis());
            // 只保留最新的 5 条浏览记录
            redisTemplate.opsForZSet().removeRange(key, 0, -6);
        }


        return R.success("添加成功");
    }
    @PostMapping("getHistory")
    public R<List<Goodsinfo>> getHistory (@RequestHeader String uid){
        String userId = uid;
        String key = "user:" + userId + ":views";
        Set<String> productIds = redisTemplate.opsForZSet().reverseRange(key, 0, 4);
        List<Goodsinfo> goodsinfos = new ArrayList<>();
        for (String id : productIds) {
            Goodsinfo goodsinfo = findById(Integer.parseInt(id)).getData();
            goodsinfos.add(goodsinfo);
            String tname = this.findTypeByTno(goodsinfo.getTno()).getData().getTname();
            goodsinfo.setTname(tname);
        }
        return R.success(goodsinfos);
    }

}
