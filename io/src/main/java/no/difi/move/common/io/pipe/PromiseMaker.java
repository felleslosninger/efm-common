package no.difi.move.common.io.pipe;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Function;

public class PromiseMaker {

    private final TaskExecutor taskExecutor;
    private final TransactionTemplate transactionTemplate;

    public PromiseMaker(TaskExecutor taskExecutor, ObjectProvider<TransactionTemplate> transactionTemplateObjectProvider) {
        this.taskExecutor = taskExecutor;
        this.transactionTemplate = transactionTemplateObjectProvider.getIfAvailable();
    }

    public <T> Promise<T> promise(Function<Reject, T> action) {
        return new Promise<>((resolve, reject) -> {
            try {
                T result = getExecute(action, reject);
                resolve.resolve(result);
            } catch (Exception e) {
                reject.reject(e);
            }
        }, taskExecutor);
    }

    @Nullable
    private <T> T getExecute(Function<Reject, T> action, Reject reject) {
        return transactionTemplate != null ?
            transactionTemplate.execute(status -> action.apply(reject))
            : action.apply(reject);
    }
}
