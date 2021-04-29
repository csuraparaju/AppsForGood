package schedulingBackEnd;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.calendar.model.Event;

public class ParcelableEvent implements Parcelable {
    private String id;
    private long startTime;
    private long endTime;

    public ParcelableEvent(Event event){
        id = event.getSummary();
        startTime = event.getStart().getDateTime().getValue();
        startTime = event.getEnd().getDateTime().getValue();
    }

    protected ParcelableEvent(Parcel in) {
        id = in.readString();
        startTime = in.readLong();
        endTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
    }

    public String getId(){
        return id;
    }

    public long getStart(){
        return startTime;
    }

    public long getEnd(){
        return endTime;
    }

    public static final Creator<ParcelableEvent> CREATOR = new Creator<ParcelableEvent>() {
        @Override
        public ParcelableEvent createFromParcel(Parcel in) {
            return new ParcelableEvent(in);
        }

        @Override
        public ParcelableEvent[] newArray(int size) {
            return new ParcelableEvent[size];
        }
    };


}
