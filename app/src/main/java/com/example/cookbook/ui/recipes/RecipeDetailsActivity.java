package com.example.cookbook.ui.recipes;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;
import com.example.cookbook.R;
import com.example.cookbook.adapter.PagerAdapter;
import com.example.cookbook.model.Recipe;
import com.example.cookbook.model.User;
import com.example.cookbook.viewmodel.RecipeDetailsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class RecipeDetailsActivity extends AppCompatActivity implements RecognitionListener, TextToSpeech.OnInitListener {

    private TabLayout tabLayout;
    private static ViewPager viewPager;
    private static PagerAdapter pagerAdapter;
    private static Recipe recipe;
    private FloatingActionButton shoppingList;

    private String LOG_TAG = "VoiceRecognitionActivity";
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private boolean isListening = false;

    private static ArrayList<String> howMuchIngredient = new ArrayList<>();
    private static ArrayList<String> whichStep = new ArrayList<>();

    private static TextToSpeech textToSpeech;

    ///komendy
    private Map<String,Runnable> commands = new HashMap<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.speechRecognizerButton){
            if(!isListening) {
                resetSpeechRecognizer();
                speech.startListening(recognizerIntent);
                isListening = true;
            }
            else {
                speech.destroy();
                isListening = false;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initializeCommands();
        textToSpeech = new TextToSpeech(this, this);
        setContentView(R.layout.activity_recipe_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);
        shoppingList = findViewById(R.id.shoppingList);
        shoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<>();
                Object[] ingredients = recipe.getIngredients().keySet().toArray();
                Object[] quantity = recipe.getIngredients().values().toArray();
                for (int i=0;i<recipe.getIngredients().size();i++){
                    list.add(quantity[i].toString()+" "+ingredients[i].toString());
                }
                FragmentManager fm = getSupportFragmentManager();
                ShoppingListDialogFragment shoppingListDialogFragment = ShoppingListDialogFragment.newInstance(list);
                shoppingListDialogFragment.show(fm, "fragment_shopping_list");
            }
        });

        recipe = getIntent().getParcelableExtra("recipe");

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,recipe);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager,true);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pageNumber = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        resetSpeechRecognizer();
        setRecogniserIntent();
        // check for permission
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        speech.startListening(recognizerIntent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speech.startListening(recognizerIntent);
            } else {
                Toast.makeText(RecipeDetailsActivity.this, "Permission Denied!", Toast
                        .LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void resetSpeechRecognizer() {

        if(speech != null)
            speech.destroy();
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        if(SpeechRecognizer.isRecognitionAvailable(this))
            speech.setRecognitionListener(this);
        else
            finish();
    }

    private void setRecogniserIntent() {

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "pl");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        speech.stopListening();
    }

    @Override
    public void onError(int error) {
        String errorMessage = getErrorText(error);
        Log.i(LOG_TAG, "FAILED " + errorMessage);

        // rest voice recogniser
        resetSpeechRecognizer();
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        //get value and set
        outerloop:
        for(String result: matches) {
        for (String command: commands.keySet()) {

            if(result.toLowerCase().contains(command)){
                //komenda została przechwycona

                if (command.equals("ile") && result.length()>4) {
                    howMuchIngredient.add(result.substring(result.toLowerCase().indexOf("ile")+4));
                    break;
                }
                else if(command.equals("pokaż krok") && result.length()>11) {
                    whichStep.add(result.substring(11));
                    break;
                }

                commands.get(command).run();

                break outerloop;
            }

        }}

        if(!howMuchIngredient.isEmpty()) {
            commands.get("ile").run();
        }
        else if(!whichStep.isEmpty()) {
            commands.get("pokaż krok").run();
        }

//        Toast.makeText(RecipeDetailsActivity.this, text, Toast
//                .LENGTH_SHORT).show();
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i(LOG_TAG, "onEvent");
    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
    void initializeCommands (){
        commands.put("dalej",RecipeDetailsActivity::next);
        commands.put("cofnij",RecipeDetailsActivity::back);
        commands.put("czytaj",RecipeDetailsActivity::read);
        commands.put("ile",RecipeDetailsActivity::howMuch);
        commands.put("pokaż składniki",RecipeDetailsActivity::showIngredients);
        commands.put("pokaż krok",RecipeDetailsActivity::showStep);
    }

    public void setStepNumber(int position) {
        stepNumber = position;
    }
    //funcje
    //następne
    private static int pageNumber = 0;
    private static int stepNumber = 0;
    private static void next() {
        if(pageNumber > -1 && pageNumber < 2) {
            pageNumber++;
            viewPager.setCurrentItem(pageNumber);
        }
        else if(pageNumber == 2 && stepNumber < recipe.getSteps().size()-1) {
            stepNumber++;
            pagerAdapter.getStepsFragment().setStepItem(stepNumber);
        }
    }

    //cofnij
    private static void back() {
        if(pageNumber == 1) {
            pageNumber--;
            viewPager.setCurrentItem(pageNumber);
        }
        else if(pageNumber == 2){
            if(stepNumber == 0) {
                pageNumber--;
                viewPager.setCurrentItem(pageNumber);
            }
            else if(stepNumber > 0){
                stepNumber--;
                pagerAdapter.getStepsFragment().setStepItem(stepNumber);
            }
        }
    }


    //czytaj
    private static void read() {
        if (pageNumber == 1) {
            String text = "";
            for(int i = 0; i < recipe.getIngredients().size(); i++){
                text = "";
                text += recipe.getIngredients().keySet().toArray()[i] + " ";
                text += recipe.getIngredients().values().toArray()[i] + ",";
                speak(text);
                textToSpeech.playSilence(500, TextToSpeech.QUEUE_ADD, null);
            }
        }
        else if(pageNumber == 2) {
            speak(recipe.getSteps().get(stepNumber));
        }
    }

    //ile ?
    private static void howMuch() {
        for(String t:howMuchIngredient) {
            Iterator it = recipe.getIngredients().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if(t.equals(pair.getKey().toString().trim()))
                speak(pair.getKey() + " " + pair.getValue());
            }
            }
        howMuchIngredient.clear();
    }

    //pokaż składniki
    private static void showIngredients() {
        pageNumber = 1;
        viewPager.setCurrentItem(pageNumber);
    }

    //pokaż krok ..
    private static void showStep() {
        int foo = -1;
        for(String item: whichStep) {
            try {
                foo = Integer.parseInt(item);
                break;
            }catch (NumberFormatException e) {
                foo = -1;
            }
        }
        if(foo > 0 && foo <= recipe.getSteps().size()) {
            pageNumber = 2;
            viewPager.setCurrentItem(pageNumber);
            stepNumber = foo-1;
            pagerAdapter.getStepsFragment().setStepItem(stepNumber);
        }
        whichStep.clear();
    }


    //textToSpeech
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
            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD,null);
        }
    }



}