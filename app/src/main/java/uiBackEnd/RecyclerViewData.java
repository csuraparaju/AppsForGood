package uiBackEnd;

import com.example.appsforgood.ModifiedEvent;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewData {
    private List<ModifiedEvent> events;
    private List<Boolean> isReal;

    public RecyclerViewData(){
        events = new ArrayList<ModifiedEvent>();
        isReal = new ArrayList<Boolean>();
    }

    public void addRealEvent(ModifiedEvent event){
        events.add(event);
        isReal.add(true);
    }

    public void addPossibleEvent(ModifiedEvent event){
        events.add(event);
        isReal.add(false);
    }

    public void remove(int index){
        events.remove(index);
        isReal.remove(index);
    }

    public void replace(int index, ModifiedEvent event, boolean isReal){
        this.events.set(index, event);
        this.isReal.set(index, isReal);
    }

    public ModifiedEvent getEvent(int index){
        return events.get(index);
    }

    public boolean isEventReal(int index){
        return isReal.get(index);
    }

    public int size(){
        return events.size();
    }


}
