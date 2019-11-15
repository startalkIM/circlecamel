package com.qunar.qtalk.cricle.camel.common.store.swift;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileNotFoundException;

@Slf4j
@Data
@Configuration
public class SwiftConfig {

    @Value("${swift.upload.auth.url}")
    private String url;

    @Value("${swift.upload.username}")
    private String username;

    @Value("${swift.upload.password}")
    private String password;

    @Value("${swift.upload.container}")
    private String containerName;

    @Value("${swift.upload.base_http_url}")
    private String swiftBaseHttpUrl;

    @Value("${local.temp.directory}")
    private String tempDirectory;


    @Bean("container")
    public Container createContainer() throws Exception {
        File file = new File(tempDirectory);
        if (!file.exists()) {
            boolean b = file.mkdirs();
            if (b) {
                log.info("tempDirectory [{}] is not exists,now create");
            } else {
                throw new FileNotFoundException(String.format("tempDirectory %s create fail", tempDirectory));
            }
        }

        AccountConfig config = new AccountConfig();
        config.setAuthUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setAuthenticationMethod(AuthenticationMethod.BASIC);
        // 访问账户
        Account account = new AccountFactory(config).createAccount();
        // 访问容器 (需提前创建，参考Shell中范例
        return account.getContainer(containerName);
    }

}
