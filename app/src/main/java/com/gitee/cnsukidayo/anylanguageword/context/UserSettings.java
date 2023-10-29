package com.gitee.cnsukidayo.anylanguageword.context;

import io.github.cnsukidayo.wword.model.token.AuthToken;

public class UserSettings {

    /**
     * 是否已经同意用户协议
     */
    private boolean acceptUserAgreement;

    /**
     * 用户登录的token信息
     */
    private AuthToken authToken;

    public boolean isAcceptUserAgreement() {
        return acceptUserAgreement;
    }

    public void setAcceptUserAgreement(boolean acceptUserAgreement) {
        this.acceptUserAgreement = acceptUserAgreement;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
