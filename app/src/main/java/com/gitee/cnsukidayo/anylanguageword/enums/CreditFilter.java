package com.gitee.cnsukidayo.anylanguageword.enums;

import com.gitee.cnsukidayo.anylanguageword.R;

/**
 * 背诵内容
 *
 * @author cnsukidayo
 * @date 2023/2/9 20:16
 */
public enum CreditFilter {
    WORD(R.string.word),PHRASE(R.string.concise_phrase);
    private int modeID;

    CreditFilter(int modeID) {
        this.modeID = modeID;
    }

    public int getModeID() {
        return modeID;
    }

}
