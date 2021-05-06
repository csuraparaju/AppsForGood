package uiBackEnd;


import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appsforgood.ModifiedEvent;
import com.example.appsforgood.R;


import java.util.ArrayList;
import java.util.List;

public class EventCardAdapter extends RecyclerView.Adapter<EventCardAdapter.ItemHolder> {
    private List<ModifiedEvent> events = new ArrayList<ModifiedEvent>();

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_view_item, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        ModifiedEvent currentEvent = events.get(position);
        holder.textViewName.setText(currentEvent.getName());
        holder.textViewStart.setText(currentEvent.getStartAsString());
        holder.textViewEnd.setText(currentEvent.getEndAsString());

        float heightDp = currentEvent.getDuration() / (60f * 1000f);

        DisplayMetrics metrics = holder.itemView.getContext().getResources().getDisplayMetrics();
        float heightPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, metrics);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.itemLayout.getLayoutParams();
        layoutParams.height = (int) heightPixels;

        holder.itemLayout.setLayoutParams(layoutParams);


        if(position != 0){
            ModifiedEvent previousEvent = events.get(position-1);
            RecyclerView.LayoutParams cardParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();

            float marginDp = 1 + (currentEvent.getStartTimeMilli()-previousEvent.getEndTimeMilli())/(60f * 1000f);
            float marginPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginDp, metrics);
            cardParams.topMargin = (int) marginPixels;

            holder.itemView.setLayoutParams(cardParams);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<ModifiedEvent> events){
        this.events = events;
        notifyDataSetChanged();
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        private TextView textViewName;
        private TextView textViewStart;
        private TextView textViewEnd;
        private RelativeLayout itemLayout;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewStart = itemView.findViewById(R.id.text_view_start);
            textViewEnd = itemView.findViewById(R.id.text_view_end);
            itemLayout = itemView.findViewById(R.id.item_holder);
        }
    }

}
