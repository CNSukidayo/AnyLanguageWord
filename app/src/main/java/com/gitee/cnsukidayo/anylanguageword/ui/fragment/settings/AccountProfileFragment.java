package com.gitee.cnsukidayo.anylanguageword.ui.fragment.settings;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;

import io.github.cnsukidayo.wword.common.request.RequestRegister;
import io.github.cnsukidayo.wword.common.request.implement.core.UserRequestUtil;
import io.github.cnsukidayo.wword.model.dto.UserProfileDTO;

/**
 *
 */
public class AccountProfileFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private TextView title, userName, describeInfo, sex, birthday, university, uuid;
    private ImageButton backToTrace;
    private Handler updateUIHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            initView();
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_account_profile, container, false);
        bindView();
        initView();
        this.title.setText(R.string.account_profile);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_to_trace:
                Navigation.findNavController(getView()).popBackStack();
                break;
        }

    }

    private void bindView() {
        this.title = rootView.findViewById(R.id.toolbar_title);
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.userName = rootView.findViewById(R.id.fragment_account_user_name);
        this.describeInfo = rootView.findViewById(R.id.fragment_account_describe_info);
        this.sex = rootView.findViewById(R.id.fragment_account_sex);
        this.birthday = rootView.findViewById(R.id.fragment_account_birthday);
        this.university = rootView.findViewById(R.id.fragment_account_university);
        this.uuid = rootView.findViewById(R.id.fragment_account_uuid);

        this.backToTrace.setOnClickListener(this);
    }

    private void initView() {
        if (RequestRegister.getAuthToken().getAccessToken() != null) {
            StaticFactory.getExecutorService().execute(() -> UserRequestUtil.getProfile()
                    .success(data -> {
                        UserProfileDTO userProfile = data.getData();
                        updateUIHandler.post(() -> {
                            userName.setText(userProfile.getNick());
                            describeInfo.setText(userProfile.getDescribeInfo());
                            sex.setText(userProfile.getSex().toString());
                            birthday.setText(userProfile.getBirthday().toString());
                            university.setText(userProfile.getUniversity());
                            uuid.setText(String.valueOf(userProfile.getUuid()));
                        });
                    })
                    .execute());
        }
    }

}