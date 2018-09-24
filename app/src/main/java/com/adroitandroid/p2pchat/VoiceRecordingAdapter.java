package com.adroitandroid.p2pchat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class VoiceRecordingAdapter extends RecyclerView.Adapter {


    public static class VoiceRecordingHolder extends RecyclerView.ViewHolder{
        public ImageView mPlayStop;
        public TextView mFileDir;
        public VoiceRecordingHolder(View itemView){
            super(itemView);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
