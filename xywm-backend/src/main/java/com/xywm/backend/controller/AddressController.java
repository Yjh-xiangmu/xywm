package com.xywm.backend.controller;

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
        address.setUserId(UserContext.getUserId());
        addressService.save(address);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateAddress(@RequestBody Address address) {
        addressService.updateById(address);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        addressService.removeById(id);
        return Result.success();
    }
}