package org.oekofen.collector.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil
{

  private static final Logger LOG = LogManager.getLogger();

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
      LOG.info("SHA-256 not available for calculating hash, falling back to MD5.");
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
