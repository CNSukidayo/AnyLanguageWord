package com.gitee.cnsukidayo.anylanguageword.ui.fragment.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.anylanguageword.R;

/**
 *
 */
public class AccountProfileFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private TextView title;
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
        rootView = inflater.inflate(R.layout.fragment_account_profile, container, false);
        bindView();
        this.title.setText(R.string.account_profile);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_back_to_trace:
                Navigation.findNavController(getView()).popBackStack();
                break;
        }

    }

    private void bindView() {
        this.title = rootView.findViewById(R.id.toolbar_title);
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.backToTrace.setOnClickListener(this);
    }

}