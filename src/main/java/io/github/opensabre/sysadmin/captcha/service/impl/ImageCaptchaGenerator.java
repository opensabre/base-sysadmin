package io.github.opensabre.sysadmin.captcha.service.impl;

import cn.hutool.core.util.RandomUtil;
import io.github.opensabre.sysadmin.captcha.enums.BusinessScenario;
import io.github.opensabre.sysadmin.captcha.model.po.ClientInfo;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaInfo;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaGenerator;
import io.github.opensabre.sysadmin.captcha.enums.CaptchaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.time.LocalDateTime;
import java.util.Base64;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Image captcha generator implementation
 */
@Slf4j
@Component
public class ImageCaptchaGenerator implements ICaptchaGenerator {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;

    @Override
    public CaptchaInfo generate(String businessKey, BusinessScenario scenario, ClientInfo clientInfo) {
        // Generate a random code
        String code = RandomUtil.randomString(scenario.getCaptchaLength());
        // Generate the captcha image
        String imageData = generateCaptchaImage(code);
        log.info("Generated image captcha for businessKey: {}, scenario: {}", businessKey, scenario.getCode());
        return CaptchaInfo.builder()
                .businessKey(businessKey)
                .businessScenario(scenario)
                .captchaType(scenario.getType())
                .clientInfo(clientInfo)
                .code(code)
                .data("data:image/png;base64," + imageData)
                .expireTime(LocalDateTime.now().plusSeconds(scenario.getCaptchaExpireTime()))
                .build();
    }

    @Override
    public String getType() {
        return CaptchaType.IMAGE.getCode();
    }

    /**
     * Generate a captcha image with distortion and noise
     *
     * @param code The captcha code to represent
     * @return Base64 encoded image string
     */
    private String generateCaptchaImage(String code) {
        // Create buffered image
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        try {
            // Set background color
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // Add noise
            Random random = new Random();
            for (int i = 0; i < 100; i++) {
                int x = random.nextInt(WIDTH);
                int y = random.nextInt(HEIGHT);
                g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                g.drawOval(x, y, 2, 2);
            }

            // Draw captcha text with distortion
            g.setFont(new Font("Arial", Font.BOLD, 20));
            for (int i = 0; i < code.length(); i++) {
                char c = code.charAt(i);
                g.setColor(new Color(random.nextInt(128), random.nextInt(128), random.nextInt(128)));

                // Add some distortion to each character
                int x = 15 + i * 15 + random.nextInt(5);
                int y = 25 + random.nextInt(10) - 5;

                // Rotate the character slightly
                g.rotate(random.nextDouble() * 0.2 - 0.1, x, y);
                g.drawString(String.valueOf(c), x, y);
                g.rotate(-(random.nextDouble() * 0.2 - 0.1), x, y); // Reset rotation for next character
            }

            // Draw some interference lines
            for (int i = 0; i < 5; i++) {
                g.setColor(new Color(random.nextInt(128), random.nextInt(128), random.nextInt(128)));
                int x1 = random.nextInt(WIDTH);
                int y1 = random.nextInt(HEIGHT);
                int x2 = random.nextInt(WIDTH);
                int y2 = random.nextInt(HEIGHT);
                g.drawLine(x1, y1, x2, y2);
            }

            // Finalize the image
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.dispose();

            // Convert to base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("Error generating captcha image", e);
            // Return a placeholder if image generation fails
            return Base64.getEncoder().encodeToString(("IMAGE_" + code).getBytes());
        }
    }
}