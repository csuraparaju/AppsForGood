package schedulingBackEnd;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.calendar.model.Event;

/**
 * Provides a parcelable version of an event in order to allow events to be passed between
 * activities in {@link android.content.Intent}s. This class can easily be converted to and
 * generated from Google's {@link Event} and {@link com.example.appsforgood.ModifiedEvent}.
 *
 * @see Parcelable
 */
public class ParcelableEvent implements Parcelable {
    private String id;
    private long[] times = new long[2];

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

    /**
     * Constructs a {@link ParcelableEvent} from a Google {@link Event}.
     * @param event
     */
    public ParcelableEvent(Event event){
        id = event.getSummary();
        times[0] = event.getStart().getDateTime().getValue();
        times[1] = event.getEnd().getDateTime().getValue();
    }

    /**
     * Constructs a {@link ParcelableEvent} from its basic fields, name, start time (millis) and end
     * time (millis).
     *
     * @param name
     * @param start
     * @param end
     */
    public ParcelableEvent(String name, long start, long end){
        id = name;
        times[0] = start;
        times[1] = end;
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

    /**
     *
     * @return the name/id of this event
     */
    public String getId(){
        return id;
    }

    /**
     *
     * @return the start time of the event in milliseconds
     */
    public long getStart(){
        return times[0];
    }

    /**
     *
     * @return the end time of the event in milliseconds
     */
    public long getEnd(){
        return times[1];
    }
}
