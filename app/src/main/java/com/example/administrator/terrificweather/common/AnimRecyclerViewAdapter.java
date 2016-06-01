package com.example.administrator.terrificweather.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.administrator.terrificweather.R;

/**
 * Created by Administrator on 2016/6/1.
 */
public class AnimRecyclerViewAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T>{

    private static final int DELAY = 100;
    private int mLastPosition = -1;

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(T holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void showCardAnim(final View view,final int position){
        final Context context = view.getContext();
        if(position > mLastPosition){
            view.setAlpha(0);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_down_toup);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            view.setAlpha(1);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    view.startAnimation(animation);
                }
            },DELAY * position);
            mLastPosition = position;
        }
    }
}
