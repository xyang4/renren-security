package io.renren.modules.orders.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "订单列表分页")
public class OrderPageForm {
    @ApiModelProperty("订单类型")
    private Integer orderType;
    @ApiModelProperty("订单状态")
    private Integer orderState;
    @ApiModelProperty("页码")
    private Integer pageIndex;
    @ApiModelProperty("每页大小")
    private Integer pageSize;
    private Integer userId;
}
