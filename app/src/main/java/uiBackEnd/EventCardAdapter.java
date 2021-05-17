package uiBackEnd;


import android.util.DisplayMetrics;
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

/**
 * Stores and handles the contents of the recycler view in {@link com.example.appsforgood.CalViewActivity}.
 * @see androidx.recyclerview.widget.RecyclerView.Adapter
 */
public class EventCardAdapter extends RecyclerView.Adapter<EventCardAdapter.ItemHolder> {
    private RecyclerViewData data = new RecyclerViewData();
    private OnCardClickListener listener;

    /**
     * Determines if the event at position in this {@link EventCardAdapter}'s data is a real event
     * or only an available time slot.
     * @param position
     * @return <code>0</code> if the event at position is real, <code>1</code> if the event is
     * only an available time slot.
     */
    public int getItemViewType(int position){
        if(data.isEventReal(position)) return 0;
        else return 1;
    }

    /**
     * Generates {@link ItemHolder}s for each card created.
     * @param parent
     * @param viewType
     * @return
     */
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

    /**
     * Run when an {@link ItemHolder}, holder, is bound to this {@link EventCardAdapter} to provide
     * holder with the proper data and positioning in the UI. This method also provides available
     * time slot cards with an {@link android.view.View.OnClickListener} that runs their respective
     * {@link OnCardClickListener}
     * @param holder
     * @param position
     */
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

    /**
     * Returns the number of events stored in this {@link EventCardAdapter}.
     * @return the number of events
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Sets the data of this {@link EventCardAdapter}.
     * @param data a {@link RecyclerViewData} that holds all of the data being added to this {@link EventCardAdapter}
     */
    public void setEvents(RecyclerViewData data){
        this.data = data;
        notifyDataSetChanged();
    }

    /**
     * Replaces a single event in this {@link EventCardAdapter}'s data with a new event.
     * @param event the {@link ModifiedEvent} being added
     * @param isReal <code>true</code> if event is real, <code>false</code> if event is only an
     *               available time slot
     * @param index the index being replaced
     */
    public void replaceEvent(ModifiedEvent event, boolean isReal, int index){
        data.replace(index, event, isReal);
        notifyDataSetChanged();
    }

    /**
     * Stores and handles the contents of a single event item card in the recycler view.
     */
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

    /**
     * An interface that allows for the on click listeners for each card to be specific to each
     * card by ensuring the listeners created for a card are specific to a {@link ModifiedEvent}
     * and an index.
     */
    public interface OnCardClickListener{
        /**
         * Any click listener that includes a specific event and the index of that event as
         * parameters.
         * @param event the event the card of which will result in the call of this {@link OnCardClickListener}
         * @param index the index of that event
         */
        void onCardClick(ModifiedEvent event, int index);
    }

    /**
     * Sets the general {@link OnCardClickListener} for {@link ModifiedEvent}s in this {@link EventCardAdapter}.
     * @param listener the listener to be used by cards in this adapter
     */
    public void setOnCardClickListener(OnCardClickListener listener){
        this.listener = listener;
    }
}
