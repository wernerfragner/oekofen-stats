package org.oekofen.collector.target;

import org.oekofen.collector.CollectorRecord;

public interface CollectorTarget
{
  void accept(CollectorRecord rec);
}
