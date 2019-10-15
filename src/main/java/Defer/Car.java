package Defer;

import io.reactivex.Observable;

public class Car {

    String brand = "Default";

    public Observable<String> getBrandObservable() {
        return Observable.just(brand);
    }

    public Observable<String> getBrandDeferObservable() {
        return Observable.defer(() -> Observable.just(brand));
    }
}
