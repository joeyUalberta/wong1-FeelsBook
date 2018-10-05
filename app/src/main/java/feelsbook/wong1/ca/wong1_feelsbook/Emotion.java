package feelsbook.wong1.ca.wong1_feelsbook;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Emotion {
    private String comment;
    private String type;
    private Date date;
    public Emotion(){
    }
    public Emotion(String type,String comment,Date date){
        this.type=type;
        this.comment=comment;
        this.date=date;

    }
    //The most basic display, but it is not used in this assignment (just included it for future changes)
    @Override
    public String toString() {
        SimpleDateFormat df = new SimpleDateFormat(
                "yyyy-MM-dd'T'hh:mm:ss");
        return "Type: "+this.type+"\n"+this.comment+"\n Date:"+df.format(this.date);
    }


    public Date getDate(){
        return this.date;
    }
    public void setDate(Date date){
        this.date = date;
    }
    public String getComment(){
        return this.comment;
    }
    public void setComment(String comment){
        this.comment=comment;
    }
    public String getType(){
        return this.type;
    }
}
