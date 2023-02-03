package com.gitee.cnsukidayo.traditionalenglish.test;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.entity.PostCover;
import com.gitee.cnsukidayo.traditionalenglish.entity.UserInfo;

import java.util.Random;
import java.util.UUID;

/**
 * @author cnsukidayo
 * @date 2023/2/2 14:00
 */
public class BeanTest {
    private static Random random = new Random();
    private static UserInfo userInfo;

    public static PostCover createPostCover() {
        PostCover postCover = new PostCover();
        postCover.setPraiseCount(random.nextInt(1000000));
        postCover.setUserName(UUID.randomUUID().toString().substring(0, 8));
        postCover.setTitle(UUID.randomUUID().toString().substring(0, random.nextInt(36)));
        return postCover;
    }

    public static UserInfo createUserInfo() {
        if (userInfo == null) {
            userInfo = new UserInfo();
            int position = random.nextInt(4);
            if (position % 4 == 0) {
                userInfo.setUserFacePathID(R.drawable.post_cover0);
            } else if (position % 4 == 1) {
                userInfo.setUserFacePathID(R.drawable.post_cover1);
            } else if (position % 4 == 2) {
                userInfo.setUserFacePathID(R.drawable.post_cover2);
            } else {
                userInfo.setUserFacePathID(R.drawable.post_cover3);
            }
            userInfo.setUserName(UUID.randomUUID().toString().substring(0, 8));
            userInfo.setUserLevel(random.nextInt(5) + 1);
            userInfo.setVipLevel(random.nextInt(5) + 1);
            userInfo.setMoney(random.nextInt(1000000));
        }
        return userInfo;
    }

}
