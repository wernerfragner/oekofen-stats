package org.oekofen.collector.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil
{

  private HashUtil()
  {
  }

  public static String createHash(String input)
  {
    byte[] messageDigest = getMessageDigest().digest(input.getBytes(StandardCharsets.UTF_8));
    return convertToHex(messageDigest);
  }

  private static MessageDigest getMessageDigest()
  {
    try
    {
      return MessageDigest.getInstance("SHA-256");
    }
    catch (NoSuchAlgorithmException alg)
    {
      try
      {
        return MessageDigest.getInstance("MD5");
      }
      catch (NoSuchAlgorithmException e)
      {
        throw new RuntimeException(e);
      }
    }
  }

  private static String convertToHex(final byte[] messageDigest)
  {
    BigInteger bigint = new BigInteger(1, messageDigest);
    return bigint.toString(16);
  }
}
