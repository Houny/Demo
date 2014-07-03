package com.houny.apkinfo.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.houny.apkinfo.R;
import com.houny.apkinfo.Util.DebugLog;
import com.houny.apkinfo.Util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Houny on 2014/6/5.
 */
public class LocalAppFragment extends Fragment {
    private static final int MSG_OK = 0001;
    private ListView listViewLV;
    private ProgressBar progressBar;
    LocalAppAdapter adapter;
    private ApplicationInfo selectedAppInfo;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    setListsAdapter((List<ApplicationInfo>) msg.obj);
                    break;
            }
        }
    };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_local_app, container, false);
            listViewLV = (ListView) rootView.findViewById(R.id.frag_local_app_list_LV);
            progressBar = (ProgressBar) rootView.findViewById(R.id.frag_local_app_progress_Pro);
            getLocalApps();
            setViewsListener();
            return rootView;
        }

        private void setViewsListener() {
            listViewLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    DebugLog.e("Item Click");
                    try {
                    ApplicationInfo applicationInfo = adapter.getItem(position);
                        Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName);
                        getActivity().startActivity(launchIntent);
                    } catch (Exception e) {
                        DebugLog.e(e);
                    }


                }
            });
        }

        private void getLocalApps() {
            new Thread() {
                @Override
                public void run() {
                    List<ApplicationInfo> appList = Utils.getLocalApp(getActivity());
                    Message msg = new Message();
                    msg.what = MSG_OK;
                    msg.obj = appList;
                    DebugLog.e(appList.toString());
                    handler.sendMessage(msg);
                }
            }.start();
        }

        private void setListsAdapter(List<ApplicationInfo> list) {
            adapter = new LocalAppAdapter(getActivity(), list);
            listViewLV.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
            listViewLV.setVisibility(View.VISIBLE);
        }







        class LocalAppAdapter extends BaseAdapter {

            private List<ApplicationInfo> appList;
            private Context context;

            public LocalAppAdapter(Context context) {
                this.context = context;
            }

            public LocalAppAdapter(Context context, List<ApplicationInfo> appList) {
                this.appList = appList;
                this.context = context;
            }

            public void addItem(ApplicationInfo applicationInfo) {
                if (null != this.appList) {
                    if (!this.appList.contains(applicationInfo)) {
                        this.appList.add(this.appList.size(), applicationInfo);
                    }
                } else {
                    this.appList = new ArrayList<ApplicationInfo>();
                    this.appList.add(applicationInfo);
                }
            }

            @Override
            public int getCount() {
                if (null != appList)
                    return appList.size();
                else
                    return 0;
            }

            @Override
            public ApplicationInfo getItem(int position) {
                if (null != appList)
                    return appList.get(position);
                else
                    return null;
            }

            @Override
            public long getItemId(int position) {
                if (null != appList)
                    return position;
                else
                    return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ApplicationInfo appInfo = appList.get(position);
                ViewHolder viewHolder;
                if (null != convertView) {
                    viewHolder = (ViewHolder) convertView.getTag();
                } else {
                    viewHolder = new ViewHolder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_local_app, parent, false);
                    viewHolder.appImage = (ImageView) convertView.findViewById(R.id.item_local_app_pic_Image);
                    viewHolder.appName = (TextView) convertView.findViewById(R.id.item_local_app_title_TV);
                    viewHolder.appVersion = (TextView) convertView.findViewById(R.id.item_local_app_version_TV);
                    viewHolder.saveBtn = (Button) convertView.findViewById(R.id.item_local_app_download_Btn);
                    viewHolder.appPackage =(TextView)convertView.findViewById(R.id.item_local_app_package_TV);

                    convertView.setTag(viewHolder);
                }
                viewHolder.appImage.setImageDrawable(appInfo.loadIcon(context.getPackageManager()));
                viewHolder.appName.setText(appInfo.loadLabel(context.getPackageManager()).toString());
                viewHolder.appPackage.setText(appInfo.packageName);
                try {
                    final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(appInfo.packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
                    if (null != packageInfo) {
                        viewHolder.appVersion.setText(packageInfo.versionName + "");
                    }

                    viewHolder.saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.addShortcutToHomeScreen(getActivity(),appInfo.processName);
                        }
                    });
                } catch (Exception e) {
                    DebugLog.e(e);
                }


                return convertView;
            }

           private class ViewHolder {
                private ImageView appImage;
                private TextView appName;
                private TextView appVersion;
                private TextView appPackage;
                private Button saveBtn;
            }
        }

    }

