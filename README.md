# Toothpick Reflect

The toothpick-reflect artifact is an API-compatible configuration for Toothpick 1.x which uses 100% reflection instead of annotation processing for use during development.

# Usage

```groovy
dependencies {
  if (properties.containsKey('android.injected.invoked.from.ide')) {
    implementation 'com.lukaville.toothpick.reflect:toothpick-reflect-runtime:0.1.0'
  } else {
    implementation 'com.github.stephanenicolas.toothpick:toothpick-runtime:1.1.3'
    kapt 'com.github.stephanenicolas.toothpick:toothpick-compiler:1.1.3'
  }
}
```

_* replace `kapt` with `annotationProcessor` for Java modules_

If you are using factory registries and member injector registries you need to instanciate them using reflection:

* #### Replace this:
  ```java
  FactoryRegistryLocator.setRootRegistry(FactoryRegistry());
  MemberInjectorRegistryLocator.setRootRegistry(MemberInjectorRegistry());
  ```

* #### With this:
  ```java
  FactoryRegistryLocator.setRootRegistry(Class.forName("mypackage.FactoryRegistry").getConstructor().newInstance());
  MemberInjectorRegistryLocator.setRootRegistry(Class.forName("mypackage.MemberInjectorRegistry").getConstructor().newInstance());
  ```

Also, if you are using obfuscation you need to keep names for the registries so reflective instantiation doesn't fail.

# Limitations

* `@ProvidesSingletonInScope` annotation is not supported because it's not available in runtime, use `providesSingletonInScope()` binding instead. It can be fixed in the futuer by monkey-patching this annoation.
* Superclass member injection is not supported for dependencies created using factories (see [FactoryGenerator.java](https://github.com/stephanenicolas/toothpick/blob/master/toothpick-compiler/src/main/java/toothpick/compiler/factory/generators/FactoryGenerator.java#L56)) 
* This implementation may contain bugs, please do not use for production
