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

import static com.splunk.shuttl.testutil.TUtilsFile.*;
import static org.testng.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.splunk.shuttl.archiver.bucketlock.BucketLock;
import com.splunk.shuttl.archiver.model.Bucket;
import com.splunk.shuttl.testutil.TUtilsBucket;

@Test(groups = { "fast-unit" })
public class ThawBucketLockerTest {

	private File thawLocksDirectory;
	private ThawBucketLocker thawBucketLocker;

	@BeforeMethod
	public void setUp() {
		thawLocksDirectory = createDirectory();
		thawBucketLocker = new ThawBucketLocker(thawLocksDirectory);
	}

	@AfterMethod
	public void tearDown() {
		FileUtils.deleteQuietly(thawLocksDirectory);
	}

	public void getLockForBucket_givenThawLocksDirectory_lockIsInThatDirectory() {
		Bucket bucket = TUtilsBucket.createBucket();
		BucketLock lockForBucket = thawBucketLocker.getLockForBucket(bucket);
		assertEquals(thawLocksDirectory.getAbsolutePath(), lockForBucket
				.getLockFile().getParentFile().getAbsolutePath());
	}
}
