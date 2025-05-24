package com.feishu.blog.service;

import com.feishu.blog.dto.GetAbnormalEventDTO;
import com.feishu.blog.entity.AbnormalEvent;

import java.util.List;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/24
 */
public interface AbnormalEventService {
    int addAbnormalEvent(AbnormalEvent abnormalEvent);
    List<AbnormalEvent> getAbnormalEvents(GetAbnormalEventDTO dto);
}
