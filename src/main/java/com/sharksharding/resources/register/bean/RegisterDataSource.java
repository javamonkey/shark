/*
 * Copyright 2015-2101 gaoxianglong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sharksharding.resources.register.bean;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.FileSystemResource;

import com.sharksharding.core.shard.GetJdbcTemplate;
import com.sharksharding.core.shard.SharkJdbcTemplate;
import com.sharksharding.exception.RegisterBeanException;
import com.sharksharding.util.TmpManager;

/**
 * 动态向spring的ioc容器中注册bean实例实现
 * 
 * @author gaoxianglong
 * 
 * @version 1.3.7
 */
public class RegisterDataSource implements RegisterBean {
	private static ApplicationContext aContext;
	private static Logger logger = LoggerFactory.getLogger(RegisterDataSource.class);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.aContext = applicationContext;
	}

	/**
	 * 注册bean
	 * 
	 * @author gaoxianglong
	 * 
	 * @param nodePathValue
	 *            zookeeper注册中心的节点value
	 * 
	 * @param resourceType
	 *            注册中心类型
	 * 
	 * @return void
	 */
	public static void register(String nodePathValue, String resourceType) {
		if (null == aContext)
			return;
		final String tmpdir = TmpManager.createTmp();
		logger.debug("tmpdir-->" + tmpdir);
		try (BufferedWriter out = new BufferedWriter(new FileWriter(tmpdir))) {
			if (null != nodePathValue) {
				out.write(nodePathValue);
				out.flush();
				FileSystemResource resource = new FileSystemResource(tmpdir);
				ConfigurableApplicationContext cfgContext = (ConfigurableApplicationContext) aContext;
				DefaultListableBeanFactory beanfactory = (DefaultListableBeanFactory) cfgContext.getBeanFactory();
				/* 验证jdbcTemplate是否已经存在 */
				if (beanfactory.isBeanNameInUse("jdbcTemplate")) {
					beanfactory.removeBeanDefinition("jdbcTemplate");
					logger.debug("delete bean-->jdbcTemplate");
				}
				/* 将配置中心获取的配置信息与当前上下文中的ioc容器进行合并 */
				new XmlBeanDefinitionReader(beanfactory).loadBeanDefinitions(resource);
				/* 替换jdbcTemplate引用 */
				GetJdbcTemplate.setSharkJdbcTemplate((SharkJdbcTemplate) beanfactory.getBean("jdbcTemplate"));
			}
		} catch (Exception e) {
			throw new RegisterBeanException(e.toString());
		} finally {
			TmpManager.deleteTmp(tmpdir);
		}
	}

	/**
	 * 关闭连接池中持有的数据库连接,在连接池中设置属性destroy-method="close"便可自动关闭连接池所持有的数据库连接
	 * 
	 * @author gaoxianglong
	 * 
	 * @param connPoolType
	 *            连接池类型,缺省为0即ComboPooledDataSource,反之为DruidDataSource
	 * 
	 * @param beanfactory
	 * 
	 * @param num
	 * 
	 * @return void
	 */
	// @SuppressWarnings("unused")
	// @Deprecated
	// private void close(int connPoolType, DefaultListableBeanFactory
	// beanfactory, int num) {
	// final String beanName = "dataSource" + num;
	// if (beanfactory.isBeanNameInUse(beanName)) {
	// if (0 == connPoolType) {
	// ComboPooledDataSource dataSource = (ComboPooledDataSource)
	// beanfactory.getBean(beanName);
	// dataSource.close();
	// } else {
	// DruidDataSource dataSource = (DruidDataSource)
	// beanfactory.getBean(beanName);
	// dataSource.close();
	// }
	// logger.debug("关闭dataSource" + num + "所持有的数据库连接");
	// }
	// }
}