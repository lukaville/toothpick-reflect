package toothpick.configuration;

import com.lukaville.toothpick.reflect.ReflectiveFactory;
import com.lukaville.toothpick.reflect.ReflectiveMemberInjector;

import toothpick.Factory;
import toothpick.MemberInjector;

public class FullyReflectiveConfiguration implements ReflectionConfiguration {

    private ReflectiveMemberInjector<?> memberInjector = new ReflectiveMemberInjector<>();

    @Override
    public <T> Factory<T> getFactory(Class<T> clazz) {
        return new ReflectiveFactory<>(clazz);
    }

    @Override
    public <T> MemberInjector<T> getMemberInjector(Class<T> clazz) {
        //noinspection unchecked
        return (MemberInjector<T>) memberInjector;
    }
}
