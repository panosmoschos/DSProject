import java.io.Serializable;

public class Request implements Serializable {
    String type,function,details;

    public Request(String type,String function, String details){
        this.type = type;
        this.function = function;
        this.details = details;
    }
}
