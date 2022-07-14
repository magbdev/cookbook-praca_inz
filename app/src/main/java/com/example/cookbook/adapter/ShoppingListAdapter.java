package com.example.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookbook.R;

import java.util.ArrayList;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.sViewHolder>  {

    private ArrayList<String> list;
    private OnDeleteClickListener onDeleteClickListener;

    public ShoppingListAdapter(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void addShoppingList(ArrayList<String> list){
        this.list = list;
    }

    public ArrayList<String> getList() {
        return list;
    }

    @NonNull
    @Override
    public ShoppingListAdapter.sViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingListAdapter.sViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_shopping, parent, false),onDeleteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull sViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        if (list == null){
            return 0;
        }
        return list.size();
    }
    public interface OnDeleteClickListener {
        void deleteItem(int position,ArrayList<String> list);
    }

    public class sViewHolder extends RecyclerView.ViewHolder{
        TextView ingredientName;
        ImageButton deleteButton;
        OnDeleteClickListener onDeleteClickListener;
        public sViewHolder(@NonNull View itemView,OnDeleteClickListener onDeleteClickListener) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.shopping_item);
            deleteButton = itemView.findViewById(R.id.delete_item_button);
            this.onDeleteClickListener = onDeleteClickListener;
        }

        public void bind(String item) {
            ingredientName.setText(item);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(getAdapterPosition());
                    onDeleteClickListener.deleteItem(getAdapterPosition(),list);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
