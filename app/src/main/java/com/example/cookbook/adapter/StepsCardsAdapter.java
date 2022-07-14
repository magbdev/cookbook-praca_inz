package com.example.cookbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import com.example.cookbook.R;

import java.util.ArrayList;

public class StepsCardsAdapter extends PagerAdapter {

    private ArrayList<String> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public StepsCardsAdapter(ArrayList<String> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //return super.instantiateItem(container, position);
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.step_card, container, false);

        TextView stepText,stepNumber;

        stepText = view.findViewById(R.id.stepTextView);
        stepNumber = view.findViewById(R.id.stepNumberTextView);

        stepText.setText(data.get(position));
        stepNumber.setText("Krok " + (position+1) + " z " + data.size());

        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View)object);
    }
}