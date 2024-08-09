 ![example branch parameter](https://github.com/RDBreed/stored-retry/actions/workflows/gradle.yml/badge.svg)
 # Stored Retry

*Store & distribute your retries*

## Introduction

In the world of APIs, the ultimate scenario is, that applications can communicate with each-other successfully, within milliseconds, so that a customer has almost direct feedback on any action they undertake.

*Just like communication between human-beings.*

However, reality is often worse than best and API calls are:

- Unresponsive or have slow response times
- Fail unpredictably or depend on other (async) processes to be successful
- Unreachable due service outages

In Java applications, well-known libraries such as Resilience4J or Spring Retry have solutions in place to try to make the applications more resilient, by providing throttling and retry mechanisms.

Still, customers will have to get responses like “please retry again” or “something went wrong”. Which is - most of the times - not what we want, as it can lead to lower customer conversion rates and customer satisfaction scores.

So what if we could perform retries async (for a limited time and meanwhile) let the customer have a more satisfactory message like “just relax and sit back, we are working on it”, until we can notify them the retry has applied their action successfully?

Here the library `Stored Retry` will try to implement the use case of a storable, distributable retry mechanism.

## Example usage

TODO

## Other useful references

- [Bucket4J](https://github.com/bucket4j/bucket4j); a distributed throttling library
