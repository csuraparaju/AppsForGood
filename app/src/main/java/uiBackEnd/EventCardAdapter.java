package uiBackEnd;


import android.content.Intent;
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

import com.example.appsforgood.AddNoteActivity;
import com.example.appsforgood.ModifiedEvent;
import com.example.appsforgood.R;


import java.util.ArrayList;
import java.util.List;

public class EventCardAdapter extends RecyclerView.Adapter<EventCardAdapter.ItemHolder> {
    private RecyclerViewData data = new RecyclerViewData();
    private OnCardClickListener listener;

    public int getItemViewType(int position){
        if(data.isEventReal(position)) return 0;
        else return 1;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if(viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.calendar_view_item, parent, false);
        }
        else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.possible_calendar_view_item, parent, false);
        }
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        ModifiedEvent currentEvent = data.getEvent(position);
        holder.textViewName.setText(currentEvent.getName());
        holder.textViewStart.setText(currentEvent.getStartAsString());
        holder.textViewEnd.setText(currentEvent.getEndAsString());

        float heightDp = 2*currentEvent.getDuration() / (60f * 1000f);

        DisplayMetrics metrics = holder.itemView.getContext().getResources().getDisplayMetrics();
        float heightPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, metrics);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.itemLayout.getLayoutParams();
        layoutParams.height = (int) heightPixels;

        holder.itemLayout.setLayoutParams(layoutParams);


        if(position != 0){
            ModifiedEvent previousEvent = data.getEvent(position-1);
            RecyclerView.LayoutParams cardParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();

            float marginDp = 8 + (2 * (currentEvent.getStartTimeMilli()-previousEvent.getEndTimeMilli())/(60f * 1000f));
            float marginPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginDp, metrics);
            cardParams.topMargin = (int) marginPixels;

            holder.itemView.setLayoutParams(cardParams);
        }

        if(!data.isEventReal(position)) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(listener != null) listener.onCardClick(data.getEvent(position), position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setEvents(RecyclerViewData data){
        this.data = data;
        notifyDataSetChanged();
    }

    public void replaceEvent(ModifiedEvent event, boolean isReal, int index){
        data.replace(index, event, isReal);
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

    public interface OnCardClickListener{
        void onCardClick(ModifiedEvent event, int index);
    }

    public void setOnCardClickListener(OnCardClickListener listener){
        this.listener = listener;
    }
}
