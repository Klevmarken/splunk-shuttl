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
package com.splunk.shuttl.archiver.thaw;

import static com.splunk.shuttl.archiver.LogFormatter.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.splunk.shuttl.archiver.bucketsize.ArchiveBucketSize;
import com.splunk.shuttl.archiver.model.Bucket;

/**
 * Resolves sizes for buckets.
 */
public class BucketSizeResolver {

	private static final Logger logger = Logger
			.getLogger(BucketSizeResolver.class);

	private final ArchiveBucketSize archiveBucketSize;

	/**
	 * @param archiveBucketSize
	 */
	public BucketSizeResolver(ArchiveBucketSize archiveBucketSize) {
		this.archiveBucketSize = archiveBucketSize;
	}

	/**
	 * Takes a list of buckets and gives the buckets the size which is persisted
	 * in the archive file system.
	 */
	public List<Bucket> resolveBucketsSizes(List<Bucket> bucketsWithFormats) {
		ArrayList<Bucket> bucketsWithSize = new ArrayList<Bucket>();
		for (Bucket bucket : bucketsWithFormats)
			bucketsWithSize.add(createBucketWithSize(bucket));
		return bucketsWithSize;
	}

	private Bucket createBucketWithSize(Bucket bucket) {
		long size = archiveBucketSize.getSize(bucket);
		return createBucketWithErrorHandling(bucket, size);
	}

	private Bucket createBucketWithErrorHandling(Bucket bucket, long size) {
		try {
			return new Bucket(bucket.getURI(), bucket.getIndex(), bucket.getName(),
					bucket.getFormat(), size);
		} catch (IOException e) {
			logger
					.error(did("Tried creating " + "bucket with size", e,
							"To create a bucket from " + "an existing bucket object"
									+ " keeping everything but size.", "bucket", bucket, "size",
							size));
			throw new RuntimeException(e);
		}
	}
}
