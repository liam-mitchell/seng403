package org.lsmr.vendingmachine.product;

public interface ProductKindListener {
    void outOfStock(ProductKind pk);

    void hardwareFailure(ProductKind pk);
    
    void selected(ProductKind pk);
}
