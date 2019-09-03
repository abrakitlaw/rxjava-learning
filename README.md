# RxJava

RX = OBSERVABLE + OBSERVER + SCHEDULERS

Observable -- any object whose state may be of interest, and in whom another object may register an interest
Observer -- any object that wishes to be notified when the state of another object changes
Schedulers -- used to schedule a thread or task that executes at a certain period of time or periodically at a fixed interval

RxJava is a Java based implementation of Reactive Programming.

RxAndroid is specific to Android platform which utilises some classes on top of the RxJava library.

Reactive Programming is a programming paradigm oriented around data flows and the propagation of change i.e. it is all about responding to value changes. For example, let’s say we define x = y+z. When we change the value of y or z, the value of x automatically changes. This can be done by observing the values of y and z.

Why RxJava?
1. Asynchronous streams
2. Functional approach
3. Caching is easy
4. Operators with Schedulers
5. Using of Subjects

#### 1. Asynchronous streams
You need to send a request to the database, and then you should start getting both messages and settings immediately. After everything is completed, you need a welcome message to be displayed.

If we want to do the same in Java SE or Android, we have to do all of the following:

1. Run 3-4 different AsyncTasks
2. Create a semaphore that waits for both requests to complete (settings and messages)
3. Create fields at the Objects level to store the results

by using RxJava can simplify.

Observable.fromCallable(createNewUser())
        .subscribeOn(Schedulers.io())
        .flatMap(new Func1<User, Observable<Pair<Settings, List<Message>>>>() {
                //  works with settings})
        .doOnNext(new Action1<Pair<Settings, List<Message>>>() {
            @Override
            public void call(Pair<Settings, List<Message>> pair) {
                System.out.println("Received settings" + pair.first);
            }
        })
        .flatMap(new Func1<Pair<Settings, List<Message>>, Observable<Message>>() {
                    //works with messages)
        .subscribe(new Action1<Message>() {
            @Override
            public void call(Message message) {
                System.out.println("New message " + message);
            }
        });

#### 2. Functional approach
We can say that functional programming consists of the active use of functions as the parameters as well as results in other functions. For example, map is a function of higher order, used in many programming languages. It is applied to each element in the list, returning a list of results.

Observable.from(jsonFile)
            .map(new Func1<File, String>() {
        @Override public String call(File file) {
            try {
                return new Gson().toJson(new FileReader(file), Object.class);
            } catch (FileNotFoundException e) {
                // this exception is a part of rx-java
                throw OnErrorThrowable.addValueAsLastCause(e, file);
            }
        }
    });
 
#### 3. Caching become easy
The next piece of code uses the caching method in the intent that n only one copy saves its result after it was successful for the first time.

Single < List < Todo >> todosSingle = Single.create(emitter - > {
      Thread thread = new Thread(() - > {
          try {
              List < Todo > todosFromWeb = // query a webservice
                  System.out.println("I am only called once!");
              emitter.onSuccess(todosFromWeb);
          } catch (Exception e) {
              emitter.onError(e);
          }
      });
      thread.start();
  }); 
  
  // cache the result of the single, so that the web query is only done once
      Single < List < Todo >> cachedSingle = todosSingle.cache();

#### 4. Operator with schedulers
There is a variety of operators which require to define Scheduler in order to function. At the same time, they have their overloaded methods that use computation(), delay () as a Scheduler
example:
TestSubscriber<Integer> subscriber = new TestSubscriber<>();
Observable.just(1).delay(1, TimeUnit.SECONDS).subscribe(subscriber);
subscriber.awaitTerminalEvent();
Logger.d("LastSeenThread: " + subscriber.getLastSeenThread().getName());
  
#### 5. Using objects
Working with Objects, you should take into account, that by default the sequence of changes in data, sent to onNext subject will be executed(implemented)in the same thread, that was used to call onNext() method, until observeOn() operator doesn’t appear in this sequence.
example:
BehaviorSubject<Object> subject = BehaviorSubject.create();
                subject
                .doOnNext(obj -> Logger.logThread("doOnNext"))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onNext(Object o) {
                        Logger.logThread("onNext");
                    }
                });
        subject.onNext("str");
        Handler handler = new Handler();
        handler.postDelayed(() -> subject.onNext("str"), 1000);
  
## Advantages using RxJava:
1. Handling the cache without creating caching classes
2. Combining the reception of requests and results processing and getting rid of standard AsyncTask
3. Decreasing memory leak by 90%
4. Optimizing the code to increase an application response
5. Making methods easier to combine

## Operator
Operators allow you to manipulate the data that was emitted or create new Observables.

#### Operators for creating Observables.
1. just()
This operator emits the same values provided in the arguments
example:
Observable.just(1, 2, 3, 4, 5)

2. from()
This operator takes array of an object as input and emits the object one after another same as just() operator
example:
Observable.from(new Integer[]{1, 2, 3, 4, 5});

#### Filtering Operator
1. filter()
This operator filter operator filters items emitted by an Observable
Observable
    .just(1, 2, 3, 4, 5)
    .filter(new Func1<Integer, Boolean>() {
        @Override
        public Boolean call(Integer integer) {
            //check if the number is odd? If the number is odd return true, to emmit that object.
            return integer % 2 != 0;
        }
    });

2. skip()
Skip(n) will suppress the first n items emitted by an Observable and emits data after n elements. So, skip(2) will emit first 2 elements and starts from emitting the 3rd element.
Observable<Integer> observable = Observable.from(new Integer[]{1,2,3,4,5})  //emit 1 to 5
        .skip(2);   //Skip first two elements

observable
        .subscribeOn(Schedulers.newThread())
        .observeOn(Schedulers.io())
        .subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.d("Observer", "Output:" + integer);
            }
        });
