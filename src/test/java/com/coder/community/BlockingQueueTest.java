package com.coder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class BlockingQueueTest {
    public static void main(String[] args) {
        ArrayBlockingQueue queue = new ArrayBlockingQueue(10);
        new Thread(new producer(queue)).start();
        new Thread(new consumer(queue)).start();
        new Thread(new consumer(queue)).start();
        new Thread(new consumer(queue)).start();
        new Thread(new consumer(queue)).start();
    }
}

class producer implements Runnable{
    private ArrayBlockingQueue<String> queue;
    public producer(ArrayBlockingQueue queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(50);
                queue.put("product:" + i);
                System.out.println(Thread.currentThread().getName() + "生产了" + queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class consumer implements Runnable{
    private ArrayBlockingQueue<String> queue;
    public consumer(ArrayBlockingQueue queue){
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            while (true){
                Thread.sleep(new Random().nextInt(1000));
                String s = queue.take();
                System.out.println(Thread.currentThread().getName() + "消费了"+ s
                        +"，现在池子：" + queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
