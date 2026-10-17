package org.example.store;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class DarkStoreManager {
    private final List<DarkStore> darkStores = new CopyOnWriteArrayList<>();

    public void registerDarkStore(DarkStore darkStore) {
        darkStores.add(darkStore);
    }

    public List<DarkStore> getNearbyDarkStores(double userX, double userY, double radius) {
        return darkStores.stream()
                .filter(store -> store.distanceTo(userX, userY) <= radius)
                .sorted(Comparator.comparingDouble(store -> store.distanceTo(userX, userY)))
                .toList();
    }
}