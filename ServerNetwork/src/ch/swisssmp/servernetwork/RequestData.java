package ch.swisssmp.servernetwork;

public class RequestData {
    private int intValue;
    private String stringValue;

    public void setIntValue(int value){
        intValue = value;
    }

    public int getIntValue(){
        return intValue;
    }

    public void setStringValue(String value){
        stringValue = value;
    }

    public String getStringValue(){
        return stringValue;
    }
}
