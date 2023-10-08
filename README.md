# My Day To-Do - Document sharing system

This is a simple document sharing system similar to Google Drive. The content upload will not be limited to documents
hence each document, hence the class name Asset in the model folder. In this system a user will be able to upload and
track multiple assets.

> **_NOTE:_**  User roles and permissions will be be a part of the MVP.

# Objective
The goal is to gain an understanding of the complexity of creating a document sharing system and provide code samples 
for anyone looking to create a system like this. Once fully developed a user will be able to, 
- Create an account and login via basic authentication
- Upload assets (e.g. documents)
- View all the assets uploaded
- Share the assets with other users

> **_NOTE:_** at this stage, it will either be a full REST API or depending on time, may have simple UI


# Tech stack
The app is built using 
- Java 17
- Spring Boot 3
- Spring Security (_to be added_)
- Lombok
- AWS DynamoDB
- S3 (_to be added_)

# Setup and run
To run this spring boot project, 
1. Clone the repository and navigate to the repo directory
```shell
git clone git@github.com:cptdanko/document-sharing.git
cd document-sharing
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
> **_NOTE:_** : if you don't add your AWS credentials, the app will fail at startup when trying to create the DynamoDB beans

## Any help?
If you have difficulty understanding anything about this repo, feel free to reach out to me through this Github account or at bhuman at mydaytodoDOTcom or bhuman.soni@gmail.com.

## Tutorials
Have a read of some of the tutorials with code samples on my blog,

- [Call Rest API with Spring WebClient]
- [Jokes API with Spring RestTemplate]
- [AWS DynamoDB how to] (NodeJS/Express & Typescript)
- [AWS DynamoDB query by non-primary] (NodeJS/Express & Typescript)

and you can find more at https://www.mydaytodo.com/blog/

# More great tutorials and code samples
I will be writing a detailed tutorial on how to work with this repo on my blog. Until then refer to [my blog] for other tutorials and "how-to" articles with detailed code samples.

If you like what I am doing, you can [buy me a coffee]

Click on the next link for more info on the [software engineering career journey] of the author.

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