package no.difi.move.common.io.pipe;

import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;

import java.io.PipedOutputStream;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class Plumber {

    private final TaskExecutor taskExecutor;

    public Pipe pipe(String description, Consumer<PipedOutputStream> consumer, Reject reject) {
        return Pipe.of(taskExecutor, description, consumer, reject);
    }
}
