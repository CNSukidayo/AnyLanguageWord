package com.gitee.cnsukidayo.anylanguageword.enums;

import com.gitee.cnsukidayo.anylanguageword.R;

/**
 * 背诵格式
 *
 * @author cnsukidayo
 * @date 2023/2/9 20:09
 */
public enum CreditFormat {

    CLASSIC(R.string.classic), ASSOCIATION(R.string.association);

    private int modeID;

    CreditFormat(int modeID) {
        this.modeID = modeID;
    }

    public int getModeID() {
        return modeID;
    }
}
