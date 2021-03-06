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
package com.test.sharksharding.use1;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.sharksharding.core.shard.SharkJdbcTemplate;
import com.sharksharding.sql.PropertyPlaceholderConfigurer;
import com.sharksharding.sql.SQLTemplate;

/**
 * email反向索引表Dao接口
 * 
 * @author gaoxianglong
 */
@Repository
public class EmailDaoImpl implements EmailDao {
	@Resource
	private SharkJdbcTemplate jdbcTemplate;

	// @Resource
	private PropertyPlaceholderConfigurer property;

	@Resource
	private SQLTemplate sqlTemplate;

	@Resource
	private EmailInfoMapper emailMapper;

	@Override
	public void setEmail(EmailInfo email) throws Exception {
		final String sql = property.getSql("insertEmail", email.getEmail_hash());
		jdbcTemplate.update(sql, new Object[] { email.getEmail(), email.getUid() });
	}

	@Override
	public List<EmailInfo> getEmail(EmailInfo email) throws Exception {
		final String sql = property.getSql("queryEmail", email.getEmail_hash());
		return jdbcTemplate.query(sql, new Object[] { email.getEmail() }, emailMapper);
	}

	@Override
	public void setEmail(Map<String, Object> params) throws Exception {
		final String sql = sqlTemplate.getSql("setEmail", params);
		jdbcTemplate.update(sql);
	}

	@Override
	public List<EmailInfo> getEmail(Map<String, Object> params) throws Exception {
		final String sql = sqlTemplate.getSql("getEmail", params);
		return jdbcTemplate.query(sql, emailMapper);
	}
}