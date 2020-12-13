package com.vimal.google;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vimal.google.Map.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder>{

    private CallbackInterface mCallback;
    private final String [] mMyItems;
    private final Context mContext;

    public interface CallbackInterface{

        /**
         * Callback invoked when clicked
         * @param position - the position
         * @param text - the text to pass back
         */
        void onHandleSelection(int position, String text);
    }

    public MyAdapter(Context context, String [] myItems){

        mContext = context;
        mMyItems = myItems;

        // .. Attach the interface
        try{
            mCallback = (CallbackInterface) context;
        }catch(ClassCastException ex){
            //.. should log the error or throw and exception
            Log.e("MyAdapter","Must implement the CallbackInterface in the Activity", ex);
        }
    }

    @Override
    public int getItemCount() {
        return mMyItems.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {

        // Set the text for the View
        holder.mTextView.setText(mMyItems[position]);

        // Use your listener to pass back the data
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback != null){
                    mCallback.onHandleSelection(position, mMyItems[position]);
                }
            }
        });
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.listitem, parent, false);
        return new MyHolder(view);
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        TextView mTextView;

        public MyHolder(View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.tv_list_item);
        }
    }
}