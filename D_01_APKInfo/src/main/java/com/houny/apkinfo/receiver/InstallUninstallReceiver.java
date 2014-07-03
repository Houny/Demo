package com.houny.apkinfo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.houny.apkinfo.Util.Utils;


/**
 * Created by Houny Chang on 2014/5/15.
 */
public class InstallUninstallReceiver extends BroadcastReceiver {
    Handler handler = new Handler();

    @Override
    public void onReceive(final Context context, Intent intent) {
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            final String packageName = intent.getDataString();

            handler.post(new Runnable() {
                @Override
                public void run() {
//                    if (Utils.hasShortcut(context, packageName))
                        Utils.addShortcutToHomeScreen(context, packageName);
                }
            });
            System.out.println("安装了:" + packageName + "包名的程序");
        }
        //接收卸载广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString();
            System.out.println("卸载了:" + packageName + "包名的程序");

        }
    }


}
