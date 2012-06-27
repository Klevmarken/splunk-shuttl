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
package com.splunk.shuttl.archiver.listers;

import com.splunk.shuttl.archiver.archive.ArchiveConfiguration;
import com.splunk.shuttl.archiver.bucketsize.ArchiveBucketSize;
import com.splunk.shuttl.archiver.thaw.BucketFilter;
import com.splunk.shuttl.archiver.thaw.BucketFormatResolver;
import com.splunk.shuttl.archiver.thaw.BucketFormatResolverFactory;
import com.splunk.shuttl.archiver.thaw.BucketSizeResolver;

/**
 * Factory for creating {@link ListsBucketsFiltered} instances.
 */
public class ListsBucketsFilteredFactory {

	/**
	 * @return instace of {@link ListsBucketsFiltered} configured with specified
	 *         config.
	 */
	public static ListsBucketsFiltered create(ArchiveConfiguration config) {
		ArchiveBucketsLister bucketsLister = ArchiveBucketsListerFactory
				.create(config);
		BucketFilter bucketFilter = new BucketFilter();
		BucketFormatResolver bucketFormatResolver = BucketFormatResolverFactory
				.create(config);
		return new ListsBucketsFiltered(bucketsLister, bucketFilter,
				bucketFormatResolver, new BucketSizeResolver(
						ArchiveBucketSize.create(config)));
	}

}
