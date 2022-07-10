package xyz.mouse;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 任务计算单元需要实现的所有接口
 */
public interface ICalUnit<T> {

    void submit(Runnable task);

    void submit(Callable<T> task);

    void registerQueue(Queue<Future<T>> queue);

    void deRegisterQueue(Queue<Future<T>> queue);

    void registerSet(Set<Future<T>> map);

    void deRegisterSet(Set<Future<T>> map);

    /**
     * 检查已经完成了多少任务
     * @param tId 提交任务线程ID
     * @return 完成任务的个数
     */
    int checkProgress(String tId);

    /**
     * 检查是否所有任务完成
     * @param tId 提交任务线程ID
     * @return true 已完成 false 未完成
     */
    boolean checkIsFinished(String tId);

    /**
     * 注册异常处理器
     * @param handler 异常处理器，支持lambda表达式
     */
    void exceptionHandler(CalUnitExceptionHandler handler);
}
