package bingo.com.obs;

public interface DefObs {

    void register(SyncObs obs);

    void unRegister(SyncObs obs);

    void notifyUpdate();
}
