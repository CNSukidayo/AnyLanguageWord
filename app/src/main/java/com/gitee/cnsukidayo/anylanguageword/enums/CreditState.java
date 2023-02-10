package com.gitee.cnsukidayo.anylanguageword.enums;

import com.gitee.cnsukidayo.anylanguageword.R;

/**
 * @author cnsukidayo
 * @date 2023/1/5 15:40
 */
public enum CreditState {

    ENGLISH_TRANSLATION_CHINESE(R.string.english_translation_chinese),
    CHINESE_TRANSLATION_ENGLISH(R.string.chinese_translation_english),
    LISTENING(R.string.listening_write_mode),
    CREDIT(R.string.only_credit);

    private int modeID;

    CreditState(int modeID) {
        this.modeID = modeID;
    }
}
