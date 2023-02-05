package com.gitee.cnsukidayo.anylanguageword.enums;

import com.gitee.cnsukidayo.anylanguageword.R;

/**
 * @author cnsukidayo
 * @date 2023/1/5 15:40
 */
public enum CreditState {

    LISTENING(R.string.listening_write_mode),
    ENGLISHTRANSLATIONCHINESE(R.string.english_translation_chinese),
    CHINESETRANSLATIONENGLISH(R.string.chinese_translation_english),
    CREDIT(R.string.only_credit);

    private int modeID;

    CreditState(int modeID) {
        this.modeID = modeID;
    }
}
