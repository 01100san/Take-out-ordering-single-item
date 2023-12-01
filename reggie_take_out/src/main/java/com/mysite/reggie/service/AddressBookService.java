package com.mysite.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysite.reggie.entity.AddressBook;

/**
 * ClassName: AddressBookService
 * Package: com.mysite.reggie.service
 * Description
 *
 * @Author zhl
 * @Create 2023/11/29 21:13
 * version 1.0
 */
public interface AddressBookService extends IService<AddressBook> {
    Boolean updateByIdWithDefault(Long id, int isDefault);
}
