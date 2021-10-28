package Data.Type;


// Couldn't think of a better name for this class.
// Defines a single error type, whereas ErrorType defines all of the errors sent from Motor Controller.
// This class essentially stores an integer with a name. Also will keep value as one if error is tripped, need to reset it.
public class SingleError {
//    protected List<Integer> data;

    protected String name;
    protected String tip; // This is extra info provided in the motor controller user manual about the error

    protected int cur;
    protected boolean tripped;

    public SingleError(String name){
        this.name = name;

        cur = 0;
        tripped = false;
    }

    public SingleError(String name, String tip){
        this(name);
        this.tip = tip;
    }

    public String getName(){
        return name;
    }

    public void putValue(int val){
        cur = val;

        if (cur == 1){
            tripped = true;
        }
    }

    public int getValue(){
        if (tripped){
            return 1;
        }
        else{
            return cur;
        }
    }

    public void resetTrip(){
        tripped = false;
    }

}
