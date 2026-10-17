package org.example.store;

import org.example.inventory.DbInventoryStore;
import org.example.inventory.InventoryManager;
import org.example.model.Product;
import org.example.replenish.ReplenishStrategy;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Getter
@Setter
public class DarkStore {
    private  String name;
    private double x,y ;
    private InventoryManager inventoryManager;
    private ReplenishStrategy replenishStrategy;

    public DarkStore(String name,double x_coordination,double y_coordination) {
        this.name = name;
        this.x = x_coordination;
        this.y = y_coordination;
        this.inventoryManager = new InventoryManager(new DbInventoryStore());
    }
    public double distanceTo(double userX,double userY){
        return Math.sqrt(Math.pow((userX-x),2)+Math.pow((userY-y),2));
    }
    public void runReplenishment(Map<Integer,Integer> itemsToReplenish){
        if(replenishStrategy!=null){
            replenishStrategy.replenish(inventoryManager,itemsToReplenish);
        }else{
            System.out.println("No replenishment strategy set for DarkStore: "+name);
        }
    }
    //Delegation Methods
    public void addStock(int sku,int quantity){
        inventoryManager.addStock(sku,quantity);
    }
    public void removeStock(int sku,int quantity) {
        inventoryManager.removeStock(sku, quantity);
    }
    public int checkStock(int sku){
        return inventoryManager.checkStock(sku);
    }
    public List<Product> getAllProducts()
    {
        return inventoryManager.getAvailableProducts();
    }

}
