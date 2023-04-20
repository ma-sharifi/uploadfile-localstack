package com.example;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

//https://docs.aws.amazon.com/AmazonS3/latest/userguide/example_s3_PutObject_section.html

@ApplicationScoped
@Path("/s3")
public class S3SyncClientResource extends CommonResource {
    @Inject
    S3Client s3;

        @POST
        @Path("upload")
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        public Response uploadFile(FormData formData) throws Exception {

            System.out.println("##formData = " + formData.filename);
            System.out.println("##formData = " + formData.mimetype);

            if (formData.filename == null || formData.filename.isEmpty()) {
                return Response.status(Status.BAD_REQUEST).build();
            }

            if (formData.mimetype == null || formData.mimetype.isEmpty()) {
                return Response.status(Status.BAD_REQUEST).build();
            }

            PutObjectResponse putResponse = s3.putObject(buildPutRequest(formData),
                                                         RequestBody.fromFile(formData.data));

            ListObjectsRequest listRequest = ListObjectsRequest.builder().bucket(bucketName).build();
            List<FileObject> list=s3.listObjects(listRequest).contents().stream()
                .map(FileObject::from)
                .sorted(Comparator.comparing(FileObject::getObjectKey))
                .collect(Collectors.toList());
            System.out.println("#list = " + list);
            if (putResponse != null) {
                return Response.ok(serverAddress+"/"+bucketName+"/"+formData.filename).status(Status.CREATED).build();
            } else {
                return Response.serverError().build();
            }
        }

    @GET
    @Path("download/{objectKey}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(String objectKey) {
        System.out.println("#downloadFile: objectKey: "+objectKey);
        ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(buildGetRequest(objectKey));
        ResponseBuilder response = Response.ok(objectBytes.asUtf8String());
        response.header("Content-Disposition", "attachment;filename=" + objectKey);
        response.header("Content-Type", objectBytes.response().contentType());
        return response.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileObject> listFiles() {
        ListObjectsRequest listRequest = ListObjectsRequest.builder().bucket(bucketName).build();
        System.out.println(bucketName +" #listRequest: "+listRequest);
        //HEAD S3 objects to get metadata
        return s3.listObjects(listRequest).contents().stream()
                .map(FileObject::from)
                .sorted(Comparator.comparing(FileObject::getObjectKey))
                .collect(Collectors.toList());
    }
}
