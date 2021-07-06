package com.hc.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 **/

@Data
public class MergeVo {

    private Long purchaseId;

    private List<Long> items;

}
