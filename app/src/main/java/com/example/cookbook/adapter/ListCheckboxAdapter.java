package com.example.cookbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cookbook.R;

import java.util.ArrayList;

public class ListCheckboxAdapter extends RecyclerView.Adapter<ListCheckboxAdapter.sViewHolder> {
    Context context;
    ArrayList<String> list;
    ArrayList<String> shoppingList;

    public ListCheckboxAdapter(Context context,ArrayList<String> list) {
        this.context = context;
        this.list = list;
        this.shoppingList = new ArrayList<>();
    }

    public ListCheckboxAdapter() {
        this.shoppingList = new ArrayList<>();
    }

    public void addShoppingList(ArrayList<String> list){
        this.list = list;
    }

    @Override
    public ListCheckboxAdapter.sViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new sViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_checkbox, parent, false));
    }

    @Override
    public void onBindViewHolder(final ListCheckboxAdapter.sViewHolder holder, final int position) {
        holder.bind(list.get(position));

        //===========click listner of check box===============//

        holder.checkBoxparent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final boolean isChecked = holder.checkBoxparent.isChecked();
                for (int i=0; i<list.size();i++) {
                    if (isChecked) {
                        if (!shoppingList.contains(list.get(position)))
                            shoppingList.add(list.get(position));
                    } else {
                        shoppingList.remove(list.get(position));

                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list == null) {
            return 0;
        }
        return list.size();
    }

    class sViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientName;
        CheckBox checkBoxparent;

        public sViewHolder(View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.ingredient_check);
            checkBoxparent = (CheckBox) itemView.findViewById(R.id.checkbox);

        }
        public void bind(String name){
            ingredientName.setText(name);
        }

    }

    public ArrayList<String> getShoppingList() {
        return shoppingList;
    }
}
