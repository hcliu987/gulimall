package com.hc.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 **/

@Data
public class FareVo {

    private MemberAddressVo address;

    private BigDecimal fare;

}


