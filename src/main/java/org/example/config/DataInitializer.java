package org.example.config;

import org.example.replenish.ThresholdReplenishStrategy;
import org.example.store.DarkStore;
import org.example.store.DarkStoreManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// Seeds the demo dark stores and their stock on startup
@Component
public class DataInitializer implements ApplicationRunner {
    private final DarkStoreManager dsManager;

    public DataInitializer(DarkStoreManager dsManager) {
        this.dsManager = dsManager;
    }

    @Override
    public void run(ApplicationArguments args) {
        // DarkStore A.......
        DarkStore darkStoreA = new DarkStore("DarkStoreA", 0.0, 0.0);
        darkStoreA.setReplenishStrategy(new ThresholdReplenishStrategy(3));
        System.out.println("\nAdding stocks in DarkStoreA....");
        darkStoreA.addStock(101, 5);
        darkStoreA.addStock(102, 2);

        // DarkStore B.......
        DarkStore darkStoreB = new DarkStore("DarkStoreB", 4.0, 1.0);
        darkStoreB.setReplenishStrategy(new ThresholdReplenishStrategy(3));
        System.out.println("\nAdding stocks in DarkStoreB....");
        darkStoreB.addStock(101, 3);
        darkStoreB.addStock(103, 10);

        // DarkStore C.......
        DarkStore darkStoreC = new DarkStore("DarkStoreC", 2.0, 3.0);
        darkStoreC.setReplenishStrategy(new ThresholdReplenishStrategy(3));
        System.out.println("\nAdding stocks in DarkStoreC....");
        darkStoreC.addStock(102, 5);
        darkStoreC.addStock(201, 7);

        dsManager.registerDarkStore(darkStoreA);
        dsManager.registerDarkStore(darkStoreB);
        dsManager.registerDarkStore(darkStoreC);
    }
}
