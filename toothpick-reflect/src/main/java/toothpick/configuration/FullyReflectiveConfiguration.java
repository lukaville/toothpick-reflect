package toothpick.configuration;

import com.lukaville.toothpick.reflect.ReflectiveFactory;
import com.lukaville.toothpick.reflect.ReflectiveMemberInjector;

import toothpick.Factory;
import toothpick.MemberInjector;
import toothpick.Scope;
import toothpick.config.Binding;

public class FullyReflectiveConfiguration extends Configuration {

    private RuntimeCheckConfiguration runtimeCheckConfiguration = new RuntimeCheckOffConfiguration();
    private ReflectiveMemberInjector<?> memberInjector = new ReflectiveMemberInjector<>();

    public static Configuration forDevelopment() {
        final FullyReflectiveConfiguration configuration = new FullyReflectiveConfiguration();
        configuration.runtimeCheckConfiguration = new RuntimeCheckOnConfiguration();
        return configuration;
    }

    public static Configuration forProduction() {
        return new FullyReflectiveConfiguration();
    }

    public Configuration enableReflection() {
        throw new AssertionError("Enabling reflection is not" +
            "supported in fully reflective configuration");
    }

    @Override
    public Configuration disableReflection() {
        throw new AssertionError("Disabling reflection is not" +
            "supported in fully reflective configuration");
    }

    @Override
    public void checkIllegalBinding(Binding binding, Scope scope) {
        runtimeCheckConfiguration.checkIllegalBinding(binding, scope);
    }

    @Override
    public void checkCyclesStart(Class clazz, String name) {
        runtimeCheckConfiguration.checkCyclesStart(clazz, name);
    }

    @Override
    public void checkCyclesEnd(Class clazz, String name) {
        runtimeCheckConfiguration.checkCyclesEnd(clazz, name);
    }

    @Override
    public <T> Factory<T> getFactory(Class<T> clazz) {
        return new ReflectiveFactory<>(clazz);
    }

    @Override
    public <T> MemberInjector<T> getMemberInjector(Class<T> clazz) {
        //noinspection unchecked
        return (MemberInjector<T>) memberInjector;
    }

    private FullyReflectiveConfiguration() {
    }
}
