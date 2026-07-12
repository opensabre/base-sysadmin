package io.github.opensabre.sysadmin.notification.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class SimulateSmsProvider implements ISmsProvider {

    /**
     * 模拟短信发送
     * 在实际实现中，这里会调用短信服务商的API
     *
     * @param target   目标手机号
     * @param content     短信内容
     * @return 消息ID
     */
    public String sendSms(String target, String content) {
        // 这里应该是实际的短信发送逻辑
        // 比如调用阿里云、腾讯云等短信服务提供商的API
        log.info("Simulating SMS sending to {} with content: {}", target, content);
        // 模拟发送成功
        return UUID.randomUUID().toString();
    }
}