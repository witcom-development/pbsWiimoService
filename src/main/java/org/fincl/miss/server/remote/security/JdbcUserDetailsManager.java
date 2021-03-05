package org.fincl.miss.server.remote.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.remote.security.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

public class JdbcUserDetailsManager extends JdbcDaoImpl {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 로그인시
     * 
     * @param username
     *            유저아이디
     * 
     * @return UserDetails(ExtUser)
     * @throws UsernameNotFoundException
     *             by woo
     */
    public UserDetails loadUserByUsername(String username) {
        
        Map param = new HashMap();
        param.put("userId", username); // ibatis을 위한 Map형태 파라메터값 만들기
        
        List detailList = adminService.getLoginUserDetail(param); // 사용자정보
        List dbAuths = adminService.getAuthList(param); // 권한
        
        if (detailList.size() == 0) {
            logger.debug("Query returned no results for user '" + username + "'");
            throw new UsernameNotFoundException(messages.getMessage("JdbcDaoImpl.notFound", new Object[] { username }, "Username {0} not found"));
        }
        
        Map temp = (Map) detailList.get(0); // 사용자리스트 첫번째 데이터 (로그인은 한명이므로)
        
        String userId = (String) temp.get("USER_ID");
        String password = (String) temp.get("PASSWORD");
        boolean enabled = "1".equals("" + temp.get("ENABLED")) ? true : false;
        boolean passwordNonExpired = "1".equals("" + temp.get("PASSWORD_NON_EXPIRED")) ? true : false;
        boolean accountNonLocked = "1".equals("" + temp.get("ACCOUNT_NON_LOCKED")) ? true : false;
        
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(); // 권한 리스트 보관장소
        
        // 권한만 리스트로 변환
        java.util.Iterator it = dbAuths.iterator();
        while (it.hasNext()) {
            Map map = (Map) it.next();
            authorities.add(new SimpleGrantedAuthority(map.get("ROLE_NAME").toString()));
        }
        
        UserDetails user = new User(userId, password, enabled, accountNonLocked, passwordNonExpired, accountNonLocked, authorities);
        
        return user;
    }
    
}
