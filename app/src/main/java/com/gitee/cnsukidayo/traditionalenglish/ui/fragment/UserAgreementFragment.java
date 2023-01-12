package com.gitee.cnsukidayo.traditionalenglish.ui.fragment;

import android.os.Bundle;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.ui.markdown.plugin.MyMarkwonPlugin;

import io.noties.markwon.Markwon;

public class UserAgreementFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private Button button;
    private TextView markDownTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_user_agreement, container, false);
        }
        bindView();
        this.button.setOnClickListener(this);
        return rootView;
    }

    private void bindView() {
        this.button = rootView.findViewById(R.id.fragment_user_agreement_button);
        this.markDownTextView = rootView.findViewById(R.id.fragment_user_agreement_text_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_user_agreement_button:
                String markdownString = "# 一级标题\n" +
                        "## 二级标题\n" +
                        "### 三级标题\n" +
                        "### 三级标题\n" +
                        "#### 四级标题\n" +
                        "##### 五级标题\n" +
                        "###### 六级标题\n" +
                        "\n" +
                        "\n" +
                        "一级标题\n" +
                        "===\n" +
                        "\n" +
                        "二级标题\n" +
                        "---\n" +
                        "\n" +
                        "## 分割线:\n" +
                        "\n" +
                        "**粗体**\n" +
                        "\n" +
                        "*斜体*\n" +
                        "\n" +
                        "***斜体+粗体***\n" +
                        "\n" +
                        "~~删除线~~\n" +
                        "\n" +
                        "- - - \n" +
                        "[超链接](http://www.baidu.com)  \n" +
                        "[超链接+悬浮文字](http://www.baidu.com \"悬浮文字\")\n" +
                        "\n" +
                        "[超链接引用][1]  \n" +
                        "[超链接引用+悬浮文字][2]\n" +
                        "\n" +
                        "[1]:http://baidu.com\n" +
                        "[2]:http://baidu.com \"跳转百度\"\n" +
                        "\n" +
                        "\n" +
                        "![](https://www.baidu.com/img/flexible/logo/pc/result.png \"悬浮文字\")\n" +
                        "\n" +
                        "- - -\n" +
                        "\n" +
                        "## 有序列表:\n" +
                        "1. 一层\n" +
                        "   1. 二层\n" +
                        "   2. 二层\n" +
                        "2. 二层\n" +
                        "\n" +
                        "\n" +
                        "## 无序列表:\n" +
                        "+ 一层\n" +
                        "  - 二层\n" +
                        "  - 二层\n" +
                        "    * 三层\n" +
                        "      + 四层\n" +
                        "+ 一层\n" +
                        "\n" +
                        "## TodoList\n" +
                        "近期任务安排:\n" +
                        "- [x] 整理Markdown手册\n" +
                        "- [ ] 改善项目\n" +
                        "   - [x] 优化首页显示方式\n" +
                        "   - [x] 修复闪退问题\n" +
                        "   - [ ] 修复视频卡顿\n" +
                        "- [ ] A3项目修复\n" +
                        "   - [x] 修复数值错误\n" +
                        "\n" +
                        "- - -\n" +
                        "## 文字引用\n" +
                        "> 第一层\n" +
                        "> > 第二层\n" +
                        "> > > 第三层\n" +
                        "> \n" +
                        "> 跳出来\n" +
                        "\n" +
                        "- - - \n" +
                        "`行内代码块`\n" +
                        "\n" +
                        "## 代码块:\n" +
                        "```java\n" +
                        "public static void main(String args[]){\n" +
                        "    System.out.print(\"Hello World!\");\n" +
                        "}\n" +
                        "```\n" +
                        "\n" +
                        "- - -\n" +
                        "## 表格:\n" +
                        "| 商品 | 数量 |  单价  |\n" +
                        "| ---- | ---: | :----: |\n" +
                        "| 苹果 |   10 |  \\$1   |\n" +
                        "| 电脑 |    1 | \\$1000 |\n" +
                        "\n" +
                        "### 表格默认左对齐,第二行中的-可以是任意数量,默认是左对齐.\n" +
                        "### :--- `左对齐` \n" +
                        "### :--: `居中对齐` \n" +
                        "### ---: `右对齐` \n" +
                        "\n" +
                        "- - - \n" +
                        "## 转义符:\n" +
                        "\\*  \n" +
                        "\\*\\*  \n" +
                        "\\-  \n" +
                        "\\>  \n" +
                        "\\[\\]  \n" +
                        "\\(\\)  \n" +
                        "\n" +
                        "- - -\n" +
                        "## HTML标签:\n" +
                        "1. ### &nbsp;&nbsp;不断行的空白格\n" +
                        "    ### &ensp;&ensp;半方大的空白\n" +
                        "    ### &emsp;&emsp;全方大的空白\n" +
                        "2. ### 上标标签<sup>上标</sup>\n" +
                        "3. ### 上标标签<sub>下标</sub>\n" +
                        "4. ### <u>下划线</u>\n" +
                        "5. ### 换行标签<br>换行\n" +
                        "6. ### <kbd>Ctrl</kbd>+<kbd>Alt</kbd>+<kbd>Del</kbd> 键盘按键特效\n" +
                        "7. ### <font color=\"#dd0000\">文字着色</font>\n" +
                        "8. ### <p align=\"center\">文字居中显示</p>\n" +
                        "![图片](test.png)";
                final Markwon markwon = Markwon.builder(getContext()).usePlugin(MyMarkwonPlugin.getInstance(getContext())).build();
                final Spanned markdown = markwon.toMarkdown(markdownString);
                markwon.setParsedMarkdown(this.markDownTextView, markdown);
                break;
        }
    }
}