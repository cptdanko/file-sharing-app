# My Day To-Do - File sharing app

This is a simple file sharing system similar to Google Drive. In this system anyone who signs up to be a user is able to
upload, share and track multiple files.

> **_NOTE:_**  User roles and permissions will NOT be a part of the MVP.

# Objective

Create a file sharing app powered by Java Spring Boot, React and the uses AWS's public cloud offereing to CRUD data.
Once implemented, aspects of this repository will also serve as code samples that anyone that wants to build a system
like this can learn from. During development, there will also be a series of blogposts that will be shared on the My Day
To-Do blog. Below are some of the blogposts shared so far,

1. [Upload to AWS S3 bucket from Java Spring Boot app]
2. [File share app - social file share feature]
3. [How to build a Spring Boot API with reactjs frontend]

## Features

This app is composed of a Spring boot API with a simple and function reactjs based UI. It features the following,

1. New user sign up
2. Upload and Manage files from cloud storaege
3. Security with basic authentication
4. Share uploaded files with other users

Show below is a screenshot of the simple reactjs based UI that allows, a user to login, upload, download, delete and
share files.

<img width="549" alt="fileShareAppScreen" src="https://github.com/cptdanko/file-sharing-app/assets/919243/128dccfa-7792-42bc-a10a-8ac961356376">

# Tech stack

The app is built using

- Java 17
- Spring Boot 3
- Spring Security
- Lombok
- AWS DynamoDB
- AWS S3
- CI/CD via Github actions

# Setup and run

To run this spring boot project,

1. Clone the repository and navigate to the repo directory

```shell
git clone git@github.com:cptdanko/file-sharing.git
cd file-sharing
```

2. Open application.yml file in either VSCode or IntelliJ
3. Update the following with your AWS credentials

```shell
aws:
  region: <your AWS region e.g. 'ap-southeast-2'> 
  key: <your-key>
  secret: <your-secret>
  dynamo-db:
    amazonDBEndpoint: <get yours from here https://docs.aws.amazon.com/general/latest/gr/ddb.html>
```

4. Once you have setup everything, when running for the first time, execute the following,

 ```
mvn clean install package 
```

5. Then to run the app as you make changes, from the terminal, git bash or command line, run

```shell
mvn spring-boot:run
```

> **_NOTE:_** : if you don't add your AWS credentials, the app will fail at startup when trying to create the DynamoDB
> beans.

### How to run the monolithic web app with react based UI

By default this repo will only start the API that you can invoke via Postman, but if you want to start the full
monolithic web app with the react based UI, you need to edit the pom.xml file and remove the `<puginsManagement>` tag.

## Any help?

If you have difficulty understanding anything about this repo, feel free to reach out to me through this Github account
or at bhuman at mydaytodoDOTcom or bhuman.soni@gmail.com.

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

If you like what I am doing, you can [buy me a coffee]

Click on the next link for more info on the [software engineering career journey] of the author.

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
- [git feature branch workflow]: https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow