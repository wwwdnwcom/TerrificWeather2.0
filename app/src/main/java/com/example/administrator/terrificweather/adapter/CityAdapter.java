package com.example.administrator.terrificweather.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.terrificweather.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/26.
 */
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder>{
    private Context context;
    private ArrayList<String> dataList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public CityAdapter(Context context,ArrayList<String> dataList){
        this.context = context;
        this.dataList = dataList;
    }



    class CityViewHolder extends RecyclerView.ViewHolder{

        private TextView itemCity;
        private CardView cardView;

        public CityViewHolder(View itemView) {
            super(itemView);
            itemCity = (TextView) itemView.findViewById(R.id.item_city);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
        }
    }
    @Override
    public  int getItemCount(){
        return dataList.size();
    }

    public interface OnRecyclerViewItemClickListener{
        void onItemClick(View view,int pos);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
//        View view = LayoutInflater.from(context).inflate(R.layout.item_city,parent,false);
//        CityViewHolder holder = new CityViewHolder(view);
//        return holder;
        return new CityViewHolder(LayoutInflater.from(context).inflate(R.layout.item_city,parent,false));

    }
    @Override
    public void onBindViewHolder(final CityViewHolder holder,final int position){
        holder.itemCity.setText(dataList.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v,position);
            }
        });
    }

}
