# VMP361

For details, refer
to [如何充分发挥360加固onCreate的VMP保护特性](https://note.shlu.fyi/Android/%E5%A6%82%E4%BD%95%E5%85%85%E5%88%86%E5%8F%91%E6%8C%A5360%E5%8A%A0%E5%9B%BAonCreate%E7%9A%84VMP%E4%BF%9D%E6%8A%A4%E7%89%B9%E6%80%A7/)

## Use

Step 1. Add it in your root build.gradle at the end of repositories:
```groovy
    dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency
```groovy
    dependencies {
    implementation 'com.github.xpko:VMP361:1.4'
}
```
