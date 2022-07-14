package com.example.cookbook.ui.recipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookbook.R;
import java.util.HashMap;
import java.util.Map;

public class IngredientsListAdapter extends RecyclerView.Adapter<IngredientsListAdapter.IngredientsHolder> {

    private LayoutInflater layoutInflater;
    private HashMap<String,String> list;

    public IngredientsListAdapter(Context context, HashMap<String,String> list){
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public IngredientsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.ingredient_row, parent,false);
        return new IngredientsListAdapter.IngredientsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsListAdapter.IngredientsHolder holder, int position) {
        for (Map.Entry<String, String> entry : list.entrySet()) {
            if (entry.getKey().equals((list.keySet().toArray())[position])) {
                holder.bind(entry.getKey(),list.get( (list.keySet().toArray())[position] ));
            }
        }

    }

    @Override
    public int getItemCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }

    public class IngredientsHolder extends RecyclerView.ViewHolder{
        TextView nameTextView,quantityTextView;
        public IngredientsHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
        }
        public void bind(String name,String quantity){
            nameTextView.setText(name);
            quantityTextView.setText(quantity);
        }
    }
}
