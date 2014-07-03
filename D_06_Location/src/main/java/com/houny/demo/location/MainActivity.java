package com.houny.demo.location;

import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class MainActivity extends ActionBarActivity {

    private int mScanSpan = 150000;//定位间隔时间ms 不要调太低，电池消耗、发热等问题太严重
    private LocationMode mLocationMode  = LocationMode.Hight_Accuracy;//定位模式，高精准度
    private boolean mIsNeedAddress =true;//地理位置信息
    private String mCoordType = "bd09ll";//经纬度编码
    private boolean mIsNeedDirection = true;//是否需要方向


    public LocationClient mLocationClient;
    public GeofenceClient mGeofenceClient;
    private MyLocationListener mMyLocationListener;
    public TextView mLocationResult;
    public Vibrator mVibrator;

    private TextView showInfoTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showInfoTV = (TextView)findViewById(R.id.main_show_TV);

        initBaiduLocation();
        setLocationOption();//设置定位所用的参数
        mLocationClient.start();//开始定位
    }

    private void initBaiduLocation() {
        mLocationClient = new LocationClient(this);
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);

        mGeofenceClient = new GeofenceClient(this);
        mVibrator = (Vibrator) getApplicationContext().getSystemService(
                Service.VIBRATOR_SERVICE);
    }

    //设置Option
    private void setLocationOption() {
        try {
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(mLocationMode);
            option.setCoorType(mCoordType);
            option.setScanSpan(mScanSpan);
            option.setNeedDeviceDirect(mIsNeedDirection);
            option.setIsNeedAddress(mIsNeedAddress);
            mLocationClient.setLocOption(option);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //TODO 这里就可以向服务器传了，上传的时候要用新线程
            // 而且要弄个队列，因为如果你上面轮询时间设置的太小
            //可能手机端还没传完呢，有开始掉接口再传了
            //最好是设计一个文件或者其他的什么，把每次定位的结果记录下来
            //根据网络环境和时间间隔，一次把这些多次定位的数据全传上去
            String locTypeStr ="";
            switch (location.getLocType()) {
                case 61:
                    locTypeStr = "\nGPS定位结果";
                    break;
                case 62:
                    locTypeStr = "\n定位失败，结果无效";
                    break;
                case 63:
                    locTypeStr = "\n网络异常,结果无效";
                    break;
                case 65:
                    locTypeStr = "\n定位缓存的结果";
                    break;
                case 66:
                    locTypeStr = "\n离线定位结果";
                    break;

                case 67:
                    locTypeStr = "\n离线定位失败";
                    break;
                case 68:
                    locTypeStr = "\n网络连接失败，离线定位结果";
                    break;
                case 161:
                    locTypeStr = "\n网络定位结果";
                    break;
                case 162:
                case 163:
                case 164:
                case 165:
                case 166:
                case 167:
                    locTypeStr = "\n百度服务不给力";
                    break;
                case 502:
                    locTypeStr="\nKey参数错误，请联系开发者配置正确Key";
                    break;
                case 505:
                    locTypeStr="\nKey不存在或非法，请联系开发者配置正确Key";
                    break;
                case 601:
                    locTypeStr="\n相关服务未开启，请联系开发者配置";
                    break;
                case 602:
                    locTypeStr="\nKey mcode不匹配，请联系开发者配置正确Key";
                    break;
                default:
                    locTypeStr ="\nCode: "+ location.getLocType()+" 请查询常见问题说明";
                    break;

            }
            StringBuffer sb = new StringBuffer(256);
            sb.append("时间 : ");
            sb.append(location.getTime());

            sb.append(locTypeStr);

            sb.append("\n维度 : ");
            sb.append(location.getLatitude());
            sb.append("\n经度 : ");
            sb.append(location.getLongitude());
            sb.append("\n范围 : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\n速度 : ");
                sb.append(location.getSpeed());
                sb.append("\n卫星 : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\n方向 : ");
                sb.append(location.getDirection());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\n地址 : ");
                sb.append(location.getAddrStr());
                // 运营商信息
                sb.append("\n运营商 : ");
                if(location.getOperators()==BDLocation.OPERATORS_TYPE_MOBILE){
                    sb.append("移动");
                }else if(location.getOperators()==BDLocation.OPERATORS_TYPE_TELECOMU){
                    sb.append("电信");
                }else if(location.getOperators()==BDLocation.OPERATORS_TYPE_UNICOM){
                    sb.append("联通");
                }else{
                    sb.append("未知");
                }

            }
            logMsg(sb.toString());
            Log.i("BaiduLocationApiDem", sb.toString());
        }

        @Override
        public void onReceivePoi(BDLocation arg0) {

        }

        /**
         * 显示请求字符串
         *
         * @param str
         */
        public void logMsg(String str) {
            try {
                showInfoTV.setText(str);
               Log.e("Result:",str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
