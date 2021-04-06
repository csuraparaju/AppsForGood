import java.util.ArrayList;
public class MainClassStruct{

    public static void main(String[] args)
    {

        ArrayList<Integer> nts = new ArrayList<Integer>();
        ArrayList<Integer> goodTimes = new ArrayList<Integer>(); 

        nts.add(Integer.valueOf(1000));
        nts.add(Integer.valueOf(1900));
        nts.add(Integer.valueOf(1700));

        ExampleCalendar ec1 = new ExampleCalendar();
        ec1.generateExamples();
        ec1.addExcludedTimes(nts);

        goodTimes = ec1.findTimeSlots();

        System.out.println("The follwing time slots are free to use: " + goodTimes.toString());
    }
}