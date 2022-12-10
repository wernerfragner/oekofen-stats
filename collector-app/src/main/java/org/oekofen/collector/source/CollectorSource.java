package org.oekofen.collector.source;

import org.oekofen.collector.CollectorRecord;

import java.util.List;

public interface CollectorSource
{
  List<CollectorRecord> collect();
}
