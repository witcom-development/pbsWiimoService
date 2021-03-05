package org.fincl.miss.server.logging.profile;

import java.util.ArrayList;
import java.util.List;

public class TraceLogInheritableThreadLocal extends InheritableThreadLocal<List<TraceLog>> {
    
    @Override
    protected List<TraceLog> initialValue() {
        //
        return new ArrayList<TraceLog>();
    }
}
