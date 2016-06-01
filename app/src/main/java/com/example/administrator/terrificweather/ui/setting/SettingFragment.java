package com.example.administrator.terrificweather.ui.setting;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.terrificweather.common.AutoUpdateService;
import com.example.administrator.terrificweather.common.BaseApplication;
import com.example.administrator.terrificweather.common.FileSizeUtil;
import com.example.administrator.terrificweather.R;
import com.example.administrator.terrificweather.common.ACache;

/**
 * Created by Administrator on 2016/4/25.
 */
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{
    private Setting mSetting;
    private Preference changeIcon;
    private Preference changeUpdate;
    private Preference clearCache;
    private SwitchPreference notificationType;

    private ACache aCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        mSetting = Setting.getsInstance();
        aCache = ACache.get(getActivity());

        changeIcon = findPreference(Setting.CHANGE_ICONS);
        changeUpdate = findPreference(Setting.AUTO_UPDATE);
        clearCache = findPreference(Setting.CLEAR_CACHE);

        changeIcon.setSummary(getResources().getStringArray(R.array.icons)[mSetting.getInt(Setting.CHANGE_ICONS , 0)]);

        changeUpdate.setSummary(mSetting.getAutoUpdate()==0?"禁止刷新":"每"+mSetting.getAutoUpdate()+"小时更新");
        clearCache.setSummary(FileSizeUtil.getAutoFileOrFilesSize(BaseApplication.cacheDir + "/Data"));

        changeIcon.setOnPreferenceClickListener(this);
        changeUpdate.setOnPreferenceClickListener(this);
        clearCache.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(changeIcon == preference){
            showIconDialog();
        }
        else if(changeUpdate == preference){
            showUpdateDialog();

        }else if(clearCache == preference){
            aCache.clear();
            Glide.get(getActivity()).clearMemory();
            clearCache.setSummary(FileSizeUtil.getAutoFileOrFilesSize(BaseApplication.cacheDir + "/Data"));
            Snackbar.make(getView(),"缓存已清除",Snackbar.LENGTH_SHORT).show();

        }



        return false;
    }

    private void showIconDialog(){
        new AlertDialog.Builder(getActivity()).setTitle("更换图标")
                .setSingleChoiceItems(getResources().getStringArray(R.array.icons),
                        mSetting.getInt(Setting.CHANGE_ICONS, 0),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which != mSetting.getInt(Setting.CHANGE_ICONS, 0)){
                                    mSetting.setIconType(which);
                                }
                                dialog.dismiss();
                                changeIcon.setSummary(getResources().getStringArray(R.array.icons)[mSetting.getIconType()]);
                                Snackbar.make(getView(),"更换图标成功，重启软件生效",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                ).show();
    }

    private void showUpdateDialog(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.update_dialog, (ViewGroup) getActivity().findViewById(R.id.dialog_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(dialogLayout);

        final AlertDialog alertDialog =  builder.create();
        alertDialog.show();

        final SeekBar mSeekBar = (SeekBar) dialogLayout.findViewById(R.id.time_seekbar);
        final TextView tvShowHour = (TextView) dialogLayout.findViewById(R.id.tv_showhour);
        TextView tvDone = (TextView) dialogLayout.findViewById(R.id.done);

        mSeekBar.setMax(24);
        mSeekBar.setProgress(mSetting.getAutoUpdate());
        tvShowHour.setText("每"+mSeekBar.getProgress()+"小时");
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvShowHour.setText("每"+progress+"小时");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetting.setAutoUpdate(mSeekBar.getProgress());
                changeUpdate.setSummary(mSetting.getAutoUpdate()==0?"禁止刷新":"每"+mSetting.getAutoUpdate()+"小时更新");
                getActivity().startService(new Intent(getActivity(), AutoUpdateService.class));
                alertDialog.dismiss();
            }
        });

    }
}
