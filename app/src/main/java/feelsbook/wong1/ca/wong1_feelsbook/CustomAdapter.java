package feelsbook.wong1.ca.wong1_feelsbook;

import android.app.DatePickerDialog;
import android.content.Context;
import android.opengl.Visibility;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * This is used to show the comment/date/type of each emotion class,
 */
public class CustomAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflter;
    ArrayList<Emotion> emotionList;
    String historyFile;
    private boolean operating=false;    //indicate if any element is being changed atm
    private HistoryListener myListener;
    public CustomAdapter(Context applicationContext, ArrayList<Emotion> emotionList,HistoryListener myListener){
        this.context=applicationContext;
        this.emotionList=emotionList;
        this.historyFile= applicationContext.getResources().getString(R.string.history_file);
        this.inflter=(LayoutInflater.from(applicationContext));
        this.myListener=myListener;
    }
    @Override
    public int getCount() {
        //needed to indicate how many view to print out
        return emotionList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i,View view, ViewGroup viewGroup) {
        //prevent it from recreating everything
        Log.d("joey","operating is"+String.valueOf(operating));
        if(view==null) {
            Log.d("joey","view is null");
            view = inflter.inflate(R.layout.activity_history_list_view, null);
            createView(view,i);
        }else if(operating==false){ //this prevent the view from being repopulated when user is editing, but allow refresh after deleteion
            //if dataset is changed, update the data too
            Log.d("joey","operating=false, updating data");
            createView(view,i);
        }
        return view;
    }

    /**
     * This will populate/refresh the view, adding onclick listener for edit,delete
     * @param view  the current view
     * @param i the index of the item
     */
    private void createView(View view, final int i){
        TextView typeField= (TextView) view.findViewById(R.id.typeTextView);
        final TextView commentField= (TextView) view.findViewById(R.id.commentTextView);
        final TextView dateField= (TextView) view.findViewById(R.id.dateTextView);
        final EditText commentEditText= (EditText) view.findViewById(R.id.commentEditText);
        commentEditText.setVisibility(View.INVISIBLE);
        commentEditText.setText(emotionList.get(i).getComment());
        //hide the editText
        typeField.setText(emotionList.get(i).getType());
        commentField.setText(emotionList.get(i).getComment());
        SimpleDateFormat df = new SimpleDateFormat(
                "yyyy-MM-dd'T'hh:mm:ss");
        Log.d("joey"," updating date field");
        dateField.setText("Date: "+df.format(emotionList.get(i).getDate()));

        //add onclick listener
        final Button editBtn =(Button) view.findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if this is an edit attempt, hide the textview and show editText,
                // otherwise, save the file and update textview and hide editText
                if(commentEditText.getVisibility()==View.INVISIBLE) {
                    commentField.setVisibility(View.INVISIBLE);
                    commentEditText.setVisibility(View.VISIBLE);
                    operating=true;
                }else{
                    Log.d("joey","Changed comment to"+commentField.getText().toString());
                    emotionList.get(i).setComment(commentEditText.getText().toString());
                    saveTweets();
                    commentField.setText(commentEditText.getText());
                    commentField.setVisibility(View.VISIBLE);
                    commentEditText.setVisibility(View.INVISIBLE);
                    operating=false;
                }
            }
        });
        final Button deleteBtn = (Button) view.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emotionList.remove(i);
                saveTweets();
                notifyDataSetChanged();
            }
        });

        final Button setTimeBtn = (Button) view.findViewById(R.id.setTimeBtn);
        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myListener.setDateAndTime(i);
            }
        });
    }
    private void saveTweets(){
        try {
            FileOutputStream fos= context.openFileOutput(historyFile,0);
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
        }catch(Exception e){
            Log.d("joey",e.getMessage());
        }
        Log.d("Joey","No error in saveTweets");
    }
    //never used but might be needed later
    private void loadFromFile(){
        try {
            FileInputStream fis = context.openFileInput(historyFile);
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

    /**
     * Need to create our own notify data set so it can update itself
     */
    @Override
    public void notifyDataSetChanged() {
        Log.d("joey","notifydatasetchange");
        loadFromFile();
        this.myListener.getCounts();
        super.notifyDataSetChanged();
    }
}
