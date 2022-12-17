package org.oekofen.collector.source;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Utils
{
  public static String loadResourceAsString(String resName) throws IOException
  {
    InputStream iStream = Utils.class.getClassLoader().getResourceAsStream(resName);
    return new String(iStream.readAllBytes(), StandardCharsets.UTF_8);
  }
}
