package com.example.administrator.terrificweather.ui.about;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.administrator.terrificweather.R;

/**
 * Created by Administrator on 2016/4/25.
 */
public class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    private final String GITHUB = "github";
    private final String EMAIL = "email";

    private Preference pGithub;
    private Preference pEmail;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.aboutme);

        pGithub = findPreference(GITHUB);
        pEmail = findPreference(EMAIL);

        pGithub.setOnPreferenceClickListener(this);
        pEmail.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(pGithub == preference){
            copyAndPaste(getView(),pGithub.getSummary().toString());
        }else if(pEmail == preference){
            copyAndPaste(getView(),pEmail.getSummary().toString());
        }
        return false;
    }
    private void copyAndPaste(View view,String info){
        ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("msg",info);
        manager.setPrimaryClip(data);
        Snackbar.make(view,"复制成功",Snackbar.LENGTH_SHORT).show();
    }
}
