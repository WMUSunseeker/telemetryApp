package Data.Type;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorType implements DataTypeInterface {
    /*
     * Various attributes
     */
    protected String name;
    protected String displayName;

    protected String units;
    protected String displayUnits;

    protected Color color;

    protected List<Double> data;
    protected Map<Integer, SingleError> errors;
    protected Map<Integer, SingleError> limits;

    protected double cur, min, max;

    /*
     * Should this type be shown
     */
    protected boolean enabled;

    public ErrorType(String name, String units) {
        /*
         * Set the given attributes
         */
        this.name  = name;
        this.units = units;

        /*
         * Use black as the default color
         */
        color = Color.BLACK;

        /*
         * Create a synconized data list
         */
        data = new ArrayList<Double>();
        errors = new HashMap<Integer, SingleError>();
        limits = new HashMap<Integer, SingleError>();

        /*
         * This value will root the data at the origin of the graph
         */
        data.add(0.0);

        /*
         * Initialize other values
         */
        enabled = true;
        min     = 0;
        cur     = 0;
        max     = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setDisplayName(String name) {
        this.displayName = name;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getUnits() {
        return units;
    }

    @Override
    public void setDisplayUnits(String units) { }

    @Override
    public String getDisplayUnits() {
        return displayUnits;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void putValue(double value){
//        System.out.println("value = " + value);
//        String result = Long.toBinaryString( (int)value & 0xffffffffL | 0x100000000L ).substring(1);
//        Integer[] bits = new Integer[32];
//        for (int i = 0; i < result.length(); i++) {
//            bits[i] = Integer.parseInt(Character.toString(result.charAt(i)));
//        }
//        for (int bit : bits) {
//            System.out.print(bit);
//        }
//        System.out.println();
//
//        for (int i = 0; i < 7; i++) {
//            limits.get(i).putValue(bits[i]);
//        }
//        for (int i = 16; i < 23; i++) {
//            errors.get(i).putValue(bits[i]);
//        }

    }

    // value: 32 bit string
    public void putValue(String value){
        // put each bit from the string 'value' into an array of ints
        Integer[] bits = new Integer[32];
        for (int i = 0; i < value.length(); i++) {
            bits[i] = Integer.parseInt(Character.toString(value.charAt(i)));
        }

        // put the bits into the error and limit maps
        if(!limits.isEmpty()) {
            for (int i = 0; i < 7; i++) { // put the first 7 bits into the limits map
                limits.get(i).putValue(bits[31-i]);
            }
            for (int r = 16; r < 23; r++) { // put the next 6 bits into the errors map
                errors.get(r).putValue(bits[31-r]);
            }
        }

    }

    @Override
    public List<Double> getData() {
        return data;
    }

    @Override
    public double getMinimumValue() {
        return min;
    }

    @Override
    public double getCurrentValue() {
        return cur;
    }

    @Override
    public double getMaximumValue() {
        return max;
    }

    public void addError(SingleError error, int bitID){
        errors.put(bitID, error);
    }

    public Map<Integer, SingleError> getErrors(){
        return errors;
    }

    public void addLimit(SingleError limit, int bitID){
        limits.put(bitID, limit);
    }

    public Map<Integer, SingleError> getLimits() {
        return limits;
    }

    public static void addMotorErrors(ErrorType container) {
        container.addError(new SingleError("Motor Over Speed", "15% overshoot above max RMP"), 23);
        container.addError(new SingleError("Desaturation Fault", "IGBT desaturation, IGBT driver UVLO"), 22);
        container.addError(new SingleError("15V rail Under Voltage Lock out", "UVLO"), 21);
        container.addError(new SingleError("Config read error", "some values may be reset to defaults"), 20);
        container.addError(new SingleError("WatchDog caused Last reset"), 19);
        container.addError(new SingleError("Bad Motor position hall sequence"), 18);
        container.addError(new SingleError("DC Bus over voltage"), 17);
        container.addError(new SingleError("Software over current"), 16);
    }

    public static void addMotorLimits(ErrorType container){
//        System.out.println("adding motor limits!");
        container.addLimit(new SingleError("IPM Temperature or Motor Temperature"), 6);
        container.addLimit(new SingleError("Bus Voltage Lower Limit"), 5);
        container.addLimit(new SingleError("Bus Voltage Upper Limit"), 4);
        container.addLimit(new SingleError("Bus Current"), 3);
        container.addLimit(new SingleError("Velocity") ,2);
        container.addLimit(new SingleError("Motor Current"), 1);
        container.addLimit(new SingleError("Output Voltage PWM"), 0);
    }
}
