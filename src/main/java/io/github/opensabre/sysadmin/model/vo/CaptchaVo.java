package io.github.opensabre.sysadmin.model.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CaptchaVo {
    @NonNull
    private String sceneId;
    @NonNull
    private String sendId;
}
