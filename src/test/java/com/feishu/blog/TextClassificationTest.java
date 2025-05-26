package com.feishu.blog;

import com.feishu.blog.dto.ClassificationDTO;
import com.feishu.blog.service.CozeTextService;
import com.feishu.blog.service.LocalTextService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class TextClassificationTest {
    @Resource
    private LocalTextService localTextService;
    @Resource
    private CozeTextService cozeTextService;

    @Test
    public void testLocal() {
        String content = "你好,我是你爹";
        ClassificationDTO response = localTextService.processContentCompliance(content);
        log.debug("{}", response);
    }

    @Test
    public void testCoze()  {
        String content = "你好，我是你爹";
        ClassificationDTO response = cozeTextService.processContentCompliance(content);
        log.debug("{}", response);
    }
}
