# Upload File to Localstack

If you want to learn more about Quarkus, please visit its website:https://quarkiverse.github.io/quarkiverse-docs/quarkus-amazon-services/dev/amazon-s3.html

## Run Docker compose
```shell script
docker compose up --remove-orphans
```
## Test the availability of server with
S3 whould be ""s3": "running""
```shell
http://localhost:4566/health
http://localhost:4566/_localstack/init
```

## Create a bucket automatically
Create a bucket will be automatically done by asw-init.sh, you can see the result of the scrip by following script:
Mke sure the script is executable: chmod +x init-aws.sh
```
http :4566/_localstack/init/ready -b
```
result must be:
```json
{
    "completed": true,
    "scripts": [
        {
            "name": "init-aws.sh",
            "stage": "READY",
            "state": "SUCCESSFUL"
        }
    ]
}
```
You also can see the result on the log file.  

## Create a bucket manually
If the bucket has not been created automatically, follow the below commands:
```shell
$aws configure --profile localstack
AWS Access Key ID [None]: test-key
AWS Secret Access Key [None]: test-secret
Default region name [None]: us-east-1
Default output format [None]:
```
Create a S3 bucket using AWS CLI
```shell
aws s3 mb s3://s3.test.bucket --profile localstack --endpoint-url=http://localhost:4566
```
If you ave already installed `awslocal`, you can use the following command:
```shell
awslocal s3 mb s3://s3.test.bucket
```

# How to work with API

## Uplaod a file
```shell
http --form POST :8080/s3/upload \
'file@test-file.txt' 'mimetype'='text/plain' 'filename'='my-test.txt' -b
```

## Download a file
```shell
http  GET :4566/s3.test.bucket/my-test.txt

```
## You can see the result
`
http://localhost:4566/s3.test.bucket/test.txt
`

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.


## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```