{
    "_id" : ObjectId("54f095012dd27d0d2ca6147c"),
    "timestamp" : "2015-02-27T16:02:09.355Z",
    "journeyId" : "705e98d5-7d16-43b4-9a78-fddc767cbb25",
    "resource" : "Contract",
    "clientId" : "",
    "serviceProvider" : "",
    "journeyName" : "",
    "serviceName" : "",
    "serviceName" : "",
    "environmentId" : "",
    "payload" : "{}",
    "messageLocation" : "CLIENT_REQUEST"
}
{
    "_id" : ObjectId("54f095022dd27d0d2ca6147d"),
    "timestamp" : "2015-02-27T16:02:10.529Z",
    "journeyId" : "705e98d5-7d16-43b4-9a78-fddc767cbb25",
    "resource" : "Contract",\
    "clientId" : "",
    "serviceProvider" : "",
    "journeyName" : "",
    "serviceName" : "",
    "environmentId" : "",
    "payload" : "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
    "messageLocation" : "NETWORK_REQUEST"
}
{
    "_id" : ObjectId("54f095122dd27d0d2ca6147f"),
    "timestamp" : "2015-02-27T16:02:26.577Z",
    "journeyId" : "705e98d5-7d16-43b4-9a78-fddc767cbb25",
    "resource" : "Contract",
    "clientId" : "",
    "serviceProvider" : "",
    "journeyName" : "",
    "serviceName" : "",
    "environmentId" : "",
    "payload" : "{}",
    "messageLocation" : "CLIENT_RESPONSE"
}
{
    "_id" : ObjectId("54f095122dd27d0d2ca6147e"),
    "timestamp" : "2015-02-27T16:02:26.445Z",
    "journeyId" : "705e98d5-7d16-43b4-9a78-fddc767cbb25",
    "resource" : "Contract",
    "clientId" : "",
    "serviceProvider" : "",
    "journeyName" : "",
    "serviceName" : "",
    "environmentId" : "",
    "payload" : "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
    "messageLocation" : "NETWORK_RESONSE"
}


{
    "_id" : "ENV001",
    "hosts" : [ 
        {
            "type" : "INTEGRATION",
            "url" : "localhost:27017",
            "dbName" : "canada-api",
            "connectionPerHost" : 10,
            "threadsAllowedToBlockForConnectionMultiplier" : 5,
            "connectTimeout" : 1500,
            "maxWaitTime" : 1000,
            "autoConnectRetry" : true,
            "socketKeepAlive" : true,
            "socketTimeout" : 1500,
            "slaveOk" : true
        },
        {
            "type" : "APPLICATION",
            "url" : "localhost:27017",
            "dbName" : "honeyBee-gl-canada",
            "connectionPerHost" : 10,
            "threadsAllowedToBlockForConnectionMultiplier" : 5,
            "connectTimeout" : 1500,
            "maxWaitTime" : 1000,
            "autoConnectRetry" : true,
            "socketKeepAlive" : true,
            "socketTimeout" : 1500,
            "slaveOk" : true
        }
    ]
}


{
    "_id" : "ENV002",
    "hosts" : [ 
        {
            "type" : "INTEGRATION",
            "url" : "localhost:27017",
            "dbName" : "germany-api",
            "connectionPerHost" : 10,
            "threadsAllowedToBlockForConnectionMultiplier" : 5,
            "connectTimeout" : 1500,
            "maxWaitTime" : 1000,
            "autoConnectRetry" : true,
            "socketKeepAlive" : true,
            "socketTimeout" : 1500,
            "slaveOk" : true
        },
        {
            "type" : "APPLICATION",
            "url" : "localhost:27017",
            "dbName" : "honeyBee-gl-germany",
            "connectionPerHost" : 10,
            "threadsAllowedToBlockForConnectionMultiplier" : 5,
            "connectTimeout" : 1500,
            "maxWaitTime" : 1000,
            "autoConnectRetry" : true,
            "socketKeepAlive" : true,
            "socketTimeout" : 1500,
            "slaveOk" : true
        }
    ]
}




{
    "environmentId" : "",
    "journeyId" : "",
    "serviceProvider" : "",
    "fromDate" : "",
    "toDate" : ""
}



[
    { 
        "environmentId" : "",
        "journeyId" : "",
        "serviceProvider" : "",
        "timestamp" : ""
    },
    { 
        "environmentId" : "",
        "journeyId" : "",
        "serviceProvider" : "",
        "timestamp" : ""
    }
]






GET     http://localhost:8080/api/environments
GET     http://localhost:8080/api/environments?fields=id
GET     http://localhost:8080/api/environments/{id}
POST    http://localhost:8080/api/environments
PUT     http://localhost:8080/api/environments/{id}
DELETE  http://localhost:8080/api/environments/{id}

POST    http://localhost:8080/api/journeys/search

POST    http://localhost:8080/api/tests
POST    http://localhost:8080/api/tests/search
GET     http://localhost:8080/api/tests/{id}
GET     http://localhost:8080/api/tests/{id}/export
POST    http://localhost:8080/api/tests/import

GET     http://localhost:8080/api/tests/{id}/replay?fields=applicationData
GET     http://localhost:8080/api/tests/{id}/history

GET     http://localhost:8080/api/users
GET     http://localhost:8080/api/users/{id}
POST    http://localhost:8080/api/users
PUT     http://localhost:8080/api/users/{id}
DELETE  http://localhost:8080/api/users/{id}

POST    http://localhost:8080/api/login
POST    http://localhost:8080/api/logout




