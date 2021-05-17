package uiBackEnd;

import com.example.appsforgood.ModifiedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that holds a list of {@link ModifiedEvent}s for the {@link androidx.recyclerview.widget.RecyclerView} in
 * {@link com.example.appsforgood.CalViewActivity}. This class also stores whether or not each of
 * these {@link ModifiedEvent}s is real or only an available time slot.
 */
public class RecyclerViewData {
    private List<ModifiedEvent> events;
    private List<Boolean> isReal;

    /**
     * Initializes the data in this class.
     */
    public RecyclerViewData(){
        events = new ArrayList<ModifiedEvent>();
        isReal = new ArrayList<Boolean>();
    }

    /**
     * Adds a real calendar event to this classes data.
     * @param event the {@link ModifiedEvent} being added.
     */
    public void addRealEvent(ModifiedEvent event){
        events.add(event);
        isReal.add(true);
    }

    /**
     * Adds an available time slot to this classes data.
     * @param event the {@link ModifiedEvent} being added.
     */
    public void addPossibleEvent(ModifiedEvent event){
        events.add(event);
        isReal.add(false);
    }

    /**
     * Removes the datum at the given index.
     * @param index the index of the datum to be removed
     */
    public void remove(int index){
        events.remove(index);
        isReal.remove(index);
    }

    /**
     * Replaces the data at a given index with a different {@link ModifiedEvent}. This event can be
     * real or only an available time slot.
     * @param index the index of the datum to be replaced
     * @param event the new {@link ModifiedEvent} to be added at the index
     * @param isReal <code>true</code> if event is real, <code>false</code> if event is only an
     *               available time slot
     */
    public void replace(int index, ModifiedEvent event, boolean isReal){
        this.events.set(index, event);
        this.isReal.set(index, isReal);
    }

    /**
     * Returns the event at a given index.
     * @param index
     * @return the event at the index
     */
    public ModifiedEvent getEvent(int index){
        return events.get(index);
    }

    /**
     * Returns whether or not the event at the given index is real or only an available time slot.
     * @param index
     * @return <code>true</code> if event is real, <code>false</code> if event is only an
     * available time slot
     */
    public boolean isEventReal(int index){
        return isReal.get(index);
    }

    /**
     * Determines the size of the data stored in this {@link RecyclerViewData}.
     * @return the size of the data
     */
    public int size(){
        return events.size();
    }


}
