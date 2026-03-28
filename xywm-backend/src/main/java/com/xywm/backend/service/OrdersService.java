package com.xywm.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xywm.backend.dto.OrderDTO;
import com.xywm.backend.entity.Orders;
import com.xywm.backend.vo.OrderVO;
import java.util.List;

public interface OrdersService extends IService<Orders> {
    String createOrder(Long userId, OrderDTO dto);
    List<OrderVO> getUserOrders(Long userId);
    List<OrderVO> getMerchantOrders(Long merchantId);
    void updateOrderStatus(Long orderId, Integer status);
}