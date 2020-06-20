package com.example.resource;

public class uploader {

    public String filename;
    public String fileurl;

    public uploader(){

    }
    public uploader(String filename,String fileurl){
        this.filename=filename;
        this.fileurl=fileurl;
    }

    public String getFilename() {
        return filename;
    }

    public String getFileurl() {
        return fileurl;
    }
}
