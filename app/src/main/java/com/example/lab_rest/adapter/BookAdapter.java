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

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvTitle;
        public TextView tvAuthor;
        public TextView tvDescription;
        public ImageView imgBookCover;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgBookCover = itemView.findViewById(R.id.imgBookCover);

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

    private List<Book> bookListData;   // list of book objects
    private Context mContext;       // activity context
    private int currentPos;         // currently selected item (long press)

    public BookAdapter(Context context, List<Book> listData) {
        bookListData = listData;
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
        View view = inflater.inflate(R.layout.book_list_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder instance
        Book m = bookListData.get(position);
        holder.tvTitle.setText(m.getName());
        holder.tvAuthor.setText(m.getAuthor());
        holder.tvDescription.setText(m.getDescription());

        // Use Glide to load the image into the ImageView
        Glide.with(mContext)
                .load("https://codelah.my/bakri/api/" + m.getImage())
                .placeholder(R.drawable.default_cover) // Placeholder image if the URL is empty
                .error(R.drawable.default_cover) // Error image if there is a problem loading the image
                .into(holder.imgBookCover);
    }

    @Override
    public int getItemCount() {
        return bookListData.size();
    }

    /**
     * return book object for currently selected book (index already set by long press in viewholder)
     * @return
     */
    public Book getSelectedItem() {
        // return the book record if the current selected position/index is valid
        if(currentPos>=0 && bookListData !=null && currentPos<bookListData.size()) {
            return bookListData.get(currentPos);
        }
        return null;
    }

}