package com.example;


import java.io.File;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

public class FormData {

  @RestForm("file")
  public File data;

  @RestForm
  @PartType(MediaType.TEXT_PLAIN)
  public String filename;

  @RestForm
  @PartType(MediaType.TEXT_PLAIN)
  public String mimetype;

  @Override
  public String toString()
  {
    return "FormData{ filename='" + filename + '\'' + ", mimetype='" + mimetype + '\'' + '}';
  }
}

