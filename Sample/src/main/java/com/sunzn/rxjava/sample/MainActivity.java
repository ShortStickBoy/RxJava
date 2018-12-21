package com.sunzn.rxjava.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Notification;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.FuncN;
import rx.observables.ConnectableObservable;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;
import rx.schedulers.Timestamped;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "RxJava";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Creating Observables
                 */
//                single();
//                completable();
//                range();
//                defer();
//                from();
//                interval();
//                repeat();
//                timer();

                /*
                Transforming Observables
                 */
//                buffer();
//                bufferTime();
//                flatMap();
//                groupBy();
//                map();
//                scan();
//                window();
//                windowTime();
//                throttleWithTimeout();

                /*
                Filtering Observables
                 */
//                debounce();
//                distinct();
//                distinctUntilChanged();
//                elementAt();
//                filter();
//                first();
//                blocking();
//                skip();
//                take();
//                sample();
//                throttleFirst();

                /*
                Combining Observables
                 */
//                combineLatest();
//                join();
//                merge();
//                mergeDelayError();
//                startWith();
//                switchOnNext();
//                zip();

                /*
                Error Handling Operators
                 */
//                onErrorReturn();
//                onErrorResumeNext();
//                retry();

                /*
                Observable Utility Operators
                 */
//                delay();
//                doAction();
//                materialize();
//                Dematerialize();
//                ObserveOn();
//                TimeInterval();
//                Timestamp();
//                Timeout();
//                Using();

                /*
                Conditional and Boolean Operators
                 */
//                All();
//                Amb();
//                Contains();
//                isEmpty();
//                DefaultIfEmpty();
//                SequenceEqual();
//                SkipUntil();
//                SkipWhile();
//                TakeUntil();
//                TakeWhile();

                /*
                Mathematical and Aggregate Operators
                 */
//                Concat();
//                Count();
//                Reduce();
//                Collect();

                /* Connectable Observable Operators */
//                Collect();
//                Connect();
//                RefCount();
//                Replay();

                /* Custom Operator */
//                Lift();
                Compose();

                /* Scheduler */
