package com.gitee.cnsukidayo.traditionalenglish.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.context.TraditionalEnglishProperties;
import com.gitee.cnsukidayo.traditionalenglish.factory.StaticFactory;
import com.gitee.cnsukidayo.traditionalenglish.ui.markdown.plugin.MyMarkwonPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.noties.markwon.Markwon;

/**
 * 用户协议界面
 */
public class UserAgreementFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private TextView markDownTextView, title;
    private Handler updateUIHandler;
    private AlertDialog loadingDialog = null;
    private ImageButton backToTrace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_user_information, container, false);
        updateUIHandler = new Handler();
        loadingDialog = loadingDialog = new AlertDialog.Builder(getContext()).setView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null)).setCancelable(false).show();
        bindView();
        // 展示标题信息
        this.title.setText(getResources().getString(R.string.fragment_user_information_user_agreement));
        showMarkDown();
        return rootView;
    }

    private void showMarkDown() {
        StaticFactory.getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                StringBuilder markdownOrigin = new StringBuilder();
                try {
                    String line = null;
                    reader = new BufferedReader(new FileReader(new File(TraditionalEnglishProperties.getExternalFilesDir(), TraditionalEnglishProperties.cacheRoot + File.separator + "userAgreement.md")));
                    while ((line = reader.readLine()) != null) {
                        markdownOrigin.append(line);
                        markdownOrigin.append("\n");
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final Markwon markwon = Markwon.builder(getContext()).usePlugin(MyMarkwonPlugin.getInstance(getContext())).build();
                final Spanned markdown = markwon.toMarkdown(markdownOrigin.toString());
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateUIHandler.post(() -> {
                    markwon.setParsedMarkdown(markDownTextView, markdown);
                    loadingDialog.dismiss();
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        Navigation.findNavController(getView()).popBackStack();
    }

    private void bindView() {
        this.markDownTextView = rootView.findViewById(R.id.fragment_user_information_markdown_context);
        this.title = rootView.findViewById(R.id.toolbar_title);
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.backToTrace.setOnClickListener(this);
    }

}