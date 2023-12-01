package com.mysite.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mysite.reggie.common.R;
import com.mysite.reggie.entity.AddressBook;
import com.mysite.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * ClassName: AddressBookController
 * Package: com.mysite.reggie.controller
 * Description
 *
 * @Author zhl
 * @Create 2023/11/29 21:15
 * version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 有前端需要接受的数据可知，返回的参数是 AddressBook ==> add-order.html 103行 返回address
     * 获取 is_default = 1的地址并且必须是当前登录用户的id
     * @param session 用来存储修改后的默认地址id
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> defaultAddress(HttpSession session){
        //通过session获取userId的值
        Long userId = (Long)session.getAttribute("user");
        //log.info("userId：{}",userId);
        //将is_default=1设为默认地址,按照 is_default=1 且 userId和登录用户 相同进行查询
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getIsDefault,1).eq(AddressBook::getUserId,userId);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        //把addressBook表中 id值添加在session对象中，方便后续修改默认地址
        session.setAttribute("defaultAddressId",addressBook.getId());
        //log.info(addressBook.toString());
        return R.success(addressBook);
    }

    /**
     * 获取所有的地址
     * 按照address_book表中的当前用户的user_id
     * @param session
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> addressList(HttpSession session){
        Long userId = (Long) session.getAttribute("user");
        log.info("当前需要查询的订单列表的user_id：{}",userId);
        //按照userId查询对应的订单地址列表
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        List<AddressBook> addressBooks = addressBookService.list(queryWrapper);
        return R.success(addressBooks);
    }

    /**
     * bug：无法动态修改----------------------------------------------------------
     * 这里的addressBook中只有 addressBook.getId() 只能获取到对应的id值，无法获取地址默认状态 也就是 is_default=null
     * session.getAttribute("defaultAddressId"); 可以获取对应的地址状态
     * @param addressBook address_book表对应的id值
     * @return
     */
    @Transactional
    @PutMapping("/default")
    public R<AddressBook> updateAddress(@RequestBody AddressBook addressBook,HttpSession session){
        //获取要修改成默认地址的id
        Long id = addressBook.getId();
        log.info("要修改默认地址：{}", id);
        //获取当前默认地址的id
        Long defaultAddressId = (Long) session.getAttribute("defaultAddressId");
        log.info("当前默认地址的id：{}",defaultAddressId);

        //修改当前的默认地址
        Boolean flag2 = addressBookService.updateByIdWithDefault(defaultAddressId, 0);
        //将id修改为默认地址
        Boolean flag1 = addressBookService.updateByIdWithDefault(id, 1);

        return flag2 && flag1 ?
                R.success(addressBookService.getById(id)) : R.error("无法修改默认地址😭");
    }

    /**
     * 查询单个地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getAddressBook(@PathVariable Long id){
        log.info("获取单个地址的id: {}",id);
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId,id);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }

    /**
     * 修改订单地址
     * @param addressBook 前端需要修改的订单
     * @return
     */
    @PutMapping
    public R<AddressBook> editAddressBook(@RequestBody AddressBook addressBook){
        return addressBookService.updateById(addressBook) ?
                R.success(addressBook) : R.error("修改订单地址失败😭");
    }

    /**
     * 新增订单地址
     * 还需要在新增订单中 设置userId字段  => 通过session对象获取
     * @param addressBook
     * @param session
     * @return
     */
    @PostMapping
    public R<String> saveAddressBook(@RequestBody AddressBook addressBook,HttpSession session){
        //获取当前登录用户的id，给当前登录用户新增订单地址 且 为addressBook表中的user_id字段赋值
        Long userId = (Long) session.getAttribute("user");
        log.info("需要新增的订单地址的用户id：{}",userId);
        log.info("新增的地址：{}",addressBook);
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success("添加成功😋");
    }

    /**
     * -----------------------------------
     * 存在bug => 删除addressBook时，会把当前地址设为默认地址
     * 根据addressBook表中 的id 和 is_default 删除订单地址
     * 删除的不能是默认地址
     * @param ids 对应的addressBook表中的id值
     * @return
     */
    @DeleteMapping
    public R<String> deleteAddressBook(@RequestParam Long ids){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getIsDefault,0).eq(AddressBook::getId,ids);
        boolean flag = addressBookService.remove(queryWrapper);

        return flag ? R.success("删除订单地址成功😔") : R.error("无法删除默认地址😮");
    }


}

