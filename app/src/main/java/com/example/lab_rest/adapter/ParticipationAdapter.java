package com.example.lab_rest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.R;
import com.example.lab_rest.model.Participation;


import java.util.List;

public class ParticipationAdapter extends RecyclerView.Adapter<ParticipationAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvTitle;
        public TextView tvDate;
        public TextView tvParticipationDate;
        public TextView tvReturnDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvParticipationDate = itemView.findViewById(R.id.tvParticipationDate);

            itemView.setOnLongClickListener(this);  //register long click action to this viewholder instance
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition(); //key point, record the position here
            return false;
        }
    } // close ViewHolder class

    //////////////////////////////////////////////////////////////////////
    // adapter class definitions

    private List<Participation> participationListData;   // list of borrow objects
    private Context mContext;       // activity context
    private int currentPos;         // currently selected item (long press)

    public ParticipationAdapter(Context context, List<Participation> listData) {
        participationListData = listData;
        mContext = context;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate layout using the single item layout
        View view = inflater.inflate(R.layout.participation_list_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder instance
        Participation b = participationListData.get(position);
        holder.tvTitle.setText(b.getEvent().getEvent_name());
        holder.tvDate.setText("Event Date: " + b.getEvent().getDate());
        holder.tvParticipationDate.setText("Participation Date: " + b.getParticipation_date());
    }

    @Override
    public int getItemCount() {
        return participationListData.size();
    }

    /**
     * return borrow object for currently selected borrow record (index already set by long press in viewholder)
     * @return
     */
    public Participation getSelectedItem() {
        // return the book record if the current selected position/index is valid
        if(currentPos>=0 && participationListData !=null && currentPos<participationListData.size()) {
            return participationListData.get(currentPos);
        }
        return null;
    }

}