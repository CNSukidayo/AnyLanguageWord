package com.gitee.cnsukidayo.traditionalenglish.test;

import com.gitee.cnsukidayo.traditionalenglish.entity.PostCover;

import java.util.Random;
import java.util.UUID;

/**
 * @author cnsukidayo
 * @date 2023/2/2 14:00
 */
public class BeanTest {
    private static Random random = new Random();

    public static PostCover createPostCover() {
        PostCover postCover = new PostCover();
        postCover.setPraiseCount(random.nextInt(1000000));
        postCover.setUserName(UUID.randomUUID().toString().substring(0, 8));
        postCover.setTitle(UUID.randomUUID().toString().substring(0, random.nextInt(36)));
        return postCover;
    }

}
