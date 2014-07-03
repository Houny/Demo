package com.houny.shortcut.Util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

/**
 * Created by Houny Chang on 2014/5/15.
 */
public class Utils {

    /**
     * 检测快捷方式是否已经创建
     * @param context
     * @param pkg
     * @return
     */
    public static boolean hasShortcut(Context context,String pkg) {
        boolean isInstallShortcut = false;
        String packageName =pkg;
        if(packageName.startsWith("package:")){
            packageName =packageName.replace("package:","");
        }
        try {
            PackageManager mPackageManager = context.getPackageManager();
            //通过PackageName可以获取 ApplicationInfo对象和PackageInfo对象
            ApplicationInfo mApplicationInfo = mPackageManager.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            String mAppName = mApplicationInfo.loadLabel(mPackageManager).toString();

            final ContentResolver cr = context.getContentResolver();
            final String AUTHORITY = "com.android.launcher.settings";
            final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
            Cursor c = cr.query(CONTENT_URI, new String[]{"title", "iconResource"}, "title=?",
                    new String[]{mAppName}, null);
            if (c != null && c.getCount() > 0) {
                isInstallShortcut = true;
            }
        }catch (Exception e){
            e.printStackTrace();
            isInstallShortcut = false;
        }
        return isInstallShortcut;
    }

    /**
     * 为程序创建桌面快捷方式
     */
    public static boolean addShortcutToHomeScreen(Context context, String pkg) {
        String packageName =pkg;
        try {
            if(packageName.startsWith("package:")){
                packageName =packageName.replace("package:","");
            }
            PackageManager mPackageManager = context.getPackageManager();
            //通过PackageName可以获取 ApplicationInfo对象和PackageInfo对象
            ApplicationInfo mApplicationInfo = mPackageManager.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            String mAppName = mApplicationInfo.loadLabel(mPackageManager).toString();



            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,mAppName);//快捷方式的名称
            shortcut.putExtra("duplicate", false); //不允许重复创建

            //设置点击快捷方式点击的Intent
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            String className = launchIntent.getComponent().getClassName();
            if(null==className){
                return false;
            }
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent);

            //快捷方式的图标
            Drawable iconDrawable = mApplicationInfo.loadIcon(mPackageManager);
            BitmapDrawable bd =(BitmapDrawable)iconDrawable;
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bd.getBitmap());

            //发送广播，让系统创建快捷方式
            context.sendBroadcast(shortcut);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
