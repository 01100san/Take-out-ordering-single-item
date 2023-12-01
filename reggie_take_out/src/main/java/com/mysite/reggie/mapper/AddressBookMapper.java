package com.mysite.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysite.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * ClassName: AddressBookMapper
 * Package: com.mysite.reggie.mapper
 * Description
 *
 * @Author zhl
 * @Create 2023/11/29 21:14
 * version 1.0
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
    @Update("update address_book set is_default = #{isDefault} where id = #{id}")
    Boolean updateByIdWithDefault(@Param("id") Long id, @Param("isDefault") int isDefault);
}
