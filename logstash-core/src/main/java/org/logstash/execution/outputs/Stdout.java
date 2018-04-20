package org.logstash.execution.outputs;

import org.logstash.Event;
import org.logstash.execution.LogstashPlugin;
import org.logstash.execution.LsConfiguration;
import org.logstash.execution.LsContext;
import org.logstash.execution.Output;
import org.logstash.execution.plugins.PluginConfigSpec;
import org.logstash.execution.PluginHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

@LogstashPlugin(name = "java-stdout")
public class Stdout implements Output {
    public static final String DEFAULT_CODEC_NAME = "line"; // no codec support, yet

    private PrintStream printer;
    private final CountDownLatch done = new CountDownLatch(1);

    /**
     * Required Constructor Signature only taking a {@link LsConfiguration}.
     *
     * @param configuration Logstash Configuration
     * @param context       Logstash Context
     */
    public Stdout(final LsConfiguration configuration, final LsContext context) {
        this(configuration, context, System.out);
    }

    Stdout(final LsConfiguration configuration, final LsContext context, OutputStream targetStream) {
        printer = new PrintStream(targetStream); // replace this with a codec
    }

    @Override
    public void output(final Collection<Event> events) {
        try {
            for (Event e : events) {
                printer.println(e.toJson()); // use codec here
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void stop() {
        done.countDown();
    }

    @Override
    public void awaitStop() throws InterruptedException {
        done.await();
    }

    @Override
    public Collection<PluginConfigSpec<?>> configSchema() {
        return PluginHelper.commonOutputOptions();
    }
}