//Output will be : 3, 4, 5

3. take()
take() is counter to the skip() operator. take(n) will emit only first n data elements and ignores all data elements after n elements emitted.
  takeLast() = operator will emit only the last element from the data stream.
  takeFirst() = perator will emit only first element of the data stream and ignores subsequent data elements.

#### Combining Operator
1. concat() 
This operator to concat two different observable and emit the data stream for both the operators one after another.
example:
Observable<Integer> observer1 = Observable.from(new Integer[]{1, 2, 3, 4, 5});  //Emit 1 to 5
Observable<Integer> observer2 = Observable.from(new Integer[]{6, 7, 8, 9, 10}); //Emit 6 to 10

Observable.concat(observer1, observer2) //Concat the output of both the operators.
        .subscribeOn(Schedulers.newThread())
        .observeOn(Schedulers.io())
        .subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.d("Observer", "Output:" + integer);
            }
        });
  
2. merge()
merge() operator works same as the concat() operator and combines data stream from two different observables. The only difference between merge() and concat() operator is merge can interleave the outputs, while concat will first wait for earlier streams to finish before processing later streams.

3. zip()
zip() operator combine the emissions of multiple Observables together via a specified function and emit single items for each combination based on the results of this function.
example:
//Class that combines both data streams
class ZipObject {
    int number; 
    String alphabet;
}

Observable<Integer> observable1 = Observable.from(new Integer[]{1, 2, 3, 4, 5});  //Emits integers
Observable<String> observable2 = Observable.from(new String[]{"A", "B", "C", "D", "F"});  //Emits alphabets
Observable<ZipObject> observable = Observable.zip(observable1, observable2,   
    //Function that define how to zip outputs of both the stream into single object.
    new Func2<Integer, String, ZipObject>() { 
        @Override
        public ZipObject call(Integer integer, String s) {
            ZipObject zipObject = new ZipObject();
            zipObject.alphabet = s;
            zipObject.number = integer;
            return zipObject;
        }
    });

observable
    .subscribeOn(Schedulers.newThread())
    .observeOn(Schedulers.io())
    .subscribe(new Action1<ZipObject>() {
        @Override
        public void call(ZipObject zipObject) {
            Log.d("Observer", "Output:" + zipObject.number + " " + zipObject.alphabet);
        }
    });
}

#### Transforming Operator
1. buffer()
This operator periodically gather items from an Observable into bundles and emit these bundles rather than emitting the items one at a time.
example:
Observable.just("A", "B", "C", "D", "E", "F")
                .buffer(2)
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<String> strings) {
                        System.out.println("onNext(): ");
                        for (String s : strings) {
                            System.out.println("String: " + s);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
-- output --               
onNext(): 
String: A
String: B
onNext(): 
String: C
String: D
onNext(): 
String: E
String: F

2. map()
This operator transforms the items emitted by an Observable by applying a function to each item. map() operator allows for us to modify the emitted item from the Observable and then emits the modified item.
example:
getOriginalObservable()
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(final Integer integer)  {
                        return (integer * 2);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
 });


private Observable<Integer> getOriginalObservable() {
        final List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6);

        return Observable
                .create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                for(Integer integer : integers) {

                    if (!emitter.isDisposed()) {
                        emitter.onNext(integer);
                    }
                }

                if(!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }

        });
    }
    
