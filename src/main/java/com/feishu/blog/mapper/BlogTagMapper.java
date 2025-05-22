package com.feishu.blog.mapper;

import com.feishu.blog.entity.BlogTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/21
 */
@Mapper
public interface BlogTagMapper {
    /**
     * 为文章创建一个标签
     * @param blogTag
     * @return
     */
    int createBlogTag(BlogTag blogTag);

    /**
     * 查找id为blogId的文章是否存在标签tag
     * @param blogId
     * @param tag
     * @return
     */
    BlogTag selectBlogTagByBidAndTag(@Param("blogId")Integer blogId, @Param("tag") String tag);

    /**
     * 查找文章的所有tag
     * @param blogId
     * @return
     */
    List<BlogTag> selectBlogTagsByBid(@Param("blogId") Integer blogId);

    /**
     * 删除某一个文章的某个标签
     * @param id
     * @return
     */
    int deleteByPrimaryKey(@Param("id") Integer id);

    /**
     * 删除某一个文章的所有标签
     * @param blogId 文章Id
     * @return
     */
    int deleteByBlogId(@Param("blogId") Integer blogId);
}
