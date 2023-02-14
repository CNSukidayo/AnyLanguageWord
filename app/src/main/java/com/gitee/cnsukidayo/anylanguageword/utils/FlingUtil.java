package com.gitee.cnsukidayo.anylanguageword.utils;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.ViewConfiguration;

public class FlingUtil {

    private static final float INFLEXION = 0.35f;
    private static float mFlingFriction = ViewConfiguration.getScrollFriction();
    private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));

    private static double getSplineDeceleration(Context context,int velocity) {
        return Math.log(INFLEXION * Math.abs(velocity) / (mFlingFriction * getPhysicalCoeff(context)));
    }

    private static double getSplineDecelerationByDistance(Context context,double distance) {
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return decelMinusOne * (Math.log(distance / (mFlingFriction * getPhysicalCoeff(context)))) / DECELERATION_RATE;
    }

    private static double getPhysicalCoeff(Context context){
        float ppi = context.getResources().getDisplayMetrics().density * 160.0f;
        return SensorManager.GRAVITY_EARTH* 39.37f* ppi* 0.84f;
    }

    //通过初始速度获取最终滑动距离
    public static double getDistanceByVelocity(Context context,int velocity) {
        final double l = getSplineDeceleration(context,velocity);
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return mFlingFriction * getPhysicalCoeff(context) * Math.exp(DECELERATION_RATE / decelMinusOne * l);
    }


    //通过需要滑动的距离获取初始速度
    public static int getVelocityByDistance(Context context,double distance) {
        final double l = getSplineDecelerationByDistance(context,distance);
        int velocity = (int) (Math.exp(l) * mFlingFriction * getPhysicalCoeff(context) / INFLEXION);
        return Math.abs(velocity);
    }

}
