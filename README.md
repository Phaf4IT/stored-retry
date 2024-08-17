![example branch parameter](https://github.com/RDBreed/stored-retry/actions/workflows/gradle.yml/badge.svg) [![](https://jitpack.io/v/Phaf4IT/stored-retry.svg)](https://jitpack.io/#Phaf4IT/stored-retry)

# Stored Retry

*Store & distribute your retries*

## Introduction

In the world of APIs, the ultimate scenario is, that applications can communicate with each-other successfully, within
milliseconds, so that a customer has almost direct feedback on any action they undertake.

*Just like communication between human-beings.*

However, reality is often worse than best and API calls are:

- Unresponsive or have slow response times
- Fail unpredictably or depend on other (async) processes to be successful
- Unreachable due service outages

In Java applications, well-known libraries such as Resilience4J or Spring Retry have solutions in place to try to make
the applications more resilient, by providing throttling and retry mechanisms.

Still, customers will have to get responses like “please retry again” or “something went wrong”. Which is - most of the
times - not what we want, as it can lead to lower customer conversion rates and customer satisfaction scores.

So what if we could perform retries async (for a limited time and meanwhile) let the customer have a more satisfactory
message like “just relax and sit back, we are working on it”, until we can notify them the retry has applied their
action successfully?

Here the library `Stored Retry` will try to implement the use case of a storable, distributable retry mechanism.

## Quick installation

Add Jitpack to your Maven repositories:
<details>
<summary>Gradle</summary>

```groovy
repositories {
    mavenCentral()
    maven {
        url 'https://jitpack.io'
        content {
            // this will ensure we only use jitpack for this specific library with group "com.github.Phaf4IT.stored-retry"
            includeGroup("com.github.Phaf4IT.stored-retry")
        }
    }
}
```

</details>

<details>
<summary>Gradle Kotlin</summary>

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
        content {
            // this will ensure we only use jitpack for this specific library with group "com.github.Phaf4IT.stored-retry"
            includeGroup("com.github.Phaf4IT.stored-retry")
        }
    }
}
```

</details>
<details>
<summary>Maven</summary>

```xml

<repositories>
    <!-- !Be aware that you put this repository as last of the list of repositories. Additionally, you could try to filter on this artifact only.
    See https://maven.apache.org/resolver/remote-repository-filtering.html for further info !-->
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

</details>
Then, you can add the core of stored retry:
<details>
<summary>Gradle Kotlin</summary>

```groovy
dependencies {
    implementation 'com.github.Phaf4IT.stored-retry:stored-retry-core:0.0.1-SNAPSHOT'
}
```

</details>
<details>
<summary>Gradle Kotlin</summary>

```kotlin
dependencies {
    implementation("com.github.Phaf4IT.stored-retry:stored-retry-core:0.0.1-SNAPSHOT")
}
```

</details>
<details>
<summary>Maven</summary>

```xml

<dependency>
    <groupId>com.github.Phaf4IT.stored-retry</groupId>
    <artifactId>stored-retry-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

</details>

## Other useful references

- [Bucket4J](https://github.com/bucket4j/bucket4j); a distributed throttling library
