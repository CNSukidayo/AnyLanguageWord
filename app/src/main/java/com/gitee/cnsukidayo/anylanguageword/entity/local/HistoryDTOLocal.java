package com.gitee.cnsukidayo.anylanguageword.entity.local;

import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;

import java.util.List;
import java.util.Set;

/**
 * @author cnsukidayo
 * @date 2024/7/16 23:05
 */
public class HistoryDTOLocal extends DivideDTOLocal {
    /**
     * 当前历史的文件路径
     */
    private String path;

    /**
     * 变色龙相关功能持久化
     */
    private List<Set<FlagColor>> flag;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Set<FlagColor>> getFlag() {
        return flag;
    }

    public void setFlag(List<Set<FlagColor>> flag) {
        this.flag = flag;
    }
}