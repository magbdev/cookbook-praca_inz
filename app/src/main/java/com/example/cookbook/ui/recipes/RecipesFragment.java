package com.example.cookbook.ui.recipes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Filter;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookbook.R;
import com.example.cookbook.adapter.RecipesAdapter;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.viewmodel.RecipesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipesFragment extends Fragment implements RecipesAdapter.OnRecipeListener, TextToSpeech.OnInitListener, RecognitionListener {

    private RecipesViewModel recipesViewModel;
    private static RecipesAdapter adapter;
    private static RecyclerView recyclerView;
    private FloatingActionButton addRecipeButton;
    private SearchView searchView;
    private static TextToSpeech textToSpeech;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        recipesViewModel =
                ViewModelProviders.of(this).get(RecipesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recipes, container, false);
        recyclerView = root.findViewById(R.id.recipes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new RecipesAdapter(this.getContext(),this);
        recyclerView.setAdapter(adapter);
        textToSpeech = new TextToSpeech(getContext(),this);
        resetSpeechRecognizer();
        setRecogniserIntent();

        Bundle bundle = this.getArguments();
        recipesViewModel.fetchRecipe().observe(getViewLifecycleOwner(), recipes -> {
            adapter.addRecipeList(recipes);
            adapter.addRecipeFullList(recipes);
            adapter.notifyDataSetChanged();
            if (bundle != null) {
                String searchBundle = bundle.getString("search");
                searchView.setQuery(searchBundle,true);
            }
        });

        searchView = root.findViewById(R.id.searchView);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query, count -> {
                    if (bundle != null) {
                        List<String> list = adapter.getTitles();
                        StringBuilder text = new StringBuilder();
                        for (String s:list) {
                            text.append(s);
                            text.append(",");
                        }
                        speak(text.toString());
                        speech.startListening(recognizerIntent);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


        addRecipeButton = root.findViewById(R.id.addRecipeButton);
        addRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddRecipeActivity.class);
            startActivity(intent);
        });
        return root;
    }

    @Override
    public void onRecipeClick(Recipe position) {
        Intent intent = new Intent(getActivity(),RecipeDetailsActivity.class);
        intent.putExtra("recipe",position);
        startActivity(intent);
    }

    @Override
    public void onFavouriteClick(Recipe position) {
        if (position.getFavourite()) {
            position.setFavourite(false);
            recipesViewModel.unlike(position.getRecipeId());
        }
        else {
            position.setFavourite(true);
            recipesViewModel.like(position.getRecipeId());
        }
        adapter.notifyDataSetChanged();
    }

    //Text to speech
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("pl_PL"));

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            }
        }
    }

    private static void speak(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        }
        else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }


    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        speech.stopListening();
    }

    @Override
    public void onError(int error) {
        resetSpeechRecognizer();
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for(String result : matches) {
            if(result.toLowerCase().contains("wybierz") && result.length()>8) {
                int index = -1;

                try{
                    index = Integer.parseInt(result.substring(result.toLowerCase().indexOf("wybierz")+8));
                }catch (NumberFormatException e){
                    index = -1;
                }
                goToIndex(index);
            }
        }
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    private void setRecogniserIntent() {

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "pl");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    private void resetSpeechRecognizer() {

        if(speech != null)
            speech.destroy();
        speech = SpeechRecognizer.createSpeechRecognizer(getContext());
        if(SpeechRecognizer.isRecognitionAvailable(getContext()))
            speech.setRecognitionListener(this);
    }

    public void goToIndex(int index) {
        if(index > 0){
            Recipe r = adapter.getObject(--index);
            if (r != null) {
                Intent intent = new Intent(getActivity(), RecipeDetailsActivity.class);
                intent.putExtra("recipe", r);
                startActivity(intent);
                speech.destroy();
            }
        }
    }
}