package com.rickie.ordercenter.esorder.service;

import java.util.Random;

public class Shop {

    public Integer sleep(){
        try {
            Thread.sleep(Constant.WORK_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Random().nextInt();
    }

}