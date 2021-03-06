// Copyright (C) 2011 Splunk Inc.
//
// Splunk Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.splunk.shuttl.server.mbeans.rest;

import static com.splunk.shuttl.ShuttlConstants.*;
import static com.splunk.shuttl.archiver.LogFormatter.*;
import static java.util.Arrays.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.splunk.shuttl.archiver.flush.Flusher;
import com.splunk.shuttl.archiver.listers.ArchivedIndexesListerFactory;
import com.splunk.shuttl.archiver.model.Bucket;
import com.splunk.shuttl.archiver.model.IllegalIndexException;
import com.splunk.shuttl.archiver.thaw.SplunkSettingsFactory;
import com.splunk.shuttl.server.model.BucketBean;

/**
 * @author petterik
 * 
 */
@Path(ENDPOINT_ARCHIVER + ENDPOINT_BUCKET_FLUSH)
public class FlushEndpoint {

	private static final Logger logger = Logger.getLogger(FlushEndpoint.class);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String flushBuckets(@FormParam("index") final String index,
			@FormParam("from") String from, @FormParam("to") String to) {
		logger.info(happened("Received REST request to list buckets", "endpoint",
				ENDPOINT_LIST_BUCKETS, "index", index, "from", from, "to", to));

		Date fromDate = RestUtil.getValidFromDate(from);
		Date toDate = RestUtil.getValidToDate(to);

		List<Exception> errors = new ArrayList<Exception>();
		Flusher flusher = new Flusher(SplunkSettingsFactory.create(),
				ArchivedIndexesListerFactory.create());

		List<String> indexes;
		if (index == null)
			indexes = ArchivedIndexesListerFactory.create().listIndexes();
		else
			indexes = asList(index);

		for (String i : indexes) {
			try {
				flusher.flush(i, fromDate, toDate);
			} catch (IllegalIndexException e) {
				errors.add(e);
			}
		}

		return respondWithFlushedBuckets(flusher.getFlushedBuckets(), errors);
	}

	private String respondWithFlushedBuckets(List<Bucket> flushedBuckets,
			List<Exception> errors) {
		List<BucketBean> responseBeans = new ArrayList<BucketBean>();
		for (Bucket b : flushedBuckets)
			responseBeans.add(BucketBean.createBeanFromBucket(b));

		return RestUtil.writeBucketAction(responseBeans, new ArrayList<Object>());
	}
}
