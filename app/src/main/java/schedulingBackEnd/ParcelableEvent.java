package schedulingBackEnd;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.calendar.model.Event;

public class ParcelableEvent implements Parcelable {
    private String id;
    private long[] times = new long[2];

    public ParcelableEvent(Event event){
        id = event.getSummary();
        times[0] = event.getStart().getDateTime().getValue();
        times[1] = event.getEnd().getDateTime().getValue();
    }

    protected ParcelableEvent(Parcel in) {
        id = in.readString();
        times[0] = in.readLong();
        times[1] = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(times[0]);
        dest.writeLong(times[1]);
    }

    public String getId(){
        return id;
    }

    public long getStart(){
        return times[0];
    }

    public long getEnd(){
        return times[1];
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
