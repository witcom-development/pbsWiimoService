package org.fincl.miss.server.message.parser.telegram.model;
/** 
 * - 전문 모델 인터페이스 - 
 */

public interface Model {
    public String getId();
    public String getKey();
    public Model getParent();
    public String getName();
}
