package io.github.opensabre.sysadmin.notification.provider;

public interface ISmsProvider {
    /**
     * 发送短信
     *
     * @param target   目标手机号
     * @param content  短信内容
     * @return 发送结果
     */
    String sendSms(String target, String content);
}