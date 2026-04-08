package com.xywm.backend.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xywm.backend.common.Result;
import com.xywm.backend.entity.Address;
import com.xywm.backend.service.AddressService;
import com.xywm.backend.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public Result<List<Address>> getAddresses() {
        return Result.success(addressService.getUserAddresses(UserContext.getUserId()));
    }

    @PostMapping
    public Result<Void> addAddress(@RequestBody Address address) {
        Long userId = UserContext.getUserId();
        address.setUserId(userId);

        // 如果新增的是默认地址，先把该用户其他地址的默认取消
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefault(userId);
        }

        addressService.save(address);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateAddress(@RequestBody Address address) {
        Long userId = UserContext.getUserId();

        // 如果改成了默认地址，先把该用户其他地址的默认取消
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefault(userId);
        }

        addressService.updateById(address);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        addressService.removeById(id);
        return Result.success();
    }

    /**
     * 把某用户所有地址的默认标记清除
     */
    private void clearDefault(Long userId) {
        addressService.update(
                new LambdaUpdateWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .set(Address::getIsDefault, 0)
        );
    }
}