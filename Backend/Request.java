
import java.io.Serializable;

public class Request implements Serializable {
    String type,function,details;
    int UserPort;

    public Request(String type,String function, String details, int UserPort ){
        this.type = type;
        this.function = function;
        this.details = details;
        this.UserPort = UserPort;
    }
}