-- output --
onNext: 2
onNext: 4
onNext: 6
onNext: 8
onNext: 10
onNext: 12

3. flatmap()
This operator transforms each item emitted by an Observable but instead of returning the modified item, it returns the Observable itself which can emit data again. In other words, they merge items emitted by multiple Observables and returns a single Observable. The important difference between FlatMap and other transformation operators is that the order in which the items are emitted is not maintained.
example:
getOriginalObservable()
                .flatMap(new Function<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> apply(final Integer integer)  {
                        return getModifiedObservable(integer);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        Thread.sleep(2000);
    }
  
/*   
 * Here we are creating an Observable that iterates through the list of 
 * integers, and emits each integer.
 */
    private Observable<Integer> getOriginalObservable() {
        final List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6);

        return Observable
                .create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                for(Integer integer : integers) {

                    if (!emitter.isDisposed()) {
                        emitter.onNext(integer);
                    }
                }

                if(!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }

        });
    }

    private Observable<Integer> getModifiedObservable(final Integer integer) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws InterruptedException {
                emitter.onNext((integer * 2));
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }
    
-- output --
onNext: 2
onNext: 6
onNext: 10
onNext: 12
onNext: 4
onNext: 8

4. switchMap()
Whenever a new item is emitted by the Observable, it will unsubscribe to the Observable that was generated from the previously emitted item and begin only mirroring the current one. In other words, it returns the latest Observable and emits the items from it.
example:
getOriginalObservable()
                .switchMap(new Function<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> apply(final Integer integer)  {
                        return getModifiedObservable(integer);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    private Observable<Integer> getOriginalObservable() {
        final List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6);

        return Observable
                .create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                for(Integer integer : integers) {

                    if (!emitter.isDisposed()) {
                        emitter.onNext(integer);
                    }
                }

                if(!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }

        });
    }

    private Observable<Integer> getModifiedObservable(final Integer integer) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws InterruptedException {
                emitter.onNext((integer * 2));
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

-- output --
onNext: 12

5. concatMap()
This operator functions the same way as flatMap(), the difference being in concatMap() the order in which items are emitted are maintained. One disadvantage of concatMap() is that it waits for each observable to finish all the work until next one is processed.
example:
 getOriginalObservable()
                .concatMap(new Function<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> apply(final Integer integer)  {
                        return getModifiedObservable(integer);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    private Observable<Integer> getOriginalObservable() {
        final List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6);

        return Observable
                .create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                for(Integer integer : integers) {

                    if (!emitter.isDisposed()) {
                        emitter.onNext(integer);
                    }
                }

                if(!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }

        });
    }

    private Observable<Integer> getModifiedObservable(final Integer integer) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws InterruptedException {
                emitter.onNext((integer * 2));
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }
    
-- output -- 
onNext: 2
onNext: 4
onNext: 6
onNext: 8
onNext: 10
onNext: 12

6. groupBy()
This operator divides an Observable into a set of Observables that each emit a different group of items from the original Observable, organised by key.
example:
/*   
 * We will create an Observable with range of 1 to 10 numbers.
 * We use the groupBy operator to emit only even numbers from the list.
 * The output will be 2,4,6,8,10
 */
Observable.range(1, 10)
                .groupBy(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) {
                        return (integer % 2 == 0) ? true : false;
                    }
                })
                .subscribe(new Observer<GroupedObservable<Boolean, Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GroupedObservable<Boolean, Integer> booleanIntegerGroupedObservable) {
                        if(booleanIntegerGroupedObservable.getKey()) {
                            booleanIntegerGroupedObservable.subscribe(new Observer<Integer>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(Integer integer) {
                                    System.out.println("onNext: " + integer);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
-- output --
onNext: 2
onNext: 4
onNext: 6
onNext: 8
onNext: 10

7. scan()
This operator Transform each item into another item, like you did with map. But also include the “previous” item when you get around to doing a transform.
example:
 Observable.range(1, 10)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) {
                        return (integer + integer2);
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
-- output -- 
onNext: 1
onNext: 3
onNext: 6
onNext: 10
onNext: 15
onNext: 21
onNext: 28
onNext: 36
onNext: 45
onNext: 55
