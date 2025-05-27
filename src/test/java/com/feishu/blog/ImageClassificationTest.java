package com.feishu.blog;

import com.feishu.blog.dto.ClassificationDTO;
import com.feishu.blog.service.CozeImageService;
import com.feishu.blog.service.LocalImageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;


@Slf4j
@SpringBootTest
public class ImageClassificationTest {
    @Resource
    private LocalImageService localImageService;

    @Resource
    private CozeImageService cozeImageService;

    // @Test
    public void test() throws IOException {
//         MultipartFile file = fileToMultipartFile("/Users/wlupus/Downloads/Cat_August_2010-3.jpg");
//        MultipartFile file = fileToMultipartFile("/Users/wlupus/Downloads/dog.jpeg");
//        MultipartFile file = fileToMultipartFile("/Users/wlupus/Downloads/24.png");
//        MultipartFile file = fileToMultipartFile("/Users/wlupus/Downloads/25.png");
//        MultipartFile file = fileToMultipartFile("/Users/wlupus/Downloads/illust_64841566_20211014_160931.jpg");
//        byte[] imageData = file.getBytes();
        try {
            var path = "/Users/wlupus/Downloads/25.png";
            ClassificationDTO response = localImageService.classifyImageWithPath(path);
            log.debug("{}", response);
        } catch (Exception e) {
        }
    }

    // @Test
    public void testCoze() throws IOException {
//        MultipartFile file = fileToMultipartFile("/Users/wlupus/Downloads/dog.jpeg");
//        MultipartFile file = fileToMultipartFile("/Users/wlupus/Downloads/24.png");
//        MultipartFile file = fileToMultipartFile("/Users/wlupus/Downloads/illust_64841566_20211014_160931.jpg");
//        byte[] imageData = file.getBytes();
        var path = "/Users/wlupus/Downloads/25.png";
        ClassificationDTO response = cozeImageService.processImageComplianceWithPath(path);
        log.debug("{}", response);
    }
}
