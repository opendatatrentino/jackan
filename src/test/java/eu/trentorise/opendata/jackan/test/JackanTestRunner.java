package eu.trentorise.opendata.jackan.test;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import eu.trentorise.opendata.jackan.test.JackanTestRunner;
import junitparams.JUnitParamsRunner;

/**
 * Runs tests as {@link JUnitParamsRunner} would, with a <a href="https://github.com/opendatatrentino/jackan/issues/25" 
 * target="_blank"> workaround to run single tests in Eclipse</a>
 * 
 * @since 0.4.3
 */
// TODO: use this to generate reports
public class JackanTestRunner extends JUnitParamsRunner {

    private final ConcurrentHashMap<FrameworkMethod, Description> methodDescriptions = new ConcurrentHashMap<>();

    private final ThreadLocal<FrameworkMethod> describeChildAlreadyCalled = new ThreadLocal<>();

    public JackanTestRunner (Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        if (describeChildAlreadyCalled.get() == method) {
            return super.describeChild(method);
        }
        describeChildAlreadyCalled.set(method);
        try {
            Description description = methodDescriptions.get(method);

            if (description == null) {
                // Call the JUnitParamsRunner logic instead of the org.junit.runners.BlockJUnit4ClassRunner.describeChild(FrameworkMethod) logic
                description = describeMethod(method);

                methodDescriptions.putIfAbsent(method, description);
            }

            return description;
        } finally {
            describeChildAlreadyCalled.set(null);
        }
    }

}