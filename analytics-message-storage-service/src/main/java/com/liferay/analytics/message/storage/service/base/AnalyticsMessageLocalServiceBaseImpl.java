/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.analytics.message.storage.service.base;

import com.liferay.analytics.message.storage.model.AnalyticsMessage;
import com.liferay.analytics.message.storage.model.AnalyticsMessageBodyBlobModel;
import com.liferay.analytics.message.storage.service.AnalyticsMessageLocalService;
import com.liferay.analytics.message.storage.service.persistence.AnalyticsMessagePersistence;
import com.liferay.petra.io.AutoDeleteFileInputStream;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DefaultActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalServiceImpl;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.InputStream;
import java.io.Serializable;

import java.sql.Blob;

import java.util.List;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the base implementation for the analytics message local service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link com.liferay.analytics.message.storage.service.impl.AnalyticsMessageLocalServiceImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.analytics.message.storage.service.impl.AnalyticsMessageLocalServiceImpl
 * @generated
 */
public abstract class AnalyticsMessageLocalServiceBaseImpl
	extends BaseLocalServiceImpl
	implements AnalyticsMessageLocalService, AopService,
			   IdentifiableOSGiService {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Use <code>AnalyticsMessageLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>com.liferay.analytics.message.storage.service.AnalyticsMessageLocalServiceUtil</code>.
	 */

	/**
	 * Adds the analytics message to the database. Also notifies the appropriate model listeners.
	 *
	 * @param analyticsMessage the analytics message
	 * @return the analytics message that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AnalyticsMessage addAnalyticsMessage(
		AnalyticsMessage analyticsMessage) {

		analyticsMessage.setNew(true);

		return analyticsMessagePersistence.update(analyticsMessage);
	}

	/**
	 * Creates a new analytics message with the primary key. Does not add the analytics message to the database.
	 *
	 * @param analyticsMessageId the primary key for the new analytics message
	 * @return the new analytics message
	 */
	@Override
	@Transactional(enabled = false)
	public AnalyticsMessage createAnalyticsMessage(long analyticsMessageId) {
		return analyticsMessagePersistence.create(analyticsMessageId);
	}

	/**
	 * Deletes the analytics message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param analyticsMessageId the primary key of the analytics message
	 * @return the analytics message that was removed
	 * @throws PortalException if a analytics message with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	public AnalyticsMessage deleteAnalyticsMessage(long analyticsMessageId)
		throws PortalException {

		return analyticsMessagePersistence.remove(analyticsMessageId);
	}

	/**
	 * Deletes the analytics message from the database. Also notifies the appropriate model listeners.
	 *
	 * @param analyticsMessage the analytics message
	 * @return the analytics message that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	public AnalyticsMessage deleteAnalyticsMessage(
		AnalyticsMessage analyticsMessage) {

		return analyticsMessagePersistence.remove(analyticsMessage);
	}

	@Override
	public DynamicQuery dynamicQuery() {
		Class<?> clazz = getClass();

		return DynamicQueryFactoryUtil.forClass(
			AnalyticsMessage.class, clazz.getClassLoader());
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return analyticsMessagePersistence.findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsMessageModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return analyticsMessagePersistence.findWithDynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsMessageModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return analyticsMessagePersistence.findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return analyticsMessagePersistence.countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection) {

		return analyticsMessagePersistence.countWithDynamicQuery(
			dynamicQuery, projection);
	}

	@Override
	public AnalyticsMessage fetchAnalyticsMessage(long analyticsMessageId) {
		return analyticsMessagePersistence.fetchByPrimaryKey(
			analyticsMessageId);
	}

	/**
	 * Returns the analytics message with the primary key.
	 *
	 * @param analyticsMessageId the primary key of the analytics message
	 * @return the analytics message
	 * @throws PortalException if a analytics message with the primary key could not be found
	 */
	@Override
	public AnalyticsMessage getAnalyticsMessage(long analyticsMessageId)
		throws PortalException {

		return analyticsMessagePersistence.findByPrimaryKey(analyticsMessageId);
	}

	@Override
	public ActionableDynamicQuery getActionableDynamicQuery() {
		ActionableDynamicQuery actionableDynamicQuery =
			new DefaultActionableDynamicQuery();

		actionableDynamicQuery.setBaseLocalService(
			analyticsMessageLocalService);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(AnalyticsMessage.class);

		actionableDynamicQuery.setPrimaryKeyPropertyName("analyticsMessageId");

		return actionableDynamicQuery;
	}

	@Override
	public IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			new IndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setBaseLocalService(
			analyticsMessageLocalService);
		indexableActionableDynamicQuery.setClassLoader(getClassLoader());
		indexableActionableDynamicQuery.setModelClass(AnalyticsMessage.class);

		indexableActionableDynamicQuery.setPrimaryKeyPropertyName(
			"analyticsMessageId");

		return indexableActionableDynamicQuery;
	}

	protected void initActionableDynamicQuery(
		ActionableDynamicQuery actionableDynamicQuery) {

		actionableDynamicQuery.setBaseLocalService(
			analyticsMessageLocalService);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(AnalyticsMessage.class);

		actionableDynamicQuery.setPrimaryKeyPropertyName("analyticsMessageId");
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException {

		return analyticsMessageLocalService.deleteAnalyticsMessage(
			(AnalyticsMessage)persistedModel);
	}

	@Override
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return analyticsMessagePersistence.findByPrimaryKey(primaryKeyObj);
	}

	/**
	 * Returns a range of all the analytics messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of analytics messages
	 * @param end the upper bound of the range of analytics messages (not inclusive)
	 * @return the range of analytics messages
	 */
	@Override
	public List<AnalyticsMessage> getAnalyticsMessages(int start, int end) {
		return analyticsMessagePersistence.findAll(start, end);
	}

	/**
	 * Returns the number of analytics messages.
	 *
	 * @return the number of analytics messages
	 */
	@Override
	public int getAnalyticsMessagesCount() {
		return analyticsMessagePersistence.countAll();
	}

	/**
	 * Updates the analytics message in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param analyticsMessage the analytics message
	 * @return the analytics message that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AnalyticsMessage updateAnalyticsMessage(
		AnalyticsMessage analyticsMessage) {

		return analyticsMessagePersistence.update(analyticsMessage);
	}

	@Override
	public AnalyticsMessageBodyBlobModel getBodyBlobModel(
		Serializable primaryKey) {

		Session session = null;

		try {
			session = analyticsMessagePersistence.openSession();

			return (AnalyticsMessageBodyBlobModel)session.get(
				AnalyticsMessageBodyBlobModel.class, primaryKey);
		}
		catch (Exception e) {
			throw analyticsMessagePersistence.processException(e);
		}
		finally {
			analyticsMessagePersistence.closeSession(session);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public InputStream openBodyInputStream(long analyticsMessageId) {
		try {
			AnalyticsMessageBodyBlobModel AnalyticsMessageBodyBlobModel =
				getBodyBlobModel(analyticsMessageId);

			Blob blob = AnalyticsMessageBodyBlobModel.getBodyBlob();

			if (blob == null) {
				return _EMPTY_INPUT_STREAM;
			}

			InputStream inputStream = blob.getBinaryStream();

			if (_useTempFile) {
				inputStream = new AutoDeleteFileInputStream(
					_file.createTempFile(inputStream));
			}

			return inputStream;
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	@Activate
	protected void activate() {
		DB db = DBManagerUtil.getDB();

		if ((db.getDBType() != DBType.DB2) &&
			(db.getDBType() != DBType.MYSQL) &&
			(db.getDBType() != DBType.MARIADB) &&
			(db.getDBType() != DBType.SYBASE)) {

			_useTempFile = true;
		}
	}

	@Override
	public Class<?>[] getAopInterfaces() {
		return new Class<?>[] {
			AnalyticsMessageLocalService.class, IdentifiableOSGiService.class,
			PersistedModelLocalService.class
		};
	}

	@Override
	public void setAopProxy(Object aopProxy) {
		analyticsMessageLocalService = (AnalyticsMessageLocalService)aopProxy;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return AnalyticsMessageLocalService.class.getName();
	}

	protected Class<?> getModelClass() {
		return AnalyticsMessage.class;
	}

	protected String getModelClassName() {
		return AnalyticsMessage.class.getName();
	}

	/**
	 * Performs a SQL query.
	 *
	 * @param sql the sql query
	 */
	protected void runSQL(String sql) {
		try {
			DataSource dataSource = analyticsMessagePersistence.getDataSource();

			DB db = DBManagerUtil.getDB();

			sql = db.buildSQL(sql);
			sql = PortalUtil.transformSQL(sql);

			SqlUpdate sqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(
				dataSource, sql);

			sqlUpdate.update();
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	protected AnalyticsMessageLocalService analyticsMessageLocalService;

	@Reference
	protected AnalyticsMessagePersistence analyticsMessagePersistence;

	@Reference
	protected com.liferay.counter.kernel.service.CounterLocalService
		counterLocalService;

	@Reference
	protected com.liferay.portal.kernel.service.UserLocalService
		userLocalService;

	@Reference
	protected File _file;

	private static final InputStream _EMPTY_INPUT_STREAM =
		new UnsyncByteArrayInputStream(new byte[0]);

	private boolean _useTempFile;

}