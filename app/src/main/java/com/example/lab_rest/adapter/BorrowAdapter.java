//package com.example.lab_rest.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.lab_rest.R;
//import com.example.lab_rest.model.Borrow;
//
//import java.util.List;
//
//public class BorrowAdapter extends RecyclerView.Adapter<BorrowAdapter.ViewHolder> {
//
//    /**
//     * Create ViewHolder class to bind list item view
//     */
//    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
//        public TextView tvTitle;
//        public TextView tvBorrowDate;
//        public TextView tvReturnDate;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            tvTitle = itemView.findViewById(R.id.tvTitle);
//            tvBorrowDate = itemView.findViewById(R.id.tvBorrowDate);
//            tvReturnDate = itemView.findViewById(R.id.tvReturnDate);
//
//            itemView.setOnLongClickListener(this);  //register long click action to this viewholder instance
//        }
//
//        @Override
//        public boolean onLongClick(View v) {
//            currentPos = getAdapterPosition(); //key point, record the position here
//            return false;
//        }
//    } // close ViewHolder class
//
//    //////////////////////////////////////////////////////////////////////
//    // adapter class definitions
//
//    private List<Borrow> borrowListData;   // list of borrow objects
//    private Context mContext;       // activity context
//    private int currentPos;         // currently selected item (long press)
//
//    public BorrowAdapter(Context context, List<Borrow> listData) {
//        borrowListData = listData;
//        mContext = context;
//    }
//
//    private Context getmContext() {
//        return mContext;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Context context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//        // Inflate layout using the single item layout
//        View view = inflater.inflate(R.layout.borrow_list_item, parent, false);
//        // Return a new holder instance
//        ViewHolder viewHolder = new ViewHolder(view);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        // bind data to the view holder instance
//        Borrow b = borrowListData.get(position);
//        holder.tvTitle.setText(b.getBook().getName());
//        holder.tvBorrowDate.setText("Borrow Date: " + b.getBorrow_date());
//        holder.tvReturnDate.setText("Return Date: " + b.getReturn_date());
//    }
//
//    @Override
//    public int getItemCount() {
//        return borrowListData.size();
//    }
//
//    /**
//     * return borrow object for currently selected borrow record (index already set by long press in viewholder)
//     * @return
//     */
//    public Borrow getSelectedItem() {
//        // return the book record if the current selected position/index is valid
//        if(currentPos>=0 && borrowListData !=null && currentPos<borrowListData.size()) {
//            return borrowListData.get(currentPos);
//        }
//        return null;
//    }
//
//}