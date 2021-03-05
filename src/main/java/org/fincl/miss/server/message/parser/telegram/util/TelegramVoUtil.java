package org.fincl.miss.server.message.parser.telegram.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.message.parser.telegram.Message;
import org.fincl.miss.server.message.parser.telegram.TelegramConstant;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramInterfaceManager;
import org.fincl.miss.server.message.parser.telegram.factory.db.TB_IFS_TLGM_IFVo;
import org.fincl.miss.server.message.parser.telegram.model.DynamicHeaderModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldGroupModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldNodeModel;
import org.fincl.miss.server.message.parser.telegram.model.HeaderModel;
import org.fincl.miss.server.message.parser.telegram.model.MessageModel;
import org.fincl.miss.server.message.parser.telegram.valueobjects.FieldGroupVO;
import org.fincl.miss.server.message.parser.telegram.valueobjects.FieldNodeVO;
import org.fincl.miss.server.message.parser.telegram.valueobjects.FieldVO;
import org.fincl.miss.server.service.ServiceRegister;
import org.fincl.miss.server.util.EnumCode.InOutModeType;
import org.fincl.miss.server.util.EnumCode.SyncType;

public class TelegramVoUtil {
    
    private static void convertGroupVoToMessage(List<?> voList, final Message message, FieldGroupVO fieldGroupVO, String groupName, int gIdx, int sIdx) throws MessageParserException {
        
        FieldGroupModel fieldGroupModel = (FieldGroupModel) fieldGroupVO.getModel();
        List<FieldModel> fieldModelList = fieldGroupModel.getFieldModelList();
        
        for (int i = 0; i < voList.size(); i++) {// VO 리스트 만큼 반복한다.
            Object clsInListObj = (Object) voList.get(i);
            Field[] fields = getFields(clsInListObj.getClass());
            
            for (int j = 0; j < fieldModelList.size(); j++) {// 모델 갯수 만큼 반복처리
                FieldModel fieldModel = fieldModelList.get(j);
                
                if (fieldModel instanceof FieldNodeModel) {// 싱글
                    Object objectValue = null;
                    for (Field field : fields) {
                        if (fieldModel.getId().equals(getModelName(field.getName()))) {
                            objectValue = invokeGetterMethod(clsInListObj, field);
                            break;
                        }
                    }
                    if (objectValue != null) {
                        fieldGroupVO.setValue(fieldModel.getId(), objectValue, i);
                    }
                    else {// default value 셋팅
                        fieldGroupVO.setValue(fieldModel.getId(), ((FieldNodeModel) fieldModel).getInit_value(), i);
                        
                    }
                    
                }
                else {// 그룹
                    List<?> voSubList = (List<?>) getVoClassListValue(clsInListObj, fieldModel.getId());
                    FieldGroupVO feldGroupVO = (FieldGroupVO) fieldGroupVO.getFieldVO(fieldModel.getId(), i);
                    // 자신의 상위에 size를 넣어준다.
                    FieldGroupModel sGroupModel = (FieldGroupModel) feldGroupVO.getModel();
                    String length_field_id = sGroupModel.getLength_filed_id();
                    if (voSubList == null) {
                        fieldGroupVO.setValue(length_field_id, 0, i);
                    }
                    else {
                        fieldGroupVO.setValue(length_field_id, voSubList.size(), i);
                        convertGroupVoToMessage(voSubList, message, feldGroupVO, fieldModel.getId(), gIdx + 1, i);
                    }
                    
                }
            }
            
        }
        // try {
        // message.printLog();
        // }
        // catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }
    
    // private static void convertHeaderGroupVoToMessage(String headerId, final List<?> voList,final Message message, String groupName) throws InstantiationException, IllegalAccessException {
    //
    //
    // for(int i=0;i<voList.size();i++){
    // Object object = voList.get(i);
    // Field[] fields = getFields(object.getClass());
    //
    // for(Field field : fields){
    // String fieldName = getModelName(field.getName());
    // if (!Collection.class.isAssignableFrom(field.getType())) {//싱글일때
    // Object retObj = invokeGetterMethod(object, field);
    //
    // message.setHeaderFieldValue(headerId, groupName+"."+fieldName, retObj,i);
    // }else{// 그룹이면
    //
    // List<?> vosList = (List<?>)invokeGetterMethod(object, field);
    // FieldGroupVO fieldGroup = (FieldGroupVO)message.getFieldVO(groupName+"."+fieldName);
    // FieldGroupModel groupModel = (FieldGroupModel)fieldGroup.getModel();
    // String length_field_id = groupModel.getLength_filed_id();
    //
    // message.setHeaderFieldValue(headerId,groupName+"."+fieldName+"."+length_field_id, voList.size());
    //
    // convertHeaderGroupVoToMessage(headerId,vosList, message, groupName+"."+fieldName);
    // }
    // }
    // }
    //
    // }
    public static void convertHeaderVoToMessage(String headerId, final Object pvResultClass, final Message message) throws MessageParserException {
        
        LinkedHashMap<String, FieldVO> messageMap = message.getAllFields();
        Iterator<?> it = messageMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<?, ?> entry = (Entry<?, ?>) it.next();
            String fieldName = entry.getKey().toString();
            if (entry.getKey().toString().indexOf(".") > 0 && fieldName.substring(0, fieldName.indexOf(".")).equals(headerId)) {
                // fieldName = getMethodName(fieldName.substring(headerId.length()+1));
                fieldName = fieldName.substring(headerId.length() + 1);
                if (entry.getValue() instanceof FieldNodeVO) {// 싱글
                    FieldNodeVO vo = (FieldNodeVO) entry.getValue();
                    Object objectValue = getVoClassValue(pvResultClass, fieldName);
                    if (objectValue != null) {
                        vo.setValue("", objectValue, 0);
                    }
                    else {// default value 셋팅
                        vo.setValue("", ((FieldNodeModel) vo.getModel()).getInit_value(), 0);
                    }
                }
                else {// 그룹
                    FieldGroupVO fieldGroupVO = (FieldGroupVO) entry.getValue();
                    List<?> voList = (List<?>) getVoClassListValue(pvResultClass, fieldName);
                    String length_field_id = ((FieldGroupModel) fieldGroupVO.getModel()).getLength_filed_id();
                    if (voList == null) {
                        message.setFieldValue(length_field_id, 0); // /리스트의 크기를 넣어준다.
                    }
                    else {
                        message.setFieldValue(length_field_id, voList.size()); // /리스트의 크기를 넣어준다.
                        convertGroupVoToMessage(voList, message, fieldGroupVO, fieldName, 0, 0);
                    }
                }
            }
            else {
            }
        }
    }
    
