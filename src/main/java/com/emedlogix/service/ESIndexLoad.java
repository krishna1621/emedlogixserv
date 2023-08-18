package com.emedlogix.service;

import com.emedlogix.entity.EindexLevels;
import com.emedlogix.entity.EindexVO;

public interface ESIndexLoad {
    public void loadIndexMainTerm();

    public void loadIndexLevelTerm();
}
