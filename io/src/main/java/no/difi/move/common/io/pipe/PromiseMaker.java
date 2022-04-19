package no.difi.move.common.io.pipe;

import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Function;

@RequiredArgsConstructor
public class PromiseMaker {

    private final TaskExecutor taskExecutor;
    private final TransactionTemplate transactionTemplate;

    public <T> Promise<T> promise(Function<Reject, T> action) {
        return new Promise<>((resolve, reject) -> {
            try {
                T result = transactionTemplate.execute(status -> action.apply(reject));
                resolve.resolve(result);
            } catch (Exception e) {
                reject.reject(e);
            }
        }, taskExecutor);
    }
}
