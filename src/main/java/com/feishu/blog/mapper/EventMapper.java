package com.feishu.blog.mapper;

import com.feishu.blog.dto.GetAbnormalEventDTO;
import com.feishu.blog.entity.AbnormalEvent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/24
 */
@Mapper
public interface EventMapper {
    int insertAbnormalEvent(AbnormalEvent abnormalEvent);

    /**
     * 获取异常事件列表，可以根据用户、事件类型、时间进行筛选
     * @param dto
     * @return
     */
    List<AbnormalEvent> selectAbnormalEvent(GetAbnormalEventDTO dto);
}
