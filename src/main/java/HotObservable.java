import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

public class HotObservable {

    public static void main(String[] args) throws InterruptedException {
        Observable<Long> hotObservable = Observable.interval(1, TimeUnit.SECONDS);
        ConnectableObservable<Long> connectableObservable = hotObservable.publish();
        connectableObservable.subscribe(i -> System.out.println("Observer 1: " + i));
        connectableObservable.connect();
        Thread.sleep(3000);
        connectableObservable.subscribe(i -> System.out.println("Observer 2: " + i));
        Thread.sleep(5000);
    }
}
