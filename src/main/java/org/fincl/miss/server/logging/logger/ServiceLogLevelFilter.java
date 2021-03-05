package org.fincl.miss.server.logging.logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.message.Message;

/**
 * 서비스별 로그 레벨을 조정하는 필터 ( key = "server.service" )
 */
@Plugin(name="ServiceLogLevelFilter", category=Node.CATEGORY, elementType="Filter", printObject=true)
public class ServiceLogLevelFilter extends AbstractFilter {

	private static final long serialVersionUID = 1429425791978213999L;

	private Level defaultThreshold;
	
	private final String key;

	private Map<String, Level> levelMap = new HashMap<String, Level>();
	
	@PluginFactory
    public static ServiceLogLevelFilter createFilter(
            @PluginAttribute("key") final String key,
            @PluginElement("Pairs") final KeyValuePair[] pairs,
            @PluginAttribute("defaultThreshold") final Level defaultThreshold,
            @PluginAttribute("onMatch") final Result onMatch,
            @PluginAttribute("onMismatch") final Result onMismatch) {
		
        final Map<String, Level> map = new HashMap<String, Level>();
        for (final KeyValuePair pair : pairs) {
            map.put(pair.getKey(), Level.toLevel(pair.getValue()));
        }
        final Level level = defaultThreshold == null ? Level.INFO : defaultThreshold;
        return new ServiceLogLevelFilter(key, map, level, onMatch, onMismatch);
    }
	
	public static ServiceLogLevelFilter createFilter(ServiceLogLevelFilter oldFilter, Level defaultThreshold) {
        return new ServiceLogLevelFilter(oldFilter.getKey(), oldFilter.getLevelMap(), defaultThreshold, oldFilter.getOnMatch(), oldFilter.getOnMismatch());
    }
	
	private ServiceLogLevelFilter(final String key, final Map<String, Level> pairs, final Level defaultLevel,
	                                   final Result onMatch, final Result onMismatch) {
        super(onMatch, onMismatch);
        if (key == null) {
            throw new NullPointerException("key cannot be null");
        }
        this.key = key;
        this.levelMap = pairs;
        this.defaultThreshold = defaultLevel;
	}
	
	public void addLogLevel(String key, Level level) {
		levelMap.put(key, level);
	}
	
	@Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServiceLogLevelFilter other = (ServiceLogLevelFilter) obj;
        if (defaultThreshold == null) {
            if (other.defaultThreshold != null) {
                return false;
            }
        } else if (!defaultThreshold.equals(other.defaultThreshold)) {
            return false;
        }
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        if (levelMap == null) {
            if (other.levelMap != null) {
                return false;
            }
        } else if (!levelMap.equals(other.levelMap)) {
            return false;
        }
        return true;
    }
	
	public Result filter(Level level) {
		final Object value = ThreadContext.get(key);
		Level ctxLevel = levelMap.get(value);
        if (ctxLevel == null) {
            ctxLevel = defaultThreshold;
        }
        
//        System.out.println("=====================================");
//        System.out.println(level);
//        System.out.println(value);
//        System.out.println(ctxLevel);
//        System.out.println(this);
//        System.out.println(Thread.currentThread().getId());
//        System.out.println("=====================================");
        return level.isMoreSpecificThan(ctxLevel) ? onMatch : onMismatch;
	}
	
	@Override
	public Result filter(LogEvent event) {
		return filter(event.getLevel());
	}
	
	@Override
	public Result filter(Logger logger, Level level, Marker marker,
			Message msg, Throwable t) {
		return filter(level);
	}
	
	@Override
	public Result filter(Logger logger, Level level, Marker marker, Object msg,
			Throwable t) {
		return filter(level);
	}
	
	@Override
	public Result filter(Logger logger, Level level, Marker marker, String msg,
			Object... params) {
		return filter(level);
	}
	
	public Level getDefaultThreshold() {
		return defaultThreshold;
	}
	
	public void setDefaultThreshold(Level defaultThreshold) {
		this.defaultThreshold = defaultThreshold;
	}
	
	public String getKey() {
		return key;
	}
	
	public KeyValuePair[] getKeyValuePair() {
		Iterator<String> it = levelMap.keySet().iterator();
		KeyValuePair[] pairs = new KeyValuePair[levelMap.size()];
		for(int i=0;i<pairs.length;i++) {
			String key = it.next();
			pairs[i] = new KeyValuePair(key, levelMap.get(key).name());
		}
		return pairs;
	}
	
	public Map<String, Level> getLevelMap() {
		return levelMap;
	}
	
	public void removeLevelMapMember(String key) {
		if(levelMap.containsKey(key)) {
			levelMap.remove(key);
		}
	}

	@Override
	public String toString() {
		return "ServiceLogLevelFilter [defaultThreshold=" + defaultThreshold
				+ ", key=" + key + ", levelMap=" + levelMap + "] :::: " +getClass().getName() + "@" + Integer.toHexString(hashCode());
	}
	
//	public static boolean isServiceFilterable(AbstractAppender appender) {
//		boolean result = false;
//		
//		try {
//			CompositeFilter comFilters = (CompositeFilter)appender.getFilter();
//			if(!comFilters.getFilters().isEmpty() && 
//					comFilters.getFilters().get(0) instanceof ServiceLogLevelFilter) {
//				result = true;	
//			}
//		} catch(ClassCastException e) {
//			return false; 
//		}
//		
//		return result;
//	}
}
