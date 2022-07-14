package com.example.cookbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookbook.R;

import java.util.ArrayList;

public class StringListAdapter extends RecyclerView.Adapter<StringListAdapter.IngredientsHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<String> list;

    public StringListAdapter(Context context, ArrayList<String> list){
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public IngredientsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.step_row, parent,false);
        return new StringListAdapter.IngredientsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StringListAdapter.IngredientsHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }

    public class IngredientsHolder extends RecyclerView.ViewHolder{
        TextView nameTextView;
        public IngredientsHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.stepTextView);
        }
        public void bind(String name){
            nameTextView.setText(name);
        }
    }
}
