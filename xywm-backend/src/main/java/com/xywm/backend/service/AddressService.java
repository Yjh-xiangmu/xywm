package com.xywm.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xywm.backend.entity.Address;
import java.util.List;

public interface AddressService extends IService<Address> {
    List<Address> getUserAddresses(Long userId);
}