//                Scheduler();
//                SchedulerFrom();

                /* Sample */
                Pi();
            }
        });

    }

    private void Pi() {
        getPi(5, 10000000).subscribe(new Action1<Double>() {
            @Override
            public void call(Double aDouble) {
                Log.e(TAG, "Pi : " + aDouble);
            }
        });
    }

    private Observable<Double> getPi(int workNum, int num) {
        ArrayList<Observable<Double>> list = new ArrayList<>();
        for (int i = 0; i < workNum; i++) {
            list.add(createPiObservable(num));
        }

        return Observable.zip(list, new FuncN<Double>() {
            @Override
            public Double call(Object... args) {
                int len = args.length;
                double result = 0;
                for (Object arg : args) {
                    result += (Double) (arg);
                }
                return result / len;
            }
        });
    }

    private Observable<Double> createPiObservable(final int num) {
        final Random random = new Random();
        return Observable.range(0, num)
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        double x = random.nextDouble() * 2 - 1;
                        double y = random.nextDouble() * 2 - 1;
                        return (x * x + y * y) < 1 ? 1 : 0;
                    }
                }).reduce(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                }).map(new Func1<Integer, Double>() {
                    @Override
                    public Double call(Integer integer) {
                        return 4.0 * integer / num;
                    }
                }).subscribeOn(Schedulers.computation());
    }

    private void SchedulerFrom() {

        Executor executor = new ThreadPoolExecutor(
                2,
                2,
                2000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1000),
                new SimpleThreadFactory()
        );

        Scheduler schedulers = Schedulers.from(executor);
        Observable.interval(1, TimeUnit.SECONDS).take(5)
                .observeOn(schedulers)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e(TAG, "[" + aLong + "] : " + Thread.currentThread().getName());
                    }
                });

    }

    class SimpleThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    }

    private void Scheduler() {
        Observable.unsafeCreate(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Log.e(TAG, "call : " + Thread.currentThread().getName());
                subscriber.onNext(1);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                Log.e(TAG, "map : " + Thread.currentThread().getName());
                return integer + 1;
            }
        }).observeOn(Schedulers.io()).map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                Log.e(TAG, "map : " + Thread.currentThread().getName());
                return integer + 1;
            }
        }).subscribeOn(Schedulers.computation()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, "subscribe : " + Thread.currentThread().getName());
            }
        });
    }

    private void Compose() {
        composeObservable().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, s);
            }
        });
    }

    private Observable<String> composeObservable() {
        Observable.Transformer<Integer, String> transformer = new Observable.Transformer<Integer, String>() {
            @Override
            public Observable<String> call(Observable<Integer> integerObservable) {
                return integerObservable.map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "Transformer : " + integer;
                    }
                }).doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(TAG, "doOnNext : " + s);
                    }
                });
            }
        };

        return Observable.just(1, 2, 3, 4).compose(transformer);
    }

    private void Lift() {
        liftObserver().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, s);
            }
        });

    }

    private Observable<String> liftObserver() {
        Observable.Operator<String, String> operator = new Observable.Operator<String, String>() {
            @Override
            public Subscriber<? super String> call(final Subscriber<? super String> subscriber) {
                return new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(e);
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext("operator : " + s);
                        }
                    }
                };
            }
        };

        return Observable.just(1, 2, 3, 4).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                return "map_1 : " + integer;
            }
        }).lift(operator).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return "map_2 : " + s;
            }
        });

    }

    private void Replay() {
        final ConnectableObservable observable = Observable.interval(1, TimeUnit.SECONDS)
                .take(8)
                .observeOn(Schedulers.newThread())
                .replay(2);

        final Action1 action1 = new Action1<Long>() {
            @Override
            public void call(Long o) {
                Log.e(TAG, "Action1 : " + o);
            }
        };

        Action1 action2 = new Action1<Long>() {
            @Override
            public void call(Long o) {
                Log.e(TAG, "Action1 : " + o);
                if (o == 3) {
                    observable.subscribe(action1);
                }
            }
        };

        observable.subscribe(action2);

        observable.connect();
    }

    private void RefCount() {
        Observable.interval(1, TimeUnit.SECONDS).take(5).publish().refCount()
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e(TAG, "Action1 : " + aLong);
                    }
                });
    }

    private void Connect() {
        final ConnectableObservable observable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.newThread())
                .publish();

        final Action1<Long> action1 = new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Log.e(TAG, "Action1 : " + aLong);
            }
        };

        Action1<Long> action11 = new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                if (aLong == 3) {
                    Log.e(TAG, "Action2 : " + aLong);
                    observable.subscribe(action1);
                }
            }
        };

        observable.subscribe(action11);

        observable.connect();
    }

    private void Collect() {
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(i);
        }

        Observable.from(data).collect(new Func0<ArrayList<Integer>>() {
            @Override
            public ArrayList<Integer> call() {
                return new ArrayList<>();
            }
        }, new Action2<ArrayList<Integer>, Integer>() {
            @Override
            public void call(ArrayList<Integer> integers, Integer integer) {
                if (integer < 6) {
                    integers.add(integer);
                }
            }
        }).subscribe(new Action1<ArrayList<Integer>>() {
            @Override
            public void call(ArrayList<Integer> integers) {
                Log.e(TAG, integers.toString());
            }
        });
    }

    private void Reduce() {
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(2);
        }

        Observable.from(data).reduce(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                return integer * integer2;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, integer.toString());
            }
        });
    }

    private void Count() {
        Observable.just(1, 2, 3).count().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, integer.toString());
            }
        });
    }

    private void Concat() {
        Observable.concat(Observable.just(1, 2, 3), Observable.just(4, 5, 6), Observable.just(7, 8, 9))
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, integer.toString());
                    }
                });
    }

    private void TakeWhile() {
        Observable.interval(1, TimeUnit.SECONDS)
                .takeWhile(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong < 6;
                    }
                }).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Log.e(TAG, aLong.toString());
            }
        });
    }

    private void TakeUntil() {
        Observable.interval(1, TimeUnit.SECONDS)
                .takeUntil(Observable.timer(5, TimeUnit.SECONDS))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e(TAG, aLong.toString());
                    }
                });
    }

    private void SkipWhile() {
        Observable.interval(1, TimeUnit.SECONDS)
                .skipWhile(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong < 5;
                    }
                }).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Log.e(TAG, aLong.toString());
            }
        });
    }

    private void SkipUntil() {
        Observable.interval(1, TimeUnit.SECONDS)
                .skipUntil(Observable.timer(4, TimeUnit.SECONDS))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e(TAG, aLong.toString());
                    }
                });
    }

    private void SequenceEqual() {
        Observable.sequenceEqual(Observable.just(1, 2, 3), Observable.just(1, 2, 4))
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Log.e(TAG, aBoolean.toString());
                    }
                });
    }

    private void DefaultIfEmpty() {
        Observable.unsafeCreate(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
//                subscriber.onNext(9);
                subscriber.onCompleted();
            }
        }).defaultIfEmpty(10).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, integer.toString());
            }
        });
    }

    private void isEmpty() {
        Observable.unsafeCreate(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(1);
                subscriber.onCompleted();
            }
        }).isEmpty().subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                Log.e(TAG, aBoolean.toString());
            }
        });
    }

    private void Contains() {
        Observable.unsafeCreate(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 6; i++) {
                    Log.e(TAG, "onNext : " + i);
                    subscriber.onNext(i);
                }
                Log.e(TAG, "onCompleted");
                subscriber.onCompleted();
            }
        }).contains(7).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                Log.e(TAG, aBoolean.toString());
            }
        });
    }

    private void Amb() {
        Observable.amb(
                Observable.just(1, 2, 3).delay(3000, TimeUnit.MILLISECONDS),
                Observable.just(4, 5, 6).delay(2000, TimeUnit.MILLISECONDS),
                Observable.just(7, 8, 9).delay(1000, TimeUnit.MILLISECONDS)
        ).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, integer.toString());
            }
        });
    }

    private void All() {
        Observable.interval(1, TimeUnit.SECONDS).take(7).all(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLong) {
                return aLong < 5;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                Log.e(TAG, aBoolean.toString());
            }
        });
    }

    private void Using() {
        Observable.using(new Func0<Animal>() {
            @Override
            public Animal call() {
                return new Animal();
            }
        }, new Func1<Animal, Observable<?>>() {
            @Override
            public Observable<?> call(Animal animal) {
                return Observable.timer(10000, TimeUnit.MILLISECONDS);
            }
        }, new Action1<Animal>() {
            @Override
            public void call(Animal animal) {
                animal.release();
            }
        }).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError : " + e.toString());
            }

            @Override
            public void onNext(Object o) {
                Log.e(TAG, "onNext : " + o);
            }
        });
    }

    private class Animal {

        Subscriber<Long> subscriber = new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "Animal eat");
            }
        };


        public Animal() {

            Log.e(TAG, "Create Animal");

            Observable.interval(1000, TimeUnit.MILLISECONDS).subscribe(subscriber);

        }

        public void release() {

            Log.e(TAG, "Animal Release");
            subscriber.unsubscribe();

        }
    }

    private void Timeout() {
        Observable.unsafeCreate(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i <= 3; i++) {
                    try {
                        Thread.sleep(i * 100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        })
//                .timeout(200, TimeUnit.MILLISECONDS)
                .timeout(200, TimeUnit.MILLISECONDS, Observable.just(5, 6))
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError : " + e.toString());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext : " + integer.toString());
                    }
                });
    }

    private void Timestamp() {
        Observable.interval(1, TimeUnit.SECONDS).take(3).timestamp()
                .subscribe(new Action1<Timestamped<Long>>() {
                    @Override
                    public void call(Timestamped<Long> longTimestamped) {
                        Log.e(TAG, "timeInterval : " + longTimestamped.getValue() + " - " + longTimestamped.getTimestampMillis());
                    }
                });
    }

    private void TimeInterval() {
        Observable.interval(1, TimeUnit.SECONDS).take(3).timeInterval()
                .subscribe(new Action1<TimeInterval<Long>>() {
                    @Override
                    public void call(TimeInterval<Long> longTimeInterval) {
                        Log.e(TAG, "timeInterval : " + longTimeInterval.getValue() + " - " + longTimeInterval.getIntervalInMilliseconds());
                    }
                });
    }

    private void ObserveOn() {
        Observable.unsafeCreate(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Log.e(TAG, "subscribeOn : " + Thread.currentThread().getName());
                subscriber.onNext(1);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, "observeOn : " + Thread.currentThread().getName());
                    }
                });

    }

    private void Dematerialize() {
        Observable.just(1, 2, 3).materialize().dematerialize().subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                Log.e(TAG, o.toString());
            }
        });
    }

    private void materialize() {
        Observable.just(1, 2, 3).materialize()
                .subscribe(new Action1<Notification<Integer>>() {
                    @Override
                    public void call(Notification<Integer> integerNotification) {
                        Log.e(TAG, "materialize：" + integerNotification.getValue() + " type：" + integerNotification.getKind());
                    }
                });
    }

    private void doAction() {
        Observable.just(1, 2, 3, 4).doOnEach(new Action1<Notification<? super Integer>>() {
            @Override
            public void call(Notification<? super Integer> notification) {
                Log.e(TAG, "doOnEach：" + notification.getValue());
            }
        }).doOnNext(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, "doOnNext：" + integer);
            }
        }).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                Log.e(TAG, "doOnSubscribe");
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                Log.e(TAG, "doOnUnsubscribe");
            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
                Log.e(TAG, "doOnCompleted");
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, "call : " + integer);
            }
        });
    }

    private void delay() {
        Observable.unsafeCreate(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Log.e(TAG, "订阅时间：" + System.currentTimeMillis());
                for (int i = 0; i < 3; i++) {
                    subscriber.onNext(i);
                }
            }
        }).delay(2000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, "延迟时间：" + System.currentTimeMillis() + " [" + integer + "]");
                    }
                });
    }

    private void retry() {
        Observable.unsafeCreate(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 3; i++) {
                    if (i == 2) {
                        subscriber.onError(new Exception("Exception"));
                    } else {
                        subscriber.onNext(i);
                    }
                }
            }
        }).retry(2).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, integer.toString());
            }
        });
    }

    private void onErrorResumeNext() {
        Observable.unsafeCreate(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for (int i = 0; i < 6; i++) {
                    if (i < 3) {
                        subscriber.onNext("onNext : " + i);
                    } else {
                        subscriber.onError(new Throwable("onError"));
                    }
                }
            }
        }).onErrorResumeNext(Observable.just("7", "8", "9"))
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError" + e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, s);
                    }
                });
    }

    private void onErrorReturn() {
        Observable.unsafeCreate(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for (int i = 0; i < 6; i++) {
                    if (i < 3) {
                        subscriber.onNext("onNext : " + i);
                    } else {
                        subscriber.onError(new Throwable("onError"));
                    }
                }
            }
        }).onErrorReturn(new Func1<Throwable, String>() {
            @Override
            public String call(Throwable throwable) {
                Log.e(TAG, "onErrorReturn : " + throwable.getMessage());
                return "Get An Error";
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError" + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, s);
            }
        });
    }

    private void zip() {
        Observable.interval(100, TimeUnit.MILLISECONDS).take(2).map(new Func1<Long, String>() {
            @Override
            public String call(Long aLong) {
                return "2 : " + aLong;
            }
        }).zipWith(Observable.interval(100, TimeUnit.MILLISECONDS).take(3).map(new Func1<Long, String>() {
            @Override
            public String call(Long aLong) {
                return "3 : " + aLong;
            }
        }), new Func2<String, String, String>() {
            @Override
            public String call(String s, String s2) {
                return s + " - " + s2;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, s);
            }
        });
    }

    private void switchOnNext() {
        Observable.switchOnNext(Observable.interval(3000, TimeUnit.MILLISECONDS).take(3).map(new Func1<Long, Observable<String>>() {
            @Override
            public Observable<String> call(Long aLong) {
                return createObservable(aLong);
            }

            private Observable<String> createObservable(final Long index) {
                return Observable.interval(1000, 1000, TimeUnit.MILLISECONDS).take(5).map(new Func1<Long, String>() {
                    @Override
                    public String call(Long aLong) {
                        return index + " - " + aLong;
                    }
                });
            }
        })).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, s);
            }
        });
    }

    private void startWith() {
        Observable.just(1, 2, 3, 4).startWith(-1, 0)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, integer.toString());
                    }
                });
    }

    private void mergeDelayError() {
        Observable.mergeDelayError(Observable.unsafeCreate(new Observable.OnSubscribe<Observable<Integer>>() {
            @Override
            public void call(Subscriber<? super Observable<Integer>> subscriber) {
                for (int i = 0; i < 5; i++) {
                    if (i == 3) {
                        subscriber.onError(new Throwable("Error"));
                    }
                    subscriber.onNext(Observable.just(i));
                }
            }
        }), Observable.unsafeCreate(new Observable.OnSubscribe<Observable<Integer>>() {
            @Override
            public void call(Subscriber<? super Observable<Integer>> subscriber) {
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext(Observable.just(5 + i));
                }
                subscriber.onCompleted();
            }
        })).subscribe(new Subscriber<Observable<Integer>>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError");
            }

            @Override
            public void onNext(Observable<Integer> integerObservable) {
                integerObservable.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, integer.toString());
                    }
                });
            }
        });
    }

    private void merge() {
        Observable.merge(Observable.just(1, 2, 3, 4), Observable.just(6, 7, 8, 9))
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, integer.toString());
                    }
                });
    }

    private void join() {
        Observable.just("A", "B", "C").join(Observable.just(1, 2, 3), new Func1<String, Observable<Long>>() {
            @Override
            public Observable<Long> call(String s) {
                return Observable.timer(1000, TimeUnit.MILLISECONDS);
            }
        }, new Func1<Integer, Observable<Long>>() {
            @Override
            public Observable<Long> call(Integer integer) {
                return Observable.timer(500, TimeUnit.MILLISECONDS);
            }
        }, new Func2<String, Integer, String>() {
            @Override
            public String call(String s, Integer integer) {
                return s + " : " + integer;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, s);
            }
        });
    }

    private void combineLatest() {
        Observable.combineLatest(Observable.interval(500, TimeUnit.MILLISECONDS), Observable.interval(500 * 2, TimeUnit.MILLISECONDS), new Func2<Long, Long, String>() {
            @Override
            public String call(Long aLong, Long aLong2) {
                return aLong + " <--> " + aLong2;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, s);
            }
        });
    }

    private void throttleFirst() {
        Observable.interval(200, TimeUnit.MILLISECONDS)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e(TAG, aLong.toString());
                    }
                });
    }

    private void sample() {
        Observable.interval(200, TimeUnit.MILLISECONDS)
                .sample(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e(TAG, aLong.toString());
                    }
                });
    }

    private void take() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).take(4)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, integer.toString());
                    }
                });
    }

    private void skip() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).skip(4)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, integer.toString());
                    }
                });
    }

    private void blocking() {
        Integer result = Observable.just(1, 2, 3, 5, 4, 5, 6).toBlocking()
                .first(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 3;
                    }
                });
        Log.e(TAG, result.toString());
    }

    private void first() {
        Observable.just(1, 2, 2, 4, 8, 6, 7).first(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer > 5;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, integer.toString());
            }
        });
    }

    private void filter() {
        Observable.just(1, 2, 3, 4, 5, 6, 7).filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer % 2 == 0;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, integer.toString());
            }
        });
    }

    private void elementAt() {
        Observable.just(0, 1, 2, 3, 4, 5, 6).elementAt(3)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, integer.toString());
                    }
                });
    }

    private void distinctUntilChanged() {
        Observable.just(1, 2, 2, 3, 1, 4, 2, 4, 2, 5).distinctUntilChanged()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, integer.toString());
                    }
                });
    }

    private void distinct() {
        Observable.just(1, 2, 3, 1, 4, 2, 4, 2, 5).distinct()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, integer.toString());
                    }
                });
    }

    private void debounce() {
        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .debounce(new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long aLong) {
                        return Observable.timer(aLong % 2 * 1500, TimeUnit.MILLISECONDS);
                    }
                }).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Log.e(TAG, aLong.toString());
            }
        });
    }

    private void throttleWithTimeout() {
        Observable.unsafeCreate(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 10; i++) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(i);
                    }
                    int sleep = 100;
                    if (i % 3 == 0) {
                        sleep = 300;
                    }
                    try {
                        Thread.sleep(sleep);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation())
                .throttleWithTimeout(200, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e(TAG, integer.toString());
                    }
                });
    }

    private void windowTime() {
        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .window(3000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Observable<Long>>() {
                    @Override
                    public void call(Observable<Long> longObservable) {
                        Log.e(TAG, "Time " + System.currentTimeMillis() / 1000);
                        longObservable.subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                Log.e(TAG, aLong.toString());
                            }
                        });
                    }
                });
    }

    private void window() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).window(3)
                .subscribe(new Action1<Observable<Integer>>() {
                    @Override
                    public void call(Observable<Integer> integerObservable) {
                        Log.e(TAG, integerObservable.getClass().getName());
                        integerObservable.subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                Log.e(TAG, integer.toString());
                            }
                        });
                    }
                });
    }

    private void scan() {
        Observable.from(new Integer[]{2, 3, 6, 5, 3})
                .scan(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer x, Integer y) {
                        Log.e(TAG, "x = " + x + "   y = " + y);
                        return x * y;
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, integer.toString());
            }
        });
    }

    private void map() {
        Observable.just(1, 2, 3)
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        return integer * 10;
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, integer.toString());
            }
        });
    }

    private void groupBy() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .groupBy(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        return integer % 2;
                    }
                }).subscribe(new Action1<GroupedObservable<Integer, Integer>>() {
            @Override
            public void call(GroupedObservable<Integer, Integer> integerIntegerGroupedObservable) {
                if (integerIntegerGroupedObservable.getKey() == 0) {
                    integerIntegerGroupedObservable.subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            Log.e(TAG, integer.toString());
                        }
                    });
                }
            }
        });
    }

    private void flatMap() {
        Observable.just(1, 2, 3)
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override
                    public Observable<String> call(Integer integer) {
                        return Observable.just("flatMap : " + integer);
                    }
                }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, s);
            }
        });
    }

    private void bufferTime() {
        Observable.interval(1, TimeUnit.SECONDS)
                .buffer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Long>>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError");
                    }

                    @Override
                    public void onNext(List<Long> longs) {
                        Log.e(TAG, longs.toString());
                    }
                });
    }

    private void buffer() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).buffer(2, 3)
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError");
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Log.e(TAG, integers.toString());
                    }
                });
    }

    private void timer() {
        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, aLong.toString());
                    }
                });
    }

    private void repeat() {
        Observable.just(1, 2, 3).repeat(4)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, integer.toString());
                    }
                });
    }

    private void interval() {
        Observable.interval(10, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, aLong.toString());
                    }
                });
    }

    private void from() {
        Integer[] data = {0, 1, 2, 3, 4, 5};
        Observable.from(data).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, integer.toString());
            }
        });
    }

    private void defer() {
        Observable.defer(new Func0<Observable<Long>>() {
            @Override
            public Observable<Long> call() {
                return Observable.just(System.currentTimeMillis());
            }
        }).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Log.e(TAG, aLong.toString());
            }
        });
    }

    private void range() {
        Observable.range(10, 5).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, integer.toString());
            }
        });
    }

    private void completable() {
        Completable.error(new Throwable("Error"))
                .subscribe(new CompletableSubscriber() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.toString());
                    }

                    @Override
                    public void onSubscribe(Subscription d) {

                    }
                });
    }

    private void single() {
        Single.create(new Single.OnSubscribe<String>() {
            @Override
            public void call(SingleSubscriber<? super String> singleSubscriber) {
                singleSubscriber.onSuccess("Success");
            }
        }).subscribe(new SingleSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable error) {

            }
        });
    }
}
