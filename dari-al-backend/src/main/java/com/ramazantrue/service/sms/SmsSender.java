package com.ramazantrue.service.sms;

public interface SmsSender {
    public void send(String phone, String code);
}