package com.example.segundoauqui.amazonbook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.segundoauqui.amazonbook.Model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    List<Book> bookList;
    Context context;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    private boolean isLoadingAdded = false;

    public BookAdapter(Context context) {
        this.context = context;
        bookList = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvUrl;
        ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
            tvUrl = (TextView) itemView.findViewById(R.id.tvUrl);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    protected class LoadingVH extends ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    private ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.books_item, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == bookList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Book result = bookList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                holder.tvTitle.setText(result.getTitle());
                holder.tvAuthor.setText(result.getAuthor());
                holder.tvUrl.setText(result.getImageURL());
                Glide.with(holder.itemView.getContext()).load(result.getImageURL()).into(holder.ivImage);
                break;

            case LOADING:
//                Do nothing
                break;
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void add(Book r) {
        bookList.add(r);
        notifyItemInserted(bookList.size() - 1);
    }

    public void addAll(List<Book> moveResults) {
        for (Book book : moveResults) {
            add(book);
        }
    }

    public void remove(Book r) {
        int position = bookList.indexOf(r);
        if (position > -1) {
            bookList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Book());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = bookList.size() - 1;
        Book book = getItem(position);

        if (book != null) {
            bookList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Book getItem(int position) {
        return bookList.get(position);
    }
}
