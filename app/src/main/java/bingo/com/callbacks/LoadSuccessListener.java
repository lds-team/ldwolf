package bingo.com.callbacks;

import java.util.Map;

public interface LoadSuccessListener {

    void onReceiveResult(String body, Map<String, String[]> result);

}
