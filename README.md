# MDT - File Sharing App

A full stack file sharing app like DropBox or Google Drive. Unlike those apps, this is limited to documents only.

# Summary

This repo shows a file sharing app built with Java Spring Boot, React, with appropriate security mechanisms and
stores data in AWS services. Aspects of this repository serve as code
samples for other developers who want to build an app like this. A series of blogposts i.e.
How-To tutorials have also been shared on the My Day To-Do blog, they are,

1. [Upload to AWS S3 bucket from Java Spring Boot app]
2. [How to Catch ExpiredJwtException in Spring OncePerRequestFilter]
3. [File share app - social file share feature]
4. [How to build a Spring Boot API with reactjs frontend]

## Features

This app is composed of a Spring boot API with a simple and function reactjs based UI. It features the following,

1. New user sign up
2. Upload and Manage files from cloud storage
3. A combination of Google login via Oauth2.0, new user sign-ups and JWT auth
4. Share uploaded files with other users

Show below is a screenshot of the simple reactjs based UI that allows, a user to login, upload, download, delete and
share files.

<img width="549" alt="fileShareAppScreen" src="https://github.com/user-attachments/assets/791d0d71-3c2f-4212-94cb-7f8045768cf2">

# Tech stack

The app is built using

| Technology   | Framework | Libraries                                                  |
|--------------| ------- |------------------------------------------------------------|
| --------     | **Backend** | -------                                                    |
| Java 17      | Spring Boot    | spring-boot-starter-mail, security, webflux, oauth2-client |
| -            | -    | spring-data-dynamodb                                       |
| -            | -    | springdoc-openapi-starter-webmvc-ui                        |
| -            | -    | org.projectlombok                                          |
| -            | -    | io.jsonwebtoken                                            |
| --------     | **Frontend** | -------                                                    |
| Javascript   | ReactJS    | @mui/material, @mui/icons-material                         |
| -            | -    | react-cookie                                               |
| -            | -    | @react-oauth/google                                        |
| --------     | **Cloud** | **Services**                                               |
| Public cloud | AWS    | DynamoDB                                                   |
| -            | -    | S3                                                         |
| -            | -    | Route 53                                                   |
| -            | -    | Elastic Beanstalk                                          |


In addition to the above, this project has,
- CI/CD via Github actions
- Docker

# Setup and run

To run this spring boot project,

Clone the repository and navigate to the repo directory

```shell
git clone git@github.com:cptdanko/file-sharing.git
cd file-sharing
```

Update the application.yml file with your AWS credentials

```shell
aws:
  region: <your AWS region e.g. 'ap-southeast-2'> 
  key: <your-key>
  secret: <your-secret>
  dynamo-db:
    amazonDBEndpoint: <get yours from here https://docs.aws.amazon.com/general/latest/gr/ddb.html>
```

After you have setup everything, when running for the first time, execute the following,

 ```
mvn clean install package 
```

Then to run the app as you make changes, from the terminal, git bash or command line, run

```shell
mvn spring-boot:run
```
Alternatively, you can run the app via docker, for this first install docker and run the following commands,
```
cd file-sharing-app/
docker compose up
```
Then you should see the instructions on the site

**(OPTIONAL)**: if you want to use Google login
1. create a .env file in the root frontend directory
2. create a variable REACT_APP_GOOGLE_CLIENT_ID=<your-google-client-id>
3. in case you don't know, an online search should tell you more about 'how to add Gogole login to your app'

> **_NOTE:_**
> 1. if you don't add your AWS credentials, the app will fail at startup
> 2. if you are on a mac, change server port in application.yml to <anything-else-than-5000>
> 3. also change the proxy in package.json to http://localhost:<your-new-no>

### How to run the monolithic web app with react based UI

By default this repo will only start the API that you can invoke via Postman, but if you want to start the full
monolithic web app with the react based UI, you need to edit the pom.xml file and remove the `<puginsManagement>` tag.

## Any help?

If you have difficulty understanding anything about this repo, feel free to reach out to me through this Github account
or at _**bhuman@mydaytodo.com**_ or **_bhuman.soni@gmail.com_**

## How to contribute to this repo?
This repository uses the Git feature based workflow, so if you would like to add something to this
1. Create an issue on Github
2. Create a feature branch
```
git branch feature/your-awesome-feature
git checkout feature/your-awesome-feature
```
3. Build your awesome feature, commit as much as you like
4. Squash your commits (if you haven't done this before, please ask me and I am happy to help)
5. Push all your changes and create a PR

If you you would like to know more about how to the git feature based workflow, you can read [git feature branch workflow].


## Tutorials

Have a read of some of the tutorials with code samples on my blog,

- [Call Rest API with Spring WebClient]
- [Jokes API with Spring RestTemplate]
- [AWS DynamoDB how to] (NodeJS/Express & Typescript)
- [AWS DynamoDB query by non-primary] (NodeJS/Express & Typescript)

and you can find more at https://www.mydaytodo.com/blog/

# More great tutorials and code samples

I will be writing a detailed tutorial on how to work with this repo on my blog. Until then refer to [my blog] for other
tutorials and "how-to" articles with detailed code samples.

If you like what I am doing, you can [buy me a coffee].

Until next time, live long and prosper!


[How to Catch ExpiredJwtException in Spring OncePerRequestFilter]: https://mydaytodo.com/how-to-catch-expiredjwtexception-in-spring-onceperrequestfilter/

[Upload to AWS S3 bucket from Java Spring Boot app]: https://mydaytodo.com/upload-to-aws-s3-bucket-from-java-spring-boot-app/

[File share app - social file share feature]: https://mydaytodo.com/epic-social-file-share-feature/

[How to build a Spring Boot API with reactjs frontend]: https://mydaytodo.com/spring-boot-api-with-reactjs/

[Jokes API with Spring RestTemplate]: https://mydaytodo.com/how-to-build-a-jokes-client-in-java-spring-boot-with-resttemplate/

[Call Rest API with Spring WebClient]: https://mydaytodo.com/how-to-call-rest-api-with-webclient/

[Node Typescript CRUD Notes]: https://github.com/cptdanko/node_typescript_crud_notes

[AWS DynamoDB query by non-primary]: https://mydaytodo.com/how-to-query-dynamodb-with-non-primary-key-column/

[AWS DynamoDB how to]: https://mydaytodo.com/aws-dynamodb-typescript-how-to/

[frontend in the repo]: https://github.com/cptdanko/react_typescript_todo_list

[native iOS app]: https://apps.apple.com/au/app/my-day-to-do-smart-task-list/id1020072048

[line 16]: https://github.com/cptdanko/nodetypescriptcrudnotes/blob/main/src/db.ts#L16

[my blog]: https://mydaytodo.com/blog/

[line 17]: https://github.com/cptdanko/nodetypescriptcrudnotes/blob/main/src/db.ts#L17

[AWS docs]: https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-envvars.html

[blogpost]: https://mydaytodo.com/blog/

[buy me a coffee]: https://www.buymeacoffee.com/bhumansoni
[git feature branch workflow]: https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow
