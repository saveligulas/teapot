package com.sagu.fhv;

import com.sagu.fhv.pipeline.Pipe;
import com.sagu.fhv.pipeline.ThreadedPushFilter;
import com.sagu.fhv.pipeline.ThreadedPushFilterBuilder;
import com.sagu.fhv.pool.PushFilterPool;

import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello, World!");
        PushFilterPool<String> stringPushFilterPool = new PushFilterPool<>(100, new ThreadedPushFilterBuilder<String>() {
            @Override
            public ThreadedPushFilter<String> build() {
                return new ThreadedPushFilter<String>() {
                    private String string;

                    @Override
                    public void setInput(String s) {
                        string = s;
                    }

                    @Override
                    public void flush() {
                        string = null;
                    }

                    @Override
                    public void push(String s) {
                        System.out.println("Simulate push to Pipe");
                    }

                    @Override
                    public void consume(String s) {
                        setInput(s);
                        run();
                    }

                    @Override
                    public void setDownStreamPipe(Pipe<String> downStreamPipe) {
                        return;
                    }

                    @Override
                    public void run() {
                        System.out.println("Simulating Filter run: " + string);
                    }
                };
            }
        }, Executors.newVirtualThreadPerTaskExecutor());

        for (int i = 0; i < 1000; i++) {
            stringPushFilterPool.input(String.valueOf(i));
        }

        System.out.println("Finished input");
        Thread.sleep(100000);
    }
}