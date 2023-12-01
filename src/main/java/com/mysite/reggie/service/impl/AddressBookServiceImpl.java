package com.mysite.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysite.reggie.entity.AddressBook;
import com.mysite.reggie.mapper.AddressBookMapper;
import com.mysite.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClassName: AddressBookServiceImpl
 * Package: com.mysite.reggie.service.impl
 * Description
 *
 * @Author zhl
 * @Create 2023/11/29 21:13
 * version 1.0
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Override
    public Boolean updateByIdWithDefault(Long id, int isDefault) {
        return addressBookMapper.updateByIdWithDefault(id,isDefault);
    }
}