    public static void convertVoToMessage(final Object pvResultClass, final Message message) throws MessageParserException {
        
        LinkedHashMap<String, FieldVO> messageMap = message.getAllFields();
        Iterator<?> it = messageMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<?, ?> entry = (Entry<?, ?>) it.next();
            String fieldName = entry.getKey().toString();
            if (entry.getKey().toString().indexOf(".") > 0) {
                // 헤더 처리 는 header Util에서 처리한다.
            }
            else {
                // 바디 처리
                if (entry.getValue() instanceof FieldNodeVO) {// 싱글
                    FieldNodeVO vo = (FieldNodeVO) entry.getValue();
                    Object objectValue = getVoClassValue(pvResultClass, fieldName);
                    if (objectValue != null) {
                        vo.setValue("", objectValue, 0);
                    }
                    else {// default value 셋팅
                        vo.setValue("", ((FieldNodeModel) vo.getModel()).getInit_value(), 0);
                    }
                }
                else {// 그룹
                    FieldGroupVO fieldGroupVO = (FieldGroupVO) entry.getValue();
                    // pvResultClass 의 list를 넘겨준다
                    
                    List<?> voList = (List<?>) getVoClassListValue(pvResultClass, fieldName);
                    String length_field_id = ((FieldGroupModel) fieldGroupVO.getModel()).getLength_filed_id();
                    if (voList == null) {
                        message.setFieldValue(length_field_id, 0); // /리스트의 크기를 넣어준다.
                    }
                    else {
                        message.setFieldValue(length_field_id, voList.size()); // /리스트의 크기를 넣어준다.
                        convertGroupVoToMessage(voList, message, fieldGroupVO, fieldName, 0, 0);
                    }
                    
                }
                
            }
        }
    }
    
    private static Object getVoClassListValue(Object pvResultClass, String methodName) {
        
        Field[] fields = getFields(pvResultClass.getClass());
        for (Field field : fields) {
            
            int fieldModifiers = field.getModifiers();
            if (Modifier.isStatic(fieldModifiers)) {
                continue;
            }
            String fieldName = getModelName(field.getName());
            if (fieldName.equals(methodName) && Collection.class.isAssignableFrom(field.getType())) {
                
                return invokeGetterMethod(pvResultClass, field);
            }
        }
        return null;
    }
    
    private static Object getVoClassValue(Object pvResultClass, String methodName) {
        
        Field[] fields = getFields(pvResultClass.getClass());
        for (Field field : fields) {
            
            int fieldModifiers = field.getModifiers();
            if (Modifier.isStatic(fieldModifiers)) {
                continue;
            }
            String fieldName = getModelName(field.getName());
            if (fieldName.equals(methodName)) {
                return invokeGetterMethod(pvResultClass, field);
            }
        }
        return null;
        
    }
    
    @SuppressWarnings("unchecked")
    public static Object convertMessageToInterface(Class<?> interfaceCls, TelegramSysUtil tu, BoundChannel extChannelInbound) throws MessageParserException {
        
        Object messageInterfaceVO = null;
        ServiceRegister serviceRegister = ApplicationContextSupport.getBean(ServiceRegister.class);
        try {
            
            // System.out.println("bodyCls::" + interfaceCls);
            
            messageInterfaceVO = (MessageInterfaceVO) convertMessageToVO(interfaceCls, tu, extChannelInbound);
            
            // 헤더맵을 가져온다.
            LinkedHashMap<String, Object> headerVoMap = null;
            Field[] fields = getFields(messageInterfaceVO.getClass());
            for (Field field : fields) {
                if (field.getName().equalsIgnoreCase(TelegramConstant.TELE_HEADER_VO_MAP)) {
                    headerVoMap = (LinkedHashMap<String, Object>) invokeGetterMethod(messageInterfaceVO, field);
                    break;
                }
            }
            
            // 헤더클래스 목록을 가져와 루프 돌린다.
            MessageModel messageModel = tu.getModel();
            List<HeaderModel> headerModelList = messageModel.getHeaders();
            for (int i = 0; i < headerModelList.size(); i++) {
                HeaderModel headerModel = headerModelList.get(i);
                Class<?> headerCls = serviceRegister.getBizClassLoader().loadClass(TelegramConstant.TELE_HEADER_PAKAGE + headerModel.getId());
                Object headerObj = convertHeaderMessageToVO(headerModel.getId(), headerCls, tu);
                headerVoMap.put(headerModel.getId(), headerObj);
            }
            
            List<DynamicHeaderModel> dynaMicHeaderModelList = messageModel.getDynamic_headers();
            for (int i = 0; i < dynaMicHeaderModelList.size(); i++) {
                DynamicHeaderModel dynamicHeaderModel = dynaMicHeaderModelList.get(i);
                if (tu.isUseDynamicHeader(dynamicHeaderModel.getId())) {// 다이나믹 헤더가 사용중인것만 VO로 만들자.
                    Class<?> headerCls = serviceRegister.getBizClassLoader().loadClass(TelegramConstant.TELE_HEADER_PAKAGE + dynamicHeaderModel.getId());
                    Object headerObj = convertHeaderMessageToVO(dynamicHeaderModel.getId(), headerCls, tu);
                    headerVoMap.put(dynamicHeaderModel.getId(), headerObj);
                }
            } 
            
            messageInterfaceVO = invokeSetterServiceMethod(tu, messageInterfaceVO, extChannelInbound);
  
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return messageInterfaceVO;
    }
    
    public static Object convertHeaderMessageToVO(String headerId, final Class<?> pvResultClass, final TelegramSysUtil tu) throws MessageParserException {
        Message message = tu.getReciveMessage();
        
        Object lvResultVO = TelegramSysUtil.getClassInstance(pvResultClass);
        
        Field[] fields = getFields(pvResultClass);
        for (Field field : fields) {
            int fieldModifiers = field.getModifiers();
            if (Modifier.isStatic(fieldModifiers)) {
                continue;
            }
            String fieldName = getModelName(field.getName());
            
            // System.out.println("========>" + headerId +"==" +fieldName );
            if (!Collection.class.isAssignableFrom(field.getType())) {// 싱글일때
                Object fieldObj = message.getHeaderFieldValue(headerId, fieldName);
                if (null != fieldObj) invokeSetterMethod((Object) lvResultVO, field, fieldObj);
            }
            else {// 그룹일때
                Class<?> genericClass = getGenericClass(field.getGenericType());
                
                FieldGroupVO gVo = (FieldGroupVO) message.getHeaderFieldGroupVO(headerId, fieldName);
                FieldGroupModel groupModel = (FieldGroupModel) gVo.getModel();
                Object lengthFiledValue = message.getHeaderFieldValue(headerId, groupModel.getLength_filed_id());
                int sIdx = Integer.parseInt(lengthFiledValue.toString());
                List<Object> voList = new ArrayList<Object>();
                for (int idx = 0; idx < sIdx; idx++) {
                    Object lvResultListVO = convertHeaderMessageToGroupVO(headerId, genericClass, tu, fieldName, idx, 0);
                    voList.add(lvResultListVO);
                }
                invokeSetterMethod((Object) lvResultVO, field, voList);
            }
            
        }
        
        return lvResultVO;
    }
    
    public static Object convertMessageToVO(final Class<?> pvResultClass, final TelegramSysUtil tu, BoundChannel extChannelInbound) throws MessageParserException {
        Message message = tu.getReciveMessage();
        
        Object lvResultVO = TelegramSysUtil.getClassInstance(pvResultClass);
        
        Field[] fields = getFields(pvResultClass);
        for (Field field : fields) {
            int fieldModifiers = field.getModifiers();
            if (Modifier.isStatic(fieldModifiers)) {
                continue;
            }
            String fieldName = getModelName(field.getName());
            
            if (!Collection.class.isAssignableFrom(field.getType())) {// 싱글일때
            
                Object fieldObj = message.getFieldValue(fieldName);
                if (null != fieldObj) invokeSetterMethod((Object) lvResultVO, field, fieldObj);
            }
            else {// 그룹일때
                Class<?> genericClass = getGenericClass(field.getGenericType());
                FieldGroupVO gVo = (FieldGroupVO) message.getFieldGroupVO(fieldName);
                if (gVo != null) {
                    FieldGroupModel groupModel = (FieldGroupModel) gVo.getModel();
                    Object lengthFiledValue = message.getFieldValue(groupModel.getLength_filed_id());
                    int sIdx = Integer.parseInt(lengthFiledValue.toString());
                    List<Object> voList = new ArrayList<Object>();
                    for (int idx = 0; idx < sIdx; idx++) {
                        Object lvResultListVO = convertMessageToGroupVO(genericClass, tu, fieldName, idx, 0);
                        voList.add(lvResultListVO);
                    }
                    invokeSetterMethod((Object) lvResultVO, field, voList);
                }
                
            }
            
        }
        // 서비스 아이디
        if(TelegramConstant.TELE_GETTER_IF_SERVICE_FLAG){
            TelegramInterfaceManager telegramInterfaceManager = ApplicationContextSupport.getBean(TelegramInterfaceManager.class);
            TB_IFS_TLGM_IFVo vo =  telegramInterfaceManager.getInterfaceInfo(tu.getInterfaceId()); 
            invokeSetterMethod((Object) lvResultVO, "ServiceId", vo.getServiceId());//IF에서 추출
        }else{
            invokeSetterMethod((Object) lvResultVO, "ServiceId", tu.getServiceId());//전문에서 추출
        }
        
        // if 아이디
        invokeSetterMethod((Object) lvResultVO, "InterfaceId", tu.getInterfaceId());
        // guid 아이디
        invokeSetterMethod((Object) lvResultVO, "Guid", tu.getGlobalId());
        
        // 전문일련번호
        invokeSetterMethod((Object) lvResultVO, "Id", tu.getWhbnSttlSrn());
        
        
        lvResultVO = invokeSetterServiceMethod(tu, lvResultVO, extChannelInbound);
        
        
        
        return lvResultVO;
    }
    
    private static Object invokeSetterServiceMethod(TelegramSysUtil tu, Object lvResultVO, BoundChannel boundChannel){
        boolean isInvokable = true;
        // FEP인경우
        if (boundChannel.getSyncTypeEnum() == SyncType.PUBSUB) {
            // 타발이 아니면 invoke 하지말기
            if (TelegramConstant.TELE_TX_TYPE_FEP.equals(tu.getTrnmSysDcd()) && TelegramConstant.TELE_TX_SEND_CODE.equals(tu.getRqstRspnDcd())) {// FEP 타발
                isInvokable = true;
            }
            else {
                isInvokable = false;
            }
        }
        else {
            if (boundChannel.getInOutTypeEnum() == InOutModeType.CLIENT) {// 당발이면
                isInvokable = false;
            }
            else if (boundChannel.getInOutTypeEnum() == InOutModeType.SERVER) {// 타발이면
                isInvokable = true;
            }
        } 
        invokeSetterMethod((Object) lvResultVO, "Invokable", isInvokable);
        return lvResultVO;
    }
    
    private static Object convertHeaderMessageToGroupVO(String headerId, Class<?> pvResultClass, final TelegramSysUtil tu, String groupName, int gidx, int sidx) throws MessageParserException {
        Message message = tu.getReciveMessage();
        
        Object lvResultVO = TelegramSysUtil.getClassInstance(pvResultClass);
        
        Field[] fields = getFields(pvResultClass);
        for (Field field : fields) {
            
            int fieldModifiers = field.getModifiers();
            if (Modifier.isStatic(fieldModifiers)) {
                continue;
            }
            String fieldName = getModelName(field.getName());
            if (!"".equals(groupName)) {
                fieldName = groupName + "." + fieldName;
            }
            
            if (!Collection.class.isAssignableFrom(field.getType())) {// 싱글일때
                Object fieldObj = null;
                if (!"".equals(groupName) && groupName.indexOf(".") > 0) {
                    
                    fieldObj = message.getHeaderFieldValue(headerId, groupName, gidx, getModelName(field.getName()), sidx);
                }
                else {
                    fieldObj = message.getHeaderFieldValue(headerId, fieldName, gidx);
                }
                
                if (null != fieldObj) invokeSetterMethod((Object) lvResultVO, field, fieldObj);
                
            }
            else {// 그룹일때
                Class<?> genericClass = getGenericClass(field.getGenericType());
                FieldGroupVO gVo = (FieldGroupVO) message.getHeaderFieldGroupVO(headerId, fieldName);
                FieldGroupModel groupModel = (FieldGroupModel) gVo.getModel();
                Object lengthFiledValue = null;
                
                lengthFiledValue = message.getHeaderFieldValue(headerId, groupName + "." + groupModel.getLength_filed_id(), gidx);
                
                // Object lengthFiledValue = message.getFieldValue(groupName+"."+groupModel.getLength_filed_id(),arrIdx);
                int cIdx = Integer.parseInt(lengthFiledValue.toString());
                List<Object> voList = new ArrayList<Object>();
                for (int lIdx = 0; lIdx < cIdx; lIdx++) {
                    Object lvResultListVO = convertHeaderMessageToGroupVO(headerId, genericClass, tu, fieldName, gidx, lIdx);
                    voList.add(lvResultListVO);
                }
                invokeSetterMethod((Object) lvResultVO, field, voList);
            }
        }// for(Field field : fields)
        return lvResultVO;
    }
    
    private static Object convertMessageToGroupVO(Class<?> pvResultClass, final TelegramSysUtil tu, String groupName, int gidx, int sidx) throws MessageParserException {
        Message message = tu.getReciveMessage();
        
        Object lvResultVO = TelegramSysUtil.getClassInstance(pvResultClass);
        
        Field[] fields = getFields(pvResultClass);
        for (Field field : fields) {
            
            int fieldModifiers = field.getModifiers();
            if (Modifier.isStatic(fieldModifiers)) {
                continue;
            }
            String fieldName = getModelName(field.getName());
            if (!"".equals(groupName)) {
                fieldName = groupName + "." + fieldName;
            }
            
            if (!Collection.class.isAssignableFrom(field.getType())) {// 싱글일때
                Object fieldObj = null;
                if (!"".equals(groupName) && groupName.indexOf(".") > 0) {
                    
                    fieldObj = message.getFieldValue(groupName, gidx, getModelName(field.getName()), sidx);
                }
                else {
                    fieldObj = message.getFieldValue(fieldName, gidx);
                }
                
                if (null != fieldObj) invokeSetterMethod((Object) lvResultVO, field, fieldObj);
                
            }
            else {// 그룹일때
                Class<?> genericClass = getGenericClass(field.getGenericType());
                FieldGroupVO gVo = (FieldGroupVO) message.getFieldGroupVO(fieldName);
                FieldGroupModel groupModel = (FieldGroupModel) gVo.getModel();
                Object lengthFiledValue = null;
                
                lengthFiledValue = message.getFieldValue(groupName + "." + groupModel.getLength_filed_id(), gidx);
                
                // Object lengthFiledValue = message.getFieldValue(groupName+"."+groupModel.getLength_filed_id(),arrIdx);
                int cIdx = Integer.parseInt(lengthFiledValue.toString());
                List<Object> voList = new ArrayList<Object>();
                for (int lIdx = 0; lIdx < cIdx; lIdx++) {
                    Object lvResultListVO = convertMessageToGroupVO(genericClass, tu, fieldName, gidx, lIdx);
                    voList.add(lvResultListVO);
                }
                invokeSetterMethod((Object) lvResultVO, field, voList);
            }
        }// for(Field field : fields)
        return lvResultVO;
    }
    
    private static String getModelName(String inStr) {
        String retStr = "";
        for (int i = 0; i < inStr.length(); i++) {
            char chr = inStr.charAt(i);
            if (chr >= 65 && chr <= 90) {
                retStr += "_" + chr;
            }
            else {
                retStr += chr;
            }
        }
        return retStr.toUpperCase();
    }
    
    // private static String getMethodName(String inStr){
    // String retStr="";
    // inStr = inStr.toLowerCase();
    // for(int i=0;i<inStr.length();i++){
    // char chr = inStr.charAt(i);
    // if(chr=='_'){
    // chr = Character.toUpperCase(inStr.charAt(++i));
    // }
    // retStr +=chr;
    // }
    // return retStr;
    // }
    
    private static Class<?> getGenericClass(Type type) throws MessageParserException {
        
        String paramStr = type.toString();
        // System.out.println("getGenericClass=[" + paramStr + "]");
        
        int s = paramStr.indexOf("<");
        if (s > 0) {
            int e = paramStr.indexOf(">", s);
            if (e < 0) {
                return null;
            }
            
            String genericClassStr = paramStr.substring(s + 1, e);
            
            return TelegramSysUtil.getClass(genericClassStr);
            
        }
        return null;
    }
    
    private static <T> Field[] getFields(final Class<T> clazz) {
        return getFields(clazz, true);
    }
    
    private static <T> Field[] getFields(final Class<T> clazz, final boolean pbIncludeSuperClass) {
        Field[] fields = null;
        
        if (pbIncludeSuperClass) {
            fields = getAllDeclaredFields(clazz);
        }
        else {
            fields = clazz.getDeclaredFields();
        }
        return fields;
    }
    
    /**
     * Get all declared fields on the leaf class and all superclasses.
     * Leaf class methods are included first.
     */
    private static Field[] getAllDeclaredFields(Class<?> clazz) {
        List<Field> list = new LinkedList<Field>();
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            list.addAll(Arrays.asList(searchType.getDeclaredFields()));
            searchType = searchType.getSuperclass();
        }
        return (Field[]) list.toArray(new Field[list.size()]);
    }
    
    private static Object invokeGetterMethod(final Object poObject, final Field poField) {
        final Class<?> clazz = poObject.getClass();
        final String fieldName = poField.getName();
        final String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String getterMethodName = null;
        Class<?> fieldClazz = poField.getType();
        if (fieldClazz.equals(boolean.class)) {
            getterMethodName = "is" + methodName;
        }
        else {
            getterMethodName = "get" + methodName;
        }
        
        Method getterMethod = findMethod(clazz, getterMethodName);
        Object invokeResult = invokeMethod(getterMethod, poObject);
        
        return invokeResult;
    }
    
    private static void invokeSetterMethod(final Object poObject, final String methodName, final Object psParam) {
        final Class<?> clazz = poObject.getClass();
        
        String setterMethodName = "set" + methodName;
        Method setterMethod = findMethod(clazz, setterMethodName, new Class[] { psParam.getClass() });
        if (setterMethod == null && psParam instanceof Boolean) {
            setterMethod = findMethod(clazz, setterMethodName, new Class[] { boolean.class });
        }
        
        Object value = null;
        if (psParam instanceof String) {
            value = getString2Object(psParam.getClass(), (String) psParam);
        }
        else {
            value = psParam;
        }
        
        invokeMethod(setterMethod, poObject, new Object[] { value });
    }
    
    private static void invokeSetterMethod(final Object poObject, final Field poField, final Object psParam) {
        final Class<?> clazz = poObject.getClass();
        final String fieldName = poField.getName();
        final String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String setterMethodName = "set" + methodName;
        
        Method setterMethod = findMethod(clazz, setterMethodName, new Class[] { poField.getType() });
        if (setterMethod == null && psParam instanceof Boolean) {
            setterMethod = findMethod(clazz, setterMethodName, new Class[] { boolean.class });
        }
        
        Object value = null;
        if (psParam instanceof String) {
            value = getString2Object(poField.getType(), (String) psParam);
        }
        else {
            value = psParam;
        }
        
        invokeMethod(setterMethod, poObject, new Object[] { value });
    }
    
    //
    // private static void invokeSetterMethod(final Object poObject, final String fieldName, final Object psParam) {
    // final Class<?> clazz = poObject.getClass();
    // final String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    // String setterMethodName = "set" + methodName;
    //
    // Class[] tmp= {String.class};
    // Method setterMethod = findMethod(clazz, setterMethodName, tmp);
    //
    // Object value = getString2Object(String.class, (String)psParam);
    //
    // invokeMethod(setterMethod, poObject, new Object[]{value});
    // }
    
    @SuppressWarnings("unchecked")
    private static <T> T getString2Object(final Class<T> poTargetClass, final String psSourceValue) {
        
        Class<T> poSimpleClass = poTargetClass;
        Object retValue = null;
        
        if (poSimpleClass.equals(String.class)) {
            retValue = psSourceValue;
        }
        else if (poSimpleClass.equals(int.class)) {
            retValue = Integer.parseInt(psSourceValue);
        }
        else if (poSimpleClass.equals(Integer.class)) {
            retValue = Integer.parseInt(psSourceValue);
        }
        else if (poSimpleClass.equals(boolean.class)) {
            retValue = Boolean.parseBoolean(psSourceValue);
        }
        else if (poSimpleClass.equals(Boolean.class)) {
            retValue = Boolean.parseBoolean(psSourceValue);
        }
        else if (poSimpleClass.equals(BigDecimal.class)) {
            if ("".equals(psSourceValue.toString().trim())) {
                retValue = new BigDecimal("0");
            }
            else {
                retValue = new BigDecimal(psSourceValue);
            }
        }
        
        return (T) retValue;
        
    }
    
    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and no parameters. Searches all superclasses up to <code>Object</code>.
     * <p>
     * Returns <code>null</code> if no {@link Method} can be found.
     * 
     * @param clazz
     *            the class to introspect
     * @param name
     *            the name of the method
     * @return the Method object, or <code>null</code> if none found
     */
    private static Method findMethod(Class<?> clazz, String name) {
        return findMethod(clazz, name, new Class[0]);
    }
    
    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and parameter types. Searches all superclasses up to <code>Object</code>.
     * <p>
     * Returns <code>null</code> if no {@link Method} can be found.
     * 
     * @param clazz
     *            the class to introspect
     * @param name
     *            the name of the method
     * @param paramTypes
     *            the parameter types of the method
     * @return the Method object, or <code>null</code> if none found
     */
    @SuppressWarnings("rawtypes")
    private static Method findMethod(Class clazz, String name, Class[] paramTypes) {
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (name.equals(method.getName()) && Arrays.equals(paramTypes, method.getParameterTypes())) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }
    
    /**
     * Invoke the specified {@link Method} against the supplied target object
     * with no arguments. The target object can be <code>null</code> when
     * invoking a static {@link Method}.
     * <p>
     * Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     * 
     * @param method
     *            the method to invoke
     * @param target
     *            the target object to invoke the method on
     * @return the invocation result, if any
     * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
     */
    private static Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target, null);
    }
    
    /**
     * Invoke the specified {@link Method} against the supplied target object
     * with the supplied arguments. The target object can be <code>null</code> when invoking a static {@link Method}.
     * <p>
     * Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     * 
     * @param method
     *            the method to invoke
     * @param target
     *            the target object to invoke the method on
     * @param args
     *            the invocation arguments (may be <code>null</code>)
     * @return the invocation result, if any
     */
    private static Object invokeMethod(Method method, Object target, Object[] args) {
        try {
            return method.invoke(target, args);
        }
        catch (IllegalAccessException e) {
            throw new MessageParserException(ErrorConstant.TELEGRAM_METHOD_ACCESS_ERROR, "mehotd=[" + method.getName() + "]", e);// 메소드 접근실패 하였습니다.
        }
        catch (IllegalArgumentException e) {
            throw new MessageParserException(ErrorConstant.TELEGRAM_METHOD_ARGUMENT_ERROR, "mehotd=[" + method.getName() + "]", e);// 메소드 입력값 접근실패 하였습니다.
        }
        catch (InvocationTargetException e) {
            throw new MessageParserException(ErrorConstant.TELEGRAM_METHOD_INVOKE_ERROR, "mehotd=[" + method.getName() + "]", e);// 메소드 실행실패 하였습니다.
        }
        
    }
}
