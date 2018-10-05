package feelsbook.wong1.ca.wong1_feelsbook;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Use to create a a picker for user to choose time
 */
public class TimePickerFragment extends DialogFragment {


    private TimePickerDialog.OnTimeSetListener timeSetListener;

    public void setTimeSetListener(TimePickerDialog.OnTimeSetListener timeSetListener) {
        this.timeSetListener= timeSetListener ;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute=c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(),timeSetListener,hour,minute,true);
    }
}
