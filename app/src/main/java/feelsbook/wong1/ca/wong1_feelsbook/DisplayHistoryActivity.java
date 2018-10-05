package feelsbook.wong1.ca.wong1_feelsbook;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

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
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class DisplayHistoryActivity extends AppCompatActivity implements HistoryListener {
    String historyFile;
    ListView simpleList;
    CustomAdapter customAdapter;
    String countryList[] = {"India", "China", "australia", "Portugle", "America", "NewZealand"};
    ArrayList<Emotion> emotionList= new ArrayList<Emotion>();

    /**
     * Set up adapter and display counts of each emotions
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_history);
        historyFile = getResources().getString(R.string.history_file);
        loadFromFile();
        simpleList = (ListView)findViewById(R.id.history_list_view);
        customAdapter = new CustomAdapter(getApplicationContext(), emotionList,this);
        simpleList.setAdapter(customAdapter);
        getCounts();
    }

    /**
     * This method is called by CustomAdapter, which will let user choose time and update
     * the files, which will then tell the adapter to update its data
     * @param i index of the emotion to have date changed
     */
    public void setDateAndTime(final int i){
        Log.d("joey","Choose time");
        DatePickerFragment myFragment= new DatePickerFragment();
        myFragment.setDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, final int year, final int month, final int day) {
                TimePickerFragment timeFragment=new TimePickerFragment();
                timeFragment.setTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        loadFromFile();
                        Calendar c=Calendar.getInstance();
                        c.set(year,month,day,hour,minute);
                        emotionList.get(i).setDate(c.getTime());
                        saveTweets();
                        customAdapter.notifyDataSetChanged();
                    }
                });
                timeFragment.show(getSupportFragmentManager(),"Choose time");
                Log.d("joey",String.valueOf(year));
            }
        });
        myFragment.show(getSupportFragmentManager(),"Choose Date");
    }

    //Load the emotions from file
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

    //Store Emotions/Tweet back to the data file
    private void saveTweets(){
        try {
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
     * Start counting how many emotion are there,
     * and display them in the text views
     */
    public void getCounts(){
        loadFromFile();
        int num_love=0;
        int num_joy=0;
        int num_anger=0;
        int num_sadness=0;
        int num_fear=0;
        for(int i=0;i<emotionList.size();i++){
            if(emotionList.get(i).getType().equals("LOVE")){
                num_love+=1;
            }else if(emotionList.get(i).getType().equals("JOY")){
                num_joy+=1;
            }else if(emotionList.get(i).getType().equals("ANGER")){
                num_anger+=1;
            }else if(emotionList.get(i).getType().equals("SADNESS")){
                num_sadness+=1;
            }else if(emotionList.get(i).getType().equals("FEAR")){
                num_fear+=1;
            }
        }
        TextView loveCount=(TextView) findViewById(R.id.loveCount);
        TextView joyCount=(TextView) findViewById(R.id.joyCount);
        TextView angerCount=(TextView) findViewById(R.id.angerCount);
        TextView sadCount=(TextView) findViewById(R.id.sadCount);
        TextView fearCount=(TextView) findViewById(R.id.fearCount);
        loveCount.setText("Love: "+String.valueOf(num_love));
        joyCount.setText("Joy: "+String.valueOf(num_joy));
        angerCount.setText("Anger: "+String.valueOf(num_anger));
        sadCount.setText("Sadness: "+String.valueOf(num_sadness));
        fearCount.setText("Fear: "+String.valueOf(num_fear));

    }
}
