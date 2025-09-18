# Interview exercise

Hello there interviewer! Welcome to my answer to you interview exercise :) I hope you enjoy it!

So, in the request (defined [here](/docs/Test%20Task.docx)), I was asked to write the documentation including the following:

1. How to run the application
2. Sample of usage
3. List of dependencies

But wanting to make a good impression (hopefully) I will also include the following:
1. How to get the API key from Weather API
2. How to see the coverage report of the tests
3. What errors will be shown

If you're looking for the general design document, please check [this](/docs/Design.pdf).

Alright, let's get started!

## How to run the application

At this point, I'm assuming that you have Java 21 installed on your machine (as it's the version used in this application) and Maven installed.
If you don't, please install them before continuing.

To run the application, first you need to clone the repository. To do so, run the following command:
```commandline
git clone https://github.com/gastonortiz/eleks-interview-exercise.git
```
This should create a new folder called `eleks-interview-exercise` in your current directory including almost everything you need to run the app.

Except for one tiny little thing, the Weather API key.

### Getting the Weather API key

If you already have or know how to get yourself one of these ignore this section.

In order to get the weather data, this application relies on the [Weather API](https://www.weatherapi.com/) and to use its API you will need an API key.
This API key is sent as a query parameter in the requests to the API and is created to registered users. To get yourself one of these fancy keys, the process is quite simple:

Just sign up [here](https://www.weatherapi.com/signup.aspx), follow the instructions and confirm your email. The basic free account is more than enough to get the application working for the purposes I hope you are looking for

Once you have your account, you have to log in [here](https://www.weatherapi.com/login.aspx), and it will redirect you to the main dashboard of your account. In there you'll see the API key, which you can just copy and that's it.

#### Now, where do you set this key?
Simply set the `WEATHER_API_KEY` environment variable to the key you just copied.

## The command to run the application

So there are 2 default ways of running this application:
1. Using Maven
2. Using Docker

### Using Maven

To run the application using Maven, just run the following command from the root of the project:
```bash
mvn spring-boot:run
```
Please remember to set the `WEATHER_API_KEY` environment variable to the key you just copied.

### Using Docker
Please make sure to have Docker installed and the daemon running before continuing.

To run the application using Docker, first you have to build the image by running the following command from the root of the project:
```bash
docker build -t eleks/interview-exercise:dev .
```
This will create a new image called `eleks/interview-exercise` in your local Docker registry with a `dev` tag.
Then you can run the image by running the following command:
```bash
docker run --rm -p 8080:8080 -e WEATHER_API_KEY="$WEATHER_API_KEY" eleks/interview-exercise:dev
```
If you didn't want to set the `WEATHER_API_KEY` environment variable, you can set it this command by replacing the `$WEATHER_API_KEY` part with it.

## Sample of usage

Now that you have the application running, you can try to use it by sending a request to the following URLs:

 - [Weather in Kyoto](http://localhost:8080/weather/UKY)
 - [Weather in Tokyo](http://localhost:8080/weather/TYO)
 - [Weather in Osaka](http://localhost:8080/weather/OSA)

This will return the weather data for those cities. The endpoint actually accepts IATA codes, loaded from the [city_codes.csv](src/main/resources/city_codes.csv) file. Sending a code that is not there will return an error (try [this](http://localhost:8080/weather/AAA) to confirm what I'm saying).

OpenAPI documentation is available [here](http://localhost:8080/swagger-ui/index.html).

## List of dependencies

Here is a list of the dependencies used and what are they used for:

 - Spring Boot - Used as an overall framework for the application
 - Lombok - Used to generate getters and setters for the classes
 - Spring Boot starter test - Used for testing dependencies (Mockito & JUnit)
 - Apache Commons CSV - Used to parse the CSV file containing the city codes
 - OkHttp - Used to make HTTP requests to the Weather API
 - Springdoc OpenAPI - Used to generate the OpenAPI documentation

Also, the following plugins are used:
 - Spring Boot Maven plugin - Used to build the application as a layered JAR
 - Jacoco plugin - Used to generate code coverage reports
 - Springdoc OpenAPI Maven plugin - Used to generate the OpenAPI documentation

### Oh, you have a report on code coverage?

Yes! and to get it, run the following command from the root of the project:
```bash
mvn clean verify
```
This will generate a report in the `target/site/jacoco` folder. Opening the `index.html` file in your browser will show you the coverage report (guess the coverage % :) ).

## What errors will be shown?

When executing the application and invoking it's only endpoint, you may get errors. It's normal, every now and then something may happen that alters the normal flows. Fortunately, I've thought of a couple of errors that can be shown as a response:
 - Invalid city code (Bad request 400) - INVALID_CITY_CODE: This error shows up when you've invoked the endpoint with a non-existing IATA code (see [city_codes.csv](src/main/resources/city_codes.csv) for the list of valid codes).
 - City name misconfiguration (Internal server error 500) - CITY_NAME_MISCONFIGURATION: This error shows up when the configuration in the [city_codes.csv](src/main/resources/city_codes.csv) file doesn't match the Weather API requirements. For example, if you set the city name to just "a".
 - API not found (Internal server error 500) - API_NOT_FOUND: This error shows up when the configured URL in the `application.yml` file doesn't point to a valid Weather API endpoint.
 - Missing API key (Internal server error 500) MISSING_API_KEY: This error shows up when the API key is missing. Did you set the `WEATHER_API_KEY` environment variable for it?
 - Invalid API key (Internal server error 500) INVALID_API_KEY: This error shows up when the API key you've set is invalid. Please make sure you set the appropriate one.
 - Exceeded API calls limit (Forbidden 403) EXCEEDED_API_CALLS_LIMIT: This error shows up when you've made too many calls to the Weather API api.
 - Disabled API key (Forbidden 403) DISABLED_API_KEY: This error is returned when the api key you've set is disabled. Please refresh it in your Weather API dashboard and restart the application.
 - Unknown error (Internal server error 500) UNKNOWN_ERROR: This error is returned when the application has encountered an unexpected error that doesn't fall into any of the other categories.
