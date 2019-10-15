package Defer;

import io.reactivex.Observable;

public class TestRide {

    public static void main(String[] args) {
        Car car = new Car();
        Observable<String> brandObservable = car.getBrandObservable();
        Observable<String> brandDeferObservable = car.getBrandDeferObservable();

        car.brand = "BMW";

        brandObservable.subscribe(brand -> System.out.println("brandObservable : " + brand));
        brandDeferObservable.subscribe(brand -> System.out.println("brandDeferObservable : " + brand));
    }
}
