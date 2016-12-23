# API-Gateway
The API-Gateway is responsible for external request authentication, rate limiting and logging. It acts as a proxy layer on top of our service architecture. (We didn't want to implement the complexity of getkong.org)

#Consumer
A consumer represents an Exorath-Cloud account (provided by the authentication service).

#Components
The API-Gateway works with components, which are side processes/applications which make it work.

##Authentication component
Each API (endpoint) specifies whether or not it requires authentication. if so, the authentication component will only pass through authorized requests (through a secret token).
The request keys are provided by the TokenService.
The authentication component will also remove this secret token and add the unique consumer id as a header to the request. 

##Rate-Limiting component
This component is responsible for rate-limiting an API (endpoint) per consumer. What will happen when the quota is exceeded is configurable.

##Credit charging component
This component is responsible for charging a certain amount credits per request. If enabled the amount per request is configurable. The CreditService is responsible for deciding whether or not the user can go in debt, if the credit service returns a false, the request will be blocked (meaning the user does not have credit/exceeded debt limit).

##Logging component
The logging component will log requests to our logging platform.

##Event Varibles
 
 These are varibles attached to the body of a requestthat has been rerouted by the api gateway
 
```json
{
  "varname": value
}
```
