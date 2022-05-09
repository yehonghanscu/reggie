package top.yehonghan.dto;

import lombok.Data;
import top.yehonghan.entity.OrderDetail;
import top.yehonghan.entity.Orders;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
