## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

You need to have maven,docker installed on your system.

For installing docker and docker compose,follow official documentation.

## Deployment

First clone my repository, then move to project directory
```
cd sporty-assignment/
```

If you want, you can change the external url in src/main/resources/application.yml. Then run the following coomand ```./mvnw package && java -jar target/sporty-0.0.1-SNAPSHOT.jar```

Then deploy the docker project using the following command
```
docker-compose up --build
```

Above command will up the kafka and spring boot service.

## Implementation Logic
As the implementation specifically mentions in-memory state, we haven't used any DBMS.
For concurrency, we use concurrent Hash set (based on concurrent Hash map) to track which events are currently live. This ensures thread-safe operations during high-concurrency status updates without the overhead of manual synchronization.

Status Update Controller--
For status updates, we expose endpoint localhost:8080/api/v1/events/status. In our controller layer, we take request for event status update which either removes from this set(if status passed is NOT_LIVE) or add in this set(if status passed is LIVE). We're validating our input request before doing processing.

Periodic Rest Call---
We have a scheduler running in our application which runs every 10 sec. This scheduler will iterate through event ids present in our concurrent hashset and will call external endpoint and will publish to kafka for each live event id. If we have multiple live events, we can process parallely instead of sequentially. This improves performance. I have used ThreadPoolTaskExecutor to achieve asynchronous processing. 

Retry when external api is down--
While calling external api, if it encounters ResourceAccessException or 5xx error and it will retry 3 times. This is achieved using @Retryable and @EnableRetry annotation. If after 3 times with backoff policy, there is any error we will not publish to kafka for that event.

For external api, I am using a mock service called ```beeceptor.com``` . I have put ```mayukhsporty.free.beeceptor.com\events\{eventId}```.  But you can change this property. In application.yml, put your api in external.api.url prop.

Publishing to kafka---
We're running kafka in docker. While publishing to kafka, spring kafka takes care of retry. We have put enable.idempotence property as true so that in case kafka gets duplicate data, it can discard. Also we have set higher timeout for producer data.


## AI usage
I have used AI to build basic structure. But AI didn't generate concurrent hash set data structure, it provided only HashMap which is error-prone when there are concurrent request. Regarding scheduling, I have improved AI implmentation by adding async processing by adding ThreadPoolTaskExecutor. This transitioned the processing logic from a sequential bottleneck to an asynchronous execution model, allowing multiple live events to be processed in parallel. AI generated scheduler code which was very verbose, have improved it by adding proper annotations, thus increasing code readability.


