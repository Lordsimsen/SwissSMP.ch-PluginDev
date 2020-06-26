package ch.swisssmp.netherportals;

public enum CoordinateOperation {
    MULTIPLY,
    DIVIDE;

    public double apply(double a, double b){
        return this==MULTIPLY ? a * b : (Math.abs(b)>0 ? a / b : 0);
    }

    public static CoordinateOperation parse(String s){
        try{
            return s!=null ? CoordinateOperation.valueOf(s) : null;
        }
        catch(Exception ignored){
            return null;
        }
    }
}
