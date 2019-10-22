package org.apache.dubbo.common;

/**
 * Created by IntelliJ IDEA.
 * User: Luo
 * Date: 13-7-11
 * Time: 下午3:12
 */

public class TestShutdownHook {

    /**
     * 测试线程，用于模拟一个原子操作
     */
    private static class TaskThread extends Thread {
        @Override
        public void run() {
            System.out.println("thread begin ...");
            TestShutdownHook.sleep(2000);
            System.out.println("task 1 ok ...");
            TestShutdownHook.sleep(10000);
            System.out.println("task 2 ok ...");
            TestShutdownHook.sleep(2000);
            System.out.println("task 3 ok ...");
            TestShutdownHook.sleep(2000);
            System.out.println("task 4 ok ...");
            TestShutdownHook.sleep(2000);
            System.out.println("task 5 ok ...");

            System.out.println("thread end\n\n");
        }
    }

    /**
     * 注册hook程序，保证线程能够完整执行
     * @param checkThread
     */
    private static void addShutdownHook(final Thread checkThread) {
        //为了保证TaskThread不在中途退出，添加ShutdownHook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("收到关闭信号，hook起动，开始检测线程状态 ...");
                //不断检测一次执行状态，如果线程一直没有执行完毕，超时后，放弃等待       \
                for (int i = 0; i < 100; i++) {
                    if (checkThread.getState() == State.TERMINATED) {
                        System.out.println("检测到线程执行完毕，退出hook");
                        return;
                    }
                    TestShutdownHook.sleep(100);
                }
                System.out.println("检测超时，放弃等待，退出hook，此时线程会被强制关闭");
            }
        });
    }


    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final TaskThread taskThread = new TaskThread();
        //为了保证TaskThread不在中途退出，添加ShutdownHook
        addShutdownHook(taskThread);
        //执行TaskThread
        taskThread.start();
    }

}