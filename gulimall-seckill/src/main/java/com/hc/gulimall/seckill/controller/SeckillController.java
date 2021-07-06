package com.hc.gulimall.seckill.controller;

import com.hc.gulimall.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class SeckillController {
    @Autowired
    private SeckillService seckillService;
}
