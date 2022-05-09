package top.yehonghan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.yehonghan.entity.AddressBook;
import top.yehonghan.mapper.AddressBookMapper;
import top.yehonghan.service.AddressBookService;

/**
 * @Author yehonghan
 * @2022/5/4 21:33
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
