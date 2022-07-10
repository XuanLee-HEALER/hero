package xyz.mouse;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CalUnit<T> implements ICalUnit<T> {

    private AtomicInteger counter;
    private ExecutorService pool;
    private Map<String, BlockingQueue<Future<T>>> callableResQueue;
    private Map<String, BlockingQueue<Future<?>>> runnableResQueue;

    private Map<String, BlockingQueue<Future<T>>> finishedCallableResQueue;
    private Map<String, BlockingQueue<Future<?>>> finishedRunnableResQueue;

    private CalUnit() {
        // 读取配置文件

    }

    public static <T> CalUnit<T> getInstance() {
        CalUnit<T> calUnit = new CalUnit<>();
        calUnit.counter = new AtomicInteger(0);
        calUnit.pool = new ForkJoinPool();
        calUnit.callableResQueue = new LinkedHashMap<>();
        calUnit.runnableResQueue = new LinkedHashMap<>();
        return calUnit;
    }

    public void setCallableResultQueue(String id, BlockingQueue<Future<T>> queue) {
        if (this.callableResQueue.containsKey(id)) {
            throw new src.main.java.xyz.mouse.WorkFactoryRuntimeException("线程" + id + "结果队列以存在，添加队列失败");
        }
        this.callableResQueue.put(id, queue);
    }

    public void setRunnableResultQueue(String id, BlockingQueue<Future<?>> queue) {
        if (this.runnableResQueue.containsKey(id)) {
            throw new src.main.java.xyz.mouse.WorkFactoryRuntimeException("线程" + id + "结果队列以存在，添加队列失败");
        }
        this.runnableResQueue.put(id, queue);
    }

    public void removeResultQueue(String id) {

    }

    public void submit(String id, Callable<T> work) {
        counter.getAndIncrement();
        if (!this.callableResQueue.containsKey(id)) {
            throw new src.main.java.xyz.mouse.WorkFactoryRuntimeException("请先注册结果队列再提交工作任务");
        }
        BlockingQueue<Future<T>> q = this.callableResQueue.get(id);
        q.add(this.pool.submit(work));
    }

    public void submit(String id, Runnable work) {
        if (!this.runnableResQueue.containsKey(id)) {
            throw new src.main.java.xyz.mouse.WorkFactoryRuntimeException("请先注册结果队列再提交工作任务");
        }
        BlockingQueue<Future<?>> q = this.runnableResQueue.get(id);
        q.add(this.pool.submit(work));
    }

    // 第几个任务已经完成，-1为无任务完成
    public int checkIfDone(String id) {
        boolean isCallable = this.callableResQueue.containsKey(id);
        boolean isRunnable = this.runnableResQueue.containsKey(id);

        if (isCallable) {
            BlockingQueue<Future<T>> cur = this.callableResQueue.get(id);
            int cc = 0;
            for (Future<T> t: cur) {
                if (t.isDone()) {
                    return cc;
                }
                cc++;
            }
        } else {
            BlockingQueue<Future<?>> cur = this.runnableResQueue.get(id);
            int cc = 0;
            for (Future<?> t: cur) {
                if (t.isDone()) {
                    return cc;
                }
                cc++;
            }
        }
        return -1;
    }

    public Future<T> getCallable(String id, int idx) {
        if (this.callableResQueue.containsKey(id)) {
            BlockingQueue<Future<T>> cur = this.callableResQueue.get(id);
            List<Future<T>> tl = new LinkedList<>();
            while (idx-- > 0) {
                tl.add(cur.poll());
            }
            cur.addAll(tl);
            return cur.poll();
        } else {
            throw new src.main.java.xyz.mouse.WorkFactoryRuntimeException("该线程未注册带返回值的结果队列");
        }
    }

    public Future<?> getRunnable(String id, int idx) {
        if (this.runnableResQueue.containsKey(id)) {
            BlockingQueue<Future<?>> cur = this.runnableResQueue.get(id);
            List<Future<?>> tl = new LinkedList<>();
            while (idx-- > 0) {
                tl.add(cur.poll());
            }
            cur.addAll(tl);
            return cur.poll();
        } else {
            throw new src.main.java.xyz.mouse.WorkFactoryRuntimeException("该线程未注册无返回值的结果队列");
        }
    }

    @Override
    public void submit(Runnable task) {

    }

    @Override
    public void submit(Callable<T> task) {

    }

    @Override
    public void registerQueue(Queue<Future<T>> queue) {

    }

    @Override
    public void deRegisterQueue(Queue<Future<T>> queue) {

    }

    @Override
    public void registerSet(Set<Future<T>> map) {

    }

    @Override
    public void deRegisterSet(Set<Future<T>> map) {

    }

    @Override
    public int checkProgress(String tId) {
        return 0;
    }

    @Override
    public boolean checkIsFinished(String tId) {
        return false;
    }

    @Override
    public void exceptionHandler(CalUnitExceptionHandler handler) {

    }
}
