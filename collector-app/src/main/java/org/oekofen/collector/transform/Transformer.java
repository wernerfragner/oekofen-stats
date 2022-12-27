package org.oekofen.collector.transform;

import org.oekofen.collector.CollectorRecord;

public interface Transformer
{
  CollectorRecord transform(CollectorRecord rec);
}
