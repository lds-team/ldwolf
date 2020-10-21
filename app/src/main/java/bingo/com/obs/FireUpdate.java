package bingo.com.obs;

import java.util.ArrayList;
import java.util.List;

public class FireUpdate implements DefObs {

    List<SyncObs> obsList = new ArrayList<>();

    public FireUpdate() {
    }

    @Override
    public void register(SyncObs obs) {
        obsList.add(obs);
    }

    @Override
    public void unRegister(SyncObs obs) {
        obsList.remove(obs);
    }

    @Override
    public void notifyUpdate() {
        for (SyncObs obs : obsList)
        {
            obs.update();
        }
    }
}
