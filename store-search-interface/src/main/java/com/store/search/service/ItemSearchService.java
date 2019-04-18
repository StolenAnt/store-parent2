package com.store.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    public Map Search(Map searchMap);

    public void importList(List list);

    public void deleteByGoodsIds(List goodsids);
}
