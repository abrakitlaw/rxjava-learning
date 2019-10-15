import io.reactivex.Observable;

import java.util.concurrent.TimeUnit;

public class ColdObservables {

    public static void main(String[] args) throws InterruptedException {
        Observable<Long> coldObservable = Observable.interval(1, TimeUnit.SECONDS);
        coldObservable.subscribe(item -> System.out.println("First: " + item));
        Thread.sleep(3000);
        coldObservable.subscribe(i -> System.out.println("Second: " + i));
        Thread.sleep(5000);
    }
}
