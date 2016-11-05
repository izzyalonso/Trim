# TRIM - A java library to assess API endpoint field-level usage

### Features

* Bulk GET requests to API endpoints that deliver JSON
* Progress update every time a request to an endpoint completes
* Report request time
* Report request size
* Report status code of requests
  * Deliver result if status code is 4xx
* Report which attributes are used and which aren’t
* Report when attributes were stopped being used
* Report type mismatch between endpoint response fields and model fields


### Importing through maven

Coming soon.

### Usage

Trim works on top of annotated model classes. The list of available annotations is the following:

* @Endpoint: associates an endpoint to a model class. Takes a String as the argument
* @Header: contains information about a header specific to the request for the endpoint associated with a particular model. Takes two strings indexed as header and value
* @Headers: specifies multiple headers to be associated to a request. Takes an array of `@Header`s as the argument
* @Skip: marks a model attribute as irrelevant.
* @AttributeName: specifies the name of the endpoint attribute associated to a model attribute if their names mismatch
* @CollectionGenericType: for types that extend collection, the generic type of the collection needs to be specified. This is because due to type erasure the generic type ain't available at runtime. Takes a Class as the argument
* @UnusedSinceVersion specifies the version in which an attribute was left unused in the model. Takes an int as the argument

To start the analysis, you need to set up an instance of Specification. There are three methods in the Specification class to achieve this goal:

* `Specification.setCurrentApplicationVersion(int)` -> lets trim know which is the current version of the application
* `Specification.addModel(Class<?>)` -> registers a model for analysis
* `Specification.addHeader(String, String)` -> specifies a header that will be applied to all requests

Once the Specification is set up, pass it to `Trim.run(Specification)` or `Trim.run(Specification, ProgressListener)` if you want progress updates when endpoint analyses complete.

For more details check out the sample module.
