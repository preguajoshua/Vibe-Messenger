package uic.capstone.p2pchat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mFileName = new ArrayList<>();
    private Context context;

    public RecyclerViewAdapter(Context context, ArrayList<String> mFileName) {
        this.mFileName = mFileName;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voice_record, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        holder.fileDirectory.setText(mFileName.get(position));
    }

    @Override
    public int getItemCount() {
        return mFileName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView fileDirectory;
        ImageButton PlayPause;
        public ViewHolder(View itemView) {
            super(itemView);
            PlayPause = (ImageButton) itemView.findViewById(R.id.btnPlayPause);
//            fileDirectory = (TextView) itemView.findViewById(R.id.fileDirectory);
        }
    }
}
