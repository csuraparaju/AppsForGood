package uiBackEnd;

import android.content.Context;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.api.services.calendar.model.Event;

public class EventView extends AppCompatTextView {
    private Event event;

    public EventView(Context context, Event event) {
        super(context);
        this.event = event;
        this.setText("#####");
    }


}
