package com.example.lab_rest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab_rest.R;
import com.example.lab_rest.model.Book;
import com.example.lab_rest.model.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvEvent_name;
        public TextView tvDescription;
        public TextView tvLocation;
        public TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEvent_name = itemView.findViewById(R.id.tvEvent_name);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);

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

    private List<Event> eventListData;   // list of book objects
    private Context mContext;       // activity context
    private int currentPos;         // currently selected item (long press)

    public EventAdapter(Context context, List<Event> listData) {
        eventListData = listData;
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
        View view = inflater.inflate(R.layout.event_list_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder instance
        Event m = eventListData.get(position);
        holder.tvEvent_name.setText(m.getEvent_name());
        holder.tvDescription.setText(m.getDescription());
        holder.tvLocation.setText(m.getLocation());
        holder.tvDate.setText(m.getDate());

    }

    @Override
    public int getItemCount() {
        return eventListData.size();
    }

    /**
     * return book object for currently selected book (index already set by long press in viewholder)
     * @return
     */
    public Event getSelectedItem() {
        // return the book record if the current selected position/index is valid
        if(currentPos>=0 && eventListData !=null && currentPos<eventListData.size()) {
            return eventListData.get(currentPos);
        }
        return null;
    }

}