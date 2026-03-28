package com.xywm.backend.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xywm.backend.common.BusinessException;
import com.xywm.backend.dto.OrderDTO;
import com.xywm.backend.entity.*;
import com.xywm.backend.mapper.OrderItemMapper;
import com.xywm.backend.mapper.OrdersMapper;
import com.xywm.backend.service.*;
import com.xywm.backend.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired private OrderItemMapper orderItemMapper;
    @Autowired private DishService dishService;
    @Autowired private CartService cartService;
    @Autowired private UserService userService;
    @Autowired private AddressService addressService;

    @Override
    @Transactional
    public String createOrder(Long userId, OrderDTO dto) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderDTO.OrderItemDTO item : dto.getItems()) {
            Dish dish = dishService.getById(item.getDishId());
            if (dish == null || dish.getStatus() == 0)
                throw new BusinessException("菜品【" + (dish == null ? item.getDishId() : dish.getName()) + "】已下架");
            total = total.add(dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        Address address = addressService.getById(dto.getAddressId());
        String addressSnapshot = address != null ?
                address.getName() + " " + address.getPhone() + " " + address.getAddress() : "";

        Orders order = new Orders();
        order.setOrderNo(IdUtil.fastSimpleUUID());
        order.setUserId(userId);
        order.setMerchantId(dto.getMerchantId());
        order.setAddressId(dto.getAddressId());
        order.setAddressSnapshot(addressSnapshot);
        order.setTotalAmount(total);
        order.setActualAmount(total);
        order.setStatus(1);
        order.setRemark(dto.getRemark());
        save(order);

        for (OrderDTO.OrderItemDTO item : dto.getItems()) {
            Dish dish = dishService.getById(item.getDishId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setDishId(item.getDishId());
            orderItem.setDishName(dish.getName());
            orderItem.setDishImage(dish.getImage());
            orderItem.setPrice(dish.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItemMapper.insert(orderItem);
        }

        cartService.clearCart(userId, dto.getMerchantId());
        return order.getOrderNo();
    }

    @Override
    public List<OrderVO> getUserOrders(Long userId) {
        List<Orders> orders = list(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getUserId, userId)
                .orderByDesc(Orders::getCreateTime));
        return orders.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<OrderVO> getMerchantOrders(Long merchantId) {
        List<Orders> orders = list(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getMerchantId, merchantId)
                .orderByDesc(Orders::getCreateTime));
        return orders.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        Orders order = new Orders();
        order.setId(orderId);
        order.setStatus(status);
        updateById(order);
    }

    private OrderVO toVO(Orders order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        String[] statusTexts = {"待付款","待接单","已接单","配送中","已完成","已取消"};
        vo.setStatusText(statusTexts[Math.min(order.getStatus(), 5)]);
        User merchant = userService.getById(order.getMerchantId());
        if (merchant != null) vo.setMerchantName(merchant.getNickname());
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        vo.setItems(items.stream().map(item -> {
            OrderVO.OrderItemVO itemVO = new OrderVO.OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList()));
        return vo;
    }
}