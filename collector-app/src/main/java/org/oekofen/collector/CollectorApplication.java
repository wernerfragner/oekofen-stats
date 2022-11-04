package org.oekofen.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@TypeHint(types = {ObjectTransformations.class, ObjectTransformation.class, FieldTransformation.class})
@SpringBootApplication
@EnableScheduling
public class CollectorApplication
{

  public static void main(String[] args)
  {
    SpringApplication.run(CollectorApplication.class, args);
  }

}
