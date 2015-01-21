BackendAPI

BackendAPI application is based on Play Framework and Scala. It allows connecting with database through REST API and JSONs.

Prerequisities
For installing it locally you need to have scala, play framework (download and install as Activator - reactive environment for JVM), MongoDB service.
For testing API you need additionally Postman - plugin for Chrome.


Installing application (Guide for Linux Ubuntu)

1. Clone the repository

git clone git@github.com:klub-grotolazow/api.git

2. Check if MongoDB service is running in background

ps -aux |grep mongod

3. If you don't find appropriate process run it explicitly 

mongod -restart

4. Move to api directory

cd api

5. Run activator

activator run

6. Now you can test application.
Open Postman, write appropriate url starting with localhost:9000 and select appriopriate HTTP method.
Write json to create a user.


Instances

<b>Create a user</b>
POST /users

View a list of users
GET  /users

View a user with specified userId
GET  /users/$userId


User model has for fields
_id: String, (if not specified will create automatically)
firstName: String,
lastName: String,
email: String


Example json:
{
    "_id": 1,
	"firstName": "Michal",
    "lastName": "Kijania",
    "email": "michal.kijania@gmail.com"
}