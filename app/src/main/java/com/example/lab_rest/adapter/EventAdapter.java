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
        public TextView tvDay;
        public TextView tvMonth;


        public ViewHolder(View itemView) {
            super(itemView);
            tvEvent_name = itemView.findViewById(R.id.tvEvent_name);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvMonth = itemView.findViewById(R.id.tvMonth);

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

    public void setDayAndMonth(String date){

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


        String day;
        String month;
        String date = m.getDate();
        String[] dateParts = date.split(" ");
        String dateOnly = dateParts[0];
        String[] dateElements = dateOnly.split("-");
        day = dateElements[2];  // The day is the 3rd element
        String monthNumber = dateElements[1];  // The month is the 2nd element
        switch (monthNumber) {
            case "01":
                month = "JAN";
                break;
            case "02":
                month = "FEB";
                break;
            case "03":
                month = "MAR";
                break;
            case "04":
                month = "APR";
                break;
            case "05":
                month = "MAY";
                break;
            case "06":
                month = "JUN";
                break;
            case "07":
                month = "JUL";
                break;
            case "08":
                month = "AUG";
                break;
            case "09":
                month = "SEP";
                break;
            case "10":
                month = "OCT";
                break;
            case "11":
                month = "NOV";
                break;
            case "12":
                month = "DEC";
                break;
            default:
                month = "UNKNOWN";
        }

        holder.tvDay.setText(day);
        holder.tvMonth.setText(month);


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