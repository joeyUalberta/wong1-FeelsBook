package feelsbook.wong1.ca.wong1_feelsbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Main screen of the program, it will be used to navigate to see emotion history
 * or it can is be used to add emotion/feelings.
 */
public class MainActivity extends AppCompatActivity {
    private String emotionSelected="None";
    String historyFile;
    ArrayList<Emotion> emotionList= new ArrayList<Emotion>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    private EditText comment;
    @Override
    protected void onStart() {
        super.onStart();
        historyFile = getResources().getString(R.string.history_file);
        loadFromFile();
        Button loveBtn = (Button) findViewById(R.id.loveBtn);
        Button sadnessBtn=(Button) findViewById(R.id.sadnessBtn);
        Button fearBtn=(Button) findViewById(R.id.fearBtn);
        Button angerBtn=(Button) findViewById(R.id.angerBtn);
        Button joyBtn = (Button) findViewById(R.id.joyBtn);
        Button addBtn = (Button) findViewById(R.id.addEmotion);
        comment = (EditText) findViewById(R.id.comment_input);
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("joey","clicked");
                String text = comment.getText().toString();
                if(emotionSelected=="None"){
                    return;
                }
                Emotion newEmotion = new Emotion(emotionSelected,text,new Date());
                saveInFile(newEmotion);
                emotionSelected="None";
            }
        });
    }

    /**
     * Jump to an activity that show the pass emotions
     * @param view
     */
    public void navigateToHistory(View view){
        Intent intent = new Intent(this,DisplayHistoryActivity.class);
        startActivity(intent);
    }


    /**
     * Set what emotion to add
     * @param view  :the button correspond to the emotion
     */
    public void setEmotion(View view){
        Button b = (Button) view;
        emotionSelected = b.getText().toString();
        Log.d("joey",b.getText().toString());
    }

    /**
     * Save the new "Emotion" to the file
     * @param newEmotion    created by user by clicking add button
     */
    private void saveInFile(Emotion newEmotion){
        emotionList.add(newEmotion);
        emotionList.sort(new Comparator<Emotion>() {
            @Override
            public int compare(Emotion o1, Emotion o2) {
                if(o1.getDate().before(o2.getDate())){
                    return -1;
                }else{
                    return 1;
                }
            }
        });
        try {
            FileOutputStream fos= openFileOutput(historyFile,0);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            Gson gson =new Gson();
            gson.toJson(emotionList,osw);
            osw.flush();
            fos.close();
            Log.d("joey"," saved");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Load the old emotions file, so the program can save them as a arrayList in saveInFile
     * later
     */
    private void loadFromFile(){
        try {
            FileInputStream fis = openFileInput(historyFile);
            InputStreamReader isr= new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            Gson gson=new Gson();
            Type typeListEmotions=new TypeToken<ArrayList<Emotion>>(){}.getType();
            Log.d("joey","created Type");
            emotionList = gson.fromJson(reader,typeListEmotions);
            Log.d("joey","Type fail");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.d("joey","File not found");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d("joey","File not found");
            e.printStackTrace();
        }catch(Exception e){
            Log.d("joey", e.getMessage());
        }
    }
}
