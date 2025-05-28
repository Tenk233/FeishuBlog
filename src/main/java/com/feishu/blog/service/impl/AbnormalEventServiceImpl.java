package com.feishu.blog.service.impl;

import com.feishu.blog.dto.GetAbnormalEventDTO;
import com.feishu.blog.entity.AbnormalEvent;
import com.feishu.blog.exception.BizException;
import com.feishu.blog.mapper.EventMapper;
import com.feishu.blog.service.AbnormalEventService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/24
 */
@Slf4j
@Service             // 声明为 Bean
@RequiredArgsConstructor
public class AbnormalEventServiceImpl implements AbnormalEventService {

    @Resource
    private EventMapper eventMapper;

    @Override
    public int addAbnormalEvent(AbnormalEvent abnormalEvent) {
        if (eventMapper.insertAbnormalEvent(abnormalEvent) != 1) {
            throw new BizException(BizException.INTERNAL_ERROR, "插入异常事件失败");
        }
        return 0;
    }

    @Override
    public List<AbnormalEvent> getAbnormalEvents(GetAbnormalEventDTO dto) {
        if (dto.getType() != null && dto.getType() != AbnormalEvent.EVENT_ABNORMAL_LOGIN) {
            dto.setBlogId(null);
        }
        int page = 0;
        int limit = 20;
        if (dto.getPage() != null) {
            page = dto.getPage() - 1;
        }
        if (dto.getLimit() != null) {
            limit = dto.getLimit();
            if (limit < 0) {
                limit = -1;
            }
        }
        dto.setPage(page * limit);
        dto.setLimit(limit);
        return eventMapper.selectAbnormalEvent(dto);
    }
}
