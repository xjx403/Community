package com.coder.community;

import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.dao.UserMapper;
import com.coder.community.dao.elasticsearch.DiscussPostRepository;
import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.User;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Autowired
    private UserMapper userMapper;
    @Test
    public void testInsert(){
        DiscussPost save = discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        System.out.println(save);
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList() {
        for (int i = 109; i < 295; i++) {
            DiscussPost post = discussPostMapper.selectDiscussPostById(i);
            if (post != null) {
                discussPostRepository.save(post);
            }
        }
    }

    @Test
    public void testUpdate() {

    }

    @Test
    public void testDelete() {
        discussPostRepository.deleteAll();
    }

    @Test
    public void testSearchByRepository() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网","title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("<em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("<em>")
                ).build();
        Page<DiscussPost> page = discussPostRepository.search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        for (DiscussPost post: page) {
            System.out.println(post);
        }
    }
}
