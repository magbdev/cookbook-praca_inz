package com.example.cookbook.ui.recipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookbook.R;
import com.example.cookbook.adapter.ListCheckboxAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingListDialogFragment extends DialogFragment {

    private RecyclerView listView;
    private Button addToShoppingList;
    ListCheckboxAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String docID;
    private ArrayList<String> remoteList;

    public ShoppingListDialogFragment(){}
    public static ShoppingListDialogFragment newInstance(ArrayList<String> list) {
        ShoppingListDialogFragment frag = new ShoppingListDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("list", list);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shopping_list,container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.listview_checkbox);
        ArrayList<String> list = getArguments().getStringArrayList("list");
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListCheckboxAdapter(getContext(),list);
        listView.setAdapter(adapter);
        addToShoppingList = view.findViewById(R.id.addToShoppingList);
        addToShoppingList.setOnClickListener(v -> {



            CollectionReference docRef = db.collection("users");
            docRef.whereEqualTo("userID",auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            docID = document.getId();
                        }
                        getRemoteShoppingList(docID);

                        dismiss();
                    }
                }
            });
        });


        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void getRemoteShoppingList(String docID){

        db.collection("users").document(docID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    remoteList = (ArrayList<String>) document.get("shoppingList");
                }
                ArrayList<String> shoppingList = adapter.getShoppingList();
                shoppingList.addAll(remoteList);
                Map<String, Object> data = new HashMap<>();
                data.put("shoppingList",shoppingList);
                db.collection("users").document(docID).set(data, SetOptions.merge());
            }
        });
    }

}
