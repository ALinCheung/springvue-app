package com.springvue.app.common.utils;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.support.LdapUtils;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LdapUtil {

    /**
     * 获取LDAP连接
     *
     * @return
     */
    public static LdapTemplate getLdapTemplate(String url, String base, String username, String password) {
        LdapContextSource source = new LdapContextSource();
        source.setUrl(url);
        source.setBase(base);
        source.setPooled(true);
        source.setCacheEnvironmentProperties(false);
        //解决乱码
        Map<String, Object> config = new HashMap<>();
        config.put("java.naming.ldap.attributes.binary", "objectGUID");
        source.setBaseEnvironmentProperties(config);
        source.setAuthenticationSource(new AuthenticationSource() {

            @Override
            public String getPrincipal() {
                return username;
            }

            @Override
            public String getCredentials() {
                return password;
            }
        });
        return new LdapTemplate(source);
    }

    /**
     * 获取过滤条件
     * @param isSearchCn 是否查询用户名
     * @param isSearchMail 是否查询邮箱
     * @param principal 用户名
     * @return
     */
    public static OrFilter getFilter(Boolean isSearchCn, Boolean isSearchMail, String principal) {
        OrFilter filter = new OrFilter();
        boolean filterNotEmpty = false;
        if (isSearchCn != null && isSearchCn) {
            filter.append(new EqualsFilter("cn", principal));
            filterNotEmpty = true;
        }
        if (isSearchMail != null && isSearchMail) {
            filter.append(new EqualsFilter("mail", principal));
            filterNotEmpty = true;
        }
        if (!filterNotEmpty) {
            return null;
        }
        return filter;
    }

    /**
     * 验证连接
     *
     * @param ldapTemplate
     * @param filter
     * @param password
     * @return
     */
    public static boolean authenticate(LdapTemplate ldapTemplate, String filter, String password) {
        return ldapTemplate.authenticate(LdapUtils.emptyLdapName(), filter, password);
    }

    /**
     * 获取ldap用户信息，用于同步本地账号
     *
     * @param ldapTemplate
     * @param filter
     * @return
     */
    public static Map<String, String> getUserInfo(LdapTemplate ldapTemplate, OrFilter filter) {
        List<Map<String, String>> users = ldapTemplate.search(org.springframework.ldap.support.LdapUtils.emptyLdapName(), filter.toString(), new AttributesMapper<Map<String, String>>() {
            @Override
            public Map<String, String> mapFromAttributes(Attributes attributes) throws NamingException {
                Map<String, String> userInfo = null;
                if (attributes != null) {
                    userInfo = new HashMap<>();
                    userInfo.put("cn", (String) attributes.get("cn").get());
                    userInfo.put("sn", (String) attributes.get("sn").get());
                    userInfo.put("mail", (String) attributes.get("mail").get());
                    userInfo.put("employeeType", (String) attributes.get("employeeType").get());
                    userInfo.put("ou", (String) attributes.get("ou").get());
                }
                return userInfo;
            }
        });
        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }
    }

